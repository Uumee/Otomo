package jp.live2d.sample;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by oite on 16/02/08.
 */
public class SubActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondactivity);//secondactivity.xmlをセット
    }
}
