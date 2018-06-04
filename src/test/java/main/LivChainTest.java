package main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import be.maria.Block;
import be.maria.LivChain;
import org.junit.Before;
import org.junit.Test;

public class LivChainTest extends LivChain {

    @Before
    public void setUp() {
        LivChain.initialize();
    }

    @Test
    public void sendFundsBetweenTwoWalletsSuccess() {
        //testing
        Block block1 = new Block(genesis.getHash());
        float amountToSend = 40f;
        float walletAInitialBalance = walletA.getBalance();
        float walletBInitialBalance = walletB.getBalance();

        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (" + String.valueOf(amountToSend)  + ") to WalletB...");

        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), amountToSend));
        addBlock(block1);

        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        assertEquals(walletAInitialBalance - amountToSend, walletA.getBalance(), 0f);
        assertEquals(walletBInitialBalance + amountToSend, walletB.getBalance(), 0f);

        assertTrue(isChainValid());
    }

    @Test
    public void sendFundsNoBalance() {
        //testing
        Block block1 = new Block(genesis.getHash());
        float amountToSend = 1000f;
        float walletAInitialBalance = walletA.getBalance();
        float walletBInitialBalance = walletB.getBalance();

        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (" + String.valueOf(amountToSend)  + ") to WalletB...");

        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), amountToSend));
        addBlock(block1);

        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        assertEquals(walletAInitialBalance, walletA.getBalance(), 0f);
        assertEquals(walletBInitialBalance, walletB.getBalance(), 0f);

        assertTrue(isChainValid());
    }

    @Test
    public void sendFundsSuccessThree() {
        //testing
        Block block1 = new Block(genesis.getHash());
        float amountToSendStepOne = 40f;
        float amountToSendStepTwo = 20f;
        float walletAInitialBalance = walletA.getBalance();
        float walletBInitialBalance = walletB.getBalance();
        float walletCInitialBalance = walletC.getBalance();

        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), amountToSendStepOne));
        addBlock(block1);

        assertEquals(walletAInitialBalance - amountToSendStepOne, walletA.getBalance(), 0f);
        assertEquals(walletBInitialBalance + amountToSendStepOne, walletB.getBalance(), 0f);
        assertEquals(walletCInitialBalance, walletC.getBalance(), 0f);

        Block block2 = new Block(block1.getHash());
        block2.addTransaction(walletB.sendFunds(walletC.getPublicKey(), amountToSendStepTwo));
        addBlock(block2);

        assertEquals(walletAInitialBalance - amountToSendStepOne, walletA.getBalance(), 0f);
        assertEquals(walletBInitialBalance + amountToSendStepOne - amountToSendStepTwo, walletB.getBalance(), 0f);
        assertEquals(walletCInitialBalance + amountToSendStepTwo, walletC.getBalance(), 0f);

        assertTrue(isChainValid());
    }

    @Test
    public void testChainFork() {
        //testing
        Block block1 = new Block(genesis.getHash());
        float amountToSendStepOne = 40f;
        float amountToSendStepTwo = 20f;
        float walletAInitialBalance = walletA.getBalance();
        float walletBInitialBalance = walletB.getBalance();
        float walletCInitialBalance = walletC.getBalance();

        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), amountToSendStepOne));
        addBlock(block1);

        assertEquals(walletAInitialBalance - amountToSendStepOne, walletA.getBalance(), 0f);
        assertEquals(walletBInitialBalance + amountToSendStepOne, walletB.getBalance(), 0f);
        assertEquals(walletCInitialBalance, walletC.getBalance(), 0f);

        Block block2 = new Block(genesis.getHash());
        block2.addTransaction(walletB.sendFunds(walletC.getPublicKey(), amountToSendStepTwo));
        addBlock(block2);

        assertEquals(walletAInitialBalance - amountToSendStepOne, walletA.getBalance(), 0f);
        assertEquals(walletBInitialBalance + amountToSendStepOne - amountToSendStepTwo, walletB.getBalance(), 0f);
        assertEquals(walletCInitialBalance + amountToSendStepTwo, walletC.getBalance(), 0f);

        assertFalse(isChainValid());
    }
}
