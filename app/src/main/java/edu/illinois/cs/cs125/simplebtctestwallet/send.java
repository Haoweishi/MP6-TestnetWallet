package edu.illinois.cs.cs125.simplebtctestwallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

public class send extends AppCompatActivity {

    private int currentBalance;
    private String privkey;
    private String address;
    private RequestQueue queue;

    public void onCreate(Bundle savedinstance) {
        super.onCreate(savedinstance);
        setContentView(R.layout.activity_send);
        SharedPreferences appdata = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        privkey = appdata.getString("PRIVKEY", null);
        address = appdata.getString("ADDR", null);
        queue = Volley.newRequestQueue(this.getApplicationContext());
        startBalanceRequest(address);
        String presetAddress = getIntent().getStringExtra("SCAN_ADDRESS");
        if (presetAddress != null) {
            if (presetAddress.contains("bitcoin")) {
                presetAddress = presetAddress.substring(8, presetAddress.length());
            }
            EditText adressbar = findViewById(R.id.editText_sendAddress);
            adressbar.setText(presetAddress);
        }
    }

    public void startBalanceRequest(String address) {
        if (address != null) {
            String fullendpoint = getString(R.string.baseendpoint) + "addrs/" + address + "/balance";
            JsonObjectRequest requester = new JsonObjectRequest(Request.Method.GET, fullendpoint, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    parseBalResult(response);
                }
            }
                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse.statusCode == 400) {
                        Toast.makeText(getApplicationContext(), "Insufficient balance.", Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(getApplicationContext(), "Network error.", Toast.LENGTH_LONG).show();
                    Log.e("Error getting balance", error.toString());
                }
            });
            queue.add(requester);
        } else {
            Intent goback = new Intent(this, MainActivity.class);
            startActivity(goback);
        }
    }

    public void parseBalResult(JSONObject input) {
        try {
            int bal = input.getInt("final_balance");
            currentBalance = bal;
            TextView balfield = findViewById(R.id.amountavail);
            String displayed = String.format("%.9f", currentBalance / 100000000.0);
            balfield.setText(displayed);
        } catch (JSONException je) {
            Log.e("JSON PARSE FAIL", je.toString());
        }
    }

    public void onSendClick(View view) {
        int fee = 50000;
        EditText addressform = findViewById(R.id.editText_sendAddress);
        EditText amount =  findViewById(R.id.amount);

        String outputaddress = addressform.getText().toString();
        int outputSatoshi = 0;
        try {
            outputSatoshi = (int) (Double.parseDouble(amount.getText().toString()) * 100000000);
        } catch (NumberFormatException e) {
            Toast.makeText(this.getApplicationContext(), "Please enter a number", Toast.LENGTH_LONG).show();
        }
        if (outputSatoshi < 600) {
            Toast.makeText(this.getApplicationContext(), "You cannot send that little!", Toast.LENGTH_LONG).show();
            return;
        }
        if (outputSatoshi > currentBalance - fee) {
            Toast.makeText(this.getApplicationContext(), "Insufficient Balance after fee! Fee: 0.0005 BTC", Toast.LENGTH_LONG).show();
            return;
        }
        TransactionHelper maker = new TransactionHelper();
        try {
            maker.addOutPut(outputaddress, outputSatoshi);
            maker.addInput(privkey);
            if (currentBalance - outputSatoshi > fee) {
                maker.addOutPut(address, currentBalance - outputSatoshi - fee);
            }
            getFullTransaction(maker.requestFrameworkJSON());
        } catch (AddressFormatException ae) {
            Toast.makeText(this.getApplicationContext(), "Invalid destination address!", Toast.LENGTH_LONG).show();
        }
    }

    private void getFullTransaction(JSONObject input) {
        String baseendpoint = getString(R.string.baseendpoint) + "txs/new";
        Log.d("Input", input.toString());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, baseendpoint, input, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                buildFinalTransaction(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.data != null) {
                    try {
                        String str = new String(error.networkResponse.data, "UTF-8");
                        Log.e("Response error", str);
                    } catch (UnsupportedEncodingException e) {

                    }
                }
                displayNetworkError();
            }
        });
        queue.add(req);
    }

    private void buildFinalTransaction(JSONObject input) {
        try {
            JSONArray inputarray = input.getJSONArray("tosign");
            JSONArray signatures = new JSONArray();
            JSONArray pubkeys = new JSONArray();
            for (int i = 0; i < inputarray.length(); i++) {
                Sha256Hash sha256Hash = Sha256Hash.wrap(inputarray.getString(i));
                ECKey priv = ECKey.fromPrivate(new BigInteger(privkey, 16));
                byte[] signed = priv.sign(sha256Hash).encodeToDER();
                String signedstr = "";
                for (byte x : signed) {
                    String temp = String.format("%02X", x);
                    signedstr += temp;
                }
                signatures.put(signedstr);
                pubkeys.put(priv.getPublicKeyAsHex());
            }
            input.put("signatures", signatures);
            input.put("pubkeys", pubkeys);
            Log.d("Raw", input.toString());
            JsonObjectRequest pushtx = new JsonObjectRequest(Request.Method.POST, "https://api.blockcypher.com/v1/btc/test3/txs/send?token=73b4a53195c64289b64406f5f2f2ab9b", input,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            goHome();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    displayNetworkError();
                    Log.e("Error", error.networkResponse.toString());
                }
            });
            queue.add(pushtx);
        } catch (JSONException je) {
            Log.e("JSON error", je.toString());
        }
    }

    public void displayNetworkError() {
        Toast.makeText(this.getApplicationContext(), "Network error", Toast.LENGTH_LONG).show();
    }

    public void goHome() {
        Toast.makeText(this.getApplicationContext(), "Transaction sent! fee 0.0005", Toast.LENGTH_LONG).show();
        Intent returntohome = new Intent(this, SendOrReceive.class);
        startActivity(returntohome);
    }

    public void startQRScanner(View view) {
        Intent start = new Intent(this, QRActivity.class);
        startActivity(start);
    }
}
