package edu.illinois.cs.cs125.simplebtctestwallet;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.bitcoinj.core.AddressFormatException;
import org.json.JSONException;
import org.json.JSONObject;

public class NewWallet extends AppCompatActivity {

    private TextView dynamicPrivKeyLabel;
    private RequestQueue JSONRequestQueue;
    private String privkey;
    private String PREF_NAME = "USER_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wallet);
        JSONRequestQueue = Volley.newRequestQueue(this);
        dynamicPrivKeyLabel = findViewById(R.id.walletprivkey);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPrivKey();
    }


    public void requestPrivKey() {
        String newKeyWIF = PrivKeyHelper.generateKeyLocally();
        dynamicPrivKeyLabel.setText(newKeyWIF);
        privkey = PrivKeyHelper.wifToPrivKey(newKeyWIF);
    }

    public void acknowledge(View view) {
        try {
            SharedPreferences.Editor sharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
            sharedPref.putString("PRIVKEY", privkey);
            String address = PrivKeyHelper.makeAddress(privkey);
            sharedPref.putString("ADDR", address);
            sharedPref.apply();
        } catch (AddressFormatException error) {
            Log.e("Invalid addr", error.toString());
        }
    }
}
