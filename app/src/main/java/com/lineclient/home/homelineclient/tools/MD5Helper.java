package com.lineclient.home.homelineclient.tools;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Helper {

	//md5文件加密
	public static String getFileMD5String(File file) throws IOException {
		String value = null;  
		FileInputStream in = new FileInputStream(file);  
		try {  
			MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());  
			MessageDigest md5 = MessageDigest.getInstance("MD5");  
			md5.update(byteBuffer);  
			BigInteger bi = new BigInteger(1, md5.digest());  
			value = bi.toString(16);  
		} catch (Exception e) {  
			e.printStackTrace();  
		} finally {  
			if(null != in) {  
				try {  
					in.close();  
				} catch (IOException e) {  
					e.printStackTrace();  
				}  
			}  
		}  
		return value;  
	}

	//md5流码加密
	public String getFileMD5String(InputStream in) throws IOException {
		String md5 = DigestUtils.md5Hex(IOUtils.toByteArray(in));    
		IOUtils.closeQuietly(in);    
		return md5;
	}

	//md5字符串加密
	public static String stringToMD5(String string) {  
		byte[] hash;  

		try {  
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));  
		} catch (NoSuchAlgorithmException e) {  
			e.printStackTrace();  
			return null;  
		} catch (UnsupportedEncodingException e) {  
			e.printStackTrace();  
			return null;  
		}  

		StringBuilder hex = new StringBuilder(hash.length * 2);  
		for (byte b : hash) {  
			if ((b & 0xFF) < 0x10)  
				hex.append("0");  
			hex.append(Integer.toHexString(b & 0xFF));  
		}  

		return hex.toString();  
	}  

}
