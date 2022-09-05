package BruteForce;

import DTO.BruteForceTask;
import Engine.Dictionary;
import EnigmaMachine.EnigmaMachine;
import EnigmaMachineException.IllegalAgentsAmountException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DecryptionManager {
    private int maxCurrentAmountOfAgents;
    private Dictionary dictionary;
    private EnigmaMachine enigmaMachine;
    private ExecutorService threadPoolExecutor;
    private static final int MIN_AGENTS_AMOUNT = 2;
    private static final int MAX_AGENTS_AMOUNT = 50;
    private Integer taskSize;
    private BruteForceUIAdapter bruteForceUIAdapter;
    private BruteForceTask bruteForceTask;
    private DecipherStatistics decipherStatistics;
    private String decryptedMessege;

    public DecryptionManager(EnigmaMachine enigmaMachine, Dictionary dictionary, BruteForceUIAdapter bruteForceUIAdapter, BruteForceTask bruteForceTask) {
        this.bruteForceUIAdapter = bruteForceUIAdapter;
        this.decipherStatistics = new DecipherStatistics();
        this.bruteForceTask = bruteForceTask;
        this.enigmaMachine = enigmaMachine;
        this.dictionary = dictionary;
        this.threadPoolExecutor = Executors.newCachedThreadPool();
    }

    public DecryptionManager(){};

    public void setMaxCurrentAmountOfAgents(int maxCurrentAmountOfAgents) throws IllegalAgentsAmountException {
        if(maxCurrentAmountOfAgents >= MIN_AGENTS_AMOUNT && maxCurrentAmountOfAgents <= MAX_AGENTS_AMOUNT) {
            this.maxCurrentAmountOfAgents = maxCurrentAmountOfAgents;
        }
        else {
            throw new IllegalAgentsAmountException(maxCurrentAmountOfAgents, MIN_AGENTS_AMOUNT, MAX_AGENTS_AMOUNT);
        }
    }

    public int getMaxCurrentAmountOfAgents() {
        return maxCurrentAmountOfAgents;
    }

    public void startDeciphering() {
        //TODO chen: call task manager to start
    }

    public String getDecryptionCandidatesStatistics() {
        return decipherStatistics.toString();
    }

    public void setDecryptedMessege(String decryptedMessege) {
        this.decryptedMessege = decryptedMessege;
    }

    public static int getMinAgentsAmount() {
        return MIN_AGENTS_AMOUNT;
    }

    public static int getMaxAgentsAmount() {
        return MAX_AGENTS_AMOUNT;
    }
}
