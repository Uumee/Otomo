package jp.live2d.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.widget.CalendarView;

public class DetailActivity extends AppCompatActivity {

    final static String DATA_TITLE = "TITLE";
    final static String DATA_MEMO = "MEMO";
    final static String DATA_DATE = "DATE";

    private AppCompatEditText titleEdit = null;
    private AppCompatEditText memoEdit = null;
    private CalendarView calendarView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if(intent.hasExtra(DATA_TITLE) && intent.hasExtra(DATA_MEMO) && intent.hasExtra(DATA_DATE)){
            String title = intent.getStringExtra(DATA_TITLE);
            String memo = intent.getStringExtra(DATA_MEMO);
            String date = intent.getStringExtra(DATA_DATE);

            titleEdit = (AppCompatEditText)findViewById(R.id.titleEdit);
            memoEdit = (AppCompatEditText)findViewById(R.id.memoEdit);
            //calendarView = (CalendarView)findViewById(R.id.calendarView);

            titleEdit.setText(title);
            memoEdit.setText(memo);

        }else{

        }

    }

}
