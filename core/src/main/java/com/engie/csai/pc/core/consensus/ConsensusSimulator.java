package com.engie.csai.pc.core.consensus;

import com.engie.csai.pc.core.consensus.subscriber.MessageSubscriber;

public interface ConsensusSimulator {
    void subscribe(MessageSubscriber committee);

    void subscribeEndPbft(MessageSubscriber committee);

    void launch(int clientCount, int peerCount, int requestCount, String caller);
}
