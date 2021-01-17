package multimedia.project;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import static multimedia.project.Compression.COMPRESSED_DIR_PREF;
import static multimedia.project.Compression.COMPRESSED_FILE_PREF;
import static multimedia.project.Compression.PATH_BEGIN;

public class LZWCompression  {
    private static final String SEPARATOR = " ";

    public void compressDir(String dirPath, String targetFilePath) {
        File dir = new File(dirPath);
        List<String> list = new ArrayList<>();
        list.add(dir.getAbsolutePath());
        String baseDir = dir.getParent();
        StringBuilder compressed = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            String path = list.get(i);
            File tmp = new File(path);
            compressed.append(compress(path, baseDir));
            if (tmp.isDirectory()) {
                File[] files = tmp.listFiles();
                if (files != null) { for (File file : files) list.add(file.getAbsolutePath()); }
            }
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(targetFilePath));
            writer.write(compressed.toString());
            writer.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public String compress(String path, String baseDir) {
        String compressed = "";
        //Directory
        if (Files.isDirectory(Paths.get(path))) {
            String newDirPath = path.replace(baseDir, "");
            compressed = COMPRESSED_DIR_PREF + PATH_BEGIN + newDirPath + System.lineSeparator();
            return compressed;
        }
        //File
        try {
            File file = new File(path);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder all= new StringBuilder();
            while((line = reader.readLine()) != null) all.append(line);
            compressed = getLZWCode(all.toString());
            String newFilePath = file.getPath().replace(baseDir, "");
            compressed += PATH_BEGIN + newFilePath + System.lineSeparator();
        } catch (IOException e) { e.printStackTrace(); }
        return compressed;
    }

    public void decompress(String filePath, String destDir) {
        File file = new File(filePath);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            boolean isFile;
            while ((line = reader.readLine()) != null) {
                String result = "";
                Map<Byte, Integer> frequencies = new HashMap<>();
                if (line.substring(0, COMPRESSED_FILE_PREF.length()).equals(COMPRESSED_FILE_PREF)) isFile = true;
                else if (line.substring(0, COMPRESSED_DIR_PREF.length()).equals(COMPRESSED_DIR_PREF)) isFile = false;
                else throw new Exception("Invalid line start");
                int i = COMPRESSED_FILE_PREF.length();
                while (i < line.length()) {
                    if (line.substring(i, i + PATH_BEGIN.length()).equals(PATH_BEGIN)) { //End of file or dir
                        String path = line.substring(i + 1);
                        String newPath = destDir + path;
                        if (isFile) { Files.write(new File(newPath).toPath(), Collections.singleton(result)); } else { new File(newPath).mkdirs(); }
                        break;
                    } else {
                        List<Integer> list = new ArrayList<>();
                        while(i < line.length()) {
                            if (line.substring(i, i + SEPARATOR.length()).equals(SEPARATOR)) i += SEPARATOR.length();
                            if (line.substring(i, i + PATH_BEGIN.length()).equals(PATH_BEGIN)) break;
                            int val = readInt(line, i);
                            i += getIntLen(line, i);
                            list.add(val);
                        }
                        result = decompress(list);
                    }
                }
            }
        } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (Exception e) { e.printStackTrace(); }
    }


    private String decompress(List<Integer> list) {
        List<Byte> bytes = new ArrayList<>();
        int dictSize = 256;
        Map<Integer,String> dictionary = new HashMap<Integer,String>();
        for (int i = 0; i < 256; i++) dictionary.put(i, "" + (char)i);
        String w = "" + (char)(int)list.remove(0);
        StringBuilder result = new StringBuilder(w);
        for (int k : list) {
            String entry;
            if (dictionary.containsKey(k)) entry = dictionary.get(k);
            else if (k == dictSize) entry = w + w.charAt(0);
            else throw new IllegalArgumentException("Bad compressed k: " + k);
            result.append(entry);
            // Add w+entry[0] to the dictionary.
            dictionary.put(dictSize++, w + entry.charAt(0));
            w = entry;
        }
        return result.toString();
    }

    private String getLZWCode(String text) {
        System.out.println("result");
        StringBuilder result = new StringBuilder(COMPRESSED_FILE_PREF);
        int dictSize = 256;
        Map<String,Integer> dictionary = new HashMap<String,Integer>();
        for (int i = 0; i < 256; i++) dictionary.put("" + (char)i, i);
        String w = "";
        String searchWord = "";
        String newSearchWord = "";
        List<Integer> res = new ArrayList<>();
        
        for(int i =0 ; i < text.length(); i++) {
           for (int j = i + 1; j <= text.length(); j++) {    
                newSearchWord =text.substring(i, j);
                if (dictionary.containsKey(newSearchWord)) searchWord = newSearchWord; else break;
            }
            //Currently searchWord is the greatest Match.
            //Add Tag
            res.add(dictionary.get(searchWord));
            
            //Add new Dictionary Entry
            dictionary.put(newSearchWord, dictSize);
            dictSize++;

            //Shift pointer
            i += searchWord.length();
        }
        for (Integer re : res) { result.append(re.toString()).append(SEPARATOR); }
        return result.toString();
    }

    private int readInt(String from, int start) {
        int result = 0;
        for (int i = start; i < from.length(); i++) {
            if (from.substring(i, i + SEPARATOR.length()).equals(SEPARATOR)) break;
            result = result * 10 + (from.charAt(i) - '0');
        }
        return result;
    }

    private int getIntLen(String from, int start) {
        int len = 0;
        for (int i = start; i < from.length(); i++) {
            if (from.substring(i, i + SEPARATOR.length()).equals(SEPARATOR)) break;
            len++;
        }
        return len;
    }

    private byte[] toArray(List<Byte> list) {
        byte[] arr = new byte[list.size()];
        for (int i = 0; i < arr.length; i++) arr[i] = list.get(i);
        return arr;
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