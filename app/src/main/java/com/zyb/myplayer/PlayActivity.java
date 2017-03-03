package com.zyb.myplayer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.zyb.myplayer.utils.MarqueeTextView;
import com.zyb.myplayer.utils.MediaUtilsTools;
import com.zyb.myplayer.vo.musicInfo;

import java.util.ArrayList;

import static android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * Created by Administrator on 2017/2/5 0005.
 */

public class PlayActivity extends BaseActivity implements OnClickListener,OnSeekBarChangeListener {

    private MarqueeTextView title = null;
    private MarqueeTextView singer = null;
    private TextView play_time = null;
    private TextView play_duration = null;
    private SeekBar play_seekbar = null;
    private ImageView play_pre = null;
    private ImageView play_playorpause = null;
    private ImageView play_next = null;
    private ImageView play_love = null;
    private ImageView play_mode = null;
    private ViewPager mViewPager = null;
    private static final int UPDATESEEKBAR = 0x1;
    private static final int UPDATEUI = 0x2;
    private UpdateUiHandler mUpdateUiHandler = null;
    private int mode = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        play_time = (TextView) findViewById(R.id.play_time);
        play_duration = (TextView) findViewById(R.id.play_duration);
        play_seekbar = (SeekBar) findViewById(R.id.play_seekbar);
        play_pre = (ImageView) findViewById(R.id.play_btn_pre);
        play_playorpause = (ImageView) findViewById(R.id.play_btn_playorpause);
        play_next = (ImageView) findViewById(R.id.play_btn_next);
        play_mode = (ImageView) findViewById(R.id.play_btn_mode);
        play_love = (ImageView) findViewById(R.id.play_btn_love);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);

        LayoutInflater inflater = getLayoutInflater();
        View v1 = inflater.inflate(R.layout.activity_musicinfo,null);
        View v2 = inflater.inflate(R.layout.activity_lrc,null);
        title = (MarqueeTextView) v1.findViewById(R.id.play_title);
        singer = (MarqueeTextView) v1.findViewById(R.id.play_singer);
        ArrayList<View> viewList = new ArrayList<View>();
        viewList.add(v1);
        viewList.add(v2);
        ViewPagerAdapter vpa = new ViewPagerAdapter(viewList);
        mViewPager.setAdapter(vpa);

        mUpdateUiHandler = new UpdateUiHandler();
//        bindPlayService();
        play_playorpause.setOnClickListener(this);
        play_next.setOnClickListener(this);
        play_pre.setOnClickListener(this);
        play_mode.setOnClickListener(this);
        play_love.setOnClickListener(this);
        play_seekbar.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void publish(int progress, int time) {
        Message msg = mUpdateUiHandler.obtainMessage(UPDATESEEKBAR);
        msg.arg1 = progress;
        msg.arg2 = time;
        mUpdateUiHandler.sendMessage(msg);
    }

    @Override
    public void change(int position){
        Message msg = mUpdateUiHandler.obtainMessage(UPDATEUI);
        msg.arg1 = position;
        mUpdateUiHandler.sendMessage(msg);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.play_btn_pre:
                pre();
                break;
            case R.id.play_btn_next:
                next();
                break;
            case R.id.play_btn_playorpause: {
                if (mplayService.isPauseing()) {
                    start();
                    play_playorpause.setBackgroundResource(R.mipmap.pause1);
                } else {
                    pause();
                    play_playorpause.setBackgroundResource(R.mipmap.play1);
                }
                break;
            }
            case R.id.play_btn_mode:
            {
                mode+=1;
                mode%=3;
                if(mode == 0){
                    play_mode.setBackgroundResource(R.mipmap.loopall);
                    Toast.makeText(PlayActivity.this,"列表循环",Toast.LENGTH_SHORT).show();
                }else if(mode == 1){
                    play_mode.setBackgroundResource(R.mipmap.loopone);
                    Toast.makeText(PlayActivity.this,"单曲循环",Toast.LENGTH_SHORT).show();
                }else if(mode == 2){
                    play_mode.setBackgroundResource(R.mipmap.random);
                    Toast.makeText(PlayActivity.this,"随机播放",Toast.LENGTH_SHORT).show();
                }
                setPlayMode(mode);
                break;
            }
            case R.id.play_btn_love:
            {
                try {
                    musicInfo mi = mplayService.getMusicInfo();
                    musicInfo find = app.dbUtils.findFirst(Selector.from(musicInfo.class).where("musicID","=",mi.getMusicID()));
                    System.out.println("find:"+find);
                    System.out.println("mi:"+mi);
//                    System.out.println("find:"+find);
                    if(find == null){
                        System.out.println("save");
                        find = mi;
                        find.setId(getCurPosition());
                        find.setLove(true);
                    }else{
                        System.out.println("change");
                        find.setLove(!find.isLove());
                    }
                    app.dbUtils.saveOrUpdate(find);
                    if(find.isLove()){
                        play_love.setBackgroundResource(R.mipmap.loved);
                    }else{
                        play_love.setBackgroundResource(R.mipmap.love);
                    }

                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        play_playorpause.setBackgroundResource(R.mipmap.pause1);
        seekTo(seekBar.getProgress());
    }


    //更新界面handler
    class UpdateUiHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case UPDATESEEKBAR:
                    play_time.setText(MediaUtilsTools.timeFormat(msg.arg1));
                    play_seekbar.setProgress(msg.arg1);
                    break;
                case UPDATEUI: {
                    musicInfo mi =  mplayService.getMusicInfo();

                    try {
                        musicInfo find = app.dbUtils.findFirst(Selector.from(musicInfo.class).where("musicID","=",mi.getMusicID()));
//                        System.out.println("find:"+find);
                        if(find == null){
                            play_love.setBackgroundResource(R.mipmap.love);
                        }else{
                            if(find.isLove()){
                                play_love.setBackgroundResource(R.mipmap.loved);
                            }else{
                                play_love.setBackgroundResource(R.mipmap.love);
                            }
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }


                    title.setText(mi.getTitle());
                    singer.setText(mi.getSinger());
                    play_duration.setText(MediaUtilsTools.timeFormat(mi.getDuration()));
                    play_seekbar.setMax(mi.getDuration());
                    mode = mplayService.getPlayMode();

                    if(mplayService.isPauseing()){
                        play_playorpause.setBackgroundResource(R.mipmap.play1);
                    }else{
                        play_playorpause.setBackgroundResource(R.mipmap.pause1);
                    }

                    if(mode == 0){
                        play_mode.setBackgroundResource(R.mipmap.loopall);
//                        Toast.makeText(PlayActivity.this,"列表循环",Toast.LENGTH_SHORT).show();
                    }else if(mode == 1){
                        play_mode.setBackgroundResource(R.mipmap.loopone);
//                        Toast.makeText(PlayActivity.this,"单曲循环",Toast.LENGTH_SHORT).show();
                    }else if(mode == 2){
                        play_mode.setBackgroundResource(R.mipmap.random);
//                        Toast.makeText(PlayActivity.this,"随机播放",Toast.LENGTH_SHORT).show();
                    }


                    break;
                }
            }
        }
    }

    class ViewPagerAdapter extends PagerAdapter{

        ArrayList<View> viewList = null;

        public ViewPagerAdapter(ArrayList<View> viewList) {
            super();
            this.viewList = viewList;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(this.viewList.get(position));
            return this.viewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public int getCount() {
            return this.viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
