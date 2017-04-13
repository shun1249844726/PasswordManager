package com.lexinsmart.xushun.passwordmanager;

import android.app.Application;

import com.orhanobut.logger.Logger;

import cn.bmob.v3.Bmob;

/**
 * Created by xushun on 2017/4/7.
 */

public class MyApplication extends Application {
    public static String APPID ="f8c48e246f77cfdf7e6f22857215c053";
    private String TAG = "LoginDemo";

    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this,APPID);

        Logger.init(TAG);

    }
}
