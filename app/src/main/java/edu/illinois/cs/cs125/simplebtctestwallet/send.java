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

import org.json.JSONException;
import org.json.JSONObject;

public class send extends AppCompatActivity {

    private int currentBalance;
    private String privkey;
    private String address;

    public void onCreate(Bundle savedinstance) {
        super.onCreate(savedinstance);
        setContentView(R.layout.activity_send);
        SharedPreferences appdata = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        privkey = appdata.getString("PRIVKEY", null);
        address = appdata.getString("ADDR", null);
        startBalanceRequest(address);
    }

    public void startBalanceRequest(String address) {
        RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
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
            String displayed = Double.toString(currentBalance / 100000000.0);
            balfield.setText(displayed);
        } catch (JSONException je) {
            Log.e("JSON PARSE FAIL", je.toString());
        }
    }

    public void onSendClick(View view) {
        EditText addressform = findViewById(R.id.editText_sendAddress);
        EditText amount =  findViewById(R.id.amount);

        String outputaddress = addressform.getText().toString();
        int outputSatoshi = (int) (Double.parseDouble(amount.getText().toString()) * 100000000);
        if (outputSatoshi < 600) {
            Toast.makeText(this.getApplicationContext(), "You cannot send that little!", Toast.LENGTH_LONG).show();
            return;
        }
        if (outputSatoshi > currentBalance - 10000) {
            Toast.makeText(this.getApplicationContext(), "Insufficient Balance!", Toast.LENGTH_LONG).show();
            return;
        }
        TransactionHelper maker = new TransactionHelper();
        maker.addOutPut(outputaddress, outputSatoshi);
        maker.addInput(privkey);
        if (currentBalance - outputSatoshi > 10000) {
            maker.addOutPut(address, currentBalance - outputSatoshi - 10000);
        }
        Log.d("Test req string", maker.requestFrameworkJSON());
        maker.requestTransactionDetails();
    }
}
