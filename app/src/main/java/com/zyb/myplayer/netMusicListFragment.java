package com.zyb.myplayer;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.zyb.myplayer.adapter.netMusicListAdapter;
import com.zyb.myplayer.utils.Constant;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.*;

/**
 * Created by Administrator on 2017/1/29 0029.
 */

public class netMusicListFragment extends Fragment implements OnClickListener {

    private LinearLayout ll_search = null;
    private EditText edt_query = null;
    private Button btn_query = null;
    private RelativeLayout ll_loading = null;
    private Button btn_load = null;
    private ListView netlist = null;
    private QueryAsyncTask queryAsyncTask = null;
    private List<String> musicList = null;
    private netMusicListAdapter netMusicListAdapter = null;
    private MainActivity mainActivity = null;
    public static netMusicListFragment newInstance() {
        netMusicListFragment fragment = new netMusicListFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View netMusicListFra = inflater.inflate(R.layout.fragment_net_music_list,null);
        ll_search = (LinearLayout) netMusicListFra.findViewById(R.id.ll_search);
        btn_load = (Button) netMusicListFra.findViewById(R.id.btn_load);
        edt_query = (EditText) netMusicListFra.findViewById(R.id.edt_query);
        btn_query = (Button) netMusicListFra.findViewById(R.id.btn_query);
        ll_loading = (RelativeLayout) netMusicListFra.findViewById(R.id.ll_loading);
        netlist = (ListView) netMusicListFra.findViewById(R.id.netlist);
        musicList = new ArrayList<String>();
        btn_query.setOnClickListener(this);
        btn_load.setOnClickListener(this);
        return netMusicListFra;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_query:
            {
                String str = Constant.WEB_SITE+Constant.WEB_SEARCH+edt_query.getText();
                startQueryAsyncTask(str);
                break;
            }
            case R.id.btn_load:{

                String str = Constant.WEB_SITE+Constant.WEB_LIST+Constant.WEB_CONDITION;
                startQueryAsyncTask(str);
                break;
            }
        }
    }

    private void startQueryAsyncTask(String str){
        if(queryAsyncTask != null) {
            queryAsyncTask.cancel(true);
        }
        queryAsyncTask = new QueryAsyncTask();
        queryAsyncTask.execute(str);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    public class QueryAsyncTask extends AsyncTask<String,Void,Boolean>{

        @Override
        protected void onPreExecute() {
            btn_load.setVisibility(View.GONE);
            ll_loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(isCancelled()) return;
            ll_loading.setVisibility(View.GONE);
            netlist.setVisibility(View.VISIBLE);
            netMusicListAdapter = new netMusicListAdapter(mainActivity,musicList);
            netlist.setAdapter(netMusicListAdapter);

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect(params[0]).userAgent(Constant.USEAGENT).get();
                Elements musicDiv = doc.getElementsByClass("song-item");
                String temp= null;
                String title = null;
                String singer= null;
                System.out.println(musicDiv.size());
//                System.out.println("-----------------------------------------------------------------");
                musicList.clear();
                for(Element ele : musicDiv){
                    if(isCancelled()) return false;
                    System.out.println(ele);
//                    System.out.println("-----------------------------------------------------------------");
                    title = ele.getElementsByClass("song-title").text();
                    singer = ele.getElementsByClass("singer").text();
                    if(title.length() == 0){
                        Elements tempEles = ele.getElementsByTag("a");
                        if(tempEles.size() != 0) {
                            title = tempEles.get(0).text();
                            singer = tempEles.get(1).text();
                        }
                    }

                    System.out.println(title);
                    System.out.println(singer);
                    temp = title+"|"+singer;
//                    System.out.println( temp);
                    musicList.add(temp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }
    }
}
