package com.thangvnnc.receive_video_socketio;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button btnConnect;

    private Socket mSocket;
    private Activity context = null;
    private String room = "show";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        imageView = findViewById(R.id.imgView);
        btnConnect = findViewById(R.id.btnConnect);

        URI uri =  URI.create("http://192.168.1.222");
        mSocket = IO.socket(uri);
        mSocket.on("connect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        mSocket.on(room, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                final JSONObject jsonObject = (JSONObject) args[0];
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            String base64 = jsonObject.getString("streamData");
                            byte[] imageAsBytes = Base64.decode(base64.getBytes(), 0);
                            imageView.setImageBitmap(BitmapFactory.decodeByteArray(
                                    imageAsBytes, 0, imageAsBytes.length));
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }                    }
                });

            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.connect();
                mSocket.emit("register", room);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }
}
