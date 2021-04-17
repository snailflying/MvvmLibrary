package com.theone.framework

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*
import java.util.regex.Pattern

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SplitUnitTest {
    @Test
    fun addition_isCorrect() {
        val url: String = "https://www.lalala.com/acs/v1/ac_shows/13liu3456789/get_encrypt?key=vall"
        val pattern = Pattern.compile(url)
        val matcher = pattern.matcher(".*acs/v1/ac_shows/(.*)/get_encrypt.*")
        matcher.find()
        val showId = matcher.group(1)
        assertEquals(showId, "13liu3456789")

    }

    @Test
    fun testCombineSplit() {
        val result = StringBuilder()
        val projects = ArrayList<String>()
        projects.add("a")
        projects.add("b")
        for (project in projects) {
            result.append(project).append(",")
        }
        //todo:注意点dropLastWhile
        val resultStr = result.toString().dropLastWhile { it.toString() == "," }
        assertEquals(resultStr, "a,b")
        val result1 = StringBuilder()
        val projects1 = ArrayList<String>()
        projects1.add("a")
        for (project in projects1) {
            result1.append(project).append(",")
        }
        //todo:注意点result.length - 1
        val resultStr1 = result1.toString().substring(0, result1.length - 1)
        assertEquals(resultStr1, "a")
    }

    @Test
    fun testSplit() {
        val input1 = "a".split(",")
        val result1: MutableList<String> = ArrayList()
        result1.add("a")
        assertEquals(input1, result1)
        val input2 = "a,b".split(",")
        val result2: MutableList<String> = ArrayList()
        result2.add("a")
        result2.add("b")
        assertEquals(input2, result2)
        //todo:注意点dropLastWhile，否则会有空值
        val input3 = "a,".split(",").dropLastWhile { it.isEmpty()}
        val result3: MutableList<String> = ArrayList()
        result3.add("a")
//        result3.add("")
        assertEquals(input3, result3)
    }



}
