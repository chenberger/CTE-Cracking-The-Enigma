package AllieMainScenePane.Body.ContestTabPane;

import CompetitionServlets.TasksServlet;
import DesktopUserInterface.MainScene.ErrorDialog;
import Utils.HttpClientUtil;
import jakarta.servlet.http.HttpServletResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

import static Utils.Constants.ACTION;
import static Utils.Constants.TASKS_SERVLET;

public class LookForWinnerRefresher extends TimerTask {
    private Consumer<String> updateWinnerFound;
    public LookForWinnerRefresher(Consumer<String>  notifyIfWinnerFound) {
        this.updateWinnerFound = notifyIfWinnerFound;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(TASKS_SERVLET)
                .newBuilder()
                .addQueryParameter(ACTION, "checkIfContestIsOver")
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new ErrorDialog(new Exception("Something went wrong with the request"), "Something went wrong with the request");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code() == HttpServletResponse.SC_OK){
                    String winnerFound = response.body().string();
                    updateWinnerFound.accept(winnerFound);
                }
                response.close();
            }
        });
    }
}