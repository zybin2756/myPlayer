package com.zyb.myplayer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {

    private mHandler mhandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        startService(new Intent(this,PlayService.class));

        mhandler = new mHandler();
        mhandler.sendEmptyMessageDelayed(0x1,3000);
    }

    class mHandler extends  Handler{
        public void handleMessage(Message msg){
            switch(msg.what){
                case 0x1:
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                    break;
            }
        }
    }
}
