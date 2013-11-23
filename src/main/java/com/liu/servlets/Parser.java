package com.liu.servlets;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

public class Parser {
	private static Logger logger = Logger.getLogger(Parser.class);
	private static Conf conf = Conf.getInstance();
	
	public static String normLog(String log) throws Exception {
		return new String(decryptBySecretKey(log.getBytes(), getSecretKeySpec()));
	}

	/**
	 * 用对称密钥加密明文
	 * 
	 * @param byte[] 需要加密的内容
	 * @param SecretKey
	 *            用来加密的对称密钥
	 * @return byte[] 加密后的密文
	 * @throws EncryptException
	 */
	private static byte[] encryptBySecretKey(byte[] data, SecretKey secretKey)
			throws Exception {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return cipher.doFinal(data);

	}
	
	private static SecretKeySpec getSecretKeySpec() {
		String keyStr = conf.getSecretKey();
        return new SecretKeySpec(keyStr.getBytes(), "AES");
	}

	/**
	 * 用对称密钥解密密文
	 * 
	 * @param byte[] 加密后的密文
	 * @param SecretKey
	 *            用来加解密的对称密钥
	 * @return String 解密后的明文
	 * @throws DecryptException
	 */
	private static byte[] decryptBySecretKey(byte[] data, SecretKey secretKey)
			throws Exception {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return cipher.doFinal(data);
	}
}
