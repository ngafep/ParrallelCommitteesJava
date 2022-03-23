package com.engie.csai.pc.service;

import com.engie.csai.pc.model.ClientRequestMessage;
import com.engie.csai.pc.pbftSimulatorold.PBFTsimulator;

public class CommitteeService {


    private static PBFTsimulator INSTANCE_PBFT;
//    public synchronized PBFTsimulator getInstancePbft(Integer nbClients, Integer nbPeers, Integer nbRequests){
//        if(INSTANCE_PBFT == null){
//           // INSTANCE_PBFT = new PBFTsimulator(nbClients, nbPeers, nbRequests);
//        }
//        //return new PBFTsimulator(nbClients, nbPeers, nbRequests);
//        return new PBFTsimulator();
//    }

    public void callConsensus(String category, Integer nbClients, Integer nbPeers, Integer nbRequests, String clientRequest){
        ClientRequestMessage requestMessage = ClientRequestDeserializer.deserialize(clientRequest);
       // PBFTsimulator pbfTsimulator = getInstancePbft(nbClients, nbPeers, nbRequests);
        //PBFTsimulator.launch(clientRequest);
        PBFTsimulator pbfTsimulator = new PBFTsimulator(nbPeers, 0, nbClients, nbRequests);
        pbfTsimulator.launch();
    }
}
