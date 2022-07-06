package com.engie.csai.pc.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.engie.csai.pc.core.models.Committee;
import com.engie.csai.pc.core.models.ProcessorNode;
import com.engie.csai.pc.core.models.ProcessorQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

class ProtocolTest {

    private static final Logger LOG = Logger.getLogger(ProtocolTest.class.getName());

    @Test
    void shouldReturnPeerFromCommitteeWhenSelectPeerFromCommittee() {
        Committee committee = createCommittee(5,5);
        assertEquals(5, committee.getProcessorNodes().size());
        var leaderPeer = committee.selectLeaderPeer();
        assertNotNull(leaderPeer);
        assertTrue(committee.getProcessorNodes().contains(leaderPeer));
        LOG.info(leaderPeer.getId().toString());
    }

    private Committee createCommittee(int numberOfNodes, int capacity, int initialQuota){
        List<ProcessorNode> nodes = new ArrayList<>();
        for(int i = 0; i<numberOfNodes; i++){
            nodes.add(createProcessorNode(5));
        }
        ProcessorQueue processorQueue = ProcessorQueue.builder()
            .capacity(capacity)
            .processorNodes(new ArrayList<>())
            .build();
        return Committee.builder()
            .processorQueue(processorQueue)
            .clients(new ArrayList<>())
            .category("committee")
            .initialQuota(initialQuota)
            .consensus(Committee.Consensus.PBFT)
            .capacity(capacity)
            .processorNodes(nodes)
            .build();
    }
    private Committee createCommittee(int numberOfNodes, int capacity) {
        return createCommittee(numberOfNodes, capacity, 5);
    }

    private ProcessorNode createProcessorNode(int quota) {
        return ProcessorNode.builder()
            .id(UUID.randomUUID())
            .currentQuota(quota)
            .replications(new ArrayList<>()).build();
    }

    @Test
    void shouldReturnSamePeerFromCommitteeWhenSelectPeerFromCommitteeWithOnePeer() {
        var committee = createCommittee(1, 5);
        var peers = committee.getProcessorNodes();
        assertEquals(1, peers.size());
        var leaderPeer = committee.selectLeaderPeer();
        assertEquals(committee.getProcessorNodes().get(0), leaderPeer);
        LOG.info(leaderPeer.getId().toString());
    }

    @Test
    void shouldUpdateQuotatMinusOneWhenPeerIsLoweredAndQuotatIs1OrMore() {
        ProcessorNode processorNode = createProcessorNode(5);
        processorNode.reduceActualQuota();
        assertEquals(4, processorNode.getCurrentQuota());
    }

    @Test
    void shouldNotUpdateQuotatMinusOneWhenPeerIsLoweredAndQuotatIs0() {
        ProcessorNode processorNode = createProcessorNode(0);
        processorNode.reduceActualQuota();
        assertEquals(0, processorNode.getCurrentQuota());
    }

    @Test
    void shouldNotSwitchPeerIfQuotatIsDifferentFrom0() {
        var committee = createCommittee(5,5);
        ProcessorNode newProcessorNode = committee.switchPeer(committee.getProcessorNodes().get(0));
        assertEquals(committee.getProcessorNodes().get(0), newProcessorNode);
    }

    @Test
    void shouldNotSwitchPeerIfQuotatIsDifferentFrom0AndThereAre2PeersInCommitteeAndQueueIsNotEmpty() {
        var committee = createCommittee(2,2);
        final var nodeInQueue = createProcessorNode(5);
        committee.getProcessorQueue().getProcessorNodes().add(nodeInQueue);
        final var selectedNode = committee.getProcessorNodes()
            .get(0);
        selectedNode.setCurrentQuota(0);
        ProcessorNode newProcessorNode = committee.switchPeer(selectedNode);
        assertEquals(nodeInQueue, newProcessorNode);
    }

    @Test
    void shouldLeaveCommitteeWhenQuotatIsZero() {
        var committee = createCommittee(2,2);
        committee.getProcessorQueue().getProcessorNodes().add(createProcessorNode(5));
        ProcessorNode newProcessorNode = committee.switchPeer(committee.getProcessorNodes().get(0));
        assertEquals(committee.getProcessorNodes().get(0), newProcessorNode);
    }

    @Test
    void shouldResetQutotatWhenJoinQueue() {
        var committee = createCommittee(2,2,8);
        committee.getProcessorQueue().getProcessorNodes().add(createProcessorNode(5));
        final var nodeToSwitch = committee.getProcessorNodes()
            .get(0);
        nodeToSwitch.setCurrentQuota(0);
        committee.switchPeer(nodeToSwitch);
        assertEquals(8, nodeToSwitch.getCurrentQuota());
    }

}
