package com.binengg.nfctest;

import android.content.Intent;
import android.nfc.NdefRecord;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by pushpak on 11/17/14.
 */
public class Nfcservice extends HostApduService {

    private static final String TAG = "Nfc Service";


    private static final byte[] APDU_SELECT = {
            (byte)0x00, // CLA	- Class - Class of instruction
            (byte)0xA4, // INS	- Instruction - Instruction code
            (byte)0x04, // P1	- Parameter 1 - Instruction parameter 1
            (byte)0x00, // P2	- Parameter 2 - Instruction parameter 2
            (byte)0x07, // Lc field	- Number of bytes present in the data field of the command
            (byte)0xF0, (byte)0x39, (byte)0x41, (byte)0x48, (byte)0x14, (byte)0x81, (byte)0x00, // NDEF Tag Application name
            (byte)0x00  // Le field	- Maximum number of bytes expected in the data field of the response to the command
    };

    private static final byte[] CODE_REQUEST = {
            (byte)0x00,
            (byte)0xa4,
            (byte)0x00,
            (byte)0x0c,
            (byte)0x02,
            (byte)0xe1, (byte)0x03
    };

    private static final byte[] CODE = {
            (byte)0x00,
            (byte)0xa5,
            (byte)0xb6,
            (byte)0x0e
    };

    private static final byte[] NDEF_ID = {
            (byte)0xE1,
            (byte)0x04
    };

    private static final byte[] CONFIRM = {
            (byte)0x90,
            (byte)0x00
    };

    private NdefRecord NDEF_URI = new NdefRecord(
            NdefRecord.TNF_WELL_KNOWN,
            NdefRecord.RTD_TEXT,
            NDEF_ID,
            "Hello world!".getBytes(Charset.forName("UTF-8"))
    );

    private byte[] NDEF_URI_BYTES = NDEF_URI.toByteArray();
    private byte[] NDEF_URI_LEN = BigInteger.valueOf(NDEF_URI_BYTES.length).toByteArray();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra("Message")) {
            NDEF_URI = new NdefRecord(
                    NdefRecord.TNF_WELL_KNOWN,
                    NdefRecord.RTD_TEXT,
                    NDEF_ID,
                    intent.getStringExtra("Message").getBytes(Charset.forName("UTF-8"))
            );

            NDEF_URI_BYTES = NDEF_URI.toByteArray();
            NDEF_URI_LEN = BigInteger.valueOf(NDEF_URI_BYTES.length).toByteArray();
        }

        Log.d(TAG,"Service sterted with message " + intent.getStringExtra("Message").toString() + " Data: " + NDEF_URI.toString());

        return 0;
    }

    @Override
    public byte[] processCommandApdu(byte[] nfcdata, Bundle bundle) {

        Log.d(TAG, "processCommandApdu() | incoming commandApdu: " + nfcdata.toString());

        if (Arrays.equals(APDU_SELECT, nfcdata)) {
            return CONFIRM;
        }

        if (Arrays.equals(CODE_REQUEST, nfcdata)) {
            return CODE;
        }

        return new byte[0];
    }

    @Override
    public void onDeactivated(int i) {

    }
}
