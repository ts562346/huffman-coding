
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 Code
 provided from previous version and modified for 2020.
 */

public class Huffman {

    /**
     * This method reads name of the input file from the user and encodes it using
     * the huffman algorithm
     * @throws IOException
     */

    public static void encode()throws IOException{
        // initialize Scanner to capture user input
        Scanner sc = new Scanner(System.in);

        // capture file information from user and read file
        System.out.print("Enter the filename to read from/encode: ");
        String f = sc.nextLine();

        // create File object and build text String
        File file = new File(f);
        Scanner input = new Scanner(file).useDelimiter("\\z");
        String text = input.next();

        // close input file
        input.close();

        // initialize Array to hold frequencies (indices correspond to
        // ASCII values)
        int[] freq = new int[256];

        // concatenate/sanitize text String and create character Array
        // nice that \\s also consumes \n and \r
        // we can add the whitespace back in during the encoding phase
        char[] chars = text.replaceAll("\\s", "").toCharArray();

        // count character frequencies
        for(char c: chars)
            freq[c]++;


        //Your work starts here************************************8

        ArrayList<Pair> pairs = new ArrayList<>(); // pairs that store the characters with probabilities

        for(int i = 0; i<256; i++){ // calculates probability of the characters (provided in PDF)
            if(freq[i]!=0){
                // this method of rounding is good enough
                Pair p = new Pair((char)i, Math.round(freq[i]*10000d/chars.length)/10000d);
                pairs.add(p);
            }
        }

        ArrayList<Pair> S = new ArrayList<>(); // a copy of the pair arrayList
        S.addAll(pairs);

        ArrayList <Pair> sorted = new ArrayList<>(); // a sorted version of pairs arrayList

        Pair max = pairs.get(0); // set first element to the max variable for comparison
        while(pairs.size() > 0){
            for(int i = 0; i < pairs.size(); i++){
                if(max.compareTo(pairs.get(i)) < 0){
                    max = pairs.get(i); // set min as the new max
                }
            }
            sorted.add(max); // then add max to the new array which is going to be sorted
            pairs.remove(max);
            if(pairs.size() > 0){
                max = pairs.get(0);
            }
        }

        //Apply the huffman algorithm here and build the tree ************************************

        ArrayList<PairTree> T = new ArrayList<>();

        PairTree A, B;
        while (!S.isEmpty()) {
            if (T.isEmpty()) { // when T is empty save the values of first two elements in S
                A = new PairTree (S.remove(0));
                B = new PairTree (S.remove(1));
            } else {
                if (S.get(0).getProb() < T.get(0).getData().getProb()) // Find which probability is greater
                    A = new PairTree(S.remove (0)); // if S is smaller, remove that
                else
                    A = T.remove(0); // otherwise remove T

                if (!T.isEmpty() && !S.isEmpty()){ // if both of them are not empty, keep comparing data
                    if (S.get(0).compareTo(T.get(0).getData()) < 0)
                        B = new PairTree(S.get(0));
                    else
                        B = T.remove(0);
                } else { // other wise remove from the list that is not empty
                    if (S.isEmpty())
                        B = T.remove(0);
                    else
                        B = new PairTree(S.remove(0));
                }
            }

            // create the pariTree and find the probability
            PairTree P = new PairTree();
            P.attachLeft(A);
            P.attachRight(B);
            P.setData(new Pair('⁂', A.getProb() + B.getProb()));

            T.add(P); // add the tree to the list

        }

        while(T.size() > 1){ // if T has more than 1 element
            A = T.remove(0); // remove the first element
            B = T.remove(0);

            PairTree P = new PairTree(); // make the new PairTree
            P.attachLeft(A);
            P.attachRight(B);
            P.setData(new Pair('⁂', A.getProb() + B.getProb())); // add up the probabilities

            T.add(P); // then add it to list T
        }

        PairTree HuffmanTree = T.remove(0); // remove the first element of T
        String[] codes = findEncoding(HuffmanTree); // then find the HuffmanTree encoding

        PrintWriter fileToOutput = new PrintWriter("Huffman.txt"); // write the codes in this text file
        fileToOutput.println("Symbol" + "\t" + "Prob." + "\t" + "Huffman Code");
        fileToOutput.println();

        for(int i = 0; i < sorted.size(); i++){ // print the character with its probability and code
            fileToOutput.println(sorted.get(i).getValue() + "\t" + sorted.get(i).getProb()
                    + "\t" + codes[sorted.get(i).getValue()]);
        }
        fileToOutput.close();

        String encode = "";

        char[] charArray = text.toCharArray(); // print encoded text
        for (int i = 0; i < charArray.length; i++) {
            if(charArray[i] == ' ') {
                encode += " ";
            }
            else {
                encode += codes[charArray[i]];
            }
        }

        PrintWriter encodeFile = new PrintWriter("Encoded.txt");
        encodeFile.println(encode);
        encodeFile.close();

        System.out.println("Codes generated. Printing codes to Huffman.txt" +
                "\nPrinting encoded text to Encoded.txt");
    }

