package EnigmaMachine;

import EnigmaMachineException.PluginBoardSettingsException;
import EnigmaMachineException.ReflectorSettingsException;
import EnigmaMachineException.RotorsInUseSettingsException;
import EnigmaMachineException.StartingPositionsOfTheRotorException;
import javafx.util.Pair;

import java.util.*;

public class EnigmaMachine{
    private Map<Integer, Rotor> rotors;
    private List<Rotor> rotorsInUse;
    private Reflector reflectorInUse;
    private Map<RomanNumber, Reflector> reflectors;
    private PluginBoard pluginBoard;
    private final Map<Character, Integer> keyboard;


    private boolean isMachineSettingInitialized;

    public EnigmaMachine(Map<Integer, Rotor> rotors, Map<RomanNumber, Reflector> reflectors, Map<Character, Integer> keyboard) {
        this.rotors = rotors;
        this.reflectors = reflectors;
        this.keyboard = keyboard;
        this.rotorsInUse = new ArrayList<Rotor>();
        this.pluginBoard = new PluginBoard();
        this.isMachineSettingInitialized = false;
    }

    public void setPluginBoardSettings(PluginBoardSector pluginBoardSector) throws PluginBoardSettingsException {
        PluginBoardSettingsException pluginBoardSettingsException = new PluginBoardSettingsException();
        Map<Character, Boolean> charactersUsingForPluginBoard = new HashMap<>();
        initializeCharactersUsingForPluggedBoard(charactersUsingForPluginBoard);
        pluginBoard.clear();

        if(pluginBoardSector.getElements().size() > getMaximumPairs()) {
            pluginBoardSettingsException.addIllegalPairsSize(pluginBoardSector.getElements().size(), getMaximumPairs());
        }

        for(Pair<Character, Character> pluginPair : pluginBoardSector.getElements()) {
            if(pluginPair.getKey() == pluginPair.getValue()) {
                pluginBoardSettingsException.addValuePluggedToHimself(pluginPair.getKey());
            }

            if(keyboard.containsKey(pluginPair.getKey())) {
                if(keyboard.containsKey(pluginPair.getValue())) {
                    if(!charactersUsingForPluginBoard.get(pluginPair.getKey())) {
                        if(!charactersUsingForPluginBoard.get(pluginPair.getValue())) {
                            pluginBoard.addPluginPair(pluginPair);
                            charactersUsingForPluginBoard.put(pluginPair.getValue(),true);
                            charactersUsingForPluginBoard.put(pluginPair.getKey(),true);
                        }
                        else {
                            pluginBoardSettingsException.addValuePluggedToMoreThenOneChar(pluginPair.getValue());
                        }
                    }
                    else {
                        pluginBoardSettingsException.addValuePluggedToMoreThenOneChar(pluginPair.getKey());
                    }
                }
                else {
                    pluginBoardSettingsException.addIllegalCharNotFromTheKeyboard(pluginPair.getKey(), keyboard.keySet());
                }
            }
            else {
                pluginBoardSettingsException.addIllegalCharNotFromTheKeyboard(pluginPair.getValue(), keyboard.keySet());
            }
        }

        if(pluginBoardSettingsException.isExceptionNeedToThrown()) {
            pluginBoard.clear();
            throw pluginBoardSettingsException;
        }

    }

    private void initializeCharactersUsingForPluggedBoard(Map<Character, Boolean> charactersUsingForPluginBoard) {
        for(Character character : keyboard.keySet()) {
            charactersUsingForPluginBoard.put(character , false);
        }
    }

    public void setReflectorInUseSettings(ReflectorIdSector reflectorIdSector) throws ReflectorSettingsException {
        ReflectorSettingsException reflectorSettingsException = new ReflectorSettingsException();
        boolean isReflectorFound = false;
        RomanNumber reflectorId;

        if(reflectorInUse != null) {
            reflectorInUse.clear();
        }

        if(reflectorIdSector.getElements().size() != 1) {
            reflectorSettingsException.addIllegalReflectorsSize(reflectorIdSector.getElements().size());
         }

        reflectorId = (RomanNumber) reflectorIdSector.getElements().get(0);

        for(RomanNumber romanNumber : reflectors.keySet()) {
            if(romanNumber == reflectorId) {
                reflectorInUse = reflectors.get(romanNumber);
                isReflectorFound = true;
            }
        }

        if(!isReflectorFound) {
            reflectorSettingsException.addIllegalReflectorId(reflectorId, reflectors.keySet());
        }

        if(reflectorSettingsException.isExceptionNeedToThrown()) {
            if(reflectorInUse != null) {
                reflectorInUse.clear();
            }

            throw reflectorSettingsException;
        }
    }

    public void setStartingPositionRotorsSettings(StartingRotorPositionSector startPositionsOfTheRotors, RotorIDSector rotorIDSector) throws StartingPositionsOfTheRotorException {
        StartingPositionsOfTheRotorException startingPositionsOfTheRotorException = new StartingPositionsOfTheRotorException();
        //List<Character> reversedStartPositionsOfTheRotors = new ArrayList<Character>(startPositionsOfTheRotors.getElements());
        //Collections.reverse(reversedStartPositionsOfTheRotors);
        int index = 0;
        boolean isCharacterFound;

        if (startPositionsOfTheRotors.getElements().size() != rotorIDSector.getElements().size()) {
            startingPositionsOfTheRotorException.addIllegalPositionsSize(startPositionsOfTheRotors.getElements().size(), rotorIDSector.getElements().size());
        }

        for (Character startingRightCharToWindow : startPositionsOfTheRotors.getElements()) {
            isCharacterFound = false;
            for (Character character : keyboard.keySet()) {
                if (character == startingRightCharToWindow) {
                    rotors.get(rotorIDSector.getElements().get(index)).setStartingRightCharToWindow(startingRightCharToWindow);
                    isCharacterFound = true;
                }
            }

            if (!isCharacterFound) {
                startingPositionsOfTheRotorException.addIllegalCharacter(startingRightCharToWindow, keyboard.keySet());
            }
            index++;
        }

        if (startingPositionsOfTheRotorException.isExceptionNeedToThrown()) {
            throw startingPositionsOfTheRotorException;
        }
    }

