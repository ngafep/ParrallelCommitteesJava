package com.engie.csai.pc.core.models;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Committee {
    private int capacity;
    private String category;
    private int initialQuota;
    private int dataSizeMax;
    private Consensus consensus;
    private List<Node> nodes;
    private ProcessorQueue processorQueue;

    private enum Consensus{
        PBFT, PAXOS, RAFT
    }
}
