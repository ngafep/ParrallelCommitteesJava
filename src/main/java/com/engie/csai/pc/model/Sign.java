package com.engie.csai.pc.model;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

public class Sign
{

    //	public static String sign(String plainText, PrivateKey privateKey) throws Exception {
    //
    //		Signature privateSignature = Signature.getInstance("SHA256withRSA");
    //		privateSignature.initSign(privateKey);
    //		privateSignature.update(plainText.getBytes("UTF-8"));
    //		byte[] signature = privateSignature.sign();
    //		return Base64.getEncoder().encodeToString(signature);
    //	}
    //
    //	public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
    //
    //		Signature publicSignature = Signature.getInstance("SHA256withRSA");
    //		publicSignature.initVerify(publicKey);
    //		publicSignature.update(plainText.getBytes("UTF-8"));
    //		byte[] signatureBytes = Base64.getDecoder().decode(signature);
    //		return publicSignature.verify(signatureBytes);
    //	}

    public static String sign(String dataString, KeyPair kPairSign) throws Exception
    {

        Signature rsaSign = Signature.getInstance("SHA256withRSA"); // inside function of sign
        rsaSign.initSign(kPairSign.getPrivate()); // kPairSignCom0[peerIndex] as input parameter of function
        rsaSign.update(dataString.getBytes(StandardCharsets.UTF_8)); // dataString as input parameter of function
        byte[] signatureByte;
        signatureByte = rsaSign.sign();
        String signatureStr = Base64.getEncoder().encodeToString(signatureByte);
        return signatureStr;
    }

}
