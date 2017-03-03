package com.zyb.myplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zyb.myplayer.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/15 0015.
 */

public class netMusicListAdapter extends BaseAdapter {

    private List<String> musicList = null;
    private Context ctx = null;
    public netMusicListAdapter(Context ctx, List<String> musicList){

        this.musicList = new ArrayList<String>();
        this.musicList.clear();
        this.musicList.addAll(musicList);
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView == null){
            convertView = View.inflate(this.ctx,R.layout.music_list_item,null);

            vh = new ViewHolder();
            vh.title = (TextView) convertView.findViewById(R.id.title);
            vh.singer = (TextView) convertView.findViewById(R.id.singer);
            convertView.setTag(vh);
        }

        vh = (ViewHolder) convertView.getTag();
        String[] temp = musicList.get(position).split("\\|");
        if(temp.length == 2){
            vh.title.setText(temp[0]);
            vh.singer.setText(temp[1]);
        }

        return convertView;
    }


    class ViewHolder{
        TextView title;
        TextView singer;
    }
}
