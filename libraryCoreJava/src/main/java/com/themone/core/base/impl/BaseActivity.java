package com.themone.core.base.impl;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.themone.core.util.StatusBarUtil;

/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc 简单 Activity，无业务逻辑可继承此类
 */
public class BaseActivity extends AppCompatActivity {

    protected Activity mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTransparentForWindow(this);
        mContext = this;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if (isFitsSystemWindows()) {
            StatusBarUtil.setFitsSystemWindows(this);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public boolean isFitsSystemWindows() {
        return true;
    }
}
