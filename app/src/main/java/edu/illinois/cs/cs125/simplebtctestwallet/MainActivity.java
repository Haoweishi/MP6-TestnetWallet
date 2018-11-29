package edu.illinois.cs.cs125.simplebtctestwallet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

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
}