    /**
     * This method reads name of the input file from the user and decodes it using
     * the huffman algorithm and a file containing the huffman codes
     * @throws IOException
     */

    public static void decode()throws IOException{
        // initialize Scanner to capture user input
        Scanner sc = new Scanner(System.in);

        // capture file information from user and read file
        System.out.print("Enter the filename to read from/decode: ");
        String f = sc.nextLine();

        // create File object and build text String
        File file = new File(f);
        Scanner input = new Scanner(file).useDelimiter("\\Z");
        String text = input.next();
        // ensure all text is consumed, avoiding false positive end of
        // input String
        input.useDelimiter("\\z");
        text += input.next();


        // close input file
        input.close();

        // capture file information from user and read file
        System.out.print("Enter the filename of document containing Huffman codes: ");
        f = sc.nextLine();

        // create File object and build text String
        file = new File(f);
        input = new Scanner(file).useDelimiter("\\Z");
        String codes = input.next();

        // close input file
        input.close();

        //Your work starts here********************************************

        ArrayList<Character> charList = new ArrayList<>(); // list of characters

        Scanner code = new Scanner(codes);
        code.nextLine();
        code.nextLine();

        ArrayList<String> codeList = new ArrayList<>(); // list of huffman codes

        while (code.hasNextLine()) { // as long as there is more content
            char char_temp = code.next().charAt(0); // get next first character from the file
            charList.add(char_temp); // add it to character list

            code.next(); // skip to next read

            String str_temp = code.next(); // get the first string in file
            codeList.add(str_temp); // add it to code list
        }


        PrintWriter output = new PrintWriter("Decoded.txt"); // the file to print decoded text in
        String print = "";

        System.out.println("Printing decoded text to Decoded.txt");

        char[] charArray = text.toCharArray();

        for (int i = 0; i < charArray.length; i++) { // printing the decoded text
            if(charArray[i] == ' '){
                output.print(" ");
            } else {
                print += charArray[i];
                if (codeList.contains(print)) {
                    output.print(charList.get(codeList.indexOf(print)));
                    print = "";
                }
            }
        }
        output.close();
    }

    // the findEncoding helper method returns a String Array containing
    // Huffman codes for all characters in the Huffman Tree (characters not
    // present are represented by nulls)
    // this method was provided by Srini (Dr. Srini Sampalli). Two versions are below, one for Pairtree and one for BinaryTree


    private static String[] findEncoding(BinaryTree<Pair> bt){
        String[] result = new String[256];
        findEncoding(bt, result, "");
        return result;
    }


    private static void findEncoding(BinaryTree<Pair> bt, String[] a, String prefix){
        // test is node/tree is a leaf
        if (bt.getLeft()==null && bt.getRight()==null){
            a[bt.getData().getValue()] = prefix;
        }
        // recursive calls
        else{
            findEncoding(bt.getLeft(), a, prefix+"0");
            findEncoding(bt.getRight(), a, prefix+"1");
        }
    }


    private static String[] findEncoding(PairTree pt){
        // initialize String array with indices corresponding to ASCII values
        String[] result = new String[256];
        // first call from wrapper
        findEncoding(pt, result, "");
        return result;
    }

    private static void findEncoding(PairTree pt, String[] a, String prefix){
        // test is node/tree is a leaf
        if (pt.getLeft()==null && pt.getRight()==null){
            a[pt.getValue()] = prefix;
        }
        // recursive calls
        else{
            findEncoding(pt.getLeft(), a, prefix+"0");
            findEncoding(pt.getRight(), a, prefix+"1");
        }
    }
}
