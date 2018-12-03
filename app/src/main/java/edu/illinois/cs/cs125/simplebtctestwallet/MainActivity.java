package edu.illinois.cs.cs125.simplebtctestwallet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;

import java.math.BigInteger;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToNewWallet(View view) {
        Intent goToNewPage = new Intent(this, NewWallet.class);
        startActivity(goToNewPage);
    }

    public void useExsistingWallet(View view) {
        EditText savedkeyinput = findViewById(R.id.entryform);
        String str = savedkeyinput.getText().toString();
        try {
            str = wifToPrivKey(str);
            Toast.makeText(getBaseContext(), "Decoded key: " + str, Toast.LENGTH_LONG).show();
        } catch (AddressFormatException error) {
            Log.e("Invalid addr", error.toString());
            Toast.makeText(getBaseContext(), "Invalid wallet import format!", Toast.LENGTH_LONG).show();
        }
    }

    private static String wifToPrivKey(String inputWIF) throws AddressFormatException {
        BigInteger dec = Base58.decodeToBigInteger(inputWIF);
        String hexstr = dec.toString(16);
        hexstr = hexstr.substring(0, hexstr.length() - 8);
        hexstr = hexstr.substring(2, hexstr.length());
        return hexstr.toUpperCase();
    }
}
