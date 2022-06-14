package com.engie.csai.pc.core.models;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RequestQueue {
    private List<PendingRequest> pendingRequests;
}
