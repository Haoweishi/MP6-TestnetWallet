package edu.illinois.cs.cs125.simplebtctestwallet;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;

import java.math.BigInteger;

public class PrivKeyHelper {
    public static String wifToPrivKey(String inputWIF) throws AddressFormatException {
        BigInteger dec = Base58.decodeToBigInteger(inputWIF);
        String hexstr = dec.toString(16);
        hexstr = hexstr.substring(0, hexstr.length() - 10);
        hexstr = hexstr.substring(2, hexstr.length());
        return hexstr.toUpperCase();
    }

    public static String makeAddress(String inputPrivKey) throws AddressFormatException {
        ECKey active = ECKey.fromPrivate(new BigInteger(inputPrivKey, 16));
        byte[] publickeyhash = active.getPubKeyHash();
        NetworkParameters testnetenvironment = new TestNet3Params();
        Address addr = new Address(testnetenvironment ,publickeyhash);
        return addr.toBase58();
    }

    public static String generateKeyLocally() {
        ECKey newKeyPair = new ECKey();
        return newKeyPair.getPrivateKeyAsWiF(new TestNet3Params());
    }
}
