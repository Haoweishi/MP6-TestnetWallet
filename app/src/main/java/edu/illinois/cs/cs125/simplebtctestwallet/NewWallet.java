package edu.illinois.cs.cs125.simplebtctestwallet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NewWallet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wallet);
        requestPrivKey();
    }

    public void requestPrivKey() {
        try {
            URL blockcypher = new URL("https://api.blockcypher.com/v1/btc/test3/addrs");
            HttpsURLConnection getPrivKey = (HttpsURLConnection) blockcypher.openConnection();
            getPrivKey.setConnectTimeout(10000);
            getPrivKey.setReadTimeout(9000);
            getPrivKey.setRequestMethod("POST");
            getPrivKey.setDoInput(true);
            getPrivKey.setDoOutput(true);
            getPrivKey.connect();
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }
}
