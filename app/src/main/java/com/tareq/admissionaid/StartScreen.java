package com.tareq.admissionaid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class StartScreen extends Activity {
    @Override
    public void onCreate(Bundle savedIntanceState) {

        super.onCreate(savedIntanceState);
        setContentView(R.layout.start_screen);

        ImageView iv=findViewById(R.id.startScreenImage);
        iv.setBackgroundResource(R.drawable.ico);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=(new Intent(StartScreen.this,MainActivity.class));
                startActivity(intent);
                StartScreen.this.finish();
                finish();
            }
        }, 2000);
    }
}
