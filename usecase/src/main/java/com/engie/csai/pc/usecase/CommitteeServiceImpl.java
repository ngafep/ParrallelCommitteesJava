package com.engie.csai.pc.usecase;


import com.engie.csai.pc.consensus.PBFTsimulator;
import com.engie.csai.pc.core.consensus.ConsensusSimulator;
import com.engie.csai.pc.core.consensus.subscriber.MessageSubscriber;
import com.engie.csai.pc.core.model.Committee;
import com.engie.csai.pc.core.service.CommitteeService;
import java.util.HashMap;
import java.util.Map;

public class CommitteeServiceImpl implements MessageSubscriber, CommitteeService
{

    private CommitteeServiceImpl(){

    }

    private static CommitteeServiceImpl instance = null;

    public synchronized static CommitteeServiceImpl getInstance(){
        if(instance == null){
            instance = new CommitteeServiceImpl();
        }
        return instance;
    }

    Map<String, ConsensusSimulator> pbftSimulatorPerCategory = new HashMap<>();
    Map<String, Committee> committeePerCategory = new HashMap<>();

    public void register(String category, Committee committee, ConsensusSimulator consensus){
        pbftSimulatorPerCategory.putIfAbsent(category, consensus);
        committeePerCategory.putIfAbsent(category, committee);
    }


    public void callConsensus(String category, Integer clientCount, Integer peerCount, Integer requestCount, String clientRequest){

        ConsensusSimulator simulator = pbftSimulatorPerCategory.get(category);
        var committee = committeePerCategory.get(category);
        committee.subscribe(simulator);
        simulator.subscribe(this);
        long start = System.currentTimeMillis();
        try {
            simulator.launch(clientCount, peerCount, requestCount);
        }catch(Exception e){
            e.printStackTrace();
        }
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
