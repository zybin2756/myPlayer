package com.zyb.myplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;

import static android.content.SharedPreferences.Editor;

/**
 * Created by Administrator on 2017/1/29 0029.
 */

public class MainActivity extends BaseActivity {

    ViewPager mViewPager;
    PagerSlidingTabStrip mTabs;
    myMusicListFragment mMusicListFragment = null;
    netMusicListFragment nMusicListFragment = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
            1.获取PagerSlidingTabStrip  以及 ViewPager
            2.创建适配器
            3.为ViewPager绑定适配器
            4.将ViewPager 与 PagerSlidingTabStrip绑定
         */
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.mTab);

        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        mViewPager.setAdapter(new mPagerAdapter(getSupportFragmentManager()));

        mTabs.setViewPager(mViewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Editor edit = app.sp.edit();
        edit.putInt("curPosition",getCurPosition());
        edit.putInt("groupPosition",mplayService.getGroupPosition());
        edit.putInt("playMode",mplayService.getPlayMode());
        edit.putInt("curPlayPosition",mplayService.getPlayPosition());
        edit.commit();
    }

    @Override
    public void publish(int progress,int time) {
        if(mViewPager.getCurrentItem() == 0){
            if(mMusicListFragment == null){
                mMusicListFragment = myMusicListFragment.newInstance();
            }
            mMusicListFragment.changePgbOnPlay(progress, time);
        }else if(mViewPager.getCurrentItem() == 1){

        }
    }

    @Override
    public void change(int position) {
        if(mViewPager.getCurrentItem() == 0){
//            Log.i("zyb","change Main");
            if(mMusicListFragment == null){
                mMusicListFragment = myMusicListFragment.newInstance();
            }
            mMusicListFragment.changeUIStatusOnPlay(position);
        }else if(mViewPager.getCurrentItem() == 1){

        }
    }

    class mPagerAdapter extends FragmentPagerAdapter{

        String[] title = {"我的音乐","网络音乐"};

        public mPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0) {
                if (mMusicListFragment == null) {
                    mMusicListFragment = myMusicListFragment.newInstance();
                }
                return mMusicListFragment;
            }
            else if(position == 1){
                if(nMusicListFragment == null) {
                    nMusicListFragment = netMusicListFragment.newInstance();
                }
                return nMusicListFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return title.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }
}
