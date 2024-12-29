package com.Runner.CQMiau.radios;

import com.Runner.CQMiau.comPort.ComPortManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class RadioBase {
    protected Long Min;
    protected Long Max;
    // Method to set DX (frequency and mode)
    public void setDx(String frequency, RadioMode mode) {
        setFrequency(frequency);
        setMode(mode);
    }

    // Abstract method for setting frequency (to be implemented by child classes)
    public abstract void setFrequency(String frequencyMHz);

    // Abstract method for setting mode (to be implemented by child classes)
    public abstract void setMode(RadioMode mode);

    // Method to send and receive commands (shared by all radios)
    protected void sendAndReceiveCommand(String command) {
        ComPortManager.getInstance().queueCommand(command);
    }
     byte[] toBCD(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
        for (int i = 4 - 1; i >= 0; i--) {
            buffer.put((byte) ((value % 10) | ((value / 10 % 10) << 4)));
            value /= 100;
        }
        return buffer.array();
    }

     byte[] to_bcd_be(String value) {
        // Validate that the input is numeric
        if (!value.matches("\\d+")) {
            throw new IllegalArgumentException("Input string must be numeric: " + value);
        }

        // Convert string to long
        long numericValue = Long.parseLong(value);

        // Create a ByteBuffer to store the BCD representation
        ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
        for (int i = 4 - 1; i >= 0; i--) {
            buffer.put((byte) ((numericValue % 10) | ((numericValue / 10 % 10) << 4)));
            numericValue /= 100;
        }

        return buffer.array();
    }

    public  String toDecimal(long d, int len) {
        StringBuilder sdec = new StringBuilder();
        String sdecBe = toDecimalBe(d, len);

        // Reverse the string representation of sdecBe
        for (int i = sdecBe.length() - 1; i >= 0; i--) {
            sdec.append(sdecBe.charAt(i));
        }

        return sdec.toString();
    }

    public  long fmDecimal(String decimal, int len) {
        long d = 0;
        for (int i = 0; i < len; i++) {
            d *= 10;
            d += decimal.charAt(i) - '0';
        }
        return d;
    }



    private  String toDecimalBe(long d, int len) {
        // Convert the long `d` to a string and pad with zeros to match `len`.
        String s = Long.toString(d);
        while (s.length() < len) {
            s = "0" + s;
        }
        return s;
    }
}
