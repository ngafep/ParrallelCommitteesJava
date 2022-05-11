package com.engie.csai.pc.core.model;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeyGenerator
{

    public static KeyPairGenerator keyGenerator() throws NoSuchAlgorithmException
    {

        KeyPairGenerator keyPair = KeyPairGenerator.getInstance("RSA");
        keyPair.initialize(512);
        return keyPair;

    }

    public static StringBuffer publicKeyString(KeyPairGenerator keyPair)
    {

        byte[] publicKey = keyPair.genKeyPair().getPublic().getEncoded();
        StringBuffer publicKeyString = new StringBuffer();
        for (int i = 0; i < publicKey.length; ++i)
        {
            publicKeyString.append(Integer.toHexString(0x0100 + (publicKey[i] & 0x00FF)).substring(1));
        }
        return publicKeyString;
    }

    public static StringBuffer privateKeyString(KeyPairGenerator keyPair)
    {

        byte[] privateKey = keyPair.genKeyPair().getPrivate().getEncoded();
        StringBuffer privateKeyString = new StringBuffer();
        for (int i = 0; i < privateKey.length; ++i)
        {
            privateKeyString.append(Integer.toHexString(0x0100 + (privateKey[i] & 0x00FF)).substring(1));
        }
        return privateKeyString;
    }


    //	public static byte[] publicKeyByteArray(KeyPairGenerator keyPair) {
    //
    //		 byte[] publicKey = keyPair.genKeyPair().getPublic().getEncoded();
    //	        StringBuffer retString = new StringBuffer();
    //	        retString.append("[");
    //	        for (int i = 0; i < publicKey.length; ++i) {
    //	            retString.append(publicKey[i]);
    //	            retString.append(", ");
    //	        }
    //	        retString = retString.delete(retString.length()-2,retString.length());
    //	        retString.append("]");
    //	        System.out.println(retString); //e.g. [48, 92, 48, .... , 0, 1]
    //			return retString;
    //
    //	}


}
