package jp.live2d.sample;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextClock;

/**
 * Created by fuyuk on 16/2/13.
 */
public class ResizeTextClock extends TextClock {

    /** 最小のテキストサイズ。 */
    private static final float MIN_TEXT_SIZE = 1.0f;

    /** テキスト幅計測用のペイント。 */
    private Paint mPaint = new Paint();

    /**
     * コンストラクタ。
     *
     * @param context
     * @param attrs
     */
    public ResizeTextClock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        float measuredWidth = getMeasuredWidth();
        if (measuredWidth > 0) {
            shrinkTextSize();
        }
    }

    /**
     * テキストサイズを縮小します。
     */
    private void shrinkTextSize() {
        // テキストサイズを取得します。
        float tempTextSize = getTextSize();

        // テキスト幅が入りきっていない場合は、入るまで繰り返します。
        while (getMeasuredWidth() < getTextWidth(tempTextSize)) {
            // テキストサイズを縮小します。
            tempTextSize--;

            if (tempTextSize <= MIN_TEXT_SIZE) {
                // 最小テキストサイズより小さくなった場合は、最小テキストサイズをセットして終了します。
                tempTextSize = MIN_TEXT_SIZE;
                break;
            }
        }

        // 調整した結果のテキストサイズをセットします。
        setTextSize(TypedValue.COMPLEX_UNIT_PX, tempTextSize);
    }

    /**
     * テキスト幅を取得します。
     *
     * @param textSize
     * @return
     */
    float getTextWidth(float textSize) {
        mPaint.setTextSize(textSize);
        float textWidth = mPaint.measureText(getText().toString());
        return textWidth;
    }
}