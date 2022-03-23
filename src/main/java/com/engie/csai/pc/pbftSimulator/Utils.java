package com.engie.csai.pc.pbftSimulator;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
	
	public static boolean reachMajority(int f, int n) {
        /**
         * Never used.
         */
		return f == 2 * getMaxTorelentNumber(n) + 1;
	}

	public static int getMaxTorelentNumber(int n) {
        /**
         * Check if getMaxTorelentNumber is implemented correctly ...
         */
		if(n/3*3 == n) {
			return n/3 - 1;
		}
		return n/3;
	}
	
	public static int[][] flipMatrix(int[][] matrix) {
		int m = matrix.length, n = matrix[0].length;
		int[][] flipMa = new int[n][m];
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < m; j++) {
				flipMa[i][j] = matrix[j][i]; 
			}
		}
		return flipMa;
	}
	
	/**
     * 使用指定哈希算法计算摘要信息 // Use the specified hash algorithm to calculate the digest information
     * @param content 内容 // content
     * @param algorithm 哈希算法 // Hash algorithm
     * @return 内容摘要 // abstract
     */
    public static String getMD5Digest(String content){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("sha-256");
            messageDigest.update(content.getBytes("utf-8"));
            return bytesToHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 将字节数组转换成16进制字符串 // Convert byte array to hexadecimal string
     * @param bytes 即将转换的数据 // The data to be converted
     * @return 16进制字符串 // Hexadecimal string
     */
    private static String bytesToHexString(byte[] bytes){
        StringBuffer sb = new StringBuffer(bytes.length);
        String temp = null;
        for (int i = 0;i< bytes.length;i++){
            temp = Integer.toHexString(0xFF & bytes[i]);
            if (temp.length() <2){
                sb.append(0);
            }
            sb.append(temp);
        }
        return sb.toString();
    }
  
}
