package com.engie.csai.pc.pbftSimulatorold2.message;

public class RequestMsg extends Message
{

    public String operation; // o -> operation

    public long clientReqTimeStamp; // t -> clientReqTimeStamp

    public int clientId; // c -> clientId

    // Message structure
    //<REQUEST, o, t, c>:
    // o: operation requested by the client;
    // t: client request timestamp;
    // c: client id
    public RequestMsg(String operation
            /*Operation: is usually the request message.
            It can be considered as 'data' in ClientRequestMessage*/,
                      long clientReqTimeStamp/*time*/, int clientId,
                      int sndId/* c and sndId are usually the same.*/,
                      int rcvId/*rcvId is Primary node*/, long rcvtime)
    {
        super(sndId, rcvId, rcvtime);
        this.msgType = REQUEST;
        this.len = REQMSGLEN;
        this.operation = operation;
        this.clientReqTimeStamp = clientReqTimeStamp;
        this.clientId = clientId;
        /**
         * It should be added other parameters to be matched with ClientRequestMessage class
         *
         * 	public ClientRequestMessage(
         * 	String senderSignature, -> sndId // Client who sends the request.
         * 	String receiver, -> rcvId // Primary node who receives the request.
         * 	float fee, -> Should be added. No equivalent in PBFT Message class.
         * 	String data, -> Should be added. No equivalent in PBFT Message class.
         * 	float tokenToSend, -> Should be added. No equivalent in PBFT Message class.
         * 	boolean validyStatus -> Should be added. No equivalent in PBFT Message class.
         * 	)
         */
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof RequestMsg)
        {
            RequestMsg msg = (RequestMsg) obj;
            return (operation == msg.operation && clientReqTimeStamp == msg.clientReqTimeStamp
                    && clientId == msg.clientId);
        }
        return super.equals(obj);
    }

    public int hashCode()
    {
        String str = operation + clientReqTimeStamp + clientId;
        return str.hashCode();
    }

    public String toString()
    {
        return super.toString() + "Timestamp:" + clientReqTimeStamp +
                ";Client ID:" + clientId;
    }

}
