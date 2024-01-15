package huffman;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Huffman {
	private Map<String, String> codes;

	private Map<Character, Integer> createFrequencyTable(String data) {
		Map<Character, Integer> freqTable = new HashMap<>();
		
		for (char ch : data.toCharArray()) {
			freqTable.put(ch, freqTable.getOrDefault(ch, 0) + 1);
		}
		
		return freqTable;
	}
	
	private Node createHuffmanTree(Map<Character, Integer> freqTable) {
		PriorityQueue<Node> minHeap = new PriorityQueue<>((a, b) -> {
			if (a.frequency < b.frequency) {
				return -1;
			}
			else if (a.frequency == b.frequency) {
				return 0;
			}
			else {
				return 1;
			}
		});
		
		// construct min heap from frequency table
		for (char ch : freqTable.keySet()) {
			Node huffmanLeaf = new Node(ch, freqTable.get(ch));
			minHeap.add(huffmanLeaf);
		}
		
		// construct huffman tree
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
		
		Map<String, String> codesToChar = new HashMap<>();
		for (char key : codes.keySet()) {
			codesToChar.put(codes.get(key), Character.toString(key));
		}
		this.codes = codesToChar;
		
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
	
	public StringBuilder encode(String msg) {
		Map<Character, Integer> map = createFrequencyTable(msg);
		Node tree = createHuffmanTree(map);
		Map<Character, String> codes = getHuffmanCodes(tree);
		
		StringBuilder res = new StringBuilder();
		for (char ch : msg.toCharArray()) {
			res.append(codes.get(ch));
		}
		
		return res;
	}
	
	public StringBuilder decode(StringBuilder encoded) {
		StringBuilder msg = new StringBuilder();
		
		String currCode = "";
		int i = 0;
		while (i < encoded.length()) {
			currCode += encoded.charAt(i++);
			String decodedChar = this.codes.get(currCode);
			if (decodedChar != null) {
				msg.append(decodedChar);
				currCode = "";
			}
		}
		return msg;
	}
	
	public static void main(String[] args) {
		String data = "Hello World!";
		
		Huffman encoder = new Huffman();		
		StringBuilder res = encoder.encode(data);
		System.out.println(res);
		System.out.println(encoder.decode(res));
	}
	
	
	
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
