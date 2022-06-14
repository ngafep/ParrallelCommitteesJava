package com.engie.csai.pc.core.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ReplicatedRequest {
    private boolean state;
    private String data;
}
