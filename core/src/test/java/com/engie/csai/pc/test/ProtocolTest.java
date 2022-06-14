package com.engie.csai.pc.test;

import com.engie.csai.pc.core.model.Committee;
import com.engie.csai.pc.core.model.Peer;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProtocolTest
{

    private static final Logger LOG = Logger.getLogger(ProtocolTest.class.getName());

    @Test
    void shouldReturnPeerFromCommitteeWhenSelectPeerFromCommittee()
    {
        Committee committee = new Committee();
        Peer peer0 = new Peer().setAddress("p0");
        Peer peer1 = new Peer().setAddress("p1");
        Peer peer2 = new Peer().setAddress("p2");
        Peer peer3 = new Peer().setAddress("p3");
        Peer peer4 = new Peer().setAddress("p4");

        committee._addPeerOfCommittee(peer0);
        committee._addPeerOfCommittee(peer1);
        committee._addPeerOfCommittee(peer2);
        committee._addPeerOfCommittee(peer3);
        committee._addPeerOfCommittee(peer4);

        var peers = committee.getPeerOfCommittee();
        assertEquals(5, peers.size());

        var leaderPeer = committee.selectLeaderPeer();

        assertNotNull(leaderPeer);
        assertTrue(peers.contains(leaderPeer));
        LOG.info(leaderPeer.getAddress());
    }

    @Test
    void shouldReturnSamePeerFromCommitteeWhenSelectPeerFromCommitteeWithOnePeer()
    {
        Committee committee = new Committee();
        Peer peer0 = new Peer().setAddress("p0");
        committee._addPeerOfCommittee(peer0);
        var peers = committee.getPeerOfCommittee();
        assertEquals(1, peers.size());
        var leaderPeer = committee.selectLeaderPeer();
        assertEquals(peer0, leaderPeer);
        LOG.info(leaderPeer.getAddress());
    }

    @Test
    void shouldUpdateQuotatMinusOneWhenPeerIsLoweredAndQuotatIs1OrMore()
    {
        Peer peer = Peer.builder().quotaInitial(5).quotaCurrent(5).build();
        peer.updateActualQuota(1);
        assertEquals(4, peer.getQuotaCurrent());

    }

    @Test
    void shouldNotUpdateQuotatMinusOneWhenPeerIsLoweredAndQuotatIs0()
    {
        Peer peer = Peer.builder().build();
        peer.updateActualQuota(1);
        assertEquals(0, peer.getQuotaCurrent());
    }

    @Test
    void shouldNotSwitchPeerIfQuotatIsDifferentFrom0()
    {
        Committee committee = new Committee();
        Peer peer = Peer.builder().quotaInitial(5).quotaCurrent(5).address("p0").build();
        committee._addPeerOfCommittee(peer);
        Peer newPeer = committee.switchPeer(peer);
        assertEquals(peer, newPeer);
    }

    @Test
    void shouldNotSwitchPeerIfQuotatIsDifferentFrom0AndThereAre2PeersInCommittee()
    {
        Committee committee = new Committee();
        Peer peer = Peer.builder().quotaInitial(5).quotaCurrent(5).address("p0").build();
        Peer peer1 = Peer.builder().quotaInitial(5).quotaCurrent(5).address("p1").build();
        committee._addPeerOfCommittee(peer);
        committee._addPeerOfCommittee(peer1);
        Peer newPeer = committee.switchPeer(peer);
        assertEquals(peer, newPeer);
    }

    @Test
    void shouldNotSwitchPeerIfQuotatIsDifferentFrom0AndThereAre2PeersInCommitteeAndQueueIsNotEmpty()
    {
        Committee committee = new Committee();
        Peer peer = Peer.builder().quotaInitial(5).quotaCurrent(5).address("p0").build();
        Peer peer1 = Peer.builder().quotaInitial(5).quotaCurrent(5).address("p1").build();
        Peer peer2 = Peer.builder().quotaInitial(5).quotaCurrent(5).address("p2").build();
        committee._addPeerOfCommittee(peer);
        committee._addPeerOfCommittee(peer1);
        committee.insertPeerToQueue(peer2);
        Peer newPeer = committee.switchPeer(peer);
        assertEquals(peer, newPeer);
    }

    @Test
    void shouldLeaveCommitteeWhenQuotatIsZero()
    {
        Committee committee = new Committee();
        Peer peer = Peer.builder().quotaInitial(5).quotaCurrent(0).address("p0").build();
        committee._addPeerOfCommittee(peer);
        Peer newPeer = committee.switchPeer(peer);
        assertNull(newPeer);
        assertTrue(committee.getPeerOfCommittee().isEmpty());
    }

    @Test
    void shouldJoinQueueWhenQuotatIsZero()
    {
        Committee committee = new Committee();
        Peer peer = Peer.builder().quotaInitial(5).quotaCurrent(0).address("p0").build();
        committee._addPeerOfCommittee(peer);
        committee.switchPeer(peer);
        assertEquals(1,committee.getQueueSize());
        assertEquals(peer,committee.getPeerQ().get(0));
    }

    @Test
    void shouldSwitchWhenQueueHas1PeerAndCommitteeHas1PeerQuotat0()
    {
        Committee committee = new Committee();
        Peer peer = Peer.builder().quotaInitial(5).quotaCurrent(0).address("p0").build();
        committee._addPeerOfCommittee(peer);

        Peer waitingPeer = Peer.builder().quotaInitial(5).quotaCurrent(5).address("p1").build();
        committee.insertPeerToQueue(waitingPeer);

        var newPeer = committee.switchPeer(peer);
        assertEquals(1,committee.getQueueSize());
        assertEquals(peer,committee.getPeerQ().get(0));
        assertEquals(waitingPeer,newPeer);
        assertEquals(waitingPeer,committee.getPeerOfCommittee().get(0));
    }

    @Test
    void shouldSwitchWhenQueueHas2PeersAndCommitteeHas1PeerQuotat0()
    {
        Committee committee = new Committee();
        Peer peer = Peer.builder().quotaInitial(5).quotaCurrent(0).address("p0").build();
        committee._addPeerOfCommittee(peer);

        Peer waitingPeer1 = Peer.builder().quotaInitial(5).quotaCurrent(5).address("p1").build();
        committee.insertPeerToQueue(waitingPeer1);
        Peer waitingPeer2 = Peer.builder().quotaInitial(5).quotaCurrent(5).address("p2").build();
        committee.insertPeerToQueue(waitingPeer2);

        var newPeer = committee.switchPeer(peer);
        LOG.info(newPeer.getAddress());
        assertEquals(2,committee.getQueueSize());
        assertEquals(1,committee.getPeerOfCommittee().size());
        assertTrue(committee.getPeerOfCommittee().contains(waitingPeer1) || committee.getPeerOfCommittee().contains(waitingPeer2));
        assertTrue(committee.getPeerOfCommittee().contains(waitingPeer1) || committee.getPeerOfCommittee().contains(waitingPeer2));
        assertTrue(committee.getPeerQ().contains(peer));
    }

    @Test
    void shouldSwitchRandomyWhenQueueHas2PeersAndCommitteeHas1PeerQuotat0()
    {
        Set<String> peers = new HashSet<>();
        for (int i = 0; i<100; i++){
            peers.add(switchPeer());
        }
        assertEquals(2,peers.size());
    }

    @Test
    void shouldResetQutotatWhenJoinQueue()
    {
        Committee committee = new Committee();
        Peer peer = Peer.builder().quotaInitial(5).quotaCurrent(0).address("p0").build();
        committee._addPeerOfCommittee(peer);
        committee.switchPeer(peer);
        assertEquals(5,peer.getQuotaCurrent());
    }



    private String switchPeer(){
        Committee committee = new Committee();
        Peer peer = Peer.builder().quotaInitial(5).quotaCurrent(0).address("p0").build();
        committee._addPeerOfCommittee(peer);

        Peer waitingPeer1 = Peer.builder().quotaInitial(5).quotaCurrent(5).address("p1").build();
        committee.insertPeerToQueue(waitingPeer1);
        Peer waitingPeer2 = Peer.builder().quotaInitial(5).quotaCurrent(5).address("p2").build();
        committee.insertPeerToQueue(waitingPeer2);

        return committee.switchPeer(peer).getAddress();
    }



}
