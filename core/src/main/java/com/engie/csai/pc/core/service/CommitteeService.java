package com.engie.csai.pc.core.service;

import com.engie.csai.pc.core.model.Committee;

public interface CommitteeService {

    void register(String category, Committee committee);
    void callConsensus(String category, Integer clientCount, Integer peerCount, Integer requestCount, String clientRequest);
}
