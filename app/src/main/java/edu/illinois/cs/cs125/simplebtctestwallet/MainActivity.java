package edu.illinois.cs.cs125.simplebtctestwallet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;

import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;

import java.math.BigInteger;
public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "USER_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected void onResume() {
        super.onResume();
        SharedPreferences appdata = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String privkey = appdata.getString("PRIVKEY", null);
        if (privkey != null) {
            Log.d("Private key", privkey);
            Toast.makeText(getBaseContext(), "Stored private key:" + privkey, Toast.LENGTH_LONG).show();
        }
    }

    public void goToNewWallet(View view) {
        Intent goToNewPage = new Intent(this, NewWallet.class);
        startActivity(goToNewPage);
    }

    public void useExsistingWallet(View view) {
        EditText savedkeyinput = findViewById(R.id.entryform);
        String str = savedkeyinput.getText().toString();
        try {
            str = PrivKeyHelper.wifToPrivKey(str);
            Toast.makeText(getBaseContext(), "Decoded key: " + str, Toast.LENGTH_LONG).show();
            SharedPreferences.Editor sharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
            sharedPref.putString("PRIVKEY", str);
            sharedPref.commit();
        } catch (AddressFormatException error) {
            Log.e("Invalid addr", error.toString());
            Toast.makeText(getBaseContext(), "Invalid wallet import format!", Toast.LENGTH_LONG).show();
        }
    }
}
