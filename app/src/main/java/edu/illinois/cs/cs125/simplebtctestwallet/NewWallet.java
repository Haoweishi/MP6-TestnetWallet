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

    public void parseResult(JSONObject response) {
        try {
            dynamicPrivKeyLabel.setText(response.getString("wif"));
            dynamicPrivKeyLabel.setTextIsSelectable(true);
            privkey = PrivKeyHelper.wifToPrivKey(response.getString("wif"));
            Log.d("Reference key", response.getString("private"));
            Log.d("Reference address", response.getString("address"));
        } catch (JSONException e) {
            Log.e("JSON ERROR", e.toString());
            dynamicPrivKeyLabel.setText("Cannot parse response!");
        }
    }

    public void requestPrivKey() {
        /*try {
            String endpoint = getString(R.string.baseendpoint) + "addrs";
            JsonObjectRequest requester = new JsonObjectRequest(
                    Request.Method.POST,
                    endpoint,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            parseResult(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error:", error.toString());
                            dynamicPrivKeyLabel.setText("Key generation server error.");
                        }
                    });
            requester.setShouldCache(false);
            JSONRequestQueue.add(requester);
        } catch (Exception error) {
            Log.d("Error:", error.toString());
            dynamicPrivKeyLabel.setText("Key generation server error.");
        }*/
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
