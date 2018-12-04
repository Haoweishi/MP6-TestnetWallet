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
        String address = appdata.getString("ADDR", null);
        if (privkey != null && address != null) {
            Intent gotoMainPage = new Intent(this, SendOrReceive.class);
            startActivity(gotoMainPage);
        }
    }

    public void goToNewWallet(View view) {
        Intent goToNewPage = new Intent(this, NewWallet.class);
        startActivity(goToNewPage);
    }

    public void useExsistingWallet(View view) {
        EditText savedkeyinput = findViewById(R.id.entryform);
        String str = savedkeyinput.getText().toString();
        if (str.length() == 0) {
            Toast.makeText(getBaseContext(), "Form cannot be empty!", Toast.LENGTH_LONG).show();
        } else {
            try {
                str = PrivKeyHelper.wifToPrivKey(str);
                SharedPreferences.Editor sharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
                sharedPref.putString("PRIVKEY", str);
                String address = PrivKeyHelper.makeAddress(str);
                sharedPref.putString("ADDR", address);
                Log.d("Address", address);
                sharedPref.apply();
            } catch (AddressFormatException error) {
                Log.e("Invalid addr", error.toString());
                Toast.makeText(getBaseContext(), "Invalid wallet import format!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
