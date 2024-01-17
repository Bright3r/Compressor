package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class FileHandler {

	public static byte[] binaryToBytes(String binaryStr) {
		byte[] bytes = new byte[(binaryStr.length() + 7) / 8];
		
		for (int i = 0; i < binaryStr.length(); i++) {
			char ch = binaryStr.charAt(i);
			if (ch == '1') {
				bytes[i / 8] |= (1 << (7 - (i % 8)));	// set corresponding bit to 1
			}
		}
		
		return bytes;
	}
	
	public static String bytesToBinary(byte[] bytes) {
		StringBuilder str = new StringBuilder();
		for (byte b : bytes) {
			String binaryStr = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
	        str.append(binaryStr);
		}

		return str.toString();
	}
	
	public static String readFile(String filePath) throws FileNotFoundException {
		File file = new File(new File(filePath).getAbsolutePath());
		
		FileReader fileReader = new FileReader(file);
		Scanner scanner = new Scanner(fileReader);
		
		StringBuilder str = new StringBuilder();
		while (scanner.hasNext()) {
			str.append(scanner.nextLine());
		}
		
		return str.toString();
	}
}
