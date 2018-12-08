package edu.illinois.cs.cs125.simplebtctestwallet;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.params.TestNet3Params;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

public class TransactionHelper extends AppCompatActivity {

    private Transaction currentTransaction;
    private List<ECKey> pendingInputs;
    private Map<Address, Integer> pendingOutputs;
    private JSONObject frame;

    public TransactionHelper() {
        currentTransaction = new Transaction(new TestNet3Params());
        pendingInputs = new LinkedList<>();
        pendingOutputs = new HashMap<>();
    }

    public void addInput(String PrivKeyHex) {
        ECKey privKey = ECKey.fromPrivate(new BigInteger(PrivKeyHex, 16));
        pendingInputs.add(privKey);
    }

    public void addOutPut(String outputAddress, int amountInSat) throws AddressFormatException {
        Address output = Address.fromBase58(new TestNet3Params(), outputAddress);
        pendingOutputs.put(output, amountInSat);
    }

    public String requestFrameworkJSON() {
        JSONObject requestParams = new JSONObject();
        if (pendingInputs.size() == 0 || pendingOutputs.size() == 0) {
            return null;
        }
        try {
            JSONArray inputArray = new JSONArray();
            for (int i = 0; i < pendingInputs.size(); i++) {
                JSONObject inputAddressSequence = new JSONObject();
                JSONArray singleAddress = new JSONArray();
                singleAddress.put(new Address(new TestNet3Params(), pendingInputs.get(i).getPubKeyHash()).toBase58());
                inputAddressSequence.put("addresses", singleAddress);
                inputArray.put(inputAddressSequence);
            }
            requestParams.put("inputs", inputArray);
            JSONArray outputArray = new JSONArray();
            Iterator outputs = pendingOutputs.entrySet().iterator();
            while (outputs.hasNext()) {
                JSONObject outputAddressSequence = new JSONObject();
                Map.Entry outputvalue = (Map.Entry) outputs.next();
                Address address = (Address) outputvalue.getKey();
                String addrstring = address.toBase58();
                int outputsat = (int) outputvalue.getValue();
                JSONArray singleAddress = new JSONArray();
                singleAddress.put(addrstring);
                outputAddressSequence.put("addresses", singleAddress);
                outputAddressSequence.put("value", outputsat);
                outputArray.put(outputAddressSequence);
                outputs.remove();
            }
            requestParams.put("outputs", outputArray);
        } catch (JSONException e) {
            Log.e("JSON PARSE ERROR", e.toString());
        }
        frame = requestParams;
        return requestParams.toString();
    }


    public void requestTransactionDetails() {
        String edpoint = "https://api.blockcypher.com/v1/btc/test3/txs/new";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, edpoint, frame, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Transactionframe", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Network error", error.toString());
            }
        });
    }

    public void signTransaction() {

    }

    public void broadCastTX() {

    }


}
