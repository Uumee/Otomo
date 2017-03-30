/**
 * You can modify and use this source freely
 * only for the development of application related Live2D.
 * <p>
 * (c) Live2D Inc. All rights reserved.
 */
package jp.live2d.sample;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import jp.live2d.Live2D;
import jp.live2d.utils.android.FileManager;
import jp.live2d.utils.android.SoundManager;


public class SampleActivity extends AppCompatActivity {


    LinearLayout slideListViewLayout = null;

    private LAppLive2DManager live2DMgr;
    private LAppView live2DView;
    public static Point activitySize = new Point();
    static private Activity instance;
    private FloatingActionButton fab = null;
    private FloatingActionButton slidelistFab = null;
    private boolean isFabOpen = false;
    private SpeechRecognizer recognizer = null;
    private FloatingActionButton micFab = null;

    public SampleActivity() {
        instance = this;
        SoundManager.init(this);
        live2DMgr = new LAppLive2DManager();
    }

    static public void exit() {
        SoundManager.release();
        instance.finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        Live2D.init();

        //SlideListViewの取得
        getLayoutInflater().inflate(R.layout.slidelistview, (ViewGroup) findViewById(R.id.slideListLayout));
        slideListViewLayout = (LinearLayout) findViewById(R.id.slideListViewLayout);

        //Live2DViewの生成と貼り付け
        live2DView = live2DMgr.createView(this);
        ((FrameLayout) findViewById(R.id.live2dLayout)).addView(live2DView);

        //Fontの設定
        ((ResizeTextClock) findViewById(R.id.dateText)).setTypeface(AppDefine.defaultFont);
        ((ResizeTextClock) findViewById(R.id.timeText)).setTypeface(AppDefine.defaultFont);

        //ListViewの設定
        setupList();
        //Fabの設定
        setupFab();
        //SpeechRecognizerの設定
        setupRecognizer();

        //Live2DのFileManagerの設定
        FileManager.init(this.getApplicationContext());

    }

    /**
     * onWindowFocusChangedメソッド<br />
     * 　WindowFocusが変化した時に呼ばれる。<br />
     * 　TODO:Intent先から戻ってきたときの呼ばれるのでSlideListViewの開閉Fabの動作がいまいち<br />
     * 　画面サイズを算出する。そしてそのサイズを利用する処理を行う、<br />
     * 　SlideListViewの設定・アニメーション・各Listenerの設定をしてる。<br />
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        //Activityの画面サイズの算出
        this.calcActivitySize();

        //SlideListViewの設定
        setupSlideListView();


        //slideListViewのアニメーションクラス
        final SlideListViewAnimator slideListViewAnimator = new SlideListViewAnimator();

        //SlideListViewのタグのタッチリスナの設定
        slidelistFab = (FloatingActionButton) findViewById(R.id.slidelistFab);
        slidelistFab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
                    slideListViewAnimator.tapEvent();
                return true;
            }
        });

        //SlideListView以外の部分をタッチした時のリスナ
        live2DView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slideListViewAnimator.isOpen) {
                    slideListViewAnimator.tapEvent();
                }
            }
        });


    }

    void setupList(){
        //ListViewの取得・Adapterの生成
        ListView list = (ListView) findViewById(R.id.listView);
        final MemoAdapter adapter = new MemoAdapter(this);

        //ListViewへDBからデータの受け渡し
        SQLiteDatabase db = new MySQLiteOpenHelper(this).getReadableDatabase();
        Cursor c = db.rawQuery("select * from mytable",null);
        while(c.moveToNext()){
            adapter.addItem(c.getString(2),c.getString(1),c.getString(3));
        }

        //ListViewへAdapterの設定
        list.setAdapter(adapter);

        //ListViewのItemClickリスナの設定
        //SnackBarでmemoの表示
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Snackbar.make(instance.findViewById(R.id.layout), ((MemoAdapter.Memo) adapter.getItem(position)).memo, Snackbar.LENGTH_LONG).show();
                Intent intent = new Intent(instance, DetailActivity.class);
                intent.putExtra(DetailActivity.DATA_TITLE,((MemoAdapter.Memo) adapter.getItem(position)).title);
                intent.putExtra(DetailActivity.DATA_DATE,((MemoAdapter.Memo) adapter.getItem(position)).date);
                intent.putExtra(DetailActivity.DATA_MEMO,((MemoAdapter.Memo) adapter.getItem(position)).memo);
                startActivity(intent);
            }
        });
    }

    /**
     * setupSlideListViewメソッド<br />
     * 　ActivityのonCreate()でinflateしておいたslideListViewLayoutのLayoutParamsの設定<br />
     * 　width・Height・Margin・Visibilityの設定を行う<br />
     */
    void setupSlideListView() {
        ViewGroup.LayoutParams params = slideListViewLayout.getLayoutParams();
        //SlideListViewの縦横設定
        params.width = activitySize.x / 2;
        params.height = (int) ((float) activitySize.y * (2.0f / 3.0f));
        slideListViewLayout.setLayoutParams(params);
        //SlideListViewの位置設定
        ((ViewGroup.MarginLayoutParams) params).leftMargin = -activitySize.x / 2;
        ((ViewGroup.MarginLayoutParams) params).topMargin = (int) ((float) activitySize.y * (1.0f / 3.0f));
        slideListViewLayout.setLayoutParams(params);
        //可視化
        slideListViewLayout.setVisibility(View.VISIBLE);
    }

