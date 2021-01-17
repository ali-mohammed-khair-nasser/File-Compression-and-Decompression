package multimedia.project;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class Compression {
    public static final long DIRECTORY_ENCODING_LEN = Long.MAX_VALUE;
    public static final String COMPRESSED_FILE_PREF = "F";
    public static final String COMPRESSED_DIR_PREF = "D";
    public static final String PATH_BEGIN = "$";
    public abstract void compressDir(String dirPath, String targetFilePath);
    public abstract int[] compress(String path, String baseDir);
    public abstract void decompress(String filePath, String destFolder);
    public double getCompressionRatio(File in, File compressed) {
        List<String> list = new ArrayList<>();
        list.add(in.getPath());
        long size = 0;
        for (int i = 0; i < list.size(); i++) {
            String path = list.get(i);
            File tmp = new File(path);
            if (tmp.isDirectory()) {
                File[] files = tmp.listFiles();
                if (files != null) { for (File file : files) list.add(file.getAbsolutePath()); }
            } else { size += tmp.length(); }
        }
        return (size - compressed.length()) * 1.0 / size * 100.0;
    }

    public int getFilesCount(String dirPath) {
        File dir = new File(dirPath);
        List<String> list = new ArrayList<>();
        list.add(dir.getAbsolutePath());
        String baseDir = dir.getParent();
        StringBuilder compressed = new StringBuilder();
        for(int i = 0; i < list.size(); i++){
            String path = list.get(i);
            File tmp = new File(path);
            if (tmp.isDirectory()) {
                File[] files = tmp.listFiles();
                if (files != null) { for(File file: files) list.add(file.getAbsolutePath()); }
            }
        }
        return list.size();
    }

    public static int[] getBytes(long value) {
        int[] bytes = new int[8];
        int power = 64 - 8;
        for(int i = 0; i < 8; i++) {
            int tmp = (int)(value & 255);
            bytes[i] = tmp;
            value = value >> 8;
        }
        return bytes;
    }

    public static int[] getBytes(int value) {
        int[] bytes = new int[4];
        int power = 32 - 8;
        for(int i = 0; i < 4; i++) {
            int tmp = (int)(value & 255);
            bytes[i] = tmp;
            value = value >> 8;
        }
        return bytes;
    }

    public static int[] getBytes(String value) {
        int[] bytes = new int[value.length()];
        for(int i = 0; i < value.length(); i++) { bytes[i] = value.charAt(i); }
        return bytes;
    }


    public static long getLong(int[] bytes) {
        long result = 0;
        long power = 0;
        for(int i = 0; i < Math.min(bytes.length, 8); i++) {
            long b = bytes[i];
            result += b << power;
            power += 8;
        }
        return result;
    }

    public static int getInt(int[] bytes) {
        int result = 0;
        long power = 0;
        for(int i = 0; i < Math.min(bytes.length, 4); i++) {
            int b = bytes[i];
            result += b << power;
            power += 8;
        }
        return result;
    }

    public static String getString(int[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (int aByte : bytes) { builder.append(aByte); }
        return builder.toString();
    }

    public static int[] merge(int[] ... params) {
        int count = 0;
        for(int i = 0; i < params.length; i++) { count += params[i].length; }
        int[] result = new int[count];
        count = 0;
        for(int i = 0; i < params.length; i++) { for(int j = 0; j < params[i].length; j++) { result[count++] = params[i][j]; } }
        return result;
    }

    public static int[] bitsStringToBytes(String s) {
        int bytesCount = (s.length() + 7) / 8;
        int[] result = new int[bytesCount];
        int value = 0;
        int index = 0;
        int bitsCount = 0;
        for(int i = 0; i < s.length(); i++) {
            value = value << 1;
            if (s.charAt(i) == '1') value |= 1;
            bitsCount++;
            if (bitsCount == 8) {
                result[index++] = value;
                value = 0;
                bitsCount = 0;
            }
        }
        if (bitsCount != 0) {
            value = value << (8 - bitsCount);
            result[index++] = value;
        }
        return result;
    }
}