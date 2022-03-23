package com.engie.csai.pc.model;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PoW
{

    private static final String HexDecimalLetters = "0123456789abcdef";
    static int i = 1;
    //	private static int counter = 0;
    //	private static BigInteger hexToDecimal;
    //	//private static BigInteger target;
    //	private static StringBuilder CKNhashInStringBuilder = null;
    //	//private static String cat = null; // Category of address/account/node/peer.
    //	//private static String pubK = null; // Public key of the address/account/node/peer.
    //	private static String CKN = null; // Concatenating cat (C), nonce (N), public key (K).

    public PoW()
    {

    }

    //	public static String powPeerCreation(String catId, String publicK, BigInteger target)
    //			throws NoSuchAlgorithmException {
    //
    //		int counter = 0;
    //		BigInteger hexToDecimal = new BigInteger("0");
    //		// private static BigInteger target;
    //		StringBuilder CKNhashInStringBuilder = null;
    //		// private static String cat = null; // Category of address/account/node/peer.
    //		// private static String pubK = null; // Public key of the
    //		// address/account/node/peer.
    //		String CKN = null; // Concatenating cat (C), nonce (N), public key (K).
    //
    //		String nonce = null; // PoW answer.
    //		byte[] CKNhashInByteArray = null;
    //		String CK = catId + publicK; // "category plus public key";
    //
    //		for (;;) {
    //			// nonce will increase one after another in each round to be tested whether is a
    //			// correct answer for proof-of-work.
    //			counter++;
    //			nonce = Integer.toString(counter);
    //			CKN = CK + nonce;
    //
    //			MessageDigest md = MessageDigest.getInstance("SHA-256");
    //			md.update(CKN.getBytes(Charset.defaultCharset()));
    //
    //			CKNhashInByteArray = md.digest();
    //
    //			// Converting Hash from byteArray to String.
    //			CKNhashInStringBuilder = new StringBuilder(2 * CKNhashInByteArray.length);
    //			for (byte element : CKNhashInByteArray) {
    //				CKNhashInStringBuilder.append(HexDecimalLetters.charAt((element & 0xF0) >> 4))
    //						.append(HexDecimalLetters.charAt((element & 0x0F)));
    //			}
    //			String CKNhashInString = CKNhashInStringBuilder.toString();
    //
    //			// Check whether current nonce is a correct answer of PoW.
    //			// It is a correct answer if it is smaller than Target.
    //			hexToDecimal = new BigInteger(CKNhashInString, 16);
    //			if (hexToDecimal.compareTo(target) < 0) {
    //				// System.out.println("PoW is solved after : "+(System.nanoTime()-startTime)+"
    //				// nano seconds");
    //				System.out.println("PoW was solved. Answer is: " + nonce);
    //				break; // Current nonce is answer. So break infinite loop.
    //			}
    //		}
    //
    //		// Return nonce as a correct answer of PoW
    //		return nonce;
    //
    //	}
    //
    //	public static boolean CheckPowPeerCreation(String catId, String publicK, BigInteger target, String nonce)
    //			throws NoSuchAlgorithmException {
    //
    //		BigInteger hexToDecimal = new BigInteger("0");
    //		String CK = catId + publicK;
    //		String CKN = CK + nonce;
    //
    //		MessageDigest md = MessageDigest.getInstance("SHA-256");
    //		md.update(CKN.getBytes(Charset.defaultCharset()));
    //
    //		byte[] CKNhashInByteArray = md.digest();
    //
    //		// Converting Hash from byteArray to String.
    //		StringBuilder CKNhashInStringBuilder = new StringBuilder(2 * CKNhashInByteArray.length);
    //		for (byte element : CKNhashInByteArray) {
    //			CKNhashInStringBuilder.append(HexDecimalLetters.charAt((element & 0xF0) >> 4))
    //					.append(HexDecimalLetters.charAt((element & 0x0F)));
    //		}
    //		String CKNhashInString = CKNhashInStringBuilder.toString();
    //
    //		// Check whether current nonce is a correct answer of PoW.
    //		// It is a correct answer if it is smaller than Target.
    //		hexToDecimal = new BigInteger(CKNhashInString, 16);
    //		if (hexToDecimal.compareTo(target) < 0) {
    //			return true;
    //		} else {
    //			return false;
    //		}
    //	}

    //

    public static String powAnswer(String catId, String publicK, int target) throws NoSuchAlgorithmException
    {

        int counter = 0;
        String CKN = null; // Concatenating cat (C), nonce (N), public key (K).
        String nonce = null; // PoW answer.
        String CK = catId + publicK; // "category plus public key";
        String powAnswer = null;

        // ArrayList<String> threadListStr = new ArrayList<String>();

        for (; ; )
        {
            // nonce will increase one after another in each round to be tested whether is a
            // correct answer for proof-of-work.
            counter++;
            nonce = Integer.toString(counter);
            CKN = CK + nonce;

            String hashCKNstr = calculateHash(CKN);
            // System.out.println(" t" + publicK + ": " + hashCKNstr);
            String nZero = createNZero(target);

            if (hashCKNstr.startsWith(nZero))
            {
                //				System.err.println("i: " + i + " PoW is solved after " + (System.nanoTime() - startTime)
                //						+ " nano seconds by " + publicK);
                //				System.out.flush();
                // System.err.println(" t" + publicK + " finished.");
                // System.out.flush();
                // threadListStr.add(publicK);
                powAnswer = nonce;
                System.out.flush();
                break; // Current nonce is answer. So break infinite loop.
            }

        }

        return powAnswer;

    }

    public static Map<String, Long> powIdTime(String catId, String publicK, int target) throws NoSuchAlgorithmException
    {

        long startTime = System.nanoTime();
        long endTime = 0;
        int counter = 0;
        String CKN = null; // Concatenating cat (C), nonce (N), public key (K).
        String nonce = null; // PoW answer.
        String CK = catId + publicK; // "category plus public key";
        String powAnswer = null;
        Map<String, Long> kt = new HashMap<String, Long>();

        // ArrayList<String> threadListStr = new ArrayList<String>();

        for (; ; )
        {
            // nonce will increase one after another in each round to be tested whether is a
            // correct answer for proof-of-work.
            counter++;
            nonce = Integer.toString(counter);
            CKN = CK + nonce;

            String hashCKNstr = calculateHash(CKN);
            // System.out.println(" t" + publicK + ": " + hashCKNstr);
            String nZero = createNZero(target);

            if (hashCKNstr.startsWith(nZero))
            {
                endTime = System.nanoTime() - startTime;
                System.out.flush();
                System.out.println(i + ": " + " PoW is solved after " + endTime + " nano seconds by Thread " + publicK
                        + ". Thread found correct answer after calculating " + nonce + " hashes.");
                System.out.flush();
                i++;
                System.out.flush();
                // System.err.println(" t" + publicK + " finished.");
                // threadListStr.add(publicK);
                powAnswer = nonce;
                // String kPlusA = publicK + nonce;
                System.out.flush();
                kt.put(publicK, endTime);
                System.out.flush();
                break; // Current nonce is answer. So break infinite loop.
            }
        }

        return kt;

    }

    public static ConsensusResult powIdAnswerTime(String catId, String publicK, int target)
            throws NoSuchAlgorithmException
    {

        long startTime = System.nanoTime();
        long endTime = 0;
        int counter = 0;
        String CKN = null; // Concatenating cat (C), nonce (N), public key (K).
        String nonce = null; // PoW answer.
        String CK = catId + publicK; // "category plus public key";
        String powAnswer = null;
        // Map<String, Long> kt = new HashMap<String, Long>();

        // ArrayList<String> threadListStr = new ArrayList<String>();
        ConsensusResult consensusResult = null;
        while (!Thread.interrupted())
        {
            // nonce will increase one after another in each round to be tested whether is a
            // correct answer for proof-of-work.
            counter++;
            nonce = Integer.toString(counter);
            CKN = CK + nonce;

            String hashCKNstr = calculateHash(CKN);
            // System.out.println(" t" + publicK + ": " + hashCKNstr);
            String nZero = createNZero(target);

            if (hashCKNstr.startsWith(nZero))
            {
                endTime = System.nanoTime() - startTime;
                System.out.flush();
                System.out.println(i + ": " + " PoW is solved after " + endTime + " nano seconds by Thread " + publicK
                        + ". Thread found correct answer after calculating " + nonce + " hashes.");
                System.out.flush();
                i++;
                System.out.flush();
                // System.err.println(" t" + publicK + " finished.");
                // threadListStr.add(publicK);
                powAnswer = nonce;
                // String kPlusA = publicK + nonce;
                System.out.flush();
                consensusResult = new ConsensusResult(publicK, powAnswer, endTime);
                System.out.flush();
                break; // Current nonce is answer. So break infinite loop.
            }
        }

        return consensusResult;

    }

    public static Map<String, String> powIdAnswer(String catId, String publicK, int target)
            throws NoSuchAlgorithmException
    {

        int counter = 0;
        String CKN = null; // Concatenating cat (C), nonce (N), public key (K).
        String nonce = null; // PoW answer.
        String CK = catId + publicK; // "category plus public key";
        String powAnswer = null;
        Map<String, String> kPlusA = new HashMap<String, String>();

        // ArrayList<String> threadListStr = new ArrayList<String>();

        for (; ; )
        {
            // nonce will increase one after another in each round to be tested whether is a
            // correct answer for proof-of-work.
            counter++;
            nonce = Integer.toString(counter);
            CKN = CK + nonce;

            String hashCKNstr = calculateHash(CKN);
            System.out.println("         t" + publicK + ": " + hashCKNstr);
            String nZero = createNZero(target);

            if (hashCKNstr.startsWith(nZero))
            {
                System.err.println("         t" + publicK + " finished.");
                System.out.flush();
                // threadListStr.add(publicK);
                powAnswer = nonce;
                kPlusA.put(publicK, powAnswer);
                break; // Current nonce is answer. So break infinite loop.
            }

        }

        return kPlusA;

    }

    public static String powByThreads(int numberOfCat, String catId, int numberOfPeers) throws InterruptedException
    {

        String winnerThreadId = null;
        int[] targetConsensus = new int[numberOfCat];
        targetConsensus[0] = 4;
        ArrayList<Thread> threadList = new ArrayList<Thread>();
        List<ConsensusResult> consensusResultsList = new ArrayList<>();
        int threadNumber = numberOfPeers;
        for (int i = 0; i < threadNumber; i++)
        {
            String tId = String.valueOf(i);
            Thread thr = new Thread(() ->
            {
                try
                {
                    ConsensusResult consensusResult = PoW.powIdAnswerTime(catId, tId, targetConsensus[0]);
                    if (consensusResult != null)
                    {
                        consensusResultsList.add(consensusResult);
                    }
                    // STOP the threads
                    for (int j = 0; j < threadNumber; j++)
                    {
                        threadList.get(j).interrupt();
                    }
                } catch (NoSuchAlgorithmException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
            threadList.add(thr);
        }

        /**
         * START the threads
         */
        for (int i = 0; i < threadNumber; i++)
        {
            threadList.get(i).start();
        }

        /**
         * JOIN the threads
         */
        for (int i = 0; i < threadNumber; i++)
        {
            threadList.get(i).join();
        }

        Collections.sort(consensusResultsList);

        //		System.out.println("\n" + "Leader Peer is: Peer " + consensusResultsList.get(0).getThreadId() + "   "
        //				+ Ansi.RESET);


        /**
         * After selecting as a leader peer in each round of consensus, their quota is
         * reduced one unit.
         */
        winnerThreadId = consensusResultsList.get(0).getThreadId();
        //System.out.println("\n" + "Leader Peer is: Peer " + winnerThreadId + "   ");

        return winnerThreadId;
    }

    public static String calculateHash(String CKN)
    {
        String hashCKNstr = org.apache.commons.codec.digest.DigestUtils.sha256Hex(CKN);
        return hashCKNstr;
    }

    public static boolean powCheck(String catId, String publicK, int target, String powAnswer)
    {

        String CK = catId + publicK;
        String CKN = CK + powAnswer;

        String hashCKNstr = calculateHash(CKN);
        String nZero = createNZero(target);

        if (hashCKNstr.startsWith(nZero))
        {
            //System.out.println("PoW has been solved successfully.");
            return true;
        } else
        {
            //System.out.println("PoW has NOT been solved.");
            return false;
        }
    }

    private static String createNZero(int target)
    {
        StringBuilder nZeroBuilder = new StringBuilder();

        for (int i = 0; i < target; i++)
        {
            nZeroBuilder.append('0');
        }

        String nZero = nZeroBuilder.toString();
        return nZero;
    }

    public static <K, V> Map<K, V> sortByValue(Map<K, V> map)
    {
        List<Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Object>()
        {
            @SuppressWarnings("unchecked")
            public int compare(Object o1, Object o2)
            {
                return ((Comparable<V>) ((Entry<K, V>) (o1)).getValue())
                        .compareTo(((Entry<K, V>) (o2)).getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Iterator<Entry<K, V>> it = list.iterator(); it.hasNext(); )
        {
            Entry<K, V> entry = (Entry<K, V>) it.next();
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static String getKeyWithMinValue(Map<String, Long> map, String... keys)
    {
        String minKey = null;
        Long minValue = Long.MAX_VALUE;
        for (String key : keys)
        {
            Long value = map.get(key);
            if (value < minValue)
            {
                minValue = value;
                minKey = key;
            }
        }
        return minKey;
    }

}
