package com.engie.csai.pc.core.listener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Builder
@Getter
@AllArgsConstructor
@ToString
public class EndMetrics {
    private String categoryId;
    private long totalTime;
    private long totalStableMessage;
    private double throughput;
    private double timestamp;

    @Override
    public String toString() {
        return String.format("""
                Category : %s
                Total Time : %d
                Total Stable Messages : %d
                PBFT Throughput : %f
                Timestamp : %f
                """, categoryId, totalTime, totalStableMessage, throughput, timestamp);
    }
}
