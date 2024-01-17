package io;

public class FileHandler {

	public static byte[] binaryToBytes(String binaryStr) {
		byte[] bytes = new byte[(binaryStr.length() + 7) / 8];
		
		for (int i = 0; i < binaryStr.length(); i++) {
			char ch = binaryStr.charAt(i);
			if (ch == '1') {
				bytes[i / 8] |= (1 << (7 - (i % 8)));	// set corresponding bit to 1
			}
		}
		
		System.out.println(binaryStr);
		return bytes;
	}
	
	public static String bytesToBinary(byte[] bytes) {
		StringBuilder str = new StringBuilder();
		for (byte b : bytes) {
			String binaryStr = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
	        str.append(binaryStr);
		}
		System.out.println(str);
		return str.toString();
	}
}
