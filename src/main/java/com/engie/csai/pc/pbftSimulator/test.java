package com.engie.csai.pc.pbftSimulator;

import com.engie.csai.pc.pbftSimulator.message.PrePrepareMsg;
import com.engie.csai.pc.pbftSimulator.replica.Replica;

import java.util.PriorityQueue;
import java.util.Queue;

public class test
{

    public static void main(String[] args)
    {

        Queue<PrePrepareMsg> executeQ = new PriorityQueue<>(Replica.nCmp);
        executeQ.add(new PrePrepareMsg(3, 2, null, 0, 0, 0, 0));
        executeQ.add(new PrePrepareMsg(3, 4, null, 0, 0, 0, 0));
        executeQ.add(new PrePrepareMsg(3, 3, null, 0, 0, 0, 0));
        while (!executeQ.isEmpty())
        {
            System.out.println(executeQ.poll().seqNumber);
        }
    }
}
