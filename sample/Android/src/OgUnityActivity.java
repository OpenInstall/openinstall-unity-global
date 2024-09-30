package com.openinstallglobal.unity;

import android.content.Intent;
import android.os.Bundle;

import com.unity3d.player.UnityPlayerActivity;

public class OgUnityActivity extends UnityPlayerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
        OpenInstallGlobalHelper.getWakeUp(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        OpenInstallGlobalHelper.getWakeUp(intent);
    }

}
