package com.theone.mvvm.home.ui

import android.os.Bundle
import android.util.Base64
import com.themone.core.util.LogUtil
import com.theone.framework.base.BaseActivity
import com.theone.framework.encrypt.AesRsaEncrypt
import com.theone.framework.ext.clickWithTrigger
import com.theone.framework.util.BeeHelper
import com.theone.mvvm.databinding.ActivityHomeBinding
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

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
            beeAndVibrateHelper = BeeHelper(this, false)
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
        /*Single.fromCallable {
            LogUtil.i("aaron $TAG time00:${System.currentTimeMillis()}")
            val encrypt1 = AesRsaEncrypt.getInstance(this).encrypt("test1", "test1")
            LogUtil.i("aaron $TAG time11:${System.currentTimeMillis()}")
            val encrypt2 = AesRsaEncrypt.getInstance(this).encrypt("test2", "test2")
            LogUtil.i("aaron $TAG time22:${System.currentTimeMillis()}")
            AesRsaEncrypt.getInstance(this).encrypt("test1", encrypt1)
            LogUtil.i("aaron $TAG time33:${System.currentTimeMillis()}")
            AesRsaEncrypt.getInstance(this).decrypt("test2", encrypt2)
            LogUtil.i("aaron $TAG time44:${System.currentTimeMillis()}")
        }.subscribeOn(Schedulers.single()).subscribe().addTo(compositeDisposable)*/
        testDecryptByPublicKey()

    }

    fun testEncryptByPrivateKey() {
        val privateKey =
            "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAhzJJKH3r7qDTaQvdTn8bJScTiVECxRUOtuNylICmd+vq6N9DUk8N6NAIXiqIHMrEdoywQUSjCnajlzwtLEs4DQIDAQABAkBdXv9jtcPSJMSdkhIf+mz29cvqVEbDck2dReyGX2uY+hamAYwoxhff/gbRonj5OqR179fK34pYfPis+3JoRdlBAiEAyLrVqSpiEmFyRQfOKO7CE/s2e6OVY3unQXRqy+qYEfkCIQCsbBf0wFRqP9SLfgklhlB3Gr4YgHVV7sL6mSmX/DtbtQIhAIcw5nQPuoucm+SIND53R7lDaVduPlAJWQWJjeAW+SKpAiEAhmkpb6my5LTnqupQlQkUlxSo1g7l6VxccOCPNSTy3PUCIGrG25fPjS9sC7jhrBEa7LxA99mUadUVomxGHmq4QYIo"
        val data = "6006447057d98e46b2a58741|shshshshs"
        val result = encryptByPrivateKey(data, privateKey)
        print("result:$result")
    }

    fun testDecryptByPublicKey() {
        val publicKey =
            "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIcySSh96+6g02kL3U5/GyUnE4lRAsUVDrbjcpSApnfr6ujfQ1JPDejQCF4qiBzKxHaMsEFEowp2o5c8LSxLOA0CAwEAAQ=="
        val data = "PJ1_AYQdopjcLKgNfgM/X3xj9ULFzAgXgd3m8BsiuxTF8KAn1L4/QP4Ya44LiJ/v+/ZlTjbTA2fVJVKzcVjQLict5w=="
        val result = decryptByPublicKey(data, publicKey)
        print("result:$result")
    }

    val RSA = "RSA"

    /**
     * RSA 使用私钥进行解密
     * 如果返回空则表明解密错误
     */
    fun decryptByPublicKey(data: String, publicKeyBase64: String): String? {
        return try {
            //base64编码的私钥
            val decoded: ByteArray = Base64.decode(publicKeyBase64, Base64.NO_WRAP)
            val pubKey = KeyFactory.getInstance(RSA).generatePublic(
                X509EncodedKeySpec(decoded)
            ) as RSAPublicKey
            //64位解码加密后的字符串
            val inputByte: ByteArray = Base64.decode(publicKeyBase64, Base64.NO_WRAP)
            //RSA解密
            val cipher = Cipher.getInstance(RSA)
            cipher.init(Cipher.DECRYPT_MODE, pubKey)
            String(cipher.doFinal(inputByte))
        } catch (e: Exception) {
            print("解密失败，src：{$data}, publicKeyBase64:{$publicKeyBase64+}$e")
            null
        }
    }

    /**
     * 执行加密
     */
    fun encryptByPrivateKey(src: String, privateKey: String): String? {
        try {
            val decoded: ByteArray = Base64.decode(privateKey, Base64.NO_WRAP)
            val rsaPrivateKey = KeyFactory.getInstance(RSA)
                .generatePrivate(PKCS8EncodedKeySpec(decoded)) as RSAPrivateKey
            val cipher = Cipher.getInstance(RSA)
            cipher.init(Cipher.ENCRYPT_MODE, rsaPrivateKey)
            val srcBytes = src.toByteArray()
            val resultBytes = cipher.doFinal(srcBytes)
            return Base64.encode(resultBytes, Base64.NO_WRAP).toString()
        } catch (exp: Exception) {
            print("加密失败，src：{$src}, privateKey:{$privateKey+}$exp")

        }
        return null
    }
}
