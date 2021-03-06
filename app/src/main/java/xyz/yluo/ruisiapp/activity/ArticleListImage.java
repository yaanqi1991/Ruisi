package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.ArticleListImageAdapter;
import xyz.yluo.ruisiapp.data.ImageArticleListData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-31.
 * 图片文章列表activity 摄影天地等用这个模板
 */
public class ArticleListImage extends ArticleListBase {

    private List<ImageArticleListData> datas;
    private ArticleListImageAdapter adapter;
    private StaggeredGridLayoutManager layoutManager;
    private int colNum = 2;

    public static void open(Context context, int fid, String title) {
        Intent intent = new Intent(context, ArticleListImage.class);
        CurrentFid = fid;
        CurrentTitle = title;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myToolBar.setTitle(CurrentTitle);
        myToolBar.addMenu(R.drawable.ic_column_change_24dp,"CHANGE_COLUMN");

        datas = new ArrayList<>();
        layoutManager = new StaggeredGridLayoutManager(colNum, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new ArticleListImageAdapter(this, datas);
        mRecyclerView.setAdapter(adapter);

        refreshLayout.setEnabled(false);
    }

    @Override
    protected boolean OnToolBarMenuItemClick(View view,String tag) {
        if(tag.equals("CHANGE_COLUMN")){
            if (colNum == 1) {
                colNum = 2;
                layoutManager.setSpanCount(2);
            } else {
                colNum = 1;
                layoutManager.setSpanCount(1);
            }
        }
        return  super.OnToolBarMenuItemClick(view,tag);
    }

    @Override
    protected void getData() {
        String url = UrlUtils.getArticleListUrl(CurrentFid, CurrentPage, true);
        HttpUtil.get(getApplicationContext(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                new GetImageArticleListTaskRS().execute(new String(response));
            }

            @Override
            public void onFailure(Throwable e) {
                Toast.makeText(getApplicationContext(), "网络错误！！", Toast.LENGTH_SHORT).show();
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 500);

            }
        });
    }

    @Override
    protected void refresh() {
    }

    @Override
    public void onLoadMore() {
    }



    //校园网状态下获得图片板块数据 图片链接、标题等  根据html获得数据
    private class GetImageArticleListTaskRS extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String response = params[0];
            Elements list = Jsoup.parse(response).select("ul[id=waterfall]");
            Elements imagelist = list.select("li");

            for (Element tmp : imagelist) {
                //链接不带前缀
                //http://rs.xidian.edu.cn/
                String img = tmp.select("img").attr("src");
                String url = tmp.select("h3.xw0").select("a[href^=forum.php]").attr("href");
                String title = tmp.select("h3.xw0").select("a[href^=forum.php]").text();
                String author = tmp.select("a[href^=home.php]").text();
                String replyCount = tmp.select(".xg1.y").select("a[href^=forum.php]").text();
                tmp.select(".xg1.y").select("a[href^=forum.php]").remove();

                ImageArticleListData tem = new ImageArticleListData(title, url, img, author, replyCount);
                datas.add(tem);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            btn_refresh.show();
            adapter.notifyDataSetChanged();
            refreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            }, 500);
        }
    }
}
