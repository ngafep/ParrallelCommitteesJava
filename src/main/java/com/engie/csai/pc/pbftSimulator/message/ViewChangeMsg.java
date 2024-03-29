package com.engie.csai.pc.pbftSimulator.message;

import java.util.Map;
import java.util.Set;

public class ViewChangeMsg extends Message
{

    /**
     * About view-change:
     * <p>
     * <p>
     * Whenever a lot of non-faulty replicas detect that the
     * primary is faulty, they together begin the view-change
     * operation.
     * <p>
     * <p>
     * If a node receive a request from a client but not from
     * the primary, they send the request to the primary.
     * If they still do not receive reply from primary within a
     * period of time, they begin view-change.
     * <p>
     * Every node that wants to begin a view change sends a
     * view-change message to everyone.
     */

    public int viewNumber; // v -> viewNumber

    public int seqNumberOfStableState; // sn -> seqNumberOfStableState

    public Map<Integer, LastReply> replySetOfStableState; // ss -> replySetOfStableState

    public Set<Message> checkPointMsg; // C -> checkPointMsg

    public Map<Integer, Set<Message>> P;

    public int nodeId; // i -> nodeId

    // Message structure
    //<VIEWCHANGE, v, sn, ss, C, P, i>:
    // v: view number;
    // sn: sequence number of the stable state;
    // ss: last-Reply set of the stable state;
    // C: checkpoint message collection;
    // P: prePrepare message collection of n>sn;
    // i: node id
    public ViewChangeMsg(int viewNumber, int seqNumberOfStableState, Map<Integer,
            LastReply> replySetOfStableState,
                         Set<Message> checkPointMsg, Map<Integer,
            Set<Message>> P, int nodeId, int sndId, int rcvId, long rcvtime)
    {
        super(sndId, rcvId, rcvtime);
        long sLen = replySetOfStableState == null ? 0 : replySetOfStableState.size() * LASTREPLEN;
        this.msgType = VIEWCHANGE;
        this.len = VCHMSGBASELEN + sLen + accumulateLen(checkPointMsg) + accumulateLen(P);
        this.viewNumber = viewNumber;
        this.seqNumberOfStableState = seqNumberOfStableState;
        this.replySetOfStableState = replySetOfStableState;
        this.checkPointMsg = checkPointMsg;
        this.P = P;
        this.nodeId = nodeId;
    }

    public Message copy(int rcvId, long rcvtime)
    {
        // ss, C, P are copies, but it doesn’t matter, their values will not be modified
        return new ViewChangeMsg(viewNumber, seqNumberOfStableState, replySetOfStableState, checkPointMsg, P, nodeId, sndId, rcvId, rcvtime);
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof ViewChangeMsg)
        {
            ViewChangeMsg msg = (ViewChangeMsg) obj;
            return (viewNumber == msg.viewNumber && seqNumberOfStableState == msg.seqNumberOfStableState
                    && nodeId == msg.nodeId);
        }
        return super.equals(obj);
    }

    public int hashCode()
    {
        String str = "" + viewNumber + seqNumberOfStableState + nodeId;
        return str.hashCode();
    }

    public String toString()
    {
        return super.toString() + "View Number:" + viewNumber +
                ";sequence number of stable state:" + seqNumberOfStableState;
    }
}
