package MainScene.UBoatMachinePane;

import MainScene.UBoatMachinePane.CodeCalibrationPane.CodeCalibrationController;
import MainScene.UBoatMachinePane.CurrentCodeConfigurationPane.CurrentCodeConfigurationController;
import MainScene.UBoatMachinePane.MachineDetailsPane.MachineDetailsController;
import MainScene.MainUBoatScenePaneController;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class UBoatMachinePaneController {
    @FXML
    private AnchorPane UBoatMachinePane;
    @FXML private GridPane machineDetailsPane;
    @FXML private MachineDetailsController machineDetailsPaneController;
    @FXML private GridPane codeCalibration;
    @FXML private CodeCalibrationController codeCalibrationPaneController;
    @FXML private VBox currentCodeConfigurationPane;
    @FXML private CurrentCodeConfigurationController currentCodeConfigurationPaneController;
    private MainUBoatScenePaneController mainUBoatScenePaneController;

    public void initialize() {
        if (machineDetailsPaneController != null) {
            machineDetailsPaneController.setUBoatMachinePaneController(this);
        }
        if (codeCalibrationPaneController != null) {
            codeCalibrationPaneController.setUBoatMachinePaneController(this);
        }

        if(currentCodeConfigurationPaneController != null){
            currentCodeConfigurationPaneController.setUBoatCompetitionPaneController(this);
        }
    }

    public void setMainUBoatScenePaneController(MainUBoatScenePaneController mainUBoatScenePaneController) {
        this.mainUBoatScenePaneController = mainUBoatScenePaneController;
    }

    public void setCodeConfiguration(String currentMachineConfiguration) {
        currentCodeConfigurationPaneController.setCodeConfiguration(currentMachineConfiguration);
    }

    public void setNewConfiguration(String currentCodeConfiguration) {
        currentCodeConfigurationPaneController.setCodeConfiguration(currentCodeConfiguration);
        mainUBoatScenePaneController.newCodeConfigurationSetted(currentCodeConfiguration);
    }

    public void newFileLoaded() {
        codeCalibrationPaneController.newFileLoaded();
        machineDetailsPaneController.machineDetailsChanged();
    }
    public void machineDetailsChanged(){
        machineDetailsPaneController.machineDetailsChanged();
    }

    public void incrementMessagesCounter() {
        machineDetailsPaneController.incrementMessagesCounter();
    }
}
