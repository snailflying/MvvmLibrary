package com.theone.framework.util

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*


/**
 * @Author zhiqiang
 * @Email liuzhiqiang@moretickets.com
 * @Date 2019-06-19
 * @Description
 */
object DateUtils {

    /**
     * 年-月-日 时:分:秒 显示格式
     */
    // 备注:如果使用大写HH标识使用24小时显示格式,如果使用小写hh就表示使用12小时制格式。
    var DATE_TO_STRING_DETAIAL_PATTERN = "yyyy.MM.dd HH:mm:ss"

    /**
     * 年-月-日 显示格式
     */
    var DATE_TO_STRING_SHORT_PATTERN = "yyyy.MM.dd"

    private var simpleDateFormat: SimpleDateFormat? = null


    /**
     * 获取现在时间
     *
     * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
     */
    val nowDate: Date
        get() {
            val currentTime = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val dateString = formatter.format(currentTime)
            val pos = ParsePosition(8)
            return formatter.parse(dateString, pos)
        }

    /**
     * 获取现在时间
     *
     * @return返回短时间格式 yyyy-MM-dd
     */
    val nowDateShort: Date
        get() {
            val currentTime = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val dateString = formatter.format(currentTime)
            val pos = ParsePosition(8)
            return formatter.parse(dateString, pos)
        }

    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    val stringDate: String
        get() {
            val currentTime = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return formatter.format(currentTime)
        }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    val stringDateShort: String
        get() {
            val currentTime = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            return formatter.format(currentTime)
        }

    /**
     * 获取时间 小时:分;秒 HH:mm:ss
     *
     * @return
     */
    val timeShort: String
        get() {
            val formatter = SimpleDateFormat("HH:mm:ss")
            val currentTime = Date()
            return formatter.format(currentTime)
        }

    /**
     * 得到现在时间
     *
     * @return
     */
    val now: Date
        get() = Date()

    /**
     * 得到现在时间
     *
     * @return 字符串 yyyyMMdd HHmmss
     */
    val stringToday: String
        get() {
            val currentTime = Date()
            val formatter = SimpleDateFormat("yyyyMMdd HHmmss")
            return formatter.format(currentTime)
        }

    /**
     * 得到现在小时
     */
    val hour: String
        get() {
            val currentTime = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val dateString = formatter.format(currentTime)
            val hour: String
            hour = dateString.substring(11, 13)
            return hour
        }

    /**
     * 得到现在分钟
     *
     * @return
     */
    val time: String
        get() {
            val currentTime = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val dateString = formatter.format(currentTime)
            val min: String
            min = dateString.substring(14, 16)
            return min
        }


    /**
     * 产生周序列,即得到当前时间所在的年度是第几周
     *
     * @return
     */
    val seqWeek: String
        get() {
            val c = Calendar.getInstance(Locale.CHINA)
            var week = Integer.toString(c.get(Calendar.WEEK_OF_YEAR))
            if (week.length == 1)
                week = "0$week"
            val year = Integer.toString(c.get(Calendar.YEAR))
            return year + week
        }

    /**
     * Date类型转为指定格式的String类型
     *
     * @param source
     * @param pattern
     * @return
     */
    fun DateToString(source: Date, pattern: String): String {
        simpleDateFormat = SimpleDateFormat(pattern)
        return simpleDateFormat!!.format(source)
    }

    /**
     * 根据long获取日期 yyyy.MM.dd HH:mm
     */
    fun getDateText(longTime: Long): String {
        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return sdf.format(Date(longTime))
    }

