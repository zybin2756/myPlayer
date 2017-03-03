package com.zyb.myplayer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.zyb.myplayer.R;
import com.zyb.myplayer.vo.musicInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/29 0029.
 */

public class myMusicListAdapter extends BaseExpandableListAdapter{

    private Context ctx;
    private Map<String,List<musicInfo>> musicDataSet;
    private List<String> parentList;

    public myMusicListAdapter(Context ctx,List<String> parentList,Map<String,List<musicInfo>> musicDataSet){
        this.ctx = ctx;
        this.musicDataSet = musicDataSet;
        this.parentList = parentList;

    }

    @Override
    public int getGroupCount() {
        return musicDataSet.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
//        parentList.
        return musicDataSet.get(parentList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return musicDataSet.get(parentList.get(groupPosition));
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return musicDataSet.get(parentList.get(groupPosition)).get(childPosition);
    }


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        pViewHolder vh = null;
        if(convertView == null){
            convertView = View.inflate(ctx,R.layout.music_parent_item,null);
            vh = new pViewHolder();
            vh.list_type = (TextView) convertView.findViewById(R.id.list_type);
            convertView.setTag(vh);
            convertView.setBackgroundColor(Color.rgb(238,238,238));
        }

        vh = (pViewHolder) convertView.getTag();
        vh.list_type.setText(parentList.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        cViewHolder vh = null;
        if(convertView == null){
            vh = new cViewHolder();
            convertView = View.inflate(ctx,R.layout.music_list_item,null);

            vh.singer = (TextView) convertView.findViewById(R.id.singer);
            vh.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(vh);
        }

//        if(groupPosition == this.groupPosition && curPosition == childPosition){
//            convertView.setBackgroundColor(Color.rgb(124,187,228));
//        }else{
//            convertView.setBackgroundColor(Color.TRANSPARENT);
//        }

        vh = (cViewHolder) convertView.getTag();
//        System.out.println("groupPosition:"+groupPosition +"childPosition"+childPosition);
        musicInfo mi = musicDataSet.get(parentList.get(groupPosition)).get(childPosition);
        vh.title.setText(mi.getTitle());
        vh.singer.setText(mi.getSinger());
        return convertView;
    }

    public void setMusicDataSet(Map<String, List<musicInfo>> musicDataSet) {
        this.musicDataSet = musicDataSet;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class pViewHolder{
        TextView list_type;
    }

    class cViewHolder{
        TextView title;
        TextView singer;
    }

}
