package com.engie.csai.pc.core.models;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProcessorQueue {
    private List<Node> nodes;
    private int capacity;
}
