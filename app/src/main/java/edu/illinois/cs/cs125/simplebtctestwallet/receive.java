package edu.illinois.cs.cs125.simplebtctestwallet;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class receive extends AppCompatActivity {

    private RequestQueue queue;
    private ImageView addressqr;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        queue = Volley.newRequestQueue(this.getApplicationContext());
    }

    public void onStart() {
        super.onStart();
        SharedPreferences appdata = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String address = appdata.getString("ADDR", null);
        TextView addressdisplayer = findViewById(R.id.dynamic_txt_publicAddress);
        addressdisplayer.setText(address);
        addressqr = findViewById(R.id.dynamic_QRCode);
        ImageRequest downloadQR = new ImageRequest("https://chart.googleapis.com/chart?cht=qr&chl=" + address + "&chs=160x160&chld=L|0", new Response.Listener<Bitmap>(){
            @Override
            public void onResponse(Bitmap response) {
                addressqr.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(downloadQR);
    }

}
