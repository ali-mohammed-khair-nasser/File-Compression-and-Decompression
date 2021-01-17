package multimedia.project;
import java.io.IOException;
import java.io.InputStream;

public final class BitInputStream {
    // Underlying byte stream to read from.
    private InputStream input;

    // Either in the range 0x00 to 0xFF if bits are available, or is -1 if the end of stream is reached.
    private int nextBits;

    // Always between 0 and 7, inclusive.
    private int numBitsRemaining;
    private boolean isEndOfStream;

    // Creates a bit input stream based on the given byte input stream.
    public BitInputStream(InputStream in) {
        if (in == null) throw new NullPointerException("Argument is null");
        input = in;
        numBitsRemaining = 0;
        isEndOfStream = false;
    }

    public int readByte() {
        int bitBuffer = 0;
        int c = 0;
        for(int i = 0; i<8;i++) {
                try { c = readBit(); } catch (IOException e) { e.printStackTrace(); }
                bitBuffer |= c;
                if(i!=7) bitBuffer <<= 1;
        }
        return bitBuffer;
    }

    // Reads a bit from the stream. Returns 0 or 1 if a bit is available, or -1 if the end of stream is reached. The end of stream always occurs on a byte boundary.
    public int readBit() throws IOException {
        if (isEndOfStream) return -1;
        if (numBitsRemaining == 0) {
                nextBits = input.read();
                if (nextBits == -1) {
                        isEndOfStream = true;
                        return -1;
                }
                numBitsRemaining = 8;
        }
        numBitsRemaining--;
        return (nextBits >>> numBitsRemaining) & 1;
    }

    public int readInt() throws IOException {
        int value1 = input.read();
        int value2 = input.read();
        int value3 = input.read();
        int value4 = input.read();
        if(value1 == -1) value1 = 0;
        if (value2 == -1) value2 = 0;
        if (value3 == -1) value3 = 0;
        if (value4 == -1) value4 = 0;
        return value1 + (value2 << 8) + (value3 << 16) + (value4 << 24);
    }

    public long readLong() throws IOException {
        long result = 0;
        long power = 0;
        for(int i = 0; i < 8; i++) {
                long b = input.read();
                if (b == -1 && i == 0) return Long.MIN_VALUE;
                if (b != -1) result += b << power;
                power += 8;
        }
        return result;
    }

    // Closes this stream and the underlying InputStream.
    public void close() throws IOException { input.close(); }
}