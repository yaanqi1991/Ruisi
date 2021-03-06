package xyz.yluo.ruisiapp.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyAlertDialog.MyAlertDialog;
import xyz.yluo.ruisiapp.View.MyColorPicker;
import xyz.yluo.ruisiapp.View.MySmileyPicker;
import xyz.yluo.ruisiapp.View.MySpinner;
import xyz.yluo.ruisiapp.View.MyToolBar;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.PostHandler;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-6.
 * 发帖activity
 */
public class NewArticleActivity extends BaseActivity implements View.OnClickListener{

    private EditText ed_title,ed_content;
    private MyAlertDialog dialog;
    private MySpinner forum_spinner,typeid_spinner;
    private MyColorPicker myColorPicker;
    private MySmileyPicker smileyPicker;
    private TextView tv_select_forum,tv_select_type;
    private List<Pair<String,String>> typeiddatas;
    private View type_id_container;
    private String typeId = "";

    private  final int[] fids = new int[]{
            72, 549, 108, 551, 550,
            110, 217, 142, 552, 560,
            554,548, 216, 91, 555,
            145, 144, 152, 147, 215,
            125, 140};
    private  final String[] forums = new String[]{
            "灌水专区", "文章天地", "我是女生", "西电问答", "心灵花园",
            "普通交易", "缘聚睿思", "失物招领", "我要毕业啦", "技术博客",
            "就业信息发布","学习交流", "我爱运动", "考研交流", "就业交流", "软件交流",
            "嵌入式交流", "竞赛交流", "原创精品", "西电后街", "音乐纵贯线",
            "绝对漫域"};
    private int fid = fids[0];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);
        MyToolBar myToolBar = (MyToolBar) findViewById(R.id.myToolBar);
        myColorPicker = new MyColorPicker(this);
        smileyPicker = new MySmileyPicker(this);
        myToolBar.setTitle("发表新帖");
        myToolBar.setBackEnable(this);
        forum_spinner = new MySpinner(this);
        typeid_spinner = new MySpinner(this);
        typeiddatas = new ArrayList<>();
        myToolBar.addButton("备用",R.drawable.btn_gray_bg,"BTN_SUBMIT_2");
        myToolBar.addButton("发表",R.drawable.btn_light_red_bg,"BTN_SUBMIT");
        myToolBar.setToolBarClickListener(new MyToolBar.OnToolBarItemClick() {
            @Override
            public void OnItemClick(View v, String Tag) {
                if(Tag.equals("BTN_SUBMIT")&&checkPostInput()){
                    dialog = new MyAlertDialog(NewArticleActivity.this,MyAlertDialog.PROGRESS_TYPE)
                    .setTitleText("发贴中,请稍后......");
                    dialog.show();
                    begainPost(App.FORMHASH);

                }else if(Tag.equals("BTN_SUBMIT_2")){
                    new MyAlertDialog(NewArticleActivity.this,MyAlertDialog.WARNING_TYPE)
                            .setTitleText("备用发帖地址")
                            .setContentText("当本页发帖出现错误时，你可以切换到备用发帖地址")
                            .setConfirmText("切换")
                            .setCancelText("取消")
                            .setConfirmClickListener(new MyAlertDialog.OnConfirmClickListener() {
                                @Override
                                public void onClick(MyAlertDialog myAlertDialog) {
                                    startActivity(new Intent(NewArticleActivity.this,NewArticleActivity_2.class));
                                }
                            }).show();
                }
            }
        });
        type_id_container = findViewById(R.id.type_id_container);
        type_id_container.setVisibility(View.GONE);
        tv_select_forum = (TextView) findViewById(R.id.tv_select_forum);
        tv_select_type = (TextView) findViewById(R.id.tv_select_type);
        tv_select_forum.setOnClickListener(this);
        tv_select_forum.setText(forums[0]);
        tv_select_type.setOnClickListener(this);
        ed_title = (EditText) findViewById(R.id.ed_title);
        ed_content = (EditText) findViewById(R.id.ed_content);

        forum_spinner.setData(forums);
        forum_spinner.setListener(new MySpinner.OnItemSelectListener() {
            @Override
            public void onItemSelectChanged(int pos, View v) {
                if(pos>=fids.length){
                    return;
                }else{
                    fid = fids[pos];
                    tv_select_forum.setText(forums[pos]);
                    switch_fid(fid);
                }
            }
        });
        typeid_spinner.setListener(new MySpinner.OnItemSelectListener() {
            @Override
            public void onItemSelectChanged(int pos, View v) {
                if(pos>typeiddatas.size()){
                    return;
                }else{
                    typeId = typeiddatas.get(pos).first;
                    tv_select_type.setText(typeiddatas.get(pos).second);
                }
            }
        });
        final LinearLayout edit_bar = (LinearLayout) findViewById(R.id.edit_bar);
        for(int i = 0;i<edit_bar.getChildCount();i++){
            View c = edit_bar.getChildAt(i);
            if(c instanceof ImageView){
                c.setOnClickListener(this);
            }
        }

        Spinner setSize = (Spinner) findViewById(R.id.action_text_size);
        setSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //[size=7][/size]
                if(ed_content==null||(ed_content.getText().length()<=0&&i==0)){
                    return;
                }
                handleInsert("[size="+(i+1)+"][/size]");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        myColorPicker.setListener(new MyColorPicker.OnItemSelectListener() {
            @Override
            public void itemClick(int pos, View v, String color) {
                handleInsert("[color="+color+"][/color]");
            }
        });

        smileyPicker.setListener(new MySmileyPicker.OnItemClickListener() {
            @Override
            public void itemClick(String str, Drawable a) {
                PostHandler handler = new PostHandler(ed_content);
                handler.insertSmiley("{:" + str + ":}", a);
            }
        });

        switch_fid(fids[0]);
    }

    private boolean checkPostInput() {

        if(!TextUtils.isEmpty(typeId)&&typeId.equals("0")){
            Toast.makeText(this, "请选择主题分类", Toast.LENGTH_SHORT).show();
            return false;
        }else if(TextUtils.isEmpty(ed_title.getText().toString().trim())){
            Toast.makeText(this, "标题不能为空啊", Toast.LENGTH_SHORT).show();
            return false;
        }else if(TextUtils.isEmpty(ed_content.getText().toString().trim())){
            Toast.makeText(this, "内容不能为空啊", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //开始发帖
    private void begainPost(String hash  /*, String time*/) {
        String url = UrlUtils.getPostUrl(fid);
        Map<String, String> params = new HashMap<>();
        params.put("formhash", hash);
        //params.put("posttime", time);
        params.put("topicsubmit", "yes");
        if(!TextUtils.isEmpty(typeId)&&!typeId.equals("0")){
            params.put("typeid",typeId);
        }
        params.put("subject", ed_title.getText().toString());
        params.put("message", ed_content.getText().toString());
        HttpUtil.post(getApplicationContext(), url, params, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                //// TODO: 16-7-26 delete it later
                //ed_content.setText(res);
                //逆向工程 反向判断有没有发帖成功
                Log.e("post",res);
                if(res.contains("已经被系统拒绝")){
                    postFail("由于未知原因发帖失败");
                }else{
                    postSuccess();
                }
            }

            @Override
            public void onFailure(Throwable e) {
                postFail("由于未知原因发帖失败");
            }
        });
    }

    //发帖成功执行
    private void postSuccess() {
        dialog.dismiss();
        Toast.makeText(this, "主题发表成功", Toast.LENGTH_SHORT).show();
        new MyAlertDialog(this,MyAlertDialog.SUCCESS_TYPE)
                .setTitleText("发帖成功")
                .setContentText("要离开此页面吗？")
                .setCancelText("取消")
                .setConfirmText("离开")
                .setConfirmClickListener(new MyAlertDialog.OnConfirmClickListener() {
                    @Override
                    public void onClick(MyAlertDialog myAlertDialog) {
                        finish();
                    }
                });

    }
//
    //发帖失败执行
    private void postFail(String str) {
        dialog.dismiss();
        Toast.makeText(this, "发帖失败", Toast.LENGTH_SHORT).show();
    }

    private void handleInsert(String s){
        int start = ed_content.getSelectionStart();
        Editable edit = ed_content.getEditableText();//获取EditText的文字
        if (start < 0 || start >= edit.length() ){
            edit.append(s);
        }else{
            edit.insert(start,s);//光标所在位置插入文字
        }
        //[size=7][/size]
        int a = s.indexOf("[/");
        if(a>0){
            ed_content.setSelection(start+a);
        }
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()){
            case R.id.action_bold:
                handleInsert("[b][/b]");
                break;
            case R.id.action_italic:
                handleInsert("[i][/i]");
                break;
            case R.id.action_quote:
                handleInsert("[quote][/quote]");
                break;
            case R.id.action_color_text:
                myColorPicker.showAsDropDown(view, 0, 10);
                break;
            case R.id.action_emotion:
                ((ImageView)view).setImageResource(R.drawable.ic_edit_emoticon_primary_24dp);
                smileyPicker.showAsDropDown(view,0,10);
                smileyPicker.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ((ImageView)view).setImageResource(R.drawable.editer_bar_emotion_bg);
                    }
                });
                break;
            case R.id.action_backspace:
                int start = ed_content.getSelectionStart();
                int end = ed_content.getSelectionEnd();
                if(start==0){
                    return;
                }
                if((start==end)&&start>0){
                    start = start-1;
                }
                ed_content.getText().delete(start,end);
                break;
            case R.id.tv_select_forum:
                forum_spinner.setWidth(view.getWidth());
                //MySpinner.setWidth(mTView.getWidth());
                forum_spinner.showAsDropDown(view, 0, 15);
                break;
            case R.id.tv_select_type:
                String[] names = new String[typeiddatas.size()];
                for(int i=0;i<typeiddatas.size();i++){
                    names[i] = typeiddatas.get(i).second;
                }
                typeid_spinner.setData(names);
                typeid_spinner.setWidth(view.getWidth());
                typeid_spinner.showAsDropDown(view, 0, 15);
        }

    }

    private void switch_fid(int fid){
        typeiddatas.clear();
        typeId = "";
        String url = "forum.php?mod=post&action=newthread&fid="+fid+"&mobile=2";
        HttpUtil.get(this, url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                Document document = Jsoup.parse(new String(response));
                Elements types = document.select("#typeid").select("option");
                for(Element e:types){
                    typeiddatas.add(new Pair<>(e.attr("value"),e.text()));
                }
                if(typeiddatas.size()>0){
                    type_id_container.setVisibility(View.VISIBLE);
                    tv_select_type.setText(typeiddatas.get(0).second);
                    typeId = typeiddatas.get(0).first;
                }else{
                    type_id_container.setVisibility(View.GONE);
                }
            }
        });
    }

}
