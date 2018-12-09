package edu.illinois.cs.cs125.simplebtctestwallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

public class SendOrReceive extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_or_receive);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBalanceRequest();
        SharedPreferences appdata = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String privkey = appdata.getString("PRIVKEY", null);
        String address = appdata.getString("ADDR", null);
    }

    public void parseResult(JSONObject result) {
        try {
            long balanceinsatoshi = result.getLong("balance");
            double balanceinbtc = balanceinsatoshi / 100000000.0;
            TextView displaybar = findViewById(R.id.balanceview);
            String displayed = String.format("%.9f", balanceinsatoshi / 100000000.0);
            displaybar.setText("Balance: " + displayed + " BTC (TESTNET)");
            Log.d("Balance query","Balance: " + balanceinbtc + " BTC (TESTNET)");
        } catch (JSONException je) {
            Log.e("Error Parsing balance", je.toString());
            Toast.makeText(getApplicationContext(), "Invalid response from server!", Toast.LENGTH_LONG).show();
        }
    }

    public void startBalanceRequest() {
        SharedPreferences userdata = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String address = userdata.getString("ADDR", null);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        if (address != null) {
            String fullendpoint = getString(R.string.baseendpoint) + "addrs/" + address + "/balance";
            JsonObjectRequest requester = new JsonObjectRequest(Request.Method.GET, fullendpoint, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    parseResult(response);
                }
            }
            , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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

    public void onRefreshButtonClick(View view) {
        startBalanceRequest();
    }

    public void openHistory(View view) {
        String base = "https://live.blockcypher.com/btc-testnet/address/";
        SharedPreferences appdata = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String address = appdata.getString("ADDR", null);
        String uri = base + address;
        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(browser);
    }

    public void gotoSendPage(View view) {
        Intent sendpage = new Intent(this, send.class);
        startActivity(sendpage);
    }
}
