package jp.live2d.sample;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by fuyuk on 16/2/15.
 */
public class TimeLineAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<SetGetManager> manageList;

    public TimeLineAdapter(Context context){
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setMemoList(ArrayList<SetGetManager> manageList){
        this.manageList = manageList;
    }

    @Override
    public int getCount() {
        return manageList.size();
    }

    @Override
    public Object getItem(int position) {
        return manageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return manageList.get(position).getID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.managerow,parent,false);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //インテント生成
                Intent intent = new Intent(context,SubActivity.class);
                context.startActivity(intent);
            }
        });

        ((TextView)convertView.findViewById(R.id.date)).setText(manageList.get(position).getDate());
        ((TextView)convertView.findViewById(R.id.title)).setText(manageList.get(position).getTitle());
        return convertView;
    }
}