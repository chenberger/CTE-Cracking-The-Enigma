package AllyServlet;

import DTO.OnLineContestsTable;
import Engine.AlliesManager.Allie;
import Engine.AlliesManager.AlliesManager;
import Engine.BattleField;
import Engine.UBoatManager.UBoat;
import Engine.UBoatManager.UBoatManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servletUtils.ServletUtils;
import servletUtils.SessionUtils;

import java.io.IOException;

import static Utils.Constants.GSON_INSTANCE;

@WebServlet(name = "AllyOpsServlet",urlPatterns = {"/AllyOps"})
public class AllyOpsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        if(request.getParameter("action").equals("setTaskSize")) {
            setAllyTaskSize(request, response);
        }
        else if(request.getParameter("action").equals("getCurrentContestData")){
            getCurrentContestData(request, response);
        }
    }

    private void getCurrentContestData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AlliesManager alliesManager = ServletUtils.getAlliesManager(getServletContext());
        Allie allie = alliesManager.getAllie(SessionUtils.getAllieName(request));
        UBoatManager uBoatManager = ServletUtils.getUBoatManager(getServletContext());
        String uBoatName = uBoatManager.getUBoatByBattleName(allie.getBattleName());
        if(uBoatName != null) {
            UBoat uBoat = uBoatManager.getUBoat(uBoatName);
            BattleField battleField = uBoat.getBattleField();
            String teamsRegisteredAndNeeded = battleField.getNumberOfTeamsInBattleField() + "/" + battleField.getNumberOfAlliesToStartBattle();
            OnLineContestsTable onLineContestsTable = new OnLineContestsTable(battleField.getBattleFieldName(), uBoat.getName(), uBoat.getContestStatus(),
                    battleField.getLevel().name(),teamsRegisteredAndNeeded);
            String json = GSON_INSTANCE.toJson(onLineContestsTable);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(json);
            response.getWriter().flush();
        }
        else{
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
        UBoat uBoat = uBoatManager.getUBoat(uBoatName);
    }

    synchronized private void setAllyTaskSize(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long taskSize = Long.parseLong(request.getParameter("taskSize"));
        AlliesManager  alliesManager = ServletUtils.getAlliesManager(getServletContext());
        Allie allie = alliesManager.getAllie(SessionUtils.getAllieName(request));
        response.setStatus(HttpServletResponse.SC_OK);
        allie.setTaskSize(taskSize);
    }

    //create the response json string
}