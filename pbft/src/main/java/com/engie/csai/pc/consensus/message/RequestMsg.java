package com.engie.csai.pc.consensus.message;

public class RequestMsg extends Message {

    public String operation; // o -> operation

    public long clientReqTimeStamp; // t -> clientReqTimeStamp

    public int clientId; // c -> clientId

    //消息结构
    //<REQUEST, o, t, c>:o表示客户端请求的操作;t表示客户端请求时间戳;c表示客户端id
    // o: operation requested by the client;
    // t: client request timestamp;
    // c: client id
    public RequestMsg(String operation/*Operation: is usually the request message. It can be the entire PDT.*/, long clientReqTimeStamp/*time*/, int clientId, int sndId/* c and sndId are usually the same.*/, int rcvId/*rcvId is Primary node*/, long rcvtime) {
        super(sndId, rcvId, rcvtime);
        this.msgType = REQUEST;
        this.len = REQMSGLEN;
        this.operation = operation;
        this.clientReqTimeStamp = clientReqTimeStamp;
        this.clientId = clientId;
    }

    public boolean equals(Object obj) {
        if (obj instanceof RequestMsg) {
            RequestMsg msg = (RequestMsg) obj;
            return (operation == msg.operation && clientReqTimeStamp == msg.clientReqTimeStamp && clientId == msg.clientId);
        }
        return super.equals(obj);
    }

    public int hashCode() {
        String str = operation + clientReqTimeStamp + clientId;
        return str.hashCode();
    }

    public String toString() {
        return super.toString() + "时间戳:" + clientReqTimeStamp + ";客户端编号:" + clientId;
    }

}
