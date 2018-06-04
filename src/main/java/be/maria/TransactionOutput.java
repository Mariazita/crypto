package be.maria;

import be.maria.tools.StringUtil;

import java.security.PublicKey;

public class TransactionOutput {
    private String id;
    private PublicKey recipient;
    private float value;

    private String parentTransactionId; //the id of the transaction this output was created in

    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(recipient) + Float.toString(value) + parentTransactionId);
    }

    public boolean isMine(PublicKey publicKey) {
        return (publicKey == recipient);
    }

    public String getId() {
        return id;
    }

    public float getValue() {
        return value;
    }

    public PublicKey getRecipient() {
        return recipient;
    }
}
