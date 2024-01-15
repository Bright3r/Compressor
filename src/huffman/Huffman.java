package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import io.FileHandler;

public class Huffman {

	private Map<Character, Integer> createFrequencyTable(String data) {
		Map<Character, Integer> freqTable = new HashMap<>();
		
		for (char ch : data.toCharArray()) {
			freqTable.put(ch, freqTable.getOrDefault(ch, 0) + 1);
		}
		
		return freqTable;
	}
	
	private Node createHuffmanTree(Map<Character, Integer> freqTable) {
		PriorityQueue<Node> minHeap = new PriorityQueue<>((a, b) -> {
			return a.frequency - b.frequency;
		});
		
		// construct min heap from frequency table
		for (char ch : freqTable.keySet()) {
			Node huffmanLeaf = new Node(ch, freqTable.get(ch));
			minHeap.add(huffmanLeaf);
		}
		
		// construct Huffman tree
		while (minHeap.size() > 1) {
			Node node1 = minHeap.poll();
			Node node2 = minHeap.poll();
			int internalNodeFreq = node1.frequency + node2.frequency;
			
			Node internalNode = Node.createInternalNode(internalNodeFreq, node1, node2);
			minHeap.add(internalNode);
		}
		
		return minHeap.peek();
	}
	
	private Map<Character, String> getHuffmanCodes(Node root) {
		Map<Character, String> codes = new HashMap<>();
		codes = getHuffmanCodesR(root, codes, "0");
		
		return codes;
	}
	
	private Map<Character, String> getHuffmanCodesR(Node root, Map<Character, String> codes, String currCode) {
		if (root.left == null && root.right == null) {
			codes.put(root.val, currCode);
			return codes;
		}
		
		getHuffmanCodesR(root.left, codes, currCode + "0");
		getHuffmanCodesR(root.right, codes, currCode + "1");
		return codes;
	}
	
	private Map<String, String> invertHuffmanCodes(Map<Character, String> codes) {
		Map<String, String> codeToChar = new HashMap<>();
		for (char key : codes.keySet()) {
			codeToChar.put(codes.get(key), Character.toString(key));
		}
		return codeToChar;
	}
	
	private void saveCodec(String encodedMsg, Map<String, String> decodingTable, String filePath) throws IOException {
		File file = new File(filePath);
		FileOutputStream fileStream = new FileOutputStream(file);
		ObjectOutputStream objStream = new ObjectOutputStream(fileStream);
		
		objStream.writeObject(decodingTable);
		
		byte[] binary = FileHandler.binaryToBytes(encodedMsg);
		objStream.writeObject(binary);
		
		objStream.close();
		fileStream.close();
	}
	
	/**
	 * Encodes a given String using a Huffman Coding
	 * 
	 * @param msg the string to encode
	 * @return the encoded string
	 * @throws IOException 
	 */
	public void encodeToFile(String msg, String filePath) throws IOException {
		Map<Character, Integer> map = createFrequencyTable(msg);
		Node tree = createHuffmanTree(map);
		Map<Character, String> codes = getHuffmanCodes(tree);
		
		// create encoded message
		StringBuilder encodedMsg = new StringBuilder();
		for (char ch : msg.toCharArray()) {
			encodedMsg.append(codes.get(ch));
		}
		
		// store codec
		Map<String, String> decodingTable = invertHuffmanCodes(codes);
		saveCodec(encodedMsg.toString(), decodingTable, filePath);
	}
	
	/**
	 * Decodes a given String given the corresponding Huffman Codes
	 * 
	 * @param encodedMsg the encoded message
	 * @return the decoded string
	 */
	public String decode(String encodedMsg, Map<String, String> decodingTable) {
		StringBuilder msg = new StringBuilder();
		
		int idx = 0;
		StringBuilder currCode = new StringBuilder();
		while (idx < encodedMsg.length()) {
			currCode.append(encodedMsg.charAt(idx++));
			String decodedChar = decodingTable.get(currCode.toString());
			
			if (decodedChar != null) {
				msg.append(decodedChar);
				currCode.delete(0, currCode.length());	// clear string builder
			}
		}
		
		return msg.toString();
	}
	
