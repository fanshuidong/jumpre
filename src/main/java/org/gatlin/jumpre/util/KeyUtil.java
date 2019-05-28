package org.gatlin.jumpre.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

public class KeyUtil {

	private static Random UUID;
	
	static {
		SecureRandom secureRandom = new SecureRandom();
		byte[] seed = new byte[8];
		secureRandom.nextBytes(seed);
		UUID = new Random(new BigInteger(seed).longValue());
	}
	
	public static final String uuid() {
		byte[] randomBytes = new byte[16];
		UUID.nextBytes(randomBytes);
		long mostSigBits = 0;
		for (int i = 0; i < 8; i++)
			mostSigBits = (mostSigBits << 8) | (randomBytes[i] & 0xff);
		long leastSigBits = 0;
		for (int i = 8; i < 16; i++)
			leastSigBits = (leastSigBits << 8) | (randomBytes[i] & 0xff);
		return new UUID(mostSigBits, leastSigBits).toString();
	}
	
	public static String randomCode(int length, boolean number) {
		Random random = new Random();
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (number) 
				ret.append(Integer.toString(random.nextInt(10)));
			else {
				boolean isChar = (random.nextInt(2) % 2 == 0);// 输出字母还是数字
				if (isChar) { // 字符串
					int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写字母还是小写字母
					ret.append((char) (choice + random.nextInt(26)));
				} else  // 数字
					ret.append(Integer.toString(random.nextInt(10)));
			}
		}
		return ret.toString();
	}
}
