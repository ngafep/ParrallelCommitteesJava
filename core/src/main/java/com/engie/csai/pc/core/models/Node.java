package com.engie.csai.pc.core.models;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Node {
    private int capacity;
    private int currentQuota;
    private List<ReplicatedRequest> replicatedRequests;

}
