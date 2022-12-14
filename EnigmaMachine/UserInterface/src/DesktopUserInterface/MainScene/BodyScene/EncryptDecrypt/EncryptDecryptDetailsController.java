package DesktopUserInterface.MainScene.BodyScene.EncryptDecrypt;

import DesktopUserInterface.MainScene.Common.SkinType;
import EnigmaMachineException.MachineNotExistsException;
import EnigmaMachineException.SettingsNotInitializedException;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class EncryptDecryptDetailsController {

    @FXML private TextField encryptDecryptTextBox;
    @FXML private Button encryptDecryptButton;
    @FXML private Button ResetMachineStateButton;
    @FXML private Label encryptDecryptTextLabel;
    @FXML private ScrollPane encryptDecryptWordsScrollPane;
    @FXML private RadioButton fullWordButton;
    @FXML private RadioButton letterByLetterButton;
    @FXML private Button clearButton;
    @FXML private TextField decodedWordsTextArea;
    @FXML private Label encryptDecryptTextLabel1;
    @FXML private GridPane encryptDecryptGrid;
    private EncryptDecryptGridController encryptDecryptGridController;
    private Map<SkinType, String> skinPaths;
    private SimpleBooleanProperty fullWordDecodingProperty;
    private SimpleBooleanProperty fileLoadedProperty;

    public EncryptDecryptDetailsController() {
        fullWordDecodingProperty = new SimpleBooleanProperty(true);
        fileLoadedProperty = new SimpleBooleanProperty(false);
    }

    public void setEncryptDecryptGridController(EncryptDecryptGridController encryptDecryptGridController) {
        this.encryptDecryptGridController = encryptDecryptGridController;
    }

    @FXML public void initialize() {
        encryptDecryptTextBox.textProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!fullWordDecodingProperty.get() && newValue != null && newValue.length() > 0) {
                    encryptDecryptTextBox.setText(newValue);
                    encryptDecryptGridController.decodeWord(String.valueOf(encryptDecryptTextBox.getText().charAt(newValue.length() - 1)));
                }
            }
        });


        ToggleGroup toggleGroup = new ToggleGroup();
        fullWordButton.setToggleGroup(toggleGroup);
        letterByLetterButton.setSelected(true);
        letterByLetterButton.setToggleGroup(toggleGroup);

        fullWordButton.disableProperty().bind(fileLoadedProperty.not());
        letterByLetterButton.disableProperty().bind(fileLoadedProperty.not());

        initializeSkins();
    }

    private void initializeSkins() {
        int skinIndex = 1;
        skinPaths = new HashMap<>();
        for(SkinType skin : SkinType.values()) {
            skinPaths.put(skin, "EncryptDecryptDetailsSkin" + skinIndex++ + ".css");
        }
    }
    @FXML void onClearButtonClicked(ActionEvent event) {
        encryptDecryptTextBox.setText("");
        decodedWordsTextArea.setText("");
        encryptDecryptGridController.clearCurrentProccessedWord();
    }

    private void setScrollPaneColor(){
        encryptDecryptWordsScrollPane.setStyle("-fx-background-color: transparent;");
    }
    @FXML void onDecryptionButtonClicked(ActionEvent event) throws MachineNotExistsException, CloneNotSupportedException, SettingsNotInitializedException {
            try {
                if (fullWordDecodingProperty.get()) {
                    encryptDecryptGridController.decodeWord(encryptDecryptTextBox.getText());
                    encryptDecryptTextBox.clear();
                } else {

                    decodedWordsTextArea.clear();
                }
                if(encryptDecryptTextBox.textProperty().get().length() > 0 || fullWordDecodingProperty.get()) {
                    encryptDecryptGridController.onFinishInput();
                }
            } catch (MachineNotExistsException | CloneNotSupportedException | IllegalArgumentException ignored) {
            }
            encryptDecryptTextBox.clear();
}
    @FXML void onLetterByLetterButtonPressed(ActionEvent event) {
        fullWordDecodingProperty.set(false);
        encryptDecryptButton.setText("Done");
        encryptDecryptTextBox.clear();
        decodedWordsTextArea.clear();
    }
    @FXML private void onResetMachineStateButtonClicked(ActionEvent event) {
        encryptDecryptGridController.resetMachineState();
        encryptDecryptGridController.clearCurrentProccessedWord();
    }

    @FXML private void onFullWordButtonPressed(ActionEvent event){
        fullWordDecodingProperty.set(true);
        encryptDecryptButton.setText("Process");
        encryptDecryptTextBox.clear();
        decodedWordsTextArea.clear();
    }


    public void setDecodedWord(String decodeWord) {
        if(fullWordDecodingProperty.get()) {
            decodedWordsTextArea.setText(decodeWord);
        }
        else{
            decodedWordsTextArea.setText(decodedWordsTextArea.getText() + decodeWord);
        }
    }

    public void enableToProcess(Object o, Set<Character> characters) {
        fileLoadedProperty.set(true);
        fullWordDecodingProperty.set(false);
    }

    public SimpleBooleanProperty getFullWordDecodingProperty(){
        return fullWordDecodingProperty;
    }

    public void clearDecodingTextArea() {
        encryptDecryptTextBox.clear();
    }
    public void clearDecodedTextArea() {
        decodedWordsTextArea.clear();
    }

    public void keyboardButtonClicked(Character keyboardValue) {
        encryptDecryptTextBox.setText(encryptDecryptTextBox.getText() + keyboardValue);
    }

    public void setSkin(SkinType skinType) {
        encryptDecryptGrid.getStylesheets().clear();
        encryptDecryptGrid.getStylesheets().add(String.valueOf(getClass().getResource(skinPaths.get(skinType))));
    }

    public void clear() {
        encryptDecryptTextBox.clear();
        decodedWordsTextArea.clear();
    }
}
