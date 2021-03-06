package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyAlertDialog.MyAlertDialog;
import xyz.yluo.ruisiapp.View.MyToolBar;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.UrlUtils;


/**
 * Created by free2 on 2016/1/11 0011.
 * <p>
 * edit in 2016 03 14
 * <p>
 * 登陆activity
 */
public class LoginActivity extends BaseActivity {

    private EditText ed_username, ed_pass ;
    private EditText anwser_text;
    private Button btn_login;
    private ImageView imageViewl, imageViewr;
    private CheckBox rem_user, rem_pass;

    private SharedPreferences perPreferences;
    private List<String> list = new ArrayList<>();
    private String loginUrl;
    private int answerSelect = 0;
    private MyAlertDialog dialog;
    private MyToolBar myToolBar;



    public static void open(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        myToolBar = (MyToolBar) findViewById(R.id.myToolBar);
        ed_username = (EditText) findViewById(R.id.login_name);
        ed_pass = (EditText) findViewById(R.id.login_pas);
        btn_login = (Button) findViewById(R.id.btn_login);
        imageViewl = (ImageView) findViewById(R.id.iv_login_l);
        imageViewr = (ImageView) findViewById(R.id.iv_login_r);
        rem_user = (CheckBox) findViewById(R.id.rem_user);
        rem_pass = (CheckBox) findViewById(R.id.rem_pass);
        Spinner anwser_select = (Spinner) findViewById(R.id.anwser_select);
        anwser_text = (EditText) findViewById(R.id.anwser_text);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_click();
            }
        });
        myToolBar.setTitle("登陆");
        myToolBar.setBackEnable(this);

        perPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        boolean isRemUser = perPreferences.getBoolean("ISREMUSER", false);
        boolean isRemberPass = perPreferences.getBoolean("ISREMPASS", false);
        if (isRemUser) {
            rem_user.setChecked(true);
            ed_username.setText(perPreferences.getString("USERNAME", ""));
        }
        if (isRemberPass) {
            rem_pass.setChecked(true);
            rem_user.setChecked(true);
            ed_username.setText(perPreferences.getString("USERNAME", ""));
            ed_pass.setText(perPreferences.getString("PASSWORD", ""));
            btn_login.setEnabled(true);
        }

        list.add("安全提问(未设置请忽略)");
        list.add("母亲的名字");
        list.add("爷爷的名字");
        list.add("父亲出生的城市");
        list.add("您其中一位老师的名字");
        list.add("您个人计算机的型号");
        list.add("您最喜欢的餐馆名称");
        list.add("驾驶执照最后四位数字");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        anwser_select.setAdapter(spinnerAdapter);
        anwser_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                answerSelect = i;
                if (i != 0) {
                    anwser_text.setVisibility(View.VISIBLE);
                } else {
                    anwser_text.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ed_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(ed_username.getText()) && !TextUtils.isEmpty(ed_pass.getText())) {
                    btn_login.setEnabled(true);
                } else {
                    btn_login.setEnabled(false);
                }
            }
        });
        ed_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(ed_username.getText()) && !TextUtils.isEmpty(ed_pass.getText())) {
                    btn_login.setEnabled(true);
                } else {
                    btn_login.setEnabled(false);
                }

                //替换密码框图片
                if (!TextUtils.isEmpty(ed_pass.getText())) {
                    imageViewl.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_22_hide));
                    imageViewr.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_33_hide));
                } else {
                    imageViewl.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_22));
                    imageViewr.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_33));
                }
            }
        });
        rem_pass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rem_user.setChecked(true);
                }
            }
        });

        rem_user.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    rem_pass.setChecked(false);
                }
            }
        });
    }

    private void login_click() {
        dialog = new  MyAlertDialog(this,MyAlertDialog.PROGRESS_TYPE)
                .setTitleText("登陆中，请稍后......");
        dialog.show();
        final String username = ed_username.getText().toString().trim();
        final String passNo = ed_pass.getText().toString().trim();
        String url = UrlUtils.getLoginUrl(false);
        HttpUtil.get(getApplicationContext(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                if (res.contains("欢迎您回来")) {
                    login_ok(res);
                }
                Document doc = Jsoup.parse(res);
                loginUrl = doc.select("form#loginform").attr("action");
                Map<String, String> params = new HashMap<>();
                params.put("fastloginfield", "username");
                params.put("cookietime", "2592000");
                params.put("username", username);
                params.put("password", passNo);
                params.put("questionid", answerSelect + "");
                if (answerSelect == 0) {
                    params.put("answer", "");
                } else {
                    params.put("answer", anwser_text.getText().toString());
                }

                begain_login(params);
            }

            @Override
            public void onFailure(Throwable e) {
                login_fail("网络异常！！！");
                dialog.dismiss();
            }
        });
    }

    private void begain_login(Map<String, String> params) {
        HttpUtil.post(getApplicationContext(), loginUrl, params, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                if (res.contains("欢迎您回来")) {
                    login_ok(res);
                } else {
                    login_fail("账号或者密码错误");
                }
            }

            @Override
            public void onFailure(Throwable e) {
                login_fail("网络异常");
            }
        });
    }

    //登陆成功执行
    private void login_ok(String res) {
        int index = res.indexOf("欢迎您回来");
        String s = res.substring(index, index + 30).split("，")[1].split(" ")[0].trim();
        if (s.length() > 0) {
            App.USER_GRADE = s;
        }
        //写入到首选项
        SharedPreferences.Editor editor = perPreferences.edit();
        if (rem_pass.isChecked()) {
            editor.putBoolean("ISREMUSER", true);
            editor.putBoolean("ISREMPASS", true);
            String userName = ed_username.getText().toString().trim();
            editor.putString("USERNAME", userName);
            editor.putString("PASSWORD", ed_pass.getText().toString().trim());
        } else {
            editor.putBoolean("ISREMUSER", false);
            editor.putBoolean("ISREMPASS", false);
        }
        if (rem_user.isChecked()) {
            editor.putBoolean("ISREMUSER", true);
            editor.putString("USERNAME", ed_username.getText().toString().trim());
        } else {
            editor.putBoolean("ISREMUSER", false);
        }

        Document doc = Jsoup.parse(res);
        App.USER_NAME = doc.select(".footer").select("a[href^=home.php?mod=space&uid=]").text();
        String url = doc.select("a[href^=home.php?mod=space&uid=]").attr("href");
        App.USER_UID = GetId.getUid(url);

        editor.putString("USER_NAME", App.USER_NAME);
        editor.putString("USER_UID", App.USER_UID);
        editor.apply();
        //开始获取formhash
        dialog.dismiss();
        App.ISLOGIN = true;
        Toast.makeText(getApplicationContext(), "欢迎你" + App.USER_NAME + "登陆成功", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("status", "ok");
        //设置返回数据
        LoginActivity.this.setResult(RESULT_OK, intent);
        finish();
    }

    //登陆失败执行
    private void login_fail(String res) {
        dialog.dismiss();
        Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
    }
}



