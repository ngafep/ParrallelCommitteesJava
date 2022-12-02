package com.engie.csai.pc.usecase;


import com.engie.csai.pc.core.consensus.ConsensusSimulator;
import com.engie.csai.pc.core.consensus.subscriber.MessageSubscriber;
import com.engie.csai.pc.core.listener.EndMetrics;
import com.engie.csai.pc.core.models.Committee;
import com.engie.csai.pc.core.service.CommitteeService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CommitteeServiceImpl implements MessageSubscriber, CommitteeService
{

    private static final Logger LOGGER =
            Logger.getLogger(CommitteeServiceImpl.class.getName());

    public static final double COMPLIANCE_CONSTANT = 40495.7;

    private CommitteeServiceImpl(){

    }

    private static CommitteeServiceImpl instance = null;

    private List<EndMetrics> endMetrics = new ArrayList<>();

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
        simulator.subscribeEndPbft(this);
        try {
            simulator.launch(clientCount, peerCount, requestCount, category);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onMsgReceived(String msg)
    {
    }

    @Override
    public void onMsgReceived(EndMetrics metrics) {
        endMetrics.add(metrics);
        if(endMetrics.size() == committeePerCategory.size()){
            aggregateMetrics();
        }
    }

    private void aggregateMetrics() {
        double maxTime = endMetrics.stream().mapToDouble(EndMetrics::getTimestamp).max().orElseThrow();
        long requestCount = endMetrics.stream().mapToLong(EndMetrics::getTotalStableMessage).sum();
        final var msg = "transaction per second is " + (COMPLIANCE_CONSTANT * 1000.0 * requestCount / maxTime) +
                " transactions";
        LOGGER.info(()->msg);

    }
}
