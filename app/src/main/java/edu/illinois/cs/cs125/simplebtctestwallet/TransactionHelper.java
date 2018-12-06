package edu.illinois.cs.cs125.simplebtctestwallet;

import com.android.volley.toolbox.Volley;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.params.TestNet3Params;


public class TransactionHelper {
    public static void requestFramework() {

    }

    public static void broadCastTX(String input) {
        Transaction test = new Transaction(new TestNet3Params());
        test.bitcoinSerialize();
    }


}
