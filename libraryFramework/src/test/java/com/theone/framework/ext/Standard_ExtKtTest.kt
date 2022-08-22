package com.theone.framework.ext

import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * @Author zhiqiang
 * @Date 2019-11-21
 * @Email liuzhiqiang@theone.com
 * @Description
 */
class Standard_ExtKtTest {

    @Before
    fun setUp() {
    }

    val nullString: String? = null
    val testString: String? = "test"

    @Test
    fun alsoNotNull() {
        testString.alsoNotNull {
            Assert.assertEquals(it, testString)
        }
        nullString.alsoNotNull {

        }
    }

    @Test
    fun elvis() {
        val a = nullString.elvis("a")
        val b = testString.elvis("notB")
        val c = nullString.elvis { "c" }
        val d = testString.elvis { "notD" }
        Assert.assertEquals(a, "a")
        Assert.assertEquals(b, "test")
        Assert.assertEquals(c, "c")
        Assert.assertEquals(d, "test")
    }

    @Test
    fun runNotNull() {
        val equals1 = testString.runNotNull({
            1
        }, 2)
        val equals2 = nullString.runNotNull({
            1
        }, 2)
        val equals3 = testString.runNotNull({
            3
        }, { 4 })
        val equals4 = nullString.runNotNull({
            3
        }, { 4 })

        Assert.assertEquals(equals1, 1)
        Assert.assertEquals(equals2, 2)
        Assert.assertEquals(equals3, 3)
        Assert.assertEquals(equals4, 4)


    }

    /**
     * 高阶函数结果不影响返回值
     */
    @Test
    fun runNullThen() {
        val test = testString.alsoNotNull {
            "无关紧要"
        }.thenNull { "result" }
        val isNull = nullString.alsoNotNull {
            "无关紧要"
        }.thenNull { "result" }


        Assert.assertEquals(test, "test")
        Assert.assertEquals(isNull, null)
    }

    @Test
    fun thenNull() {
    }
}