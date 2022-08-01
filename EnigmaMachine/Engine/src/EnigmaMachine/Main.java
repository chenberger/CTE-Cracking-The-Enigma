package EnigmaMachine;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.*;
public class Main {

    public static void main(String[] args) {
/*        Map<Character, Integer> ABC = new HashMap<Character, Integer>();
        ABC.put('A', 0);
        ABC.put('B', 1);
        ABC.put('C', 2);
        ABC.put('D', 3);
        ABC.put('E', 4);
        ABC.put('F', 5);

        ArrayList<Pair<Character, Character>> mappingABC1 = new ArrayList<>();
        List<Pair<Character, Character>> mappingABC2 = new ArrayList<>();
        mappingABC1.add(new Pair<Character, Character>('F', 'A'));
        mappingABC1.add(new Pair<Character, Character>('E', 'B'));
        mappingABC1.add(new Pair<Character, Character>('D', 'C'));
        mappingABC1.add(new Pair<Character, Character>('C', 'D'));
        mappingABC1.add(new Pair<Character, Character>('B', 'E'));
        mappingABC1.add(new Pair<Character, Character>('A', 'F'));

        mappingABC2.add(new Pair<Character, Character>('E', 'A'));
        mappingABC2.add(new Pair<Character, Character>('B', 'B'));
        mappingABC2.add(new Pair<Character, Character>('D', 'C'));
        mappingABC2.add(new Pair<Character, Character>('F', 'D'));
        mappingABC2.add(new Pair<Character, Character>('C', 'E'));
        mappingABC2.add(new Pair<Character, Character>('A', 'F'));

        Rotor rotor1 = new Rotor(1, 3, true, mappingABC1, 'C');
        Rotor rotor2 = new Rotor(2, 0, false, mappingABC2, 'C');

        List<Rotor> rotors = new ArrayList<Rotor>();
        rotors.add(rotor1);
        rotors.add(rotor2);

        PluginBoard pluginBoard = new PluginBoard("AF");
        Map<Integer, Integer> reflectorPairs = new HashMap<Integer, Integer>();
        reflectorPairs.put(0, 3);
        reflectorPairs.put(3, 0);
        reflectorPairs.put(4, 1);
        reflectorPairs.put(1, 4);
        reflectorPairs.put(2, 5);
        reflectorPairs.put(5, 2);
        Reflctor reflector = new Reflctor("I", reflectorPairs);

        EnigmaMachine enigmaMachine = new EnigmaMachine(rotors, reflector, pluginBoard, ABC);

        System.out.println(enigmaMachine.decode('C'));
        System.out.println(enigmaMachine.decode('E'));
        System.out.println(enigmaMachine.decode('F'));
        System.out.println(enigmaMachine.decode('D'));
        System.out.println(enigmaMachine.decode('A'));
        System.out.println(enigmaMachine.decode('B'));
        System.out.println(enigmaMachine.decode('C'));
        System.out.println(enigmaMachine.decode('E'));
        System.out.println(enigmaMachine.decode('F'));
        System.out.println(enigmaMachine.decode('F'));
        System.out.println(enigmaMachine.decode('C'));
        System.out.println(enigmaMachine.decode('E'));
        System.out.println(enigmaMachine.decode('D'));
        System.out.println(enigmaMachine.decode('A'));
        System.out.println(enigmaMachine.decode('B'));
        System.out.println(enigmaMachine.decode('A'));
        System.out.println(enigmaMachine.decode('B'));
        System.out.println(enigmaMachine.decode('D'));*/
        //region Format test
        List<Integer> rotorId = new ArrayList<>(Arrays.asList(1, 2, 3));
        char openSector = '<';
        char closeSector = '>';
        CharSequence delimiter = ",";
        CharSequence delimiterPair = "|";

        RotorIDSector rotorIDSector = new RotorIDSector(rotorId);
        //InitialRotorPositionSector initialRotorPositionSector = new InitialRotorPositionSector(rotorId.toArray());
        ReflectorIdSector reflectorIdSector = new ReflectorIdSector(new ArrayList<RomanNumber>(Arrays.asList(RomanNumber.IV)));
        PluginBoardSector pluginPairSector = new PluginBoardSector(new ArrayList<>(Arrays.asList(new Pair<>('A', 'D'))));
        System.out.println(enigmaMachine.decode('D'));

        SettingsFormat test = new SettingsFormat();
        test.addSector(rotorIDSector);
        //test.addSector(initialRotorPositionSector);
        test.addSector(reflectorIdSector);
        test.addSector(pluginPairSector);

        System.out.println(test);
        //endregion
    }
}
