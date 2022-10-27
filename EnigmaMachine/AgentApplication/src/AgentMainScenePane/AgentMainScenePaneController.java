package AgentMainScenePane;

import AgentLoginPane.AgentLoginPaneController;
import AgentMainScenePane.Body.AgentCandidates.AgentCandidatesPaneController;
import AgentMainScenePane.Body.AgentProgressAndStatusPane.AgentProgressAndStatusPaneController;
import AgentMainScenePane.Body.ContestAndTeamDataPane.ContestAndTeamDataPaneController;
import DTO.DataToAgentApplicationTableView;
import DTO.DataToInitializeMachine;
import DTO.TaskToAgent;
import DesktopUserInterface.MainScene.ErrorDialog;
import Engine.AgentsManager.AgentThreadFactory;
import Engine.Dictionary;
import Engine.EngineManager;
import EnigmaMachine.EnigmaMachine;
import Utils.HttpClientUtil;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static AlliesServletsPaths.AlliesServletsPaths.ALLIES_OPS_SERVLET;
import static UBoatServletsPaths.UBoatsServletsPaths.DICTIONARY_SERVLET;
import static UBoatServletsPaths.UBoatsServletsPaths.GET_MACHINE_CONFIG_SERVLET;
import static Utils.Constants.*;

public class AgentMainScenePaneController {
    private final String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/AgentLoginPane/AgentLoginPane.fxml";
    //private SimpleIntegerProperty numberOfTasksInQueue;
    private CompetitionHandler competitionHandler;
    private SimpleBooleanProperty isContestRuns;
    private OkHttpClient client;
    private boolean isMachineExists;
    private boolean isDictionaryExists;

