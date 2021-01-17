package multimedia.project;
import java.io.IOException;
import java.io.OutputStream;

public class BitByteOutputStream {

    // Buffer of bits to write to output.
    private int bitBuffer = 0;
    private int bitsLeftInBuffer = 0;

    // Stream to write bits to.
    private OutputStream out;

    public BitByteOutputStream(OutputStream out) {
        if (out == null) { throw new NullPointerException("Output stream is invalid."); }
        this.out = out;
    }

    public void writeBit(boolean bit) {
        // Add bit to buffer.
        bitBuffer <<= 1;
        if (bit) bitBuffer |= 1;

        // Keep track of how many bits in buffer.
        bitsLeftInBuffer++;

        // If buffer full, flush.
        if (bitsLeftInBuffer == 8) flush();
    }

    public void writeByte(int x) {
        if (x < 0 || x >= 256) { throw new IllegalArgumentException("Value not in range."); }

        if (bitsLeftInBuffer == 0) {
            // If buffer empty just write x to out.
            try {
                out.write(x);
            } catch (IOException e) {
                System.err.println("Write Error");
                e.printStackTrace();
            }
            return;
        }

        for (int i = 0; i < 8; i++) {
            boolean bit = ((x >>> (8 - i - 1)) & 1) == 1;
            writeBit(bit);
        }
    }

    public void flush() {
        if (bitsLeftInBuffer == 0) { return; }
        if (bitsLeftInBuffer > 0) { bitBuffer <<= (8 - bitsLeftInBuffer); }
        
        try {
            out.write(bitBuffer);
        } catch (IOException e) {
            System.err.println("Write Error");
            e.printStackTrace();
        }
        // Reset buffer.
        bitsLeftInBuffer = 0;
        bitBuffer = 0;
    }

    public void close() {
        try {
            out.close();
        } catch (IOException e) {
            System.err.println("Error closing output stream.");
            e.printStackTrace();
        }
    }

    public void writeInt(int value) {
        // If buffer empty just write x to out.
        try {
            out.write(value);
            out.write(value >> 8);
            out.write(value >> 16);
            out.write(value >> 24);
        } catch (IOException e) {
            System.err.println("Write Error");
            e.printStackTrace();
        }
    }

    public void writeLong(long value) {
        // If buffer empty just write x to out.
        try {
            int power = 64 - 8;
            for (int i = 0; i < 8; i++) {
                int tmp = (int) (value & 255);
                out.write(tmp);
                value = value >> 8;
            }
        } catch (IOException e) {
            System.err.println("Write Error");
            e.printStackTrace();
        }
    }

    public void writeBytes(int[] bytes) { for (int b : bytes) { writeByte(b); } }
}
