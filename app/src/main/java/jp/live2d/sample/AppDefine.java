package jp.live2d.sample;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by fuyuk on 16/3/3.
 */
public class AppDefine {

    final static String font01 = "fonts/anzu.ttf";

    public static Typeface defaultFont = null;


    public AppDefine(Context context){
        defaultFont = Typeface.createFromAsset(context.getAssets(),font01);
    }



}