    /**
     * setupFabメソッド<br />
     * 　メインメニューとなるFabのClickListenerの設定<br />
     * 　各Intentを行うFabのClickListenerの設定を行う<br />
     */
    void setupFab() {
        fab = (FloatingActionButton) findViewById(R.id.fabMain);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isFabOpen) {
                    closeFab();
                } else {
                    openFab();
                }
            }
        });
        findViewById(R.id.micFab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognition();
            }
        });
        findViewById(R.id.subFab1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(instance, CalendarActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.subFab2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(instance, DetailActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * openFabメソッド<br />
     * 　メニューを開くためにMainFabのrotationアニメーション<br />
     * 　subFabのalpha・translationYアニメーションを行う<br />
     */
    void openFab() {
        ObjectAnimator mainAnim = ObjectAnimator.ofFloat(fab, "rotation", 0f, 180f);
        mainAnim.setDuration(50);

        FloatingActionButton subFab1 = (FloatingActionButton) findViewById(R.id.subFab1);
        FloatingActionButton subFab2 = (FloatingActionButton) findViewById(R.id.subFab2);

        PropertyValuesHolder holderAlpha = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);

        PropertyValuesHolder holderY1;
        PropertyValuesHolder holderY2;

        if (Build.VERSION.SDK_INT <= 21) {
            holderY1 = PropertyValuesHolder.ofFloat("translationY", 0f, -1 * (fab.getHeight() - 16 * getResources().getDisplayMetrics().density));
            holderY2 = PropertyValuesHolder.ofFloat("translationY", 0f, -2 * (fab.getHeight() - 16 * getResources().getDisplayMetrics().density));
        } else {
            holderY1 = PropertyValuesHolder.ofFloat("translationY", 0f, -1 * (fab.getHeight() + ((ViewGroup.MarginLayoutParams) fab.getLayoutParams()).bottomMargin));
            holderY2 = PropertyValuesHolder.ofFloat("translationY", 0f, -2 * (fab.getHeight() + ((ViewGroup.MarginLayoutParams) fab.getLayoutParams()).bottomMargin));
        }

        ObjectAnimator subAnim1 = ObjectAnimator.ofPropertyValuesHolder(subFab1, holderAlpha, holderY1);
        subAnim1.setDuration(200);
        ObjectAnimator subAnim2 = ObjectAnimator.ofPropertyValuesHolder(subFab2, holderAlpha, holderY2);
        subAnim2.setDuration(200);

        subFab1.setFocusable(true);
        subFab2.setFocusable(true);

        mainAnim.start();
        subAnim1.start();
        subAnim2.start();
        isFabOpen = true;
    }

    /**
     * closFabメソッド<br />
     * 　メニューを閉じるためにMainFabのrotationアニメーション<br />
     * 　subFabのalpha・translationYアニメーションを行う<br />
     */
    void closeFab() {
        ObjectAnimator mainAnim = ObjectAnimator.ofFloat(fab, "rotation", 180f, 0f);
        mainAnim.setDuration(50);

        FloatingActionButton subFab1 = (FloatingActionButton) findViewById(R.id.subFab1);
        FloatingActionButton subFab2 = (FloatingActionButton) findViewById(R.id.subFab2);

        PropertyValuesHolder holderAlpha = PropertyValuesHolder.ofFloat("alpha", 1f, 0f);
        PropertyValuesHolder holderY1;
        PropertyValuesHolder holderY2;

        if (Build.VERSION.SDK_INT <= 21) {
            holderY1 = PropertyValuesHolder.ofFloat("translationY", -1 * (fab.getHeight() - 16 * getResources().getDisplayMetrics().density), 0f);
            holderY2 = PropertyValuesHolder.ofFloat("translationY", -2 * (fab.getHeight() - 16 * getResources().getDisplayMetrics().density), 0f);
        } else {
            holderY1 = PropertyValuesHolder.ofFloat("translationY", -1 * (fab.getHeight() + ((ViewGroup.MarginLayoutParams) fab.getLayoutParams()).bottomMargin), 0f);
            holderY2 = PropertyValuesHolder.ofFloat("translationY", -2 * (fab.getHeight() + ((ViewGroup.MarginLayoutParams) fab.getLayoutParams()).bottomMargin), 0f);
        }

        ObjectAnimator subAnim1 = ObjectAnimator.ofPropertyValuesHolder(subFab1, holderAlpha, holderY1);
        subAnim1.setDuration(200);
        ObjectAnimator subAnim2 = ObjectAnimator.ofPropertyValuesHolder(subFab2, holderAlpha, holderY2);
        subAnim2.setDuration(200);

        subFab1.setFocusable(false);
        subFab2.setFocusable(false);

        mainAnim.start();
        subAnim1.start();
        subAnim2.start();
        isFabOpen = false;
    }

    /**
     * Activityの画面サイズの算出
     */
    void calcActivitySize() {
        FrameLayout layout = (FrameLayout) findViewById(R.id.layout);
        activitySize.set(layout.getWidth(), layout.getHeight());
    }


    void setupRecognizer(){
        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d("recognizer","音声認識準備完了");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("recognizer","入力開始");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                Log.d("recognizer","RMSの変更："+rmsdB);
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                Log.d("recognizer","録音データのフィードバック");
            }

            @Override
            public void onEndOfSpeech() {
                Log.d("recognizer","入力終了");
            }

            @Override
            public void onError(int error) {
                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        // 音声データ保存失敗
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        // Android端末内のエラー(その他)
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        // 権限無し
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        // ネットワークエラー(その他)
                        Log.e("recognizer", "network error");
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        // ネットワークタイムアウトエラー
                        Log.e("recognizer", "network timeout");
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        // 音声認識結果無し
                        Toast.makeText(instance, "no match Text data", Toast.LENGTH_LONG);
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        // RecognitionServiceへ要求出せず
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        // Server側からエラー通知
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        // 音声入力無し
                        Toast.makeText(instance, "no input?", Toast.LENGTH_LONG);
                        break;
                    default:
                }
            }

            @Override
            public void onResults(Bundle results) {
                Log.d("recognizer","認識結果取得");
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.d("recognizer","部分的認識結果取得");
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                Log.d("recognizer","イベント発生："+eventType);
            }
        });
    }

    //音声認識を開始します。
    void recognition(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        recognizer.startListening(intent);
    }


    /**
     * SlideListViewAnimatorクラス<br />
     * 　SlideListView(左下端からでてくるリスト）のアニメーションを司るクラス。<br />
     * 　tapEventメソッドを呼べば開閉する。
     */
    class SlideListViewAnimator {

        boolean isOpen = false;
        ObjectAnimator openAnim = null;
        ObjectAnimator closeAnim = null;

        ObjectAnimator openAnim_fab = null;
        ObjectAnimator closeAnim_fab = null;

        //Close状態・非アニメート（初期）
        SlideListViewAnimator() {
            isOpen = false;
            float temp = activitySize.x / 2;
            openAnim = ObjectAnimator.ofFloat(slideListViewLayout, "translationX", 0f, temp);
            openAnim.setDuration(300);
            closeAnim = ObjectAnimator.ofFloat(slideListViewLayout, "translationX", temp, 0f);
            closeAnim.setDuration(300);
        }

        public void open() {
            //Close状態　且つ　アニメーションしていない時　Openする
            if (!isOpen) {
                animate(openAnim);
            }
        }

        public void close() {
            //Open状態　且つ　アニメーションしていない時Closeする
            if (isOpen) {
                animate(closeAnim);
            }
        }

        public void tapEvent() {
            if (isOpen) {
                animate(closeAnim);
            } else {
                animate(openAnim);
            }
        }

        private void animate(ObjectAnimator anim) {
            if (live2DMgr.isAnimation == false) {
                anim.start();
                live2DMgr.tagTouchEvent(isOpen);
                isOpen = !isOpen;
            }
        }
    }


}