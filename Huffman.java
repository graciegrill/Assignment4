/**
 * This contains the Huffman class, which can take a text file, generate a character and relative frequency list,
 * build a Huffman tree, build the corresponding encoding table, and then encode the message into
 * binary.
 * Dependencies: BinaryIn.java, BinaryOut.java, BinaryStdIn.java, and BinaryStdOut.java
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;


public class Huffman {
    /**
     * This method takes a string and documents the frequencies of each character.
     * @param s - string to be processed.
     * @return - HashMap of characters and their integer frequencies
     */
    public static HashMap<Character, Integer> determineFrequencies(String s){
        HashMap<Character, Integer> freqs = new HashMap<Character, Integer>();
        String allowed = "abcdefghijklmnopqrstuvwxyz-!.+";
        for (char c : allowed.toCharArray()) {
            freqs.put(c, 0);
        }
        char[] chars = s.toCharArray();
        for(char c: chars){
            if(freqs.containsKey(c)){
                freqs.put(c, freqs.get(c) + 1);
            }
        }
        return freqs;
    }


    // Huffman trie node
    private static class HuffmanNode{
        private final char ch;
        private final int freq;
        private final HuffmanNode left, right;

        HuffmanNode(char ch, int freq, HuffmanNode left, HuffmanNode right) {
            this.ch    = ch;
            this.freq  = freq;
            this.left  = left;
            this.right = right;
        }
        }
        //Comparator class
        private static class MyComparator implements Comparator<HuffmanNode> { 
            public int compare(HuffmanNode x, HuffmanNode y) 
            { 
          
                return x.freq - y.freq; 
            } 
        } 
      
    /**
     * This method actually creates the Huffman tree and returns the root node of it.
     * @param chars - the frequency HashMap
     * @return - HuffmanNode root, the root node of the tree
     */
    public static HuffmanNode createHuffman(HashMap<Character, Integer> chars) {
        int n = chars.size();
        //Create huffman tree as priority queue
        PriorityQueue<HuffmanNode> q = new PriorityQueue<HuffmanNode>(n, new MyComparator());
        //Adjust code to work with HashMap 
        for (char c : chars.keySet()) {
            int freq = chars.get(c);
            if (freq > 0) {
                q.add(new HuffmanNode(c, chars.get(c), null, null));
            }
        }
        // create a root node 
        HuffmanNode root = null; 
    
        while (q.size() > 1) {
            // first min extract. 
            HuffmanNode x = q.peek(); 
            q.poll(); 
    
            // second min extract. 
            HuffmanNode y = q.peek(); 
            q.poll(); 
    
            // new node f which is equal 
            HuffmanNode f = new HuffmanNode('-', x.freq + y.freq, x, y); 
    
            // marking the f node as the root node. 
            root = f; 
    
            // add this node to the priority-queue. 
            q.add(f); 
            } 
            //return root node of tree
            return root;
        }

    /**
     * This just prints each code for each character.
     * @param root - Huffman node root node
     * @param s - will be empty string to append characters and binary code
     */
    public static void printCode(HuffmanNode root, String s) {
        // leaf node check
        if (root.left == null && root.right == null) {
            System.out.println("'" + root.ch + "': " + s);
            return;
        }
    
        //left and right recursion
        printCode(root.left, s + "0");
        printCode(root.right, s + "1");
    }
    /**
     * This merges the two tutorials a little bit to build the actual tree -- 
     * essentially mirrors printCode()
     * @param st - String array that will represent the tree
     * @param x - HuffmanNode that will be root
     * @param s - empty string to build upon
     */
    public static void buildCodeMap(String[] st, HuffmanNode x, String s) {
        //if leaf node
        if (x.left == null && x.right == null) {
            st[x.ch] = s;
            return;
        }
    
        // Recursively build the code for left and right children
        buildCodeMap(st, x.left, s + '0');
        buildCodeMap(st, x.right, s + '1');
    }
    /**
     * Writes a trie
     * @param x - Huffman Node
     */
    public static void writeTrie(HuffmanNode x) {
        if (x.left == null && x.right==null) {
            BinaryStdOut.write(true);
            BinaryStdOut.write(x.ch, 8);
            return;
        }
        BinaryStdOut.write(false);
        writeTrie(x.left);
        writeTrie(x.right);
    }

    /**
     * Compresses message to binary and handles most print statements
     * @param inputFile - what filename are we encoding
     * @param outputFile - bin filename for output
     */
    public static void compress(String inputFile, String outputFile){
        //Empty string to build from text file
        String words = "";
        //Reading text into string if in allowed characters
        try{
            File data = new File(inputFile);
            Scanner sophon = new Scanner(data);
            while (sophon.hasNextLine()){
                words += sophon.nextLine().toLowerCase().replaceAll("[^a-z\\-!.+]", "");
            }
            sophon.close();
        }
        //File error handling
        catch(FileNotFoundException x){
            System.out.println("File not found");
        }
        //Frequency map creation
        HashMap<Character, Integer> freqs = determineFrequencies(words);
        //Print frequency table - requirement 1
        System.out.println(freqs);
        //Create huffman tree
        HuffmanNode root = createHuffman(freqs);
        //Print each code
        printCode(root, "");
        //Create empty Huffman code table
        String[] st = new String[256];
        //Build code table
        buildCodeMap(st, root, "");

        //Set file for output
        BinaryStdOut.setFile(outputFile);

        // Write trie and original message length
        writeTrie(root);
        BinaryStdOut.write(words.length());
        //Empty string to print out coded message
        String codedMessage = "";
        //Encoding message
        for (int i = 0; i < words.length(); i++) {
            String code = st[words.charAt(i)];
            for (int j = 0; j < code.length(); j++) {
                if (code.charAt(j) == '0') {
                    BinaryStdOut.write(false);
                    codedMessage+=code;
                }
                else if (code.charAt(j) == '1') {
                    BinaryStdOut.write(true);
                    codedMessage+=code;
                }
                else throw new IllegalStateException("Illegal state");
            }}
            //Print coded message
            System.out.println(codedMessage);
            BinaryStdOut.close();
        
    }
   
public static void main(String[] args) {
    //String encodedFileName = "encodedfile"+System.currentTimeMillis()+".bin";
    //compress("filetoencode.txt", encodedFileName);
    String encodedFileName = "encodedfile"+System.currentTimeMillis()+".bin";
    compress("largerTest.txt", encodedFileName);
}}
/**
 * Sample output:
 * Using filetoencode.txt:
 * {a=3, !=0, b=1, c=1, d=1, e=1, f=1, g=0, h=0, i=0, j=0, k=0, +=0, l=0, m=0, -=0, n=0, .=0, o=0, p=0, q=0, r=0, s=0, t=0, u=0, v=0, w=0, x=0, y=0, z=0}
'a': 0
'b': 100
'f': 101
'c': 110
'e': 1110
'd': 1111
01001001000111111111111111110110110101101101101110111011101110

Using largerTest.txt:
{a=155, !=13, b=25, c=44, d=107, e=270, f=52, g=47, h=131, i=126, j=2, k=25, +=1, l=81, m=32, -=444, n=147, .=71, o=155, p=34, q=0, r=152, s=120, t=181, u=40, v=17, w=51, x=2, y=20, z=0}
's': 0000
'k': 000100
'm': 000101
'p': 000110
'v': 0001110
'x': 000111100
'+': 0001111010
'j': 0001111011
'!': 00011111
'i': 0010
'h': 0011
'e': 010
'n': 0110
'.': 01110
'l': 01111
'r': 1000
'o': 1001
'a': 1010
'u': 101100
'c': 101101
'y': 1011100
'b': 1011101
'g': 101111
't': 1100
'w': 110100
'f': 110101
'd': 11011
'-': 111
1100110011001100001100110011001101001001011111111100100010001000101...it goes on for a very long time
 */