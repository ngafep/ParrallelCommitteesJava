package com.engie.csai.pc.core.model;

public class PoW
{

    private PoW()
    {

    }

    public static String powAnswer(String catId, String publicK, int target)
    {

        int counter = 0;
        String ckn = null; // Concatenating cat (C), nonce (N), public key (K).
        String nonce = null; // PoW answer.
        String ck = catId + publicK; // "category plus public key"
        String powAnswer = null;

        for (; ; )
        {
            // nonce will increase one after another in each round to be tested whether is a
            // correct answer for proof-of-work.
            counter++;
            nonce = Integer.toString(counter);
            ckn = ck + nonce;

            String hashCKNstr = calculateHash(ckn);
            String nZero = createNZero(target);

            if (hashCKNstr.startsWith(nZero))
            {
                powAnswer = nonce;
                break; // Current nonce is answer. So break infinite loop.
            }

        }
        return powAnswer;
    }

    public static String calculateHash(String ckn)
    {
        return org.apache.commons.codec.digest.DigestUtils.sha256Hex(ckn);
    }

    public static boolean powCheck(String catId, String publicK, int target, String powAnswer)
    {
        String ck = catId + publicK;
        String ckn = ck + powAnswer;
        String hashCKNstr = calculateHash(ckn);
        String nZero = createNZero(target);
        return hashCKNstr.startsWith(nZero);
    }

    private static String createNZero(int target)
    {
        return "0".repeat(Math.max(0, target));
    }
}
