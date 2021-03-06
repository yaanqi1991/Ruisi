package xyz.yluo.ruisiapp.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import xyz.yluo.ruisiapp.fragment.FrageForumList;
import xyz.yluo.ruisiapp.fragment.FrageHotNew;
import xyz.yluo.ruisiapp.fragment.FrageNews;

/**
 * Created by free2 on 16-5-3.
 *
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private String[] titles;
    private Fragment fragment1, fragment2, fragment3;

    public ViewPagerAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        //super(fm);
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (fragment1 == null) {
                    fragment1 = FrageForumList.newInstance(true);
                }
                return fragment1;
            case 1:
                if (fragment2 == null) {
                    fragment2 = FrageHotNew.newInstance(0);
                }
                return fragment2;
            case 2:
                if (fragment3 == null) {
                    fragment3 = new FrageNews();
                }
                return fragment3;
        }

        return null;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