    /**
     * 根据long获取日期 yyyy.MM.dd
     */
    fun getDayDateText(longTime: Long): String {
        val sdf = SimpleDateFormat("yyyy.MM.dd")
        return sdf.format(Date(longTime))
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    fun strToDateLong(strDate: String): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val pos = ParsePosition(0)
        return formatter.parse(strDate, pos)
    }

    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss  *   * @param dateDate  * @return
     */
    fun dateToStrLong(dateDate: Date): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return formatter.format(dateDate)
    }

    /**
     * 将短时间格式时间转换为字符串 yyyy-MM-dd
     *
     * @param dateDate
     * @param
     * @return
     */
    fun dateToStr(dateDate: Date): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        return formatter.format(dateDate)
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    fun strToDate(strDate: String): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val pos = ParsePosition(0)
        return formatter.parse(strDate, pos)
    }

    /**
     * 提取一个月中的最后一天
     *
     * @param day
     * @return
     */
    fun getLastDate(day: Long): Date {
        val date = Date()
        val date_3_hm = date.time - 3600000 * 34 * day
        return Date(date_3_hm)
    }

    /**
     * 根据用户传入的时间表示格式，返回当前时间的格式 如果是yyyyMMdd，注意字母y不能大写。
     *
     * @param sformat yyyyMMddhhmmss
     * @return
     */
    fun getUserDate(sformat: String): String {
        val currentTime = Date()
        val formatter = SimpleDateFormat(sformat)
        return formatter.format(currentTime)
    }

    /**
     * 二个小时时间间的差值,必须保证二个时间都是"HH:MM"的格式，返回字符型的分钟
     */
    fun getTwoHour(st1: String, st2: String): String {
        var kk: Array<String>? = null
        var jj: Array<String>? = null
        kk = st1.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        jj = st2.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (Integer.parseInt(kk[0]) < Integer.parseInt(jj[0])) {
            return "0"
        } else {
            val y = java.lang.Double.parseDouble(kk[0]) + java.lang.Double.parseDouble(kk[1]) / 60
            val u = java.lang.Double.parseDouble(jj[0]) + java.lang.Double.parseDouble(jj[1]) / 60
            return if (y - u > 0) {
                (y - u).toString() + ""
            } else {
                "0"
            }
        }
    }

    /**
     * 得到二个日期间的间隔天数
     */
    fun getTwoDay(sj1: String, sj2: String): String {
        val myFormatter = SimpleDateFormat("yyyy-MM-dd")
        var day: Long = 0
        try {
            val date = myFormatter.parse(sj1)
            val mydate = myFormatter.parse(sj2)
            day = (date.time - mydate.time) / (24 * 60 * 60 * 1000)
        } catch (e: Exception) {
            return ""
        }

        return day.toString() + ""
    }

    /**
     * 时间前推或后推分钟,其中JJ表示分钟.
     */
    fun getPreTime(sj1: String, jj: String): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var mydate1 = ""
        try {
            val date1 = format.parse(sj1)
            val Time = date1.time / 1000 + Integer.parseInt(jj) * 60
            date1.time = Time * 1000
            mydate1 = format.format(date1)
        } catch (e: Exception) {
        }

        return mydate1
    }

    /**
     * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
     */
    fun getNextDay(nowdate: String, delay: String): String {
        try {
            val format = SimpleDateFormat("yyyy-MM-dd")
            var mdate = ""
            val d = strToDate(nowdate)
            val myTime = d.time / 1000 + Integer.parseInt(delay) * 24 * 60 * 60
            d.time = myTime * 1000
            mdate = format.format(d)
            return mdate
        } catch (e: Exception) {
            return ""
        }

    }

    /**
     * 判断是否润年
     *
     * @param ddate
     * @return
     */
    fun isLeapYear(ddate: String): Boolean {

        /**
         * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
         * 3.能被4整除同时能被100整除则不是闰年
         */
        val d = strToDate(ddate)
        val gc = Calendar.getInstance() as GregorianCalendar
        gc.time = d
        val year = gc.get(Calendar.YEAR)
        return if (year % 400 == 0) {
            true
        } else if (year % 4 == 0) {
            year % 100 != 0
        } else {
            false
        }
    }

    /**
     * 返回美国时间格式 26 Apr 2006
     *
     * @param str
     * @return
     */
    fun getEDate(str: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val pos = ParsePosition(0)
        val strtodate = formatter.parse(str, pos)
        val j = strtodate.toString()
        val k = j.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return k[2] + k[1].toUpperCase() + k[5].substring(2, 4)
    }

    /**
     * 获取一个月的最后一天
     *
     * @param dat
     * @return
     */
    fun getEndDateOfMonth(dat: String): String {// yyyy-MM-dd
        var str = dat.substring(0, 8)
        val month = dat.substring(5, 7)
        val mon = Integer.parseInt(month)
        if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8 || mon == 10 || mon == 12) {
            str += "31"
        } else if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {
            str += "30"
        } else {
            if (isLeapYear(dat)) {
                str += "29"
            } else {
                str += "28"
            }
        }
        return str
    }


    /**
     * 两个时间之间的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    fun getDays(date1: String?, date2: String?): Long {
        if (date1 == null || date1 == "") {
            return 0
        }
        if (date2 == null || date2 == "") {
            return 0
        }
        // 转换为标准时间
        val myFormatter = SimpleDateFormat("yyyy-MM-dd")
        var date: Date? = null
        var mydate: Date? = null
        try {
            date = myFormatter.parse(date1)
            mydate = myFormatter.parse(date2)
        } catch (e: Exception) {
        }

        return (date!!.time - mydate!!.time) / (24 * 60 * 60 * 1000)
    }

}
