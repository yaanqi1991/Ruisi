package xyz.yluo.ruisiapp.View;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.utils.DimmenUtils;

/**
 * Created by free2 on 16-7-19.
 *
 */

public class MySpinner extends PopupWindow implements AdapterView.OnItemClickListener {

    private Context mContext;
    private ListView listView;
    private OnItemSelectListener listener;
    private MySpinnerListAdapter adapter;

    private int currnetSelect = 0;

    public MySpinner(Context context)
    {
        super(context);
        mContext = context;
        init();
    }


    private void init()
    {

        listView = new ListView(mContext);
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        listView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.background));
        listView.setOnItemClickListener(this);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setBackgroundDrawable(ContextCompat.getDrawable(mContext,R.drawable.my_spinner_bg));

        setFocusable(true);
        setContentView(listView);
    }

    public void setData(String[] datas){
        adapter = new MySpinnerListAdapter(datas,mContext);
        listView.setAdapter(adapter);
    }

    public void setListener(OnItemSelectListener listener) {
        this.listener = listener;
    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
        dismiss();
        if(listener!=null&&currnetSelect!=pos){
            currnetSelect = pos;
            listener.onItemSelectChanged(pos,view);
        }

    }

    public interface OnItemSelectListener{
        void onItemSelectChanged(int pos, View v);
    }

    private class MySpinnerListAdapter extends BaseAdapter {

        private String[] datas;
        private Context context;


        MySpinnerListAdapter(String[] datas, Context context) {
            this.datas = datas;
            this.context = context;
        }



        @Override
        public int getCount() {
            return datas.length;
        }

        @Override
        public Object getItem(int i) {
            return datas[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            TextView v = new TextView(context);
            v.setTag(i);
            v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            v.setText(datas[i]);
            v.setLayoutParams(new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            //textView.setTextColor(COLOR_UNSELECT);
            int padding1 = DimmenUtils.dip2px(mContext,8);
            int padding2 = DimmenUtils.dip2px(mContext,6);

            v.setPadding(padding1, padding2, padding1, padding2);
            return v;
        }
    }
}