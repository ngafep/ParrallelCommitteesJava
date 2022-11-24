package com.engie.csai.pc.consensus.launcher.simulator;

import com.engie.csai.pc.core.consensus.ConsensusSimulator;
import com.engie.csai.pc.core.consensus.subscriber.MessageSubscriber;

public class PaxosSimulator implements ConsensusSimulator {
    @Override
    public void subscribe(MessageSubscriber committee) {
        System.out.println("Paxos subscribed!!");
    }

    @Override
    public void subscribeEndPbft(MessageSubscriber committee) {
        System.out.println("Paxos subscribed!!");
    }

    @Override
    public void launch(
        int clientCount,
        int peerCount,
        int requestCount,
        String caller
    ) {
        System.out.println("Paxos Launched!!");
    }
}
