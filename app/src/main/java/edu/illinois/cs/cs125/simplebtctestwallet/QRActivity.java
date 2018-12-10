package edu.illinois.cs.cs125.simplebtctestwallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView scanView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanView = new ZXingScannerView(this);
        setContentView(scanView);
    }

    @Override
    public void onPause() {
        super.onPause();
        scanView.stopCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        scanView.setResultHandler(this);
        scanView.startCamera();
    }

    @Override
    public void handleResult(Result raw) {
        Intent setAddress = new Intent(this, SendOrReceive.class);
        setAddress.putExtra("SCAN_ADDRESS", raw.getText());
        finish();
        startActivity(setAddress);
    }
}
