package com.Runner.CQMiau.radios;

public class OldYeasu extends RadioBase {

    protected  byte CAT_FREQ_SET = (byte) 0x01; // Command byte for frequency set
    private static final byte CAT_MODE_SET = (byte) 0x07; // Command byte for mode set

    // Mode constants
    private static final byte CAT_MODE_LSB = (byte) 0x00;
    private static final byte CAT_MODE_USB = (byte) 0x01;
    private static final byte CAT_MODE_CW = (byte) 0x02;
    private static final byte CAT_MODE_CWR = (byte) 0x03;
    private static final byte CAT_MODE_AM = (byte) 0x04;
    private static final byte CAT_MODE_FM = (byte) 0x05;
    private static final byte CAT_MODE_DIG = (byte) 0x06;
    private static final byte CAT_MODE_PKT = (byte) 0x07;
    private static final byte CAT_MODE_FMN = (byte) 0x08;


    @Override
    public void setFrequency(String frequencyKHz) {
        // Convert frequency from MHz to Hz
        long frequencyHz = Math.round(Double.parseDouble(frequencyKHz) * 1_000);

        // Prepare the 5-byte command for setting frequency
        byte[] rigFreq = new byte[5];
        rigFreq[4] = CAT_FREQ_SET; // Command byte

        // Convert frequency to BCD and store in the first 4 bytes
        byte[] bcdFrequency = toBCD(frequencyHz); // Convert to 4-byte BCD
        System.arraycopy(bcdFrequency, 0, rigFreq, 0, 4); // Copy to rigFreq

        // Convert byte array to a hexadecimal string
        String commandString = bytesToHexString(rigFreq);

        // Send the command as a string
        sendAndReceiveCommand(commandString);

    }


    @Override
    public void setMode(RadioMode mode) {
        // Prepare the 5-byte command for setting mode
        byte[] rigMode = new byte[5];
        rigMode[4] = CAT_MODE_SET; // Command byte

        // Map human-friendly mode to CAT mode constants
        switch (mode) {
            case LSB:
                rigMode[0] = CAT_MODE_LSB;
                break;
            case USB:
                rigMode[0] = CAT_MODE_USB;
                break;
            case CW:
                rigMode[0] = CAT_MODE_CW;
                break;
            case CWR:
                rigMode[0] = CAT_MODE_CWR;
                break;
            case AM:
                rigMode[0] = CAT_MODE_AM;
                break;
            case FM:
                rigMode[0] = CAT_MODE_FM;
                break;
            case DIG:
                rigMode[0] = CAT_MODE_DIG;
                break;
            case PKT:
                rigMode[0] = CAT_MODE_PKT;
                break;
            case FMN:
                rigMode[0] = CAT_MODE_FMN;
                break;
            default:
                throw new IllegalArgumentException("Unsupported mode: " + mode);
        }

        // Convert byte array to a hexadecimal string
        String commandString = bytesToHexString(rigMode);

        // Send the command as a string
        sendAndReceiveCommand(commandString);
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }



}