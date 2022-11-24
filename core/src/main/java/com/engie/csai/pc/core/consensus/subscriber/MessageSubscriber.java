package com.engie.csai.pc.core.consensus.subscriber;

import com.engie.csai.pc.core.listener.EndMetrics;

public interface MessageSubscriber
{
    void onMsgReceived(String msg);
    void onMsgReceived(EndMetrics metrics);

}
