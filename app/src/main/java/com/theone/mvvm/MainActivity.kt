package com.theone.mvvm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.theone.framework.base.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
