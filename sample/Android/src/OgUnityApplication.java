package com.openinstallglobal.unity;

import android.app.Application;

import io.openinstall.api.OpenInstall;


/**
 * Created by wade on 2023/10/30.
 * Copyright (c) 2023 OpenInstall All rights reserved.
 * Descride :
 */
public class OgUnityApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 预初始化，不会采集设备信息也不会和服务器交互
        OpenInstall.initialize(this);
    }
}
