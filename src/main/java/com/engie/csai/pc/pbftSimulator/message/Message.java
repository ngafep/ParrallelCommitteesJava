package com.engie.csai.pc.pbftSimulator.message;

import com.engie.csai.pc.pbftSimulator.PBFTsimulator;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public class Message
{

    public static final int REQUEST = 0;

    public static final int PREPREPARE = 1;

    public static final int PREPARE = 2;

    public static final int COMMIT = 3;

    public static final int REPLY = 4;

    public static final int CHECKPOINT = 5;

    public static final int VIEWCHANGE = 6;

    public static final int NEWVIEW = 7;

    public static final int TIMEOUT = 8;
    // Dummy message to remind the node of the timeout ??

    public static final int CLITIMEOUT = 9;
    // Dummy message used to remind the client of the timeout

    public static final long PRQMSGLEN = 0;
    // The PreRequest message is a dummy message ?? (never used)

    public static final long REQMSGLEN = 100;
    // The size of the Request message (bytes), can be set according to the actual situation

    public static final long PPRMSGLEN = 4 + REQMSGLEN;
    // RrePrepare message size

    public static final long PREMSGLEN = 36;
    // Prepare message size

    public static final long COMMSGLEN = 36;
    // Commit message size

    public static final long REPMSGLEN = 16;
    // Reply message size

    public static final long CKPMSGBASELEN = 4;
    // The basic size of the CheckPoint message (also needs to dynamically add the s collection size)

    public static final long VCHMSGBASELEN = 4;
    // The base size of the View-Change message
    /**
     * About View-Change:
     * <p>
     * All is good if primary is good, but everything changed when primary is faulty.
     * Whenever a lot of non-faulty replicas detect that the primary is faulty, they together begin the view-change operation.
     * - More specifically, if they are stuck, they will suspect that the primary is faulty.
     * - The primary is detected to be faulty by using timeout, thus this part depends on the synchrony assumption.
     * - They will then change the view.
     * - The primary will change from replica 'p' to replica 'p+1' mod |R| (R = number of replicas (nodes).)
     * Every replica that wants to begin a view change sends a View-Change message to every node.
     * When the new primary receives 2f+1 View-Change messages, it will begin the view change.
     * New primary sends New-View to all replicas.
     */

    public static final long NEVMSGBASELEN = 3;
    // The base size of the New-View message

    public static final long LASTREPLEN = REPMSGLEN - 3;
    // The size of the Last-Reply

    public static final long TIMMSGLEN = 0;
    // TimeOut messages are dummy messages ??

    public static final long CLTMSGLEN = 0;
    // CliTimeOut messages are dummy messages ??

    public static Comparator<Message> cmp = new Comparator<Message>()
    {
        @Override
        public int compare(Message c1, Message c2)
        {
//            if (c1 == null){
//                return -1;
//            }
            return (int) (c1.rcvtime - c2.rcvtime);
        }
    };

    public int msgType; // type -> msgType				 // Message type

    public int sndId;                 // Message sender id

    public int rcvId;                 // Message receiver id

    public long rcvtime;            // Message receiving time

    public long len;                // Message size

    public Message(int sndId, int rcvId, long rcvtime)
    {
        this.sndId = sndId;
        this.rcvId = rcvId;
        this.rcvtime = rcvtime;
    }

    public void print(String tag)
    {
        if (!PBFTsimulator.SHOWDETAILINFO) return;
        String prefix = "【" + tag + "】";
        System.out.println(prefix + toString());
    }

    public static long accumulateLen(Set<Message> set)
    {
        long len = 0L;
        if (set != null)
        {
            for (Message m : set)
            {
                len += m.len;
            }
        }
        return len;
    }

    public static long accumulateLen(Map<Integer, Set<Message>> map)
    {
        long len = 0L;
        if (map != null)
        {
            for (Integer n : map.keySet())
            {
                len += accumulateLen(map.get(n));
            }
        }
        return len;
    }

    public Message copy(int rcvId, long rcvtime)
    {
        return new Message(sndId, rcvId, rcvtime);
    }

    public boolean equals(Object obj)
    {
        if (obj instanceof Message)
        {
            Message msg = (Message) obj;
            return (msgType == msg.msgType && sndId == msg.sndId && rcvId == msg.rcvId && rcvtime == msg.rcvtime);
        }
        return super.equals(obj);
    }

    public int hashCode()
    {
        String str = "" + msgType + sndId + rcvId + rcvtime;
        return str.hashCode();
    }

    public String toString()
    {
        String[] typeName = {"Request", "PrePrepare", "Prepare", "Commit", "Reply"
                , "CheckPoint", "ViewChange", "NewView", "TimeOut", "CliTimeOut"};

        return "Message type: " + typeName[msgType] + ";Sender id: "
                + sndId + ";Receiver id: " + rcvId + ";Message receiving timestamp: " + rcvtime + ";";
    }

}
