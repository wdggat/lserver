package com.liu.servlets;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.liu.msg.DataType;
import com.liu.msg.EmailMsg;
import com.liu.msg.Message;

public class Parser {
	private static Logger logger = Logger.getLogger(Parser.class);
	private static Conf conf = Conf.getInstance();
	
	private static Map<String, Class> msgClazzes = getMsgClazzes();
	
	@SuppressWarnings("rawtypes")
	private static Map<String, Class> getMsgClazzes() {
		Map<String, Class> cs = new HashMap<String, Class>();
		cs.put("email", EmailMsg.class);
		return cs;
	}
	
	public static String normLog(String log) throws Exception {
		return new String(decryptBySecretKey(log.getBytes(), getSecretKeySpec()));
	}
	
	public static String completeLog(String log, String ip, String os) {
		StringBuffer ret = new StringBuffer();
		ret.append(log.substring(0, log.length() - 1));
		ret.append("\"ip\":\"" + ip + "\",");
		ret.append("\"os\":\"" + os + "\"}");
		return ret.toString();
	}
	
	public static DataType getDataType(String message) {
		String dataTypeValue = StringUtils.substringBetween(message, "\"dataType\":\"", "\"");
		return DataType.valueOfSymbal(dataTypeValue);
	}
	
	public static Message getMessage(String message) {
		if(DataType.MESSAGE.equals(getDataType(message))) {
			String msgValue = StringUtils.substringBetween(message, "\"messageType\":\"", "\"");
			return JSON.parseObject(message, msgClazzes.get(msgValue));
		}
		return null;
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
