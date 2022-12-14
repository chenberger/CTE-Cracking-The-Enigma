package AllieMainScenePane.Body.ContestTabPane.AgentsProgressData;

import AllieMainScenePane.Body.ContestTabPane.ContestTabPaneController;
import DTO.AgentsProgressAndDataTable;
import DTO.AlliesTasksProgressToLabels;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.Closeable;
import java.util.List;
import java.util.Timer;

public class AgentsProgressDataController implements Closeable {
    Timer timer;
    AgentsProgressDataRefresher agentsProgressDataRefresher;

    private SimpleBooleanProperty autoUpdate;
    private ContestTabPaneController contestTabPaneController;
    @FXML
    private TableView<AgentsProgressAndDataTable> agentProgressDataTableView;

    @FXML
    private TableColumn<AgentsProgressAndDataTable, String> agentNameCol;

    @FXML
    private TableColumn<AgentsProgressAndDataTable, String> tasksPulledAndDoneCol;

    @FXML
    private TableColumn<AgentsProgressAndDataTable, String> totalCandidatesCol;

    @FXML
    private Label totalTasksOfAllyLabel;

    @FXML
    private Label tasksProducedLabel;

    @FXML
    private Label TasksCompletedLabel;

    public AgentsProgressDataController() {
        autoUpdate = new SimpleBooleanProperty(false);
    }
    @FXML public void initialize() {
        initializeTable();
    }

    private void initializeTable() {
        agentNameCol.setCellValueFactory(new PropertyValueFactory<AgentsProgressAndDataTable, String>("agentName"));
        tasksPulledAndDoneCol.setCellValueFactory(new PropertyValueFactory<AgentsProgressAndDataTable, String>("tasksPulledAndDone"));
        totalCandidatesCol.setCellValueFactory(new PropertyValueFactory<AgentsProgressAndDataTable, String>("TotalCandidates"));
    }

    public void setContestTabPaneController(ContestTabPaneController contestTabPaneController) {
        this.contestTabPaneController = contestTabPaneController;
    }
    public void startListRefreshing(){
        agentsProgressDataRefresher = new AgentsProgressDataRefresher(
                this::updateAgentsProgressDataTable,
                this::updateAlliesTasksProgressToLabels);
        timer = new Timer();
        timer.schedule(agentsProgressDataRefresher, 0, 50);
    }
    private void updateAgentsProgressDataTable(List<AgentsProgressAndDataTable> agentProgressDataToTableList){
        Platform.runLater(()->{
            clearAgentsProgressDataTable();
            for(AgentsProgressAndDataTable agentProgressDataToTable : agentProgressDataToTableList){
                ObservableList<AgentsProgressAndDataTable> data = agentProgressDataTableView.getItems();
                data.add(agentProgressDataToTable);
                agentProgressDataTableView.setItems(data);
            }
        });
    }

    private void clearAgentsProgressDataTable() {
        agentProgressDataTableView.getItems().clear();
    }

    private void updateAlliesTasksProgressToLabels(AlliesTasksProgressToLabels alliesTasksProgressToLabels){
        Platform.runLater(()->{
            totalTasksOfAllyLabel.setText(alliesTasksProgressToLabels.getTotalTasks().toString());
            tasksProducedLabel.setText(alliesTasksProgressToLabels.getTasksPulled().toString());
            if(alliesTasksProgressToLabels.getTasksDone() > alliesTasksProgressToLabels.getTasksPulled()) {
                TasksCompletedLabel.setText(alliesTasksProgressToLabels.getTasksPulled().toString());
            }
            else {
                TasksCompletedLabel.setText(alliesTasksProgressToLabels.getTasksDone().toString());
            }
        });
    }

    @Override
    public void close() {
        if(agentsProgressDataRefresher != null){
            agentsProgressDataRefresher.cancel();
            if(timer != null){
                timer.cancel();
            }
        }
    }

    public void cleanTable() {
        Platform.runLater(()->{
            agentProgressDataTableView.getItems().clear();
        });
    }
    public void cleanLabels(){
        Platform.runLater(()->{
            totalTasksOfAllyLabel.setText("");
            tasksProducedLabel.setText("");
            TasksCompletedLabel.setText("");
        });
    }
}
