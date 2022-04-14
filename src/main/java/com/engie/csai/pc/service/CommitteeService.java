package com.engie.csai.pc.service;

import com.engie.csai.pc.model.Committee;
import com.engie.csai.pc.pbftSimulator.PBFTsimulator;
import com.engie.csai.pc.pbftSimulator.subscriber.MessageSubscriber;

import java.util.HashMap;
import java.util.Map;

public class CommitteeService implements MessageSubscriber
{

    private CommitteeService(){

    }

    private static CommitteeService instance = null;

    public synchronized static CommitteeService getInstance(){
        if(instance == null){
            instance = new CommitteeService();
        }
        return instance;
    }

    Map<String, PBFTsimulator> pbftSimulatorPerCategory = new HashMap<>();
    Map<String, Committee> committeePerCategory = new HashMap<>();

    public void register(String category, Committee committee){
        pbftSimulatorPerCategory.putIfAbsent(category, new PBFTsimulator());
        committeePerCategory.putIfAbsent(category, committee);
    }


    public void callConsensus(String category, Integer clientCount, Integer peerCount, Integer requestCount, String clientRequest){

        PBFTsimulator simulator = pbftSimulatorPerCategory.get(category);
        var committee = committeePerCategory.get(category);
        committee.subscribe(simulator);
        simulator.subscribe(this);
        long start = System.currentTimeMillis();
        simulator.launch(clientCount, peerCount, requestCount);
        long end = System.currentTimeMillis();
        System.out.println("Duration for " + category + " = " + (end-start) + " milliseconds");
    }


    @Override
    public void onMsgReceived(String msg)
    {
        var allMessagesCount = committeePerCategory.values().stream().mapToInt(Committee::getNumberOfMessages).sum();
        var timeSpent = committeePerCategory.values().stream().findAny().get().getTimeSpent();
        System.out.println("Global Mean Time is " + ((float)timeSpent / (float)allMessagesCount) + "ms");
    }
}