    String agentName;
    private boolean participateInContest = false;
    private BlockingQueue<TaskToAgent> contestTasksQueue;
    private EngineManager engineManager;
    private ThreadPoolExecutor tasksPool;
    private BlockingQueue<Runnable> tasksQueue;
    private int numberOfThreads;
    private Long tasksPullingInterval;
    AgentLoginPaneController agentLoginPaneController;
    @FXML private AnchorPane agentMainScenePane;
    @FXML private Button logOutButton;
    @FXML private AnchorPane agentCandidatesPane;
    @FXML private AgentCandidatesPaneController agentCandidatesPaneController;
    @FXML private AnchorPane agentProgressAndDataPane;
    @FXML private AgentProgressAndStatusPaneController agentProgressAndDataPaneController;
    @FXML private AnchorPane contestAndTeamDataPane;
    @FXML private ContestAndTeamDataPaneController contestAndTeamDataPaneController;
    @FXML private Label agentNameLabel;
    @FXML public void initialize() {
        if(agentLoginPaneController != null) {
            agentLoginPaneController.setAgentMainScenePaneController(this);
        }
        if (agentCandidatesPaneController != null) {
            agentCandidatesPaneController.setAgentMainSceneController(this);
        }
        if(agentProgressAndDataPaneController != null) {
            agentProgressAndDataPaneController.setAgentMainSceneController(this);
        }
        if(contestAndTeamDataPaneController != null) {
            contestAndTeamDataPaneController.setAgentMainSceneController(this);
        }
        engineManager = new EngineManager();
        isContestRuns = new SimpleBooleanProperty(false);
        isDictionaryExists = false;
        isMachineExists = false;
        client = new OkHttpClient();
        logOutButton.disableProperty().bind(isContestRuns);
        //numberOfTasksInQueue = new SimpleIntegerProperty(0);

    }
    public void setActive() {
        //agentCandidatesPaneController.startRefreshing();
        //agentProgressAndDataPaneController.startRefreshing();
        contestAndTeamDataPaneController.startRefreshing();
    }
    public void setAgentName(String agentName) {
        Platform.runLater(()->{
            this.agentName = agentName;
            this.agentNameLabel.setText(agentName);
        });

    }
    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }
    public void setTasksPullingInterval(Long tasksPullingInterval) {
        this.tasksPullingInterval = tasksPullingInterval;
    }

    public void onLogOutButtonClicked(ActionEvent actionEvent) {
        String finalUrl  = HttpUrl.parse(LOGOUT_SERVLET).newBuilder()
                .addQueryParameter(ACTION, "agentLogout")
                .build().toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    new ErrorDialog(new Exception("Error while trying to log out"), "Error while trying to log out");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code() == 200){
                    String responseString = GSON_INSTANCE.fromJson(response.body().string(), String.class);
                    Platform.runLater(()->{
                        HttpClientUtil.removeCookiesOf("localhost");
                        new ErrorDialog(new Exception(responseString), "Logged out successfully");
                        closeAgent();
                    });

                }else {
                    Platform.runLater(() -> {
                        new ErrorDialog(new Exception("Error while trying to log out"), "Error while trying to log out");
                    });
                }
                response.close();
            }
        });
    }

    private void closeAgent() {
        contestAndTeamDataPaneController.close();
        agentProgressAndDataPaneController.close();
        agentCandidatesPaneController.close();
        loadLoginPage();
    }

    public void startContest() {
    }

    public void startBruteForce()  {
        try {
            agentCandidatesPaneController.clearTable();
            checkIfParticipateInBattle();
            if (participateInContest) {
                contestTasksQueue = new LinkedBlockingQueue<>();
                tasksQueue = new LinkedBlockingQueue<>();
                tasksPool = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 5000, TimeUnit.SECONDS, tasksQueue, new AgentThreadFactory(numberOfThreads), new ThreadPoolExecutor.CallerRunsPolicy());
                getBattlesEnigmaMachine();
                getBattlesDictionary();
                startWorking();
        }
        } catch(IOException e){
            e.printStackTrace();
        }

    }

    private void getBattlesEnigmaMachine() throws IOException {
        String finalUrl = HttpUrl.parse(GET_MACHINE_CONFIG_SERVLET)
                .newBuilder()
                .addQueryParameter(ACTION, "getMachineDataForInitialize")
                .addQueryParameter("agentName", agentName)
                .build()
                .toString();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Response response = client.newCall(request).execute();
        if(response.code() == 200){
               Gson gson = new Gson();
               String responseString = response.body().string();
               DataToInitializeMachine dataToInitializeMachine = gson.fromJson(responseString, DataToInitializeMachine.class);
               engineManager.setEnigmaMachine(new EnigmaMachine(dataToInitializeMachine.getRotors(), dataToInitializeMachine.getReflectors()
                       ,dataToInitializeMachine.getKeyboard(), dataToInitializeMachine.getAmountCurrentRotorsInUse()));
        }
        response.close();
        //HttpClientUtil.runAsync(finalUrl, new Callback() {
        //    @Override
        //    public void onFailure(okhttp3.Call call, java.io.IOException e) {
        //        e.printStackTrace();
        //    }
////
        //    @Override
        //    public void onResponse(Call call, Response response) throws java.io.IOException {
        //        if(response.code() == 200) {
        //            Gson gson = new Gson();
        //            String responseString = response.body().string();
        //            DataToInitializeMachine dataToInitializeMachine = gson.fromJson(responseString, DataToInitializeMachine.class);
        //            engineManager.setEnigmaMachine(new EnigmaMachine(dataToInitializeMachine.getRotors(), dataToInitializeMachine.getReflectors()
        //                    ,dataToInitializeMachine.getKeyboard(), dataToInitializeMachine.getAmountCurrentRotorsInUse()));
        //            isMachineExists = true;
        //        }
        //        response.close();
        //    }
        //});
    }

    private void  getBattlesDictionary() throws IOException {
        String finalUrl = HttpUrl.parse(DICTIONARY_SERVLET)
                .newBuilder()
                .addQueryParameter(ACTION, "getDictionaryObject")
                .addQueryParameter("agentName", agentName)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Response response = client.newCall(request).execute();
        if(response.code() == 200){
            Gson gson = new Gson();
            String responseString = response.body().string();
            Dictionary dictionary = gson.fromJson(responseString, Dictionary.class);
            engineManager.setDictionary(dictionary);
        }
        //HttpClientUtil.runAsync(finalUrl, new Callback() {
        //    @Override
        //    public void onFailure(okhttp3.Call call, java.io.IOException e) {
        //        e.printStackTrace();
        //    }
////
        //    @Override
        //    public void onResponse(Call call, Response response) throws java.io.IOException {
        //        if(response.code() == 200) {
        //            Gson gson = new Gson();
        //            String responseString = response.body().string();
        //            Engine.Dictionary dictionary = gson.fromJson(responseString, Dictionary.class);
        //            engineManager.setDictionary(dictionary);
        //            isDictionaryExists = true;
////
        //        }
        //    }
        //});
    }
    private void startWorking() {

        competitionHandler = new CompetitionHandler(new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 5000, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new AgentThreadFactory(numberOfThreads), new ThreadPoolExecutor.CallerRunsPolicy())
        , engineManager, new LinkedBlockingQueue<>(), agentName, new OkHttpClient(),this, new LinkedBlockingQueue<>() );
        competitionHandler.start();

    }

    private void checkIfParticipateInBattle() throws IOException {
        String finalUrl = HttpUrl.parse(ALLIES_OPS_SERVLET)
                .newBuilder()
                .addQueryParameter(ACTION, "participateInBattle")
                .addQueryParameter("agentName", agentName)
                .build()
                .toString();
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Response response = client.newCall(request).execute();
        if(response.code() == 200){
            participateInContest = true;
        }
        response.close();

        //HttpClientUtil.runAsync(finalUrl, new Callback() {
        //    @Override
        //    public void onFailure(okhttp3.Call call, java.io.IOException e) {
        //        new ErrorDialog(e, "Error while trying to get participate value");
        //    }
//
        //    @Override
        //    public void onResponse(Call call, Response response) throws java.io.IOException {
        //        if (response.code() == 200) {
        //            Gson gson = new Gson();
        //            String responseString = response.body().string();
        //            //TODO: change to boolean from json
        //            participateInContest = gson.fromJson(responseString, Boolean.class);
        //        } else {
        //            new ErrorDialog(new Exception(response.body().string()), "Error while trying to get participate value");
        //        }
//
        //    }
        //});
    }

    public boolean isContestActive() {
        return contestAndTeamDataPaneController.isContestActive();
    }

    public void updateTasksCompleted(long tasksCompleted, int numberOfCandidatesFound) {
        agentProgressAndDataPaneController.updateTasksCompleted(tasksCompleted, numberOfCandidatesFound);
    }


    public void updateTasksPulled(long numberOfTasksPulled) {
        agentProgressAndDataPaneController.updateTasksPulled(numberOfTasksPulled);
    }

    public void updateCandidatesTable(List<DataToAgentApplicationTableView> agentCandidatesInformationList) {
        agentCandidatesPaneController.updateCandidatesTable(agentCandidatesInformationList);
    }

    public void stopRefreshing() {
        agentCandidatesPaneController.close();
    }

    public void updateNumberOfTasksInQueueToLabel(long numberOfTasksInQueue) {
        agentProgressAndDataPaneController.updateNumberOfTasksInQueue(numberOfTasksInQueue);
    }

    public void setContestActivity(boolean contestIsActive) {
        isContestRuns.set(contestIsActive);
    }


    //HttpClientUtil.runAsync(finalUrl, new Callback() {
       //    @Override
       //    public void onFailure(okhttp3.Call call, java.io.IOException e) {
       //        e.printStackTrace();
       //    }

       //    @Override
       //    public void onResponse(Call call, Response response) throws java.io.IOException {
       //        if(response.code() == 200) {
       //            Gson gson = new Gson();
       //            String responseString = response.body().string();
       //            List<TaskToAgent> tasksToAgent = Arrays.asList(gson.fromJson(responseString, TaskToAgent[].class));

       //            contestTasksQueue.addAll(tasksToAgent);
       //            try {
       //                 for(int i = 0; i < tasksToAgent.size(); i++) {
       //                     AgentTask agentTask = getAgentTaskFromTaskToAgent(tasksToAgent.get(i));
       //                     AgentWorker agent = new AgentWorker(agentTask);
       //                     tasksPool.execute(agent);
       //                 }

       //            } catch (CloneNotSupportedException e) {
       //                e.printStackTrace();
       //            }
       //        }

       //    }
       //});
       private void loadLoginPage() {
           Scene scene = agentMainScenePane.getScene();
           URL url = getClass().getResource(LOGIN_PAGE_FXML_RESOURCE_LOCATION);
           FXMLLoader fxmlLoader = new FXMLLoader(url);
           try {
               Parent root = fxmlLoader.load();
               agentLoginPaneController = fxmlLoader.getController();
               scene.setRoot(root);
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
}
