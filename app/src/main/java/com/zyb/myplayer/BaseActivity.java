package com.zyb.myplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.zyb.myplayer.PlayService;

/**
 * Created by Administrator on 2017/1/31 0031.
 * 实现了 UI更新接口 以及 播放service的绑定及解绑的基类
 */

public abstract class BaseActivity extends FragmentActivity{

    protected PlayService mplayService;
    protected boolean isBound;
    protected myPlayerApp app;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (myPlayerApp) getApplication();
        isBound = false;
    }

    private PlayService.updateInterface mupdateInterface = new PlayService.updateInterface() {
        @Override
        public void onPublish(int progress,int time) {
//            Log.i("zyb","onPublish");
            publish(progress, time);
        }

        @Override
        public void onChange(int position) {
//            Log.i("zyb","onChange");
            change(position);
        }
    };


    public void bindPlayService() {
        if(!isBound){
            Intent intent = new Intent(this,PlayService.class);
            bindService(intent,conn, Context.BIND_AUTO_CREATE);
            isBound = true;
        }
    }


    public void unbindPlayService() {
        if(isBound){
            unbindService(conn);
            isBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindPlayService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindPlayService();
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayService.PlayBinder playBinder = (PlayService.PlayBinder) service;
            mplayService = (PlayService) playBinder.getPlayService();
            mplayService.setMupdateInterface(mupdateInterface);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mplayService = null;
            isBound = false;
        }
    };

    public void next(){
        if(mplayService!=null) {
            mplayService.next();
        }
    }

    public void pre(){
        if(mplayService!=null) {
            mplayService.pre();
        }
    }

    public void pause(){
        if(mplayService!=null) {
            mplayService.pause();
        }
    }

    public void play(int groupPosition,int position){
        if(mplayService != null){
            mplayService.play(groupPosition,position);
        }
    }

    public void stop(){
        if(mplayService != null){
            mplayService.stop();
        }
    }

    public void start(){
        if(mplayService != null){
            mplayService.start();
        }
    }

    public boolean isPlaying(){
        if(mplayService != null) {
            return mplayService.isPlaying();
        }
        return false;
    }

    public boolean isPauseing(){
        if(mplayService != null) {
            return mplayService.isPauseing();
        }
        return false;
    }

    public int getCurPosition(){
        if(mplayService != null) {
            return mplayService.getCurPosition();
        }

        return -1;
    }

    public void setCurPosition(int position){
        if(mplayService != null) {
            mplayService.setCurPosition(position);
        }

    }

    public void seekTo(int mesc){
//        Log.i("zyb","seekTo:"+mesc);
        if(mplayService != null){
            mplayService.start();
            mplayService.seekTo(mesc);
        }
    }

//    public int getDuration(){
//        return mplayService.getDuration();
//    }
    public void setPlayMode(int mode){
        if(mplayService != null){
            mplayService.setPlayMode(mode);
        }
    }
    //延缓到子类实现,但在父类中调用
    public abstract void publish(int progress, int time);
    public abstract void change(int position);
}
