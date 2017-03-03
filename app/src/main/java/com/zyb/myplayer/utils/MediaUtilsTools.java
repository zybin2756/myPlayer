package com.zyb.myplayer.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.astuetz.PagerSlidingTabStrip;
import com.zyb.myplayer.vo.musicInfo;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/1/30 0030.
 */

public class MediaUtilsTools {

    private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

    public static ArrayList<musicInfo> getAllMusic(Context ctx){
        ArrayList<musicInfo> musicInfos = null;

        Cursor cursor = ctx.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.YEAR,
                        MediaStore.Audio.Media.MIME_TYPE,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ALBUM_ID
                },
                MediaStore.Audio.Media.MIME_TYPE + "=? or "
                + MediaStore.Audio.Media.MIME_TYPE + "=?",
                new String[]{ "audio/mpeg", "audio/x-ms-wma" },null
        );

        musicInfos = new ArrayList<musicInfo>();
        if(cursor.moveToFirst()){
            do {
                musicInfo mi = new musicInfo();


                mi.setMusicID(cursor.getLong(0));
                // 文件名
                mi.setFileName(cursor.getString(1));
                // 歌曲名
                mi.setTitle(cursor.getString(2));
                // 时长
                mi.setDuration(cursor.getInt(3));
                // 歌手名
                mi.setSinger(cursor.getString(4));
                // 专辑名
                mi.setAlbum(cursor.getString(5));
                // 年代
                if (cursor.getString(6) != null) {
                    mi.setYear(cursor.getString(6));
                } else {
                    mi.setYear("未知");
                }

                // 歌曲格式
                if ("audio/mpeg".equals(cursor.getString(7).trim())) {
                    mi.setType("mp3");
                } else if ("audio/x-ms-wma".equals(cursor.getString(7).trim())) {
                    mi.setType("wma");
                }

                // 文件大小
                if (cursor.getString(8) != null) {
                    float size = cursor.getInt(8) / 1024f / 1024f;
                    mi.setSize((size + "").substring(0, 4) + "M");
                } else {
                    mi.setSize("未知");
                }
                // 文件路径
                if (cursor.getString(9) != null) {
                    mi.setFileUrl(cursor.getString(9));
                }

                mi.setAlbumID(cursor.getLong(10));
//                Log.i("ZYB:",mi.toString());
                musicInfos.add(mi);
            }while(cursor.moveToNext());

            cursor.close();
        }
        return musicInfos;
    }

    public static String timeFormat(int duration){
        String time = "";
        duration/=1000;
        int min = duration / (60);
        int sec = duration % (60);
        if(min < 10){
            time = "0"+String.valueOf(min);
        }else{
            time = String.valueOf(min);
        }
        if(sec < 10){
            time += ":0"+String.valueOf(sec);
        }else{
            time += ":"+String.valueOf(sec);
        }

        return time;
    }
}
