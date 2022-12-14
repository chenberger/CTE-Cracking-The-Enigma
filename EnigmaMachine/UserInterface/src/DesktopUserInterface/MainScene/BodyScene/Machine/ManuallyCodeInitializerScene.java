package DesktopUserInterface.MainScene.BodyScene.Machine;

import DTO.DetailsToManualCodeInitializer;
import MainScene.UBoatMachinePane.CodeCalibrationPane.CodeCalibrationController;
import DTO.MachineDetails;
import DesktopUserInterface.MainScene.Common.SkinType;
import MainScene.UBoatMachinePane.CodeCalibrationPane.ManuallyCodeController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ManuallyCodeInitializerScene {
    private ManuallyCodeController manuallyCodeController;
    private Stage manuallyCodeStage;
    private CodeCalibrationController codeCalibrationController;
    public void show(DetailsToManualCodeInitializer machineDetails, CodeCalibrationController codeCalibrationController, SkinType currentSkinType) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ManuallyCodeInitializer.fxml"));
        Parent load = fxmlLoader.load();

        manuallyCodeController = (ManuallyCodeController) fxmlLoader.getController();
        manuallyCodeController.setMachineDetails(machineDetails);
        manuallyCodeController.setCodeCalibrationController(codeCalibrationController);
        manuallyCodeController.initializeControls();
        manuallyCodeController.setSkin(currentSkinType);

        Scene scene = new Scene(load);
        manuallyCodeStage = new Stage();
        manuallyCodeStage.initModality(Modality.APPLICATION_MODAL);
        manuallyCodeStage.setScene(scene); // set the scene
        manuallyCodeStage.setTitle("Manually Code Configuration");
        manuallyCodeStage.show();
    }

    public void close() {
        manuallyCodeStage.close();
    }
}
