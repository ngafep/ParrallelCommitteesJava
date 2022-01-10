package com.engie.csai.pc.model;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MyThread implements Runnable
{

    private final ExecutorService pool = Executors.newFixedThreadPool(10);
    ;
    // pool = Executors.newFixedThreadPool(poolSize);

    private static int counter = 0;
    String threadId;
    int target;
    String powAnswer;
    ArrayList<String> ThreadsList = new ArrayList<String>();

    public MyThread(String threadId, int target)
    {
        this.threadId = threadId;
        this.target = target;
    }

    @Override
    public void run()
    {
        try
        {
            powAnswer = PoW.powAnswer("cat1", threadId, target);
            System.out.println("Thread id is: " + threadId + " , counter value is: " + counter++ + " , PoW answer is: "
                    + powAnswer);
            ThreadsList.add(threadId);
            System.out.println("The winner is: " + ThreadsList.get(0));

        } catch (NoSuchAlgorithmException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    //	public void run() { // run the service
    //		// Running PoW here?
    //		for (;;) {
    //			try {
    //				pool.execute(new PoW("cat1", threadId, target));
    //			} catch (NoSuchAlgorithmException e) {
    //				// TODO Auto-generated catch block
    //				e.printStackTrace();
    //			}
    //		}
    //	}

    //	class PoW implements Runnable {
    //		private String catId = "cat1";
    //		private String threadId = null;
    //		private int target = 0;
    //
    //		PoW(String catId, String threadId, int target) throws NoSuchAlgorithmException {
    //			this.catId = catId;
    //			this.threadId = threadId;
    //			this.target = target;
    //
    //			String powAnswer = PowPeerCreation.powPeerCreation2(catId, threadId, target);
    //		}
    //
    //
    //
    //		public void run() {
    //			// read and service request on socket
    //		}
    //	}

}
