package com.engie.csai.pc.service;

import com.engie.csai.pc.model.ClientRequestMessage;
import com.engie.csai.pc.pbftSimulator.Client;
import com.engie.csai.pc.pbftSimulator.PBFTsimulator;

import java.util.HashMap;
import java.util.Map;

public class CommitteeService {


    Map<String, PBFTsimulator> pbftSimulatorPerCategory = new HashMap<>();

    private PBFTsimulator getSimulator(String category){
        pbftSimulatorPerCategory.putIfAbsent(category, new PBFTsimulator());
        return pbftSimulatorPerCategory.get(category);
    }

    public void callConsensus(String category, Integer clientCount, Integer peerCount, Integer requestCount, String clientRequest){
        ClientRequestMessage requestMessage = ClientRequestDeserializer.deserialize(clientRequest);
        PBFTsimulator simulator = getSimulator(category);
        simulator.launch(clientCount, peerCount, requestCount);
    }
}
