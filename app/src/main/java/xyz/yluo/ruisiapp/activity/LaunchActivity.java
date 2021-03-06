package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.CheckMessageService;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.CircleImageView;
import xyz.yluo.ruisiapp.checknet.CheckNet;
import xyz.yluo.ruisiapp.checknet.CheckNetResponse;
import xyz.yluo.ruisiapp.database.MyDB;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.TextResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.ImageUtils;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-19.
 * 启动activity
 * 检查是否登陆
 * 读取相关设置写到{@link App}
 */
public class LaunchActivity extends BaseActivity {
    private long starttime = 0;
    //等待时间
    private final static int WAIT_TIME = 800;
    private TextView launch_text;
    private CircleImageView user_image;
    private SharedPreferences perUserInfo = null;
    private boolean isrecieveMessage = false;
    private boolean isForeGround = true;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(isForeGround){
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                i.putExtra("isLogin", App.ISLOGIN);
                startActivity(i);
                finish();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        launch_text = (TextView) findViewById(R.id.launch_text);
        Button btn_inner = (Button) findViewById(R.id.btn_login_inner);
        Button btn_outer = (Button) findViewById(R.id.btn_login_outer);
        findViewById(R.id.login_fail_view).setVisibility(View.GONE);
        user_image = (CircleImageView) findViewById(R.id.user_image);
        user_image.setVisibility(View.GONE);
        starttime = System.currentTimeMillis();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btn_inner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.ISLOGIN = true;
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });
        btn_outer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.ISLOGIN = true;
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });
        getSetting();


        MyDB myDB = new MyDB(this, MyDB.MODE_READ);
        myDB.showHistoryDatabase();

        new CheckNet(this).startCheck(new CheckNetResponse() {
            @Override
            public void onFinish(int type, String response) {
                canGetRs(type);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration((long) (WAIT_TIME*0.85));// 设置动画显示时间

        TranslateAnimation animation = new TranslateAnimation(0, 0, 80, 0);
        animation.setDuration((long) (WAIT_TIME*0.8));

        // 初始化需要加载的动画资源
        RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.always_rotate);

        launch_text.startAnimation(animation);
        user_image.startAnimation(alphaAnimation);
        findViewById(R.id.loading_view).startAnimation(rotateAnimation);
    }

    //从首选项读出设置
    private void getSetting() {
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this);
        perUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String uid = perUserInfo.getString("USER_UID", "0");
        Log.i("LaunchActivity", perUserInfo.getString("USER_NAME", "null"));
        Log.i("LaunchActivity", uid);

        if (!uid.equals("0")) {
            Uri uri = ImageUtils.getImageURI(getFilesDir(), uid);
            if (uri != null) {
                user_image.setVisibility(View.VISIBLE);
                user_image.setImageURI(uri);
            }
        }

        //boolean theme = shp.getBoolean("setting_swich_theme",false);
        isrecieveMessage = shp.getBoolean("setting_show_notify", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeGround = true;
    }

    private void canGetRs(int type) {
        if (type == 1 || type == 2) {
            String url = UrlUtils.getLoginUrl(false);
            checklogin(url);
        } else {
            noNetWork();
            findViewById(R.id.login_view).setVisibility(View.GONE);
            findViewById(R.id.login_fail_view).setVisibility(View.VISIBLE);
        }
    }

    private void checklogin(String url) {
        HttpUtil.get(this, url, new TextResponseHandler() {
            @Override
            public void onSuccess(String res) {
                App.ISLOGIN = false;
                if (res.contains("loginbox")) {
                    App.ISLOGIN = false;
                } else {
                    Document doc = Jsoup.parse(res);
                    int index = res.indexOf("欢迎您回来");
                    String s = res.substring(index, index + 30).split("，")[1].split(" ")[0].trim();
                    if (s.length() > 0) {
                        App.USER_GRADE = s;
                    }
                    App.USER_NAME = doc.select(".footer").select("a[href^=home.php?mod=space&uid=]").text();
                    String url = doc.select(".footer").select("a[href^=home.php?mod=space&uid=]").attr("href");
                    App.USER_UID = GetId.getUid(url);

                    SharedPreferences.Editor editor = perUserInfo.edit();
                    editor.putString("USER_NAME", App.USER_NAME);
                    editor.putString("USER_UID", App.USER_UID);
                    editor.apply();

                    App.ISLOGIN = true;

                    startCheckMessageService();
                }
            }

            @Override
            public void onFinish() {
                finishthis();
            }
        });
    }

    //没网是执行
    private void noNetWork() {
        Toast.makeText(this, "无法连接到服务器请检查网络设置！", Toast.LENGTH_SHORT).show();
    }

    private void startCheckMessageService() {
        //启动后台服务
        Log.e("launch","启动了服务");
        Intent i = new Intent(this, CheckMessageService.class);
        i.putExtra("isRunning", true);
        i.putExtra("isNotisfy", isrecieveMessage);
        startService(i);
    }


    private void finishthis() {
        long currenttime = System.currentTimeMillis();
        long delay = WAIT_TIME - (currenttime - starttime);
        if (delay < 0) {
            mHandler.post(mRunnable);
        }else{
            mHandler.postDelayed(mRunnable, delay);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mRunnable);
        isForeGround = false;
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }
}
