package jp.live2d.sample;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by fuyuk on 16/2/12.
 */
public class MemoAdapter extends BaseAdapter {

    ArrayList<Memo> memoList = new ArrayList();
    Context context ;
    Typeface font = null;

    public MemoAdapter(Context context){
        this.context = context;
        font = Typeface.createFromAsset(context.getAssets(),"fonts/yomogi.ttf");
    }

    public void addItem(String title,String date,String memo){
        memoList.add(new Memo(title,date,memo));
    }

    @Override
    public int getCount() {
        return memoList.size();
    }

    @Override
    public Object getItem(int position) {
        return memoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.list_item,parent,false);

        float scale = context.getResources().getConfiguration().fontScale;

        TextView titleText = (TextView)convertView.findViewById(R.id.titleText);
        TextView dateText = (TextView)convertView.findViewById(R.id.dateText);

        titleText.setText(memoList.get(position).title);
        titleText.setTextSize(scale * 18);
        titleText.setTypeface(font);
        dateText.setText(memoList.get(position).date);
        dateText.setTextSize(scale * 10);
        dateText.setTypeface(font);

        return convertView;
    }

    class Memo {
        String title;
        String date;
        String memo;
        Memo(String title,String date,String memo){
            this.title = title;
            this.date = date;
            this.memo = memo;
        }
    }
}
