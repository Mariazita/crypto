package be.maria.app;

import be.maria.Block;
import be.maria.LivChain;
import be.maria.Wallet;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

public class TestApplication extends LivChain {

    private static Wallet walletA;
    private static Wallet walletB;
    private static TextTerminal terminal;
    private static TextIO textIO;
    private static Block nextBlock;

    public static void main(String[] args) {
        LivChain.initialize();

        initApplication();

        printStatusInfo();

        int chosenOption;
        do {
            chosenOption = chooseMenuOption();
            switch (chosenOption) {
                case 1:
                    sendCoins(walletA, walletB);
                    break;
                case 2:
                    sendCoins(walletB, walletA);
                    break;
                case 3:
                default:
                    terminal.println("\nDemo finished, you can close this window.");
                    break;
            }
        } while (chosenOption < 3);
    }

    private static int chooseMenuOption() {
        textIO.getTextTerminal().getProperties().setPromptColor("cyan");
        terminal.println("\n1. Send from user A to user B.");
        terminal.println("2. Send from user B to user A.");
        terminal.println("3. End the program.");
        int selectedOption = textIO.newIntInputReader()
                .withDefaultValue(3)
                .read("?");

        return selectedOption;
    }

    private static void sendCoins(Wallet walletInput, Wallet walletOutput) {
        textIO.getTextTerminal().getProperties().setPromptColor("cyan");
        float amountToSend = textIO.newFloatInputReader()
                .read("\nHow much do you want to send? ");

        terminal.getProperties().setPromptColor("red");
        terminal.println("Attempting to send funds (" + String.valueOf(amountToSend) + ")...");

        nextBlock.addTransaction(walletInput.sendFunds(walletOutput.getPublicKey(), amountToSend));
        LivChain.addBlock(nextBlock);

        nextBlock = new Block(nextBlock.getHash());

        printStatusInfo();
    }

    private static void printStatusInfo() {
        terminal.getProperties().setPromptColor("red");

        terminal.println("WalletA's balance is: " + walletA.getBalance());
        terminal.println("WalletB's balance is: " + walletB.getBalance());
        terminal.println("Chain is valid?: " + LivChain.isChainValid());
    }

    private static void initApplication() {
        nextBlock = new Block(LivChain.getGenesis().getHash());
        walletA = LivChain.getWalletA();
        walletB = LivChain.getWalletB();

        textIO = TextIoFactory.getTextIO();
        terminal = textIO.getTextTerminal();
    }
}

