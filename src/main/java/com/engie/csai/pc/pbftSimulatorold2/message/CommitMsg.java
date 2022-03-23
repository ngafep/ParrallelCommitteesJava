package com.engie.csai.pc.pbftSimulatorold2.message;

public class CommitMsg extends Message
{

    public int viewNumber; // v -> viewNumber

    public int seqNumber; // n -> seqNumber

    public String DigestMsg; // d -> DigestMsg

    public int nodeId; // i -> nodeId

    // Message structure
    //<COMMIT, v, n, d, i>:
    // v: view number;
    // n: sequence number;
    // d: Digest of the request message;
    // i: node id
    public CommitMsg(int viewNumber, int seqNumber, String DigestMsg, int nodeId, int sndId, int rcvId, long rcvtime)
    {
        super(sndId, rcvId, rcvtime);
        this.msgType = COMMIT;
        this.len = COMMSGLEN;
        this.viewNumber = viewNumber;
        this.seqNumber = seqNumber;
        this.DigestMsg = DigestMsg;
        this.nodeId = nodeId;
    }

    public Message copy(int rcvId, long rcvtime)
    {
        return new CommitMsg(viewNumber, seqNumber, new String(DigestMsg), nodeId, sndId, rcvId, rcvtime);
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof CommitMsg)
        {
            CommitMsg msg = (CommitMsg) obj;
            return (viewNumber == msg.viewNumber && seqNumber == msg.seqNumber && DigestMsg.equals(msg.DigestMsg) && nodeId == msg.nodeId);
        }
        return super.equals(obj);
    }

    public int hashCode()
    {
        String str = "" + viewNumber + seqNumber + DigestMsg + nodeId;
        return str.hashCode();
    }

    public String toString()
    {
        return super.toString() + "View number:" + viewNumber + ";Serial number:" + seqNumber;
    }
}
