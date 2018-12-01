package edu.illinois.cs.cs125.simplebtctestwallet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class NewWallet extends AppCompatActivity {

    private TextView dynamicPrivKeyLabel;
    private RequestQueue JSONRequestQueue;

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
        } catch (JSONException e) {
            Log.e("JSON ERROR", e.toString());
            dynamicPrivKeyLabel.setText("Cannot parse response!");
        }
    }

    public void requestPrivKey() {
        try {
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
        }
    }
}
