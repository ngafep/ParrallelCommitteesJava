package com.engie.csai.pc.main;

import com.engie.csai.pc.core.listener.ICommitteeListener;
import com.engie.csai.pc.core.models.Committee;
import java.util.ArrayList;
import java.util.List;

public class CommitteeListener implements
    ICommitteeListener {

    private List<Committee> committees = new ArrayList<>();

    private int finished = 0;
    private int numberOfMessages = 0;
    @Override
    public void subscribe(
        Committee committee) {
        committees.add(committee);
        committee.registerListener(this);
    }

    public void notifyFinished(
        int numberOfMessages,
        long totalTime) {
        finished ++;
        this.numberOfMessages+=numberOfMessages;
        if(finished == committees.size()){
            final var average = (double)totalTime / (double)this.numberOfMessages;
            System.out.println("Global Mean Time per request = " + average + " ms");
        }

    }
}