	/**
	 * Decodes a given file into a String
	 * 
	 * @param filePath the path of the encoded file
	 * @return a string representation of the decoded file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public String decodeFile(String filePath) throws IOException, ClassNotFoundException {
		File file = new File(filePath);
		FileInputStream fileStream = new FileInputStream(file);
		ObjectInputStream objStream = new ObjectInputStream(fileStream);
		
		Map<String, String> codeToChar = (Map<String, String>) objStream.readObject();
		byte[] encodedBytes = (byte[]) objStream.readObject();
		String encodedMsg = FileHandler.bytesToString(encodedBytes);
		
		objStream.close();
		fileStream.close();
		
		return decode(encodedMsg, codeToChar);
	}
	
	
	
	public static void main(String[] args) {
		Huffman encoder = new Huffman();
		String filePath = "F:\\Coding\\Java\\encoding\\Compressor\\test_enc.txt";
		
		try {
			encoder.encodeToFile(test, filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			String msg = encoder.decodeFile(filePath);
			System.out.println(msg.equals(test));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	private static final String test = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas convallis imperdiet dui, vitae mollis nisi ultrices at. Maecenas pulvinar nibh id lobortis iaculis. Proin congue nulla arcu, ac consequat massa ultrices eget. Donec facilisis nibh non purus dapibus pellentesque. In posuere tellus id eros feugiat, vel pulvinar diam sodales. Sed lobortis nibh sed urna maximus, non luctus est ultricies. Pellentesque porttitor malesuada nulla et tempor."
	        + "Vivamus ligula odio, mollis et tellus viverra, euismod cursus nunc. Donec eu dignissim elit. Donec gravida eu purus a suscipit. Nunc eleifend convallis condimentum. Phasellus tincidunt, magna maximus faucibus bibendum, augue orci convallis lectus, id condimentum sem lacus ac sem. Quisque vitae rutrum felis. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Aliquam molestie feugiat urna, lobortis placerat urna gravida vel."
			+ "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas convallis imperdiet dui, vitae mollis nisi ultrices at. Maecenas pulvinar nibh id lobortis iaculis. Proin congue nulla arcu, ac consequat massa ultrices eget. Donec facilisis nibh non purus dapibus pellentesque. In posuere tellus id eros feugiat, vel pulvinar diam sodales. Sed lobortis nibh sed urna maximus, non luctus est ultricies. Pellentesque porttitor malesuada nulla et tempor."
	        + "Vivamus ligula odio, mollis et tellus viverra, euismod cursus nunc. Donec eu dignissim elit. Donec gravida eu purus a suscipit. Nunc eleifend convallis condimentum. Phasellus tincidunt, magna maximus faucibus bibendum, augue orci convallis lectus, id condimentum sem lacus ac sem. Quisque vitae rutrum felis. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Aliquam molestie feugiat urna, lobortis placerat urna gravida vel."
			+ "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas convallis imperdiet dui, vitae mollis nisi ultrices at. Maecenas pulvinar nibh id lobortis iaculis. Proin congue nulla arcu, ac consequat massa ultrices eget. Donec facilisis nibh non purus dapibus pellentesque. In posuere tellus id eros feugiat, vel pulvinar diam sodales. Sed lobortis nibh sed urna maximus, non luctus est ultricies. Pellentesque porttitor malesuada nulla et tempor."
	        + "Vivamus ligula odio, mollis et tellus viverra, euismod cursus nunc. Donec eu dignissim elit. Donec gravida eu purus a suscipit. Nunc eleifend convallis condimentum. Phasellus tincidunt, magna maximus faucibus bibendum, augue orci convallis lectus, id condimentum sem lacus ac sem. Quisque vitae rutrum felis. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Aliquam molestie feugiat urna, lobortis placerat urna gravida vel."
			+ "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas convallis imperdiet dui, vitae mollis nisi ultrices at. Maecenas pulvinar nibh id lobortis iaculis. Proin congue nulla arcu, ac consequat massa ultrices eget. Donec facilisis nibh non purus dapibus pellentesque. In posuere tellus id eros feugiat, vel pulvinar diam sodales. Sed lobortis nibh sed urna maximus, non luctus est ultricies. Pellentesque porttitor malesuada nulla et tempor."
	        + "Vivamus ligula odio, mollis et tellus viverra, euismod cursus nunc. Donec eu dignissim elit. Donec gravida eu purus a suscipit. Nunc eleifend convallis condimentum. Phasellus tincidunt, magna maximus faucibus bibendum, augue orci convallis lectus, id condimentum sem lacus ac sem. Quisque vitae rutrum felis. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Aliquam molestie feugiat urna, lobortis placerat urna gravida vel."
			+ "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas convallis imperdiet dui, vitae mollis nisi ultrices at. Maecenas pulvinar nibh id lobortis iaculis. Proin congue nulla arcu, ac consequat massa ultrices eget. Donec facilisis nibh non purus dapibus pellentesque. In posuere tellus id eros feugiat, vel pulvinar diam sodales. Sed lobortis nibh sed urna maximus, non luctus est ultricies. Pellentesque porttitor malesuada nulla et tempor."
	        + "Vivamus ligula odio, mollis et tellus viverra, euismod cursus nunc. Donec eu dignissim elit. Donec gravida eu purus a suscipit. Nunc eleifend convallis condimentum. Phasellus tincidunt, magna maximus faucibus bibendum, augue orci convallis lectus, id condimentum sem lacus ac sem. Quisque vitae rutrum felis. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Aliquam molestie feugiat urna, lobortis placerat urna gravida vel.";
	
	
	private class Node {
		private char val;
		private int frequency;
		private Node left;
		private Node right;
		
		private Node(char val, int frequency) {
			this.val = val;
			this.frequency = frequency;
			this.left = null;
			this.right = null;
		}
		
		private static Node createInternalNode(int frequency, Node left, Node right) {
			Huffman huffman = new Huffman();
			Node internalNode = huffman.new Node('#', frequency);
			internalNode.left = left;
			internalNode.right = right;
			
			return internalNode;
		}
	}
	
}
