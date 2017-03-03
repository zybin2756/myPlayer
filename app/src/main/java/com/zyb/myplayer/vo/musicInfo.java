package com.zyb.myplayer.vo;

import com.lidroid.xutils.db.annotation.Id;

/**
 * Created by Administrator on 2017/1/30 0030.
 */


public class musicInfo {
    @Id
    private long id;
    private boolean isLove; // 1-喜爱 0-默认
    private long playTime; //最近播放时间戳
    private long musicID;
    private String fileName; //文件名
    private String title; //标题
    private int duration; //时长
    private String singer; //歌手
    private String album;
    private String year; //年份
    private String type; //类型
    private String size; //大小
    private String fileUrl; //文件路径
    private long albumID;

    public musicInfo(){
        super();
    }

    public musicInfo(String fileName, String title, int duration, String singer, String album,
                     String year, String type, String size, String fileUrl){
        super();
        this.fileName = fileName;
        this.title = title;
        this.duration = duration;
        this.singer = singer;
        this.album = album;
        this.year = year;
        this.type = type;
        this.size = size;
        this.fileUrl = fileUrl;
    }

    @Override
    public String toString() {
        return  "MusicInfo [id="+id+" fileName=" + fileName + ", title=" + title
                + ", duration=" + duration + ", singer=" + singer + ", album="
                + album + ", year=" + year + ", type=" + type + ", size="
                + size + ", fileUrl=" + fileUrl + ", isLove=" + isLove() +", playTime=" + playTime +"]";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
//        if(title.length() > 12){
//            title = title.substring(0,12);
//        }
        this.title = title;
    }

    public long getMusicID() {
        return musicID;
    }

    public void setMusicID(long musicID) {
        this.musicID = musicID;
    }

    public long getAlbumID() {
        return albumID;
    }

    public void setAlbumID(long albumID) {
        this.albumID = albumID;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
//        if(singer.length() > 12){
//            singer = singer.substring(0,12);
//        }

        this.singer = singer;
    }

    public boolean isLove() {
        return isLove;
    }

    public void setLove(boolean love) {
        isLove = love;
    }

    public long getPlayTime() {
        return playTime;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

}
