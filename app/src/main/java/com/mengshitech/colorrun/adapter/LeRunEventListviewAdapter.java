package com.mengshitech.colorrun.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mengshitech.colorrun.R;
import com.mengshitech.colorrun.bean.LeRunEntity;
import com.mengshitech.colorrun.utils.IPAddress;

import java.util.List;

/**
 * Created by kanghuicong on 2016/7/16  10:32.
 * 515849594@qq.com
 */
public class LeRunEventListviewAdapter extends BaseAdapter {
    View view;
    Context context;
    List<LeRunEntity> lerunlist;

    public LeRunEventListviewAdapter(Context context,List<LeRunEntity> lerunlist) {
        this.context = context;
        this.lerunlist=lerunlist;
    }

    @Override
    public int getCount() {
        return lerunlist.size();
    }

    @Override
    public Object getItem(int position) {
        LeRunEntity entity=lerunlist.get(position);
        //返回；lerun_id
        return entity.getLerun_id();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LeRunEntity entity=lerunlist.get(position);
        Holder holder = null;
        if (convertView == null) {
            view = View.inflate(context, R.layout.item_lerun_listview,
                    null);
            holder = new Holder();
            holder.lerun_event_name = (TextView) view.findViewById(R.id.tvLeRunName);
            holder.lerun_envent_time = (TextView) view.findViewById(R.id.tvLeRunTime);
//            holder.lerun_envent_state=(TextView)view.findViewById(R.id.lerun_envent_state);
            holder.lerun_event_address = (TextView) view.findViewById(R.id.tvLeRunLocation);
            holder.imageview= (ImageView) view.findViewById(R.id.ivLeRunBackground);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (Holder) view.getTag();
        }
        Glide.with(context).load(IPAddress.path+entity.getLerun_poster()).into(holder.imageview);
        holder.lerun_envent_time.setText(entity.getLerun_time());
        holder.lerun_event_address.setText(entity.getLerun_address());
        holder.lerun_event_name.setText(entity.getLerun_title());

        return view;
    }


    class Holder {
        TextView lerun_event_name;
        TextView lerun_envent_time;
        TextView lerun_envent_state;
        TextView lerun_event_address;
        ImageView imageview;
    }
}
