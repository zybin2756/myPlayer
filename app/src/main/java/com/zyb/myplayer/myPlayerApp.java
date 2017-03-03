package com.zyb.myplayer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.zyb.myplayer.utils.Constant;
import com.zyb.myplayer.utils.MediaUtilsTools;
import com.zyb.myplayer.vo.musicInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/7 0007.
 */

public class myPlayerApp extends Application {

    public static SharedPreferences sp = null;
    public static DbUtils dbUtils = null;
    public static Map<String,List<musicInfo>> musicDataSet;
    public static List<String> parentList = null;
    private static List<musicInfo> musicInfos = null;
    private static List<musicInfo> love_musicInfos = null;
    private static List<musicInfo> musicPlayList = null;
    @Override
    public void onCreate() {
        super.onCreate();
        dbUtils = DbUtils.create(getApplicationContext());
        sp = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);


        musicDataSet = new HashMap<>();
        parentList = new ArrayList<String>();
        parentList.add("音乐列表");
        parentList.add("我喜爱的音乐");
        parentList.add("最近播放");
        musicInfos = MediaUtilsTools.getAllMusic(this);
        try {
            love_musicInfos = this.dbUtils.findAll(Selector.from(musicInfo.class).where("isLove","=",true));
            musicPlayList = this.dbUtils.findAll(Selector.from(musicInfo.class).where("playTime",">",0).orderBy("playTime",true));
        } catch (DbException e) {
            e.printStackTrace();
        }

        musicDataSet.put(parentList.get(0),musicInfos);
        if(love_musicInfos == null){
            love_musicInfos = new ArrayList<musicInfo>();
        }
        musicDataSet.put(parentList.get(1),love_musicInfos);

        if(musicPlayList == null){
            musicPlayList = new ArrayList<musicInfo>();
        }
        musicDataSet.put(parentList.get(2),musicPlayList);
    }

    public void updateDataSet(int groupPos){
        try {
            switch(groupPos){
                case 0:
                    musicInfos = MediaUtilsTools.getAllMusic(this);
                    musicDataSet.put(parentList.get(0),musicInfos);
                    break;
                case 1:
                    love_musicInfos = this.dbUtils.findAll(Selector.from(musicInfo.class).where("isLove","=",true));
                    musicDataSet.put(parentList.get(1),love_musicInfos);
                    break;
                case 2:
                    musicPlayList = this.dbUtils.findAll(Selector.from(musicInfo.class).where("playTime",">",0).orderBy("playTime",true).limit(50));
                    musicDataSet.put(parentList.get(2),musicPlayList);
                    break;
            }

            System.out.println(musicDataSet.size());
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
