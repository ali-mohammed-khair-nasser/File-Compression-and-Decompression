package multimedia.project;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ShannonFanoCompression extends Compression {
    private static final String SEPARATOR = "#";
    private static final String START_ENCODING_FLAG = "(";
    private HashMap<Integer, String> encoding;

    public void compressSingleFile(String filePath, String destFilePath) {
        int[] bytes = compress(filePath, (new File(filePath).getParent()));
        BitByteOutputStream outputStream = null;
        try { outputStream = new BitByteOutputStream(new FileOutputStream(destFilePath)); } catch (FileNotFoundException e) { e.printStackTrace(); }
        outputStream.writeBytes(bytes);
        outputStream.close();
    }

    @Override
    public void compressDir(String dirPath, String targetFilePath) {
        File dir = new File(dirPath);
        List<String> list = new ArrayList<>();
        list.add(dir.getAbsolutePath());
        BitByteOutputStream outputStream = null;
        try { outputStream = new BitByteOutputStream(new FileOutputStream(targetFilePath)); } catch (FileNotFoundException e) { e.printStackTrace(); }
        String baseDir = dir.getParent();
        
        for (int i = 0; i < list.size(); i++) {
            String path = list.get(i);
            File tmp = new File(path);
            int[] bytes = compress(path, baseDir);
            outputStream.writeBytes(bytes);
            if (tmp.isDirectory()) {
                File[] files = tmp.listFiles();
                if (files != null) { for (File file : files) list.add(file.getAbsolutePath()); }
            }
        }
        outputStream.close();
    }

    @Override
    public int[] compress(String path, String baseDir) {
        int[] result = new int[0];
        String compressed = "";
        
        //Directory
        if (Files.isDirectory(Paths.get(path))) {
            String newDirPath = path.replace(baseDir, "");
            int[] bytes = merge(getBytes(Long.MAX_VALUE), getBytes(newDirPath.length()), getBytes(newDirPath));
            return bytes;
        }

        //File
        try {
            File file = new File(path);
            byte[]bytes = Files.readAllBytes(Paths.get(path));
            int[]coding = getShannonCode(bytes);
            String newFilePath = file.getPath().replace(baseDir, "");
            result = merge(coding, getBytes(newFilePath.length()), getBytes(newFilePath));
        } catch (IOException e) { e.printStackTrace(); }
        return result;
    }

    @Override
    public void decompress(String filePath, String destDir) {
        try {
            BitInputStream inputStream = new BitInputStream(new FileInputStream(filePath));

            while (true) {
                Map<Integer, Long> frequencies = new HashMap<>();
                long encodingSize = inputStream.readLong();
                if (encodingSize == Long.MIN_VALUE) break;
                if (encodingSize == DIRECTORY_ENCODING_LEN) {
                    int pathLen = inputStream.readInt();
                    StringBuilder path = new StringBuilder();
                    for (int i = 0; i < pathLen; i++) {
                        int b = inputStream.readByte();
                        path.append((char) (b));
                    }
                    String newPath = destDir + path.toString();
                    new File(newPath).mkdirs();
                    continue;
                }

                //File
                int frequencyPairsCount = inputStream.readInt();
                for (int i = 0; i < frequencyPairsCount; i++) {
                    long freq = inputStream.readLong();
                    int val = inputStream.readInt();
                    frequencies.put(val, freq);
                }

                buildShannonTree(frequencies);
                StringBuilder stringBuilder = new StringBuilder();
                
                for (long i = 0; i < encodingSize; i++) {
                    int bit = inputStream.readBit();
                    stringBuilder.append((char) (bit + '0'));
                }
                
                List<Integer> bytesList = new ArrayList<>();
                for (long i = encodingSize; i % 8 != 0; i++) inputStream.readBit();
                String s = stringBuilder.toString();
                int j = 0;
                
                while (j < s.length()) {
                    for (Map.Entry<Integer, String> entry : encoding.entrySet()) {
                        String encoding = entry.getValue();
                        if (j + encoding.length() <= s.length() && s.substring(j, j + encoding.length()).equals(encoding)) {
                            bytesList.add((entry.getKey()));
                            j += encoding.length();
                            break;
                        }
                    }
                }

                int pathLen = inputStream.readInt();
                StringBuilder path = new StringBuilder();
                for (int i = 0; i < pathLen; i++) path.append((char) (inputStream.readByte()));
                String newPath = destDir + path;
                Files.write(Paths.get(newPath), toArray(bytesList));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private int[] getShannonCode(byte[] bytes) {
        int[] intBytes = new int[bytes.length];
        for(int i = 0; i < intBytes.length; i++) intBytes[i] = bytes[i];
        buildShannonTree(getProbabilities(intBytes));
        int[] old = new int[0];
        Map<Integer, Long> probs = getProbabilities(intBytes);
        for (Map.Entry<Integer, Long> entry : probs.entrySet()) { old = merge(old, getBytes(entry.getValue()), getBytes(entry.getKey())); }
        old = merge(getBytes(probs.size()), old);
        StringBuilder s = new StringBuilder();
        for (int b : bytes) { s.append(encoding.get(b)); }
        int[] coding = bitsStringToBytes(s.toString());
        int[] result = merge(getBytes((long) s.length()), old, coding);
        return result;
    }

    private void buildShannonTree(Map<Integer, Long> probs) {
        if (probs.size() == 0) return;
        List<Node> nodes = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();
        int total = 0;
        for (Map.Entry<Integer, Long> entry : probs.entrySet()) {
            nodes.add(new Node(entry.getKey(), entry.getValue()));
            total += entry.getValue();
        }
        Node root = new Node(-1, total);
        root.nodes = nodes;
        queue.add(root);
        if (nodes.size() == 1) { root = nodes.get(0); }

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            nodes = current.nodes;
            if (current.nodes == null || current.nodes.size() == 1) continue;
            int sum = 0;
            int to = 0;
            
            for (int i = 0; i < current.nodes.size(); i++) {
                if (sum + current.nodes.get(i).prob > current.prob / 2.0) { to = i; break; }
                sum += current.nodes.get(i).prob;
            }
            
            if (to == 0) to = 1;
            List<Node> firstPart = new ArrayList<>();
            List<Node> secondPart = new ArrayList<>();
            int firstProb = 0, secondProb = 0;
            for (int i = 0; i < to; i++) {
                firstPart.add(current.nodes.get(i));
                firstProb += current.nodes.get(i).prob;
            }
            for (int i = to; i < current.nodes.size(); i++) {
                secondPart.add(current.nodes.get(i));
                secondProb += current.nodes.get(i).prob;
            }

            Node left = new Node(-1, firstProb);
            left.nodes = firstPart;
            if (firstPart.size() == 1) left = firstPart.get(0);
            queue.add(left);
            current.left = left;

            Node right = new Node(-1, secondProb);
            right.nodes = secondPart;
            if (secondPart.size() == 1) right = secondPart.get(0);
            queue.add(right);
            current.right = right;
        }
        encoding = new HashMap<>();
        traverse(root, root.left == null ? "0" : "");
    }

    private Map<Integer, Long> getProbabilities(int[] bytes) {
        int total = bytes.length;
        Map<Integer, Long> frequencies = new HashMap<>();
        Map<Integer, Long> probs = new HashMap<>();
        for (int b : bytes) { frequencies.put(b, frequencies.getOrDefault(b, 0L) + 1); }
        for (Map.Entry<Integer, Long> entry : frequencies.entrySet()) { probs.put(entry.getKey(), (long)(entry.getValue() * 1.0 / total * 1000)); }
        return probs;
    }

    //Store the encoding for each byte
    private void traverse(Node node, String pathCoding) {
        if (node == null) return;
        traverse(node.left, pathCoding + "0");
        traverse(node.right, pathCoding + "1");
        if (node.nodes == null) { encoding.put(node.value, pathCoding); }
    }

    private int readInt(String from, int start) {
        int result = 0;
        int sign = +1;
        for(int i = start; i < from.length(); i++) {
            if (from.substring(i, i + SEPARATOR.length()).equals(SEPARATOR)) break;
            if (from.charAt(i) == '-') sign = -1; else result = result * 10 + (from.charAt(i) - '0');
        }
        return result * sign;
    }

    private int getIntLen(String from, int start) {
        int len = 0;
        for(int i = start; i < from.length(); i++) {
            if (from.substring(i, i + SEPARATOR.length()).equals(SEPARATOR)) break;
            len++;
        }
        return len;
    }

    private byte[] toArray(List<Integer> list) {
        byte[] arr = new byte[list.size()];
        for (int i = 0; i < arr.length; i++) arr[i] = Byte.parseByte(list.get(i) + "");
        return arr;
    }

    class Node implements Comparable {
        Node left, right;
        List<Node> nodes;
        int value;
        long prob;

        Node(int value, long prob) {
            this.value = value;
            this.prob = prob;
        }

        @Override
        public int compareTo(Object o) {
            Node n = (Node) o;
            if (prob < n.prob) return -1;
            else if (prob > n.prob) return +1;
            return 0;
        }
    }
    
    public double getCompressionRatio(String in, String compressed) {
        List<String> list = new ArrayList<>();
        File f=new File(in);
        list.add(in);
        long size = 0;
        for (int i = 0; i < list.size(); i++) {
            String path = list.get(i);
            File tmp = new File(path);
            if (tmp.isDirectory()) {
                File[] files = tmp.listFiles();
                if (files != null) { for (File file : files) list.add(file.getAbsolutePath()); }
            } else { size += tmp.length(); }
        }
        File com=new File(compressed);
        return (size - com.length()) * 1.0 / size * 100.0;
    }
}