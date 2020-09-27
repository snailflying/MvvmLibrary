package com.theone.framework.util

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.os.Build
import com.theone.framework.base.CoreApp
import java.util.*


/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019-06-19
 * @Description 语言设置相关
 */
object I18NUtil {
    var DEFAULT_LANGUAGE = "zh_TW"
    var DEFAULT_CURRENCY = "HKD"
    private const val SELECTED_LANGUAGE = "sp_select_language"

    private const val SELECTED_CURRENCY = "sp_select_currency"

    /**
     * 获取选择的语言模式
     * 例如 auto=AUTO，Chinese=zh_CN，English=en
     */
    fun getSelectedLanguage(context: Context = CoreApp.application): String {
        return SpUtil.getSpSetting(context).getString(SELECTED_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    /**
     * 获取当前货币
     * @param context Context
     * @return Currency 例如 USD
     */
    fun getSelectedCurrency(context: Context = CoreApp.application): String {
        return SpUtil.getSpSetting(context).getString(SELECTED_CURRENCY, DEFAULT_CURRENCY) ?: DEFAULT_CURRENCY
    }

    /**
     * 判断是否需要切换语言
     * @param select 语言列表中的索引
     * @return true 选择的语言与App语言不一致，需要切换
     */
    fun needChange(select: String) = select != getSelectedLanguage(CoreApp.application)

    /**
     * 保存当前货币
     * @param context Context
     * @param select Int
     */
    fun saveSelectCurrency(context: Context = CoreApp.application, select: String?) {
        SpUtil.getSpSetting(context).edit().putString(SELECTED_CURRENCY, select).apply()
    }

    /**
     * 根据索引保存用户选择的语言环境
     * @param select 语言列表中的索引
     */
    fun saveSelectLanguage(context: Context = CoreApp.application, select: String?) {
        SpUtil.getSpSetting(context).edit().putString(SELECTED_LANGUAGE, select).apply()
//        updateResource(context)
    }

    /**
     * 用于在Application和Activity的onConfigurationChanged回调中设置选择的语言
     * 在系统语言切换后，未销毁的界面调用，例如MainActivity和App
     */
    @JvmStatic
    fun updateResource(context: Context?): Context {
        val ctx = context ?: CoreApp.application
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(ctx)
        } else {
            updateResourcesLegacy(ctx)
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context): Context {
        val locale = getSelectedLocale(context)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    @Suppress("DEPRECATION")
    private fun updateResourcesLegacy(context: Context): Context {
        val locale = getSelectedLocale(context)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = resources.configuration
        val displayMetrics = resources.displayMetrics
        resources.configuration.locale = locale
        resources.updateConfiguration(configuration, displayMetrics)

        return context
    }


    private fun getSelectedLocale(context: Context): Locale {
        val selectedLanguage = getSelectedLanguage(context)
        return getLocalByName(selectedLanguage)
    }


    /**
     * 根据索引从语言列表中取得Locale对象
     * 若为跟随系统，则返回系统当前的语言环境
     */
    private fun getLocalByName(selectedLanguage: String): Locale {
        return when (selectedLanguage.toUpperCase()) {
            "ZH_HK" -> Locale.TRADITIONAL_CHINESE
            "ZH_CN" -> Locale.CHINA
            "AUTO" -> getSystemLocale()
            else -> {
                Locale(
                    selectedLanguage.substringBefore("_").toLowerCase(),
                    selectedLanguage.substringAfter("_").toUpperCase()
                )
            }
        }
    }

    /**
     * 组合语言和国家信息 zh_cn
     * @param locale Locale
     * @return String 只能是zh_CN而不能是zh_CN_#Hans，所以不能用locale.toString()
     */
//    private fun combineHeader(locale: Locale): String = locale.toString()
    private fun combineLanguage(locale: Locale): String = locale.language + "_" + locale.country


    /*
    * 获取当前手机的语言环境，
    * 在8.0系统上 Locale.getDefault()会返回当前App的语言而不是系统的 因此使用Resources的方式获取
    * 注意：application.resources.configuration相应方法获取到的为APP的当前locale，而不一定是手机系统的。
    */
    private fun getSystemLocale() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Resources.getSystem().configuration.locales[0]
    } else {
        Resources.getSystem().configuration.locale
    }
}
