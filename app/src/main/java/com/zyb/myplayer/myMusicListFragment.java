package com.zyb.myplayer;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zyb.myplayer.adapter.myMusicListAdapter;
import com.zyb.myplayer.utils.MarqueeTextView;
import com.zyb.myplayer.utils.MediaUtilsTools;
import com.zyb.myplayer.vo.musicInfo;

import java.util.List;
import java.util.Map;

import static android.view.View.OnClickListener;
import static android.widget.ExpandableListView.OnChildClickListener;
import static android.widget.ExpandableListView.OnGroupClickListener;
import static android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * Created by Administrator on 2017/1/29 0029.
 */

public class myMusicListFragment extends Fragment implements OnClickListener,OnSeekBarChangeListener,OnChildClickListener,OnGroupClickListener {

    private ExpandableListView musicList = null;
    private myMusicListAdapter musicAdapter = null;
    private MainActivity mainActivity = null;
    private Map<String,List<musicInfo>> musicDataSet;
    List<String> parentList = null;
    private MarqueeTextView pTitle = null;
    private MarqueeTextView pSinger = null;
    private TextView pgb_time = null;
    private ImageButton palyorpause = null;
    private ImageButton next = null;
    private SeekBar seekbarMusic = null;
    private ImageView imgAblum = null;
    private RelativeLayout line_play = null;
    private boolean isPause = false;
    private UpdateUiHandler mUpdateUiHandler;
    private static final int UPDATESEEKBAR = 0x1;
    private static final int UPDATEUI = 0x2;


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        palyorpause.setBackgroundResource(R.mipmap.pause);
        mainActivity.seekTo(seekBar.getProgress());
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        mainActivity.play(groupPosition,childPosition);
        palyorpause.setBackgroundResource(R.mipmap.pause);
        return true;
    }


    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

        for(int i = 0,count = musicAdapter.getGroupCount();i<count;i++){
            if(i != groupPosition && parent.isGroupExpanded(i)) {
                parent.collapseGroup(i);
            }
        }

        if(parent.isGroupExpanded(groupPosition)){
            parent.collapseGroup(groupPosition);
        }else{
            parent.expandGroup(groupPosition,false);
            mainActivity.app.updateDataSet(groupPosition);
        }

        return true;
    }

    //更新 UI 界面的 handler
    class UpdateUiHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case UPDATESEEKBAR:
                    seekbarMusic.setProgress(msg.arg1);
                    pgb_time.setText(MediaUtilsTools.timeFormat(msg.arg2));
                    break;
                case UPDATEUI: {
//                    Log.i("zyb","mulist updateui");
                    if(mainActivity.isPauseing()){
//                        Log.i("zyb","isPauseing");
                        palyorpause.setBackgroundResource(R.mipmap.play);
                    }else{
//                        Log.i("zyb","isPlaying");
                        palyorpause.setBackgroundResource(R.mipmap.pause);
                    }

//                    musicAdapter.updateSingleRow(musicList,mainActivity.mplayService.getGroupPosition(),mainActivity.mplayService.getCurPosition());
                    musicInfo mi = mainActivity.mplayService.getMusicInfo();
                    seekbarMusic.setMax(mi.getDuration());
                    pTitle.setText(mi.getTitle());
                    pSinger.setText(mi.getSinger());

                    break;
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.bindPlayService();
    }

    @Override
    public void onPause() {
        super.onPause();
        mainActivity.unbindPlayService();
    }

    //Fragment被捕获时 获取 父对象的 上下文对象 context
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mainActivity = (MainActivity) context;
    }

    public static myMusicListFragment newInstance() {
        myMusicListFragment fragment = new myMusicListFragment();
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_music_list,null);
        musicList = (ExpandableListView) view.findViewById(R.id.musicList);
        pTitle = (MarqueeTextView) view.findViewById(R.id.pTitle);
        pSinger = (MarqueeTextView) view.findViewById(R.id.pSinger);
        palyorpause = (ImageButton)view.findViewById(R.id.playorpause);
        next = (ImageButton)view.findViewById(R.id.next);
        seekbarMusic = (SeekBar) view.findViewById(R.id.seekbar_music);
        pgb_time = (TextView) view.findViewById(R.id.pgb_time);
        imgAblum = (ImageView) view.findViewById(R.id.imgAblum);
        line_play = (RelativeLayout) view.findViewById(R.id.line_play);
        mUpdateUiHandler = new UpdateUiHandler();

        musicAdapter = new myMusicListAdapter(mainActivity,mainActivity.app.parentList,mainActivity.app.musicDataSet);
        musicList.setAdapter(musicAdapter);
        palyorpause.setOnClickListener(this);
        next.setOnClickListener(this);
        line_play.setOnClickListener(this);
        seekbarMusic.setOnSeekBarChangeListener(this);
        musicList.setOnChildClickListener(this);
        musicList.setOnGroupClickListener(this);
//        musicList.setOnGroupExpandListener(this);
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void changeUIStatusOnPlay(int position){
//        Log.i("ZYB:","changeUIStatusOnPlay");
        if(mUpdateUiHandler == null){
            mUpdateUiHandler = new UpdateUiHandler();
        }
        Message msg = mUpdateUiHandler.obtainMessage(UPDATEUI);
        msg.arg1 = position;
        if(mUpdateUiHandler ==null){
            mUpdateUiHandler = new UpdateUiHandler();
        }
        mUpdateUiHandler.sendMessage(msg);
    }

    public void changePgbOnPlay(int progress,int time){

        if(mUpdateUiHandler == null){
            mUpdateUiHandler = new UpdateUiHandler();
        }
        Message msg = mUpdateUiHandler.obtainMessage(UPDATESEEKBAR);
        msg.arg1 = progress;
        msg.arg2 = time - progress;
        mUpdateUiHandler.sendMessage(msg);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
//        Log.i("ZYB","onClick");
        switch (id){
            case R.id.playorpause:
                if(mainActivity.isPlaying()){
                    mainActivity.pause();
                    palyorpause.setBackgroundResource(R.mipmap.play);
                }else{
                    if(mainActivity.isPauseing()) {
                        mainActivity.start();
                    }else{
                        mainActivity.play(mainActivity.mplayService.getGroupPosition(),mainActivity.mplayService.getCurPosition());
                    }
                    palyorpause.setBackgroundResource(R.mipmap.pause);
                }
                break;
            case R.id.next:
//                Log.i("zyb","next");
                mainActivity.next();
//                palyorpause.setBackgroundResource(R.mipmap.pause);
                break;

            case R.id.line_play:
                if(mainActivity.isPlaying() || mainActivity.isPauseing()) {
                    Intent intent = new Intent(mainActivity, PlayActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }
}
