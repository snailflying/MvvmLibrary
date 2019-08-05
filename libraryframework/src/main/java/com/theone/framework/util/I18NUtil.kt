package com.theone.framework.util

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.os.Build
import java.util.*


/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019-06-19
 * @Description 语言设置相关
 */
object I18NUtil {

    private val DEFAULT_LANGUAGE = I18NUtil.LanguageSupported.AUTO.ordinal
    private val DEFAULT_CURRENCY = I18NUtil.CurrencySupported.HKD.ordinal
    private const val SELECTED_LANGUAGE = "sp_selected_language"

    private const val SELECTED_CURRENCY = "sp_selected_currency"
    /**
     * 用于设置Http的header language设置
     * 获取系统当前语言，中文则返回中文，非中文则返回英文
     * @return 例如 zh_CN,en_US ...
     */
    fun getLocalLanguageCode(context: Context) = combineLanguage(
        getSelectedLocale(context)
    )

    /**
     * 获取设置的货币Code
     * @return (kotlin.String..kotlin.String?)
     */
    fun getCurrencyCode(context: Context) = getSelectedCurrency(context).name

    /**
     * 判断是否需要切换语言
     * @param select 语言列表中的索引
     * @return true 选择的语言与App语言不一致，需要切换
     */
    fun needChange(context: Context, select: Int) = select != getSelectedLanguagePosition(
        context
    )

    /**
     * 保存当前货币
     * @param context Context
     * @param select Int
     */
    fun saveSelectCurrency(context: Context, select: Int) {
        SpUtil.getSpSetting(context)
            .edit().putInt(SELECTED_CURRENCY, select).apply()
    }

    /**
     * 获取当前货币
     * @param context Context
     * @return Currency
     */
    fun getSelectedCurrency(context: Context): CurrencySupported {
        return I18NUtil.CurrencySupported.getCurrencyBy(
            getSelectedCurrencyPosition(
                context
            )
        )
    }

    /**
     * 获取当前货币位置
     * @param context Context
     * @return Currency
     */
    fun getSelectedCurrencyPosition(context: Context): Int {
        return SpUtil.getSpSetting(context).getInt(
            SELECTED_CURRENCY,
            DEFAULT_CURRENCY
        )
    }

    /**
     * 根据索引保存用户选择的语言环境
     * @param select 语言列表中的索引
     */
    fun saveSelectLanguage(context: Context, select: Int) {
        SpUtil.getSpSetting(context)
            .edit().putInt(SELECTED_LANGUAGE, select).apply()
    }

    /**
     * 获取选择的语言模式
     * 例如 auto=0，Chinese=1，English=2
     */
    fun getSelectedLanguage(context: Context): LanguageSupported {
        return I18NUtil.LanguageSupported.getLanguageBy(
            getSelectedLanguagePosition(
                context
            )
        )
    }

    /**
     * 获取选择的语言模式
     * 例如 auto=0，Chinese=1，English=2
     */
    fun getSelectedLanguagePosition(context: Context): Int {
        return SpUtil.getSpSetting(context).getInt(
            SELECTED_LANGUAGE,
            DEFAULT_LANGUAGE
        )
    }


    /**
     * 用于在Application和Activity的onConfigurationChanged回调中设置选择的语言
     * 在系统语言切换后，未销毁的界面调用，例如MainActivity和App
     */
    fun updateResource(context: Context?): Context? {
        if (context == null) return null
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context)
        } else {
            updateResourcesLegacy(context)
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


    /**
     * 获取用户选择的语言环境
     * 若为跟随系统，则返回当前系统的语言环境
     */
    private fun getSelectedLocale(context: Context): Locale {
        val selectedLanguage = getSelectedLanguagePosition(context)
        return getLocalByIndex(selectedLanguage)
    }


    /**
     * 根据索引从语言列表中取得Locale对象
     * 若为跟随系统，则返回系统当前的语言环境
     */
    private fun getLocalByIndex(selectedLanguage: Int): Locale {
        return when (selectedLanguage) {
            I18NUtil.LanguageSupported.ZH_CN.ordinal -> Locale.CHINA
            I18NUtil.LanguageSupported.ZH_HK.ordinal -> Locale.TRADITIONAL_CHINESE
            I18NUtil.LanguageSupported.EN.ordinal -> Locale.US
            else -> getSystemLocale()
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

    /**
     * 语言
     */
    enum class LanguageSupported {
        AUTO,
        ZH_CN,
        ZH_HK,
        EN;

        companion object {
            fun getLanguageBy(position: Int): LanguageSupported {
                return when (position) {
                    ZH_CN.ordinal -> ZH_CN
                    ZH_HK.ordinal -> ZH_HK
                    EN.ordinal -> EN
                    else -> AUTO
                }
            }
        }
    }

    /**
     * 货币
     */
    enum class CurrencySupported {

        /**
         * 港币
         */
        HKD,
        /**
         * 美元
         */
        USD;

        companion object {
            fun getCurrencyBy(position: Int): CurrencySupported {
                return when (position) {
                    HKD.ordinal -> HKD
                    else -> USD
                }
            }
        }

    }
}
