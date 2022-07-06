package com.engie.csai.pc.core.service;

import com.engie.csai.pc.core.consensus.ConsensusSimulator;
import com.engie.csai.pc.core.models.Committee;

public interface CommitteeService {

    void register(String category, Committee committee, ConsensusSimulator consensus);
    void callConsensus(String category, Integer clientCount, Integer peerCount, Integer requestCount, String clientRequest);
}
