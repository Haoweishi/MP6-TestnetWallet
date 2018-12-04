package edu.illinois.cs.cs125.simplebtctestwallet;

import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;

import java.math.BigInteger;

public class PrivKeyHelper {
    public static String wifToPrivKey(String inputWIF) throws AddressFormatException {
        BigInteger dec = Base58.decodeToBigInteger(inputWIF);
        String hexstr = dec.toString(16);
        hexstr = hexstr.substring(0, hexstr.length() - 8);
        hexstr = hexstr.substring(2, hexstr.length());
        return hexstr.toUpperCase();
    }
}
