package jp.live2d.sample;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;

/**
 * Created by fuyuk on 16/2/15.
 */
public class TodayActivity extends Activity {

    int TitleWidth = 600;
    int TitleHeight = 200;

    SQLiteDatabase sdb;
    MySQLiteOpenHelper myhelper = new MySQLiteOpenHelper(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);





        DateFormat format = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        //タイトルにレイアウトを指定する


        //データベースに日時を記録------------------------
        ContentValues values = new ContentValues();

        myhelper.addSchedule(sdb,2016,2,5,23,00,"オフィスで開発","今日はもう終わりでいいんじゃないのかな");
        myhelper.addSchedule(sdb,2016,2,7,14,30,"オフィスで開発","まだだ、まだ終わらんよ");
        myhelper.addSchedule(sdb,2016,2,7,14,32,"オフィスで開発","まだだ、まだ終わらんよ");
        myhelper.addSchedule(sdb,2016,2,7,14,33,"オフィスで開発","まだだ、まだ終わらんよ");
        /*
        sdb = myhelper.getWritableDatabase();
        values.put("time", format.format(new Date()));
        values.put("memo", "hoge");

        sdb.insert("mytable", null, values);

        sdb.close();
        */
        //---------------------------------------------


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mainactivity);



        //データベースから日付、時間、タイトル、詳細を取得-----------------------------------
        sdb = myhelper.getReadableDatabase();
        Cursor csr = sdb.rawQuery("select * from mytable",null);
        String csrTime = "";
        String csrTitle = "";
        String csrMemo = "";


        //時間と予定のタイトル表示のList View-----------------------------------
        ListView listview = (ListView)findViewById(R.id.listView);

        ArrayList<SetGetManager> list = new ArrayList<>();
        TimeLineAdapter adapter = new TimeLineAdapter(this);




        //アタプターにSetGetManager型のデータを適用
        adapter.setMemoList(list);
        //リストビューでSetGetManager型を利用
        listview.setAdapter(adapter);

        //------------------------------------------------------
        while(csr.moveToNext()){
            csrTime = csr.getString(csr.getColumnIndex("time"));
            csrTitle = csr.getString(csr.getColumnIndex("title"));
            csrMemo = csr.getString(csr.getColumnIndex("memo"));

            int indexLV = csrTime.indexOf("　");

            SetGetManager manage = new SetGetManager();
            manage.setDate(String.format(csrTime.substring(indexLV+1)));
            manage.setTitle(csrTitle);
            list.add(manage);
            //adapter.notifyDataSetChanged();



        }

        Toolbar title = (Toolbar)findViewById(R.id.title_text);
        title.setLogo(R.mipmap.ic_launcher);
        //-----------------------------------------------------------------------------



        //テキストビューにデータベースの日付を書き込み、Toolbarにadd--------------------------
        TextView txtV = new TextView(this);
        int index = csrTime.indexOf("　");
        txtV.setText(String.format(csrTime.substring(0, index)));

        ViewGroup.LayoutParams loutPms = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        txtV.setGravity(Gravity.CENTER);
        txtV.setTextColor(Color.rgb(200, 160, 210));
        txtV.setTextSize(50);

        txtV.setLayoutParams(loutPms);

        title.addView(txtV);
        //-----------------------------------------------------------------------------





        sdb.close();








    }


}
