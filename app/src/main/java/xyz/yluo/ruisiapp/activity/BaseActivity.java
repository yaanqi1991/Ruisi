package xyz.yluo.ruisiapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.View.MyAlertDialog.MyAlertDialog;
import xyz.yluo.ruisiapp.View.MyToolBar;

/**
 * Created by free2 on 16-4-11.
 * 所有activity的基类
 */
public class BaseActivity extends AppCompatActivity {

    //判断是否需要弹出登录dialog
    boolean isneed_login() {
        if (App.ISLOGIN) {
            return true;
        } else {
            new MyAlertDialog(this,MyAlertDialog.WARNING_TYPE)
                    .setTitleText("需要登录登陆")
                    .setCancelText("取消")
                    .setContentText("你还没有登陆，要去登陆吗？？")
                    .setConfirmClickListener(new MyAlertDialog.OnConfirmClickListener() {
                        @Override
                        public void onClick(MyAlertDialog myAlertDialog) {
                            startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                        }
                    }).show();
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case  android.R.id.home:
            finish();
            break;

        }
        return super.onOptionsItemSelected(item);
    }

    protected void setToolBarMenuClick(MyToolBar myToolBar){
        myToolBar.setToolBarClickListener(new MyToolBar.OnToolBarItemClick() {
            @Override
            public void OnItemClick(View v, String Tag) {
                OnToolBarMenuItemClick(v,Tag);
            }
        });
    }

    protected  boolean OnToolBarMenuItemClick(View view,String tag){
        switch (tag){
            case "SEARCH":
                if(isneed_login())
                startActivity(new Intent(this,ActivitySearch.class));
                return true;
            case "POST":
                if(isneed_login())
                startActivity(new Intent(this,NewArticleActivity.class));
                return true;
            case "POST2":
                if(isneed_login())
                startActivity(new Intent(this, NewArticleActivity_2.class));
                return true;
            case "DEBUG":
                startActivity(new Intent(this, TestActivity.class));
                return true;
        }

        return false;
    }
}
