package com.engie.csai.pc.model;

//import com.engie.csai.pc.Ansi;

public class ClientRequestMessage
{

    public String senderSignature;
    public String receiver;
    public float fee;
    public String data;
    public float token;
    public boolean validityStatus;
    public String requestTimeStamp;

    public ClientRequestMessage(String senderSignature, String receiver, float fee, String data, float tokenToSend,
                                boolean validyStatus, String requestTimeStamp)
    {
        super();
        this.senderSignature = senderSignature;
        this.receiver = receiver;
        this.fee = fee;
        this.data = data;
        this.token = tokenToSend;
        this.validityStatus = validyStatus;
        this.requestTimeStamp = requestTimeStamp;
    }

    @Override
    public String toString()
    {
        return "\n" + " >>> Sender Signature: " + senderSignature + "\n" + " >>> Receiver: " + receiver + "\n"
                + " >>> Fee: " + fee + "\n" + " >>> Data: " + data + "\n" + " >>> Token: " + token + "\n"
                + " >>> Validity Status: " + validityStatus;
    }

}
