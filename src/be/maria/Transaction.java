package be.maria;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {

    private String transactionId; // this is also the hash of the transaction.
    private PublicKey sender;
    private PublicKey recipient;
    private float value;
    private byte[] signature; // this is to prevent anybody else from spending funds in our wallet.

    private ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    private ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0; // a rough count of how many transactions have been generated.

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    private String calulateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) +
                        Float.toString(value) + sequence
        );
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);

    }


    public boolean processTransaction() {
        if (verifySignature() == false) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        //gather transaction inputs (Make sure they are unspent):
        for (TransactionInput i : inputs) {
            i.UTXO = LivChain.getUTXOs().get(i.transactionOutputId);
        }

        //check if transaction is valid:
        if (getInputsValue() < LivChain.getMinimumTransaction()) {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        //generate transaction outputs:
        float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
        transactionId = calulateHash();
        outputs.add(new TransactionOutput(this.recipient, value, transactionId)); //send value to recipient
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId)); //send the left over 'change' back to sender

        //add outputs to Unspent list
        for (TransactionOutput o : outputs) {
            LivChain.getUTXOs().put(o.getId(), o);
        }

        //remove transaction inputs from UTXO lists as spent:
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) continue; //if Transaction can't be found skip it
            LivChain.getUTXOs().remove(i.UTXO.getId());
        }

        return true;
    }

    //returns sum of inputs(UTXOs) values
    public float getInputsValue() {
        float total = 0;
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) continue; //if Transaction can't be found skip it
            total += i.UTXO.getValue();
        }
        return total;
    }

    //returns sum of outputs:
    public float getOutputsValue() {
        float total = 0;
        for (TransactionOutput o : outputs) {
            total += o.getValue();
        }
        return total;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PublicKey getRecipient() {
        return recipient;
    }

    public float getValue() {
        return value;
    }

    public ArrayList<TransactionOutput> getOutputs() {
        return outputs;
    }

    public ArrayList<TransactionInput> getInputs() {
        return inputs;
    }

    public PublicKey getSender() {
        return sender;
    }
}
