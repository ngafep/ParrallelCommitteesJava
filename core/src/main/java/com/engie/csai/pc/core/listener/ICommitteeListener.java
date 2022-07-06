package com.engie.csai.pc.core.listener;

import com.engie.csai.pc.core.models.Committee;

public interface ICommitteeListener {

    void subscribe(
        Committee committee);
    void notifyFinished(
        int numberOfMessages,
        long totalTime);
}