    public void initializeRotorsInUseSettings(RotorIDSector rotorIDSector) throws RotorsInUseSettingsException {
        //TODO check if the size of the count rotors equal to rotorIDSector size
        RotorsInUseSettingsException rotorsInUseSettingsException = new RotorsInUseSettingsException();
        //List<Integer> reversedRotorsId = new ArrayList<Integer>(rotorIDSector.getElements());
        Map<Integer, Boolean> rotorsUsingForTheMachine = new HashMap<>();
        initializeRotorsUsingForTheMachine(rotorsUsingForTheMachine, rotorIDSector.getElements());
        //Collections.reverse(reversedRotorsId);
        rotorsInUse.clear();

        if(rotorIDSector.getElements().size() > rotors.size()) {
            rotorsInUseSettingsException.addIllegalAmountOfRotors(rotorIDSector.getElements().size(), rotors.size());
        }

        for(Integer rotorId : rotorIDSector.getElements()) {
            if(rotorsUsingForTheMachine.get(rotorId)) {
                rotorsInUseSettingsException.addRotorIdDuplicates(rotorId);
            }

            if(rotors.containsKey(rotorId)) {
                    rotorsInUse.add(rotors.get(rotorId));
            }
            else {
                rotorsInUseSettingsException.addIllegalRotorId(rotorId, rotors.keySet());
            }
        }

        if (rotorsInUseSettingsException.isExceptionNeedToThrown()) {
            rotorsInUse.clear();
            throw rotorsInUseSettingsException;
        }
    }

    private void initializeRotorsUsingForTheMachine(Map<Integer, Boolean> rotorsUsingForTheMachine, List<Integer> rotors) {
        for(Integer rotorId : rotors) {
            rotorsUsingForTheMachine.put(rotorId, false);
        }
    }

    public int getMaximumPairs() {
        return keyboard.size() / 2;
    }

    public Character decode(Character inputtedChar) {
        int currentCharIndex;
        Character currentChar;

        rotateRotors();

        currentChar = pluginBoard.getPluggedPair(inputtedChar);
        currentCharIndex = keyboard.get(currentChar);
        currentCharIndex = decodeByDirection(currentCharIndex, Direction.FORWARD);
        currentCharIndex = reflectorInUse.SetIndex(currentCharIndex);
        currentCharIndex = decodeByDirection(currentCharIndex, Direction.BACKWARD);
        currentChar = getKeyByValue(keyboard, currentCharIndex);

        currentChar = pluginBoard.getPluggedPair(currentChar);

        return currentChar;
    }


    private Character getKeyByValue(Map<Character, Integer> machineCharacters, int valueToSearch) {

        for (Map.Entry<Character,Integer> entry : machineCharacters.entrySet())
            if(entry.getValue() == valueToSearch) {
                return entry.getKey();
        }

        return null;
    }


    private void rotateRotors() {
        boolean isPreviewsRotorNotchReachedTheWindow = false;
        for(Rotor rotor : rotorsInUse) {
            isPreviewsRotorNotchReachedTheWindow = rotor.rotate(isPreviewsRotorNotchReachedTheWindow);
        }
    }

    private int decodeByDirection(int currentCharIndex, Direction direction) {

        List<Rotor> rotorsOrder = new ArrayList<>(rotorsInUse);

        if(direction == Direction.BACKWARD) {
            Collections.reverse(rotorsOrder);
        }
        for (Rotor rotor : rotorsOrder) {
           currentCharIndex = rotor.decode(currentCharIndex, direction);
        }

        return currentCharIndex;
    }
    private Reflector findReflectorById(String reflectorInUseId) {
        for(Reflector reflector : reflectors.values()) {
            if(reflector.id().equals(reflectorInUseId)) {
                return reflector;
            }
        }
        return null;
    }

    public void resetSettings() {
        // TODO implement this method after changes //
    }
    //region Setters

    public void setPluginBoard(PluginBoard pluginBoard) {
        this.pluginBoard = pluginBoard;
    }
    public void setReflectorInUseSettings(String id) {
        this.reflectorInUse = findReflectorById(id);
    }
    public void setMachineSettingInitialized(boolean machineSettingInitialized) {
        isMachineSettingInitialized = machineSettingInitialized;
    }

    //endregion

    //region Getters
    public Map<Integer, Rotor> getAllrotors() {
        return rotors;
    }

    public List<Rotor> getCurrentRotorsInUse() {
        return rotorsInUse;
    }

    public Map<RomanNumber, Reflector> getAllReflectors() {
        return reflectors;
    }

    public Reflector getCurrentReflectorInUse() {
        return reflectorInUse;
    }

    public PluginBoard getPluginBoard() {
        return pluginBoard;
    }

    public Set<Character> getKeyboard() {
        return keyboard.keySet();
    }

    public boolean isMachineSettingInitialized() {
        return isMachineSettingInitialized;
    }

    //endregion
}
