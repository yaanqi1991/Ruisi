package xyz.yluo.ruisiapp.utils;

import android.graphics.Color;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by free2 on 16-3-19.
 * 通过链接获得tid
 * uid
 * 各种id
 */
public class GetId {

    public static String getid(String url) {
        Pattern pattern = Pattern.compile("tid=[0-9]{3,}");
        Matcher matcher = pattern.matcher(url);
        String tid = "";
        if (matcher.find()) {
            tid = url.substring(matcher.start() + 4, matcher.end());
        }
        return tid;
    }

    //searchid=1340
    public static int getSearchId(String url) {
        Pattern pattern = Pattern.compile("searchid=[0-9]{3,}");
        Matcher matcher = pattern.matcher(url);
        String id = "0";
        if (matcher.find()) {
            id = url.substring(matcher.start() + 9, matcher.end());
        }
        return Integer.parseInt(id);
    }


    public static String getTouid(String url) {
        //touid="+userUid
        Pattern pattern = Pattern.compile("touid=[0-9]{3,}");
        Matcher matcher = pattern.matcher(url);
        String touid = "";
        if (matcher.find()) {
            touid = url.substring(matcher.start() + 6, matcher.end());
        }
        return touid;
    }

    public static String getHash(String url) {
        try {
            //fid=[0-9]+
            Pattern pattern = Pattern.compile("formhash=.*&");
            Matcher matcher = pattern.matcher(url);
            String hash = "";
            if (matcher.find()) {
                hash = url.substring(matcher.start() + 9, matcher.end() - 1);
            }

            return hash;

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static String getTid(String url) {
        //forum.php?mod=redirect&goto=findpost&ptid=846689&pid=21330831
        Pattern pattern = Pattern.compile("tid=[0-9]{3,}");
        Matcher matcher = pattern.matcher(url);
        String tid = "";
        if (matcher.find()) {
            tid = url.substring(matcher.start() + 4, matcher.end());
        }
        return tid;
    }

    public static int getPage(String url) {
        //forum.php?mod=redirect&goto=findpost&ptid=846689&pid=21330831
        Pattern pattern = Pattern.compile("page=[0-9]{1,}");
        Matcher matcher = pattern.matcher(url);
        int page = 1;
        if (matcher.find()) {
            page = Integer.parseInt(url.substring(matcher.start() + 5, matcher.end()));
        }
        return page;
    }

    public static String getPid(String url) {
        //forum.php?mod=redirect&goto=findpost&ptid=846689&pid=21330831
        Pattern pattern = Pattern.compile("pid=[0-9]{3,}");
        Matcher matcher = pattern.matcher(url);
        String pid = "";
        if (matcher.find()) {
            pid = url.substring(matcher.start() + 4, matcher.end());
        }
        return pid;
    }

    public static String getUid(String url) {
        if (null == url) {
            return "";
        }
        String uid = "";
        if (url.contains("uid=")) {
            //http://rs.xidian.edu.cn/ucenter/avatar.php?uid=284747&size=small
            Pattern pattern = Pattern.compile("uid=[0-9]{2,}");
            Matcher matcher = pattern.matcher(url);

            if (matcher.find()) {
                uid = url.substring(matcher.start() + 4, matcher.end());
            }
        } else {
            Pattern patternNum = Pattern.compile("[0-9]{2,}");
            Matcher matcherNum = patternNum.matcher(url);
            if (matcherNum.find() && url.length() == matcherNum.end() - matcherNum.start()) {
                uid = url;
            }
        }
        return uid;
    }

    public static String getFroumFid(String url) {

        try {
            //fid=[0-9]+
            Pattern pattern = Pattern.compile("fid=[0-9]+");
            Matcher matcher = pattern.matcher(url);
            String fid = "";
            if (matcher.find()) {
                fid = url.substring(matcher.start() + 4, matcher.end());
            }
            if (fid.equals("106")) {
                fid = "110";
            } else if (fid.equals("553")) {
                fid = "554";
            }
            return fid;

        } catch (Exception e) {
            e.printStackTrace();

            return "0";
        }
    }

    public static int getColor(String str) {
        //<a href="forum.php?mod=viewthread&amp;tid=829659&amp;extra=page%3D1&amp;mobile=2" style="color: #EC1282;">
        int color = 0xff000000;
        if(str.contains("color")){
            int start = str.indexOf("color");
            int end = str.indexOf(";",start);
            String temp = str.substring(start,end);

            int start_c = temp.indexOf("#");

            String colorStr = temp.substring(start_c).trim();
            try {
                color =  Color.parseColor(colorStr);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(str.startsWith("#")){
            try {
                color =  Color.parseColor(str);
            }catch (Exception e){
                Log.e("color",color+"");
                e.printStackTrace();
            }
        }
        return color;
    }
}
