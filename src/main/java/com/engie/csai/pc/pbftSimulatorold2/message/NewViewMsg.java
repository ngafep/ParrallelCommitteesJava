package com.engie.csai.pc.pbftSimulatorold2.message;

import java.util.Set;

public class NewViewMsg extends Message
{

    public int viewNumber; // v -> viewNumber

    public Set<Message> viewChangeMsg; // V -> viewChangeMsg

    public Set<Message> O;

    public Set<Message> N;

    public int nodeId; // i -> nodeId

    // Message structure
    //<NEWVIEW, v, V, O, N, i>:
    // v: view number;
    // V: viewChange message collection;
    // O: prePrepare message collection; ?? (Operation message ??)
    // N: prePrepare message collection; ?? (probably it's 'New-view' message collection.)
    // i: node id
    public NewViewMsg(int viewNumber, Set<Message> viewChangeMsg, Set<Message> O, Set<Message> N,
                      int nodeId, int sndId, int rcvId, long rcvtime)
    {
        super(sndId, rcvId, rcvtime);
        this.msgType = NEWVIEW;
        this.len = NEVMSGBASELEN + accumulateLen(viewChangeMsg) + accumulateLen(O) + accumulateLen(N);
        this.viewNumber = viewNumber;
        this.viewChangeMsg = viewChangeMsg;
        this.O = O;
        this.N = N;
        this.nodeId = nodeId;
    }

    public Message copy(int rcvId, long rcvtime)
    {
        // It is a copy, but it does not matter, and their values will not be modified
        return new NewViewMsg(viewNumber, viewChangeMsg, O, N, nodeId, sndId, rcvId, rcvtime);
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof NewViewMsg)
        {
            NewViewMsg msg = (NewViewMsg) obj;
            return (viewNumber == msg.viewNumber && nodeId == msg.nodeId);
        }
        return super.equals(obj);
    }

    public int hashCode()
    {
        String str = "" + viewNumber + nodeId;
        return str.hashCode();
    }

    public String toString()
    {
        return super.toString() + "View Number:" + viewNumber;
    }
}
