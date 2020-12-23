package com.theone.mvvm.home.ui

import android.os.Bundle
import com.themone.core.util.LogUtil
import com.theone.framework.base.BaseActivity
import com.theone.framework.encrypt.AesRsaEncrypt
import com.theone.framework.ext.clickWithTrigger
import com.theone.framework.util.BeeHelper
import com.theone.mvvm.databinding.ActivityHomeBinding
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers

class HomeActivity : BaseActivity() {

    lateinit var binding: ActivityHomeBinding
    val TAG = "HomeActivity"
    private var beeAndVibrateHelper: BeeHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        beeAndVibrateHelper = BeeHelper(this)
        binding.tvHello.clickWithTrigger {
            tes1t()
        }
        binding.playSound.setOnClickListener {
            beeAndVibrateHelper?.playBeeSuccess()
        }
        binding.playMedia.setOnClickListener {
            beeAndVibrateHelper?.playMediaSound()
        }
        binding.releaseSound.setOnClickListener {
            beeAndVibrateHelper?.release()
            beeAndVibrateHelper = BeeHelper(this)
        }
        test()
    }

    override fun onDestroy() {
        beeAndVibrateHelper?.release()
        super.onDestroy()
    }

    fun test() {
        LogUtil.i("aaron $TAG time00:${System.currentTimeMillis()}")
        val encrypt1 = AesRsaEncrypt.getInstance(this).encrypt("test1", "test1")
        LogUtil.i("aaron $TAG time11:${System.currentTimeMillis()}")
        val encrypt2 = AesRsaEncrypt.getInstance(this).encrypt("test2", "test2")
        LogUtil.i("aaron $TAG time22:${System.currentTimeMillis()}")
        AesRsaEncrypt.getInstance(this).encrypt("test1", encrypt1)
        LogUtil.i("aaron $TAG time33:${System.currentTimeMillis()}")
        AesRsaEncrypt.getInstance(this).decrypt("test2", encrypt2)
        LogUtil.i("aaron $TAG time44:${System.currentTimeMillis()}")
    }

    fun tes1t() {
        Single.fromCallable {
            LogUtil.i("aaron $TAG time00:${System.currentTimeMillis()}")
            val encrypt1 = AesRsaEncrypt.getInstance(this).encrypt("test1", "test1")
            LogUtil.i("aaron $TAG time11:${System.currentTimeMillis()}")
            val encrypt2 = AesRsaEncrypt.getInstance(this).encrypt("test2", "test2")
            LogUtil.i("aaron $TAG time22:${System.currentTimeMillis()}")
            AesRsaEncrypt.getInstance(this).encrypt("test1", encrypt1)
            LogUtil.i("aaron $TAG time33:${System.currentTimeMillis()}")
            AesRsaEncrypt.getInstance(this).decrypt("test2", encrypt2)
            LogUtil.i("aaron $TAG time44:${System.currentTimeMillis()}")
        }.subscribeOn(Schedulers.single()).subscribe().addTo(compositeDisposable)

    }
}
