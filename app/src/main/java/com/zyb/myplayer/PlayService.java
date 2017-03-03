package com.zyb.myplayer;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.zyb.myplayer.vo.musicInfo;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayService extends Service implements MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener{

    private MediaPlayer mediaPlayer;
    private myPlayerApp app;
    List<musicInfo> curMusicList = null;
    private List<String> parentList = null;
    private musicInfo curMusicInfo = null;
    private int curPosition = -1;
    private int groupPosition = 0;
    private int curPlayPosition = 0;
    private int playMode = 0;
    private updateInterface mupdateInterface = null;
    private ExecutorService es = Executors.newSingleThreadExecutor();
    private boolean isPause;
    private boolean isFirst;
    public PlayService() {

    }

    @Override
    public void onDestroy() {
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if(es!=null && es.isShutdown() == false){
            es.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isPause = false;

        mediaPlayer = new MediaPlayer();


        mediaPlayer.setOnPreparedListener(this);

        mediaPlayer.setOnCompletionListener(this);

        app = (myPlayerApp) getApplication();

        parentList = app.parentList;


        SharedPreferences sp = app.sp;
        curPlayPosition = sp.getInt("curPlayPosition",0);
        playMode = sp.getInt("playMode",0);
        groupPosition = sp.getInt("groupPosition",0);
        isFirst = true;
        play(groupPosition,sp.getInt("curPosition",0));

        curMusicList = app.musicDataSet.get(parentList.get(groupPosition));

        es.execute(updateRun);
    }

    Runnable updateRun = new Runnable() {
        @Override
        public void run() {
                while(true) {
                    try {
                        Thread.sleep(500);
                        if(mupdateInterface != null) {
                            mupdateInterface.onPublish(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new PlayBinder();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(playMode == 1){
            seekTo(0);
            start();
        }else{
            next();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(isFirst){
            mediaPlayer.seekTo(curPlayPosition);
            isPause = true;
            isFirst = false;
        }else {
            mediaPlayer.start();
            isPause = false;
        }
        if(mupdateInterface != null)
            mupdateInterface.onChange(curPosition);
    }


    public class PlayBinder extends Binder{
        public Service getPlayService(){
            return PlayService.this;
        }
    }

    public musicInfo getMusicInfo(){
        return curMusicInfo;
    }

    public void play(int groupPosition,int position){
        preparePlay(groupPosition,position);
    }

    private void preparePlay(int groupPosition,int position){
        if( mediaPlayer!= null) {
            try {
                curPosition = position;
                this.groupPosition = groupPosition;
                curMusicList = app.musicDataSet.get(parentList.get(groupPosition));
                curMusicInfo = curMusicList.get(position);

                musicInfo find = app.dbUtils.findFirst(Selector.from(musicInfo.class).where("musicID","=",curMusicInfo.getMusicID()));
                if(find == null){
                    find = curMusicInfo;
                }
                System.out.println("find:"+find);
                find.setPlayTime(System.currentTimeMillis());
                System.out.println("find:"+find);
                app.dbUtils.saveOrUpdate(find);

                mediaPlayer.reset();
                mediaPlayer.setDataSource(this, Uri.parse(curMusicInfo.getFileUrl()));
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isPlaying(){
        if(mediaPlayer != null){
            return mediaPlayer.isPlaying();
        }
        return false;
    }
    public void start(){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
            isPause = false;
        }
    }

    public void stop(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
    }

    public void next(){
        isPause = false;
        if(playMode == 0 || playMode == 1){
            curPosition+=1;
            curPosition%= curMusicList.size();
        }else if(playMode == 2){
            int pos  = ((int)(Math.random()*10000)) %  curMusicList.size();
            while(pos == curPosition){
                pos  = ((int)(Math.random()*10000)) %  curMusicList.size();
            }
            curPosition = pos;
        }

        play(groupPosition,curPosition);
    }

    public void pre(){
        isPause = false;
        if(playMode == 0 || playMode == 1){
            curPosition+= curMusicList.size();
            curPosition-=1;
            curPosition%= curMusicList.size();
        }else if(playMode == 2){
            int pos  = ((int)(Math.random()*10000)) %  curMusicList.size();
            while(pos == curPosition){
                pos  = ((int)(Math.random()*10000)) %  curMusicList.size();
            }
            curPosition = pos;
        }

        play(groupPosition,curPosition);
    }

    public void pause(){
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            isPause = true;
        }
    }

    public void seekTo(int mesc){
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(mesc);
        }
    }

    //更新UI的接口,
    public interface updateInterface{
        public void onPublish(int progress, int time);
        public void onChange(int position);
    }

    //自身保存了该接口的 实现类对象 用于调用
    public void setMupdateInterface(updateInterface mupdateInterface) {
//        Log.i("ZYB","setMupdateInterface");
        this.mupdateInterface = mupdateInterface;
//        if(isPlaying() || isPauseing()) {
            this.mupdateInterface.onChange(curPosition);

//        }
    }

    public void setPlayMode(int mode){
        this.playMode = mode;
    }

    public int getPlayMode() {
        return playMode;
    }

    public int getCurPosition(){
        return  curPosition;
    }

    public void setCurPosition(int curPosition){
        this.curPosition = curPosition;
    }

    public int getGroupPosition() {
        return groupPosition;
    }

    public void setGroupPosition(int groupPosition) {
        this.groupPosition = groupPosition;
    }

    public int getPlayPosition(){
        return  mediaPlayer.getCurrentPosition();
    }

    public boolean isPauseing(){
        return this.isPause;
    }
}
