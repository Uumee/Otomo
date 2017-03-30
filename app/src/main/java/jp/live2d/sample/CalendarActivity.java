package jp.live2d.sample;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CalendarView;
import android.widget.ListView;

/**
 * Created by fuyuk on 16/2/18.
 */
public class CalendarActivity extends AppCompatActivity {

    CalendarView calendarView = null;
    ListView listView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);

        calendarView = (CalendarView)findViewById(R.id.calendarView);
        listView = (ListView)findViewById(R.id.monthScheduleList);

        MemoAdapter adapter = new MemoAdapter(this);


        SQLiteDatabase database = new MySQLiteOpenHelper(this).getReadableDatabase();
        database.rawQuery("select * from mytable where ",null);


        listView.setAdapter(new MemoAdapter(this));






    }

}
