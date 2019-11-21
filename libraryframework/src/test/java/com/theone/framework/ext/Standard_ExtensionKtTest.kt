package com.theone.framework.ext

import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * @Author zhiqiang
 * @Date 2019-11-21
 * @Email liuzhiqiang@moretickets.com
 * @Description
 */
class Standard_ExtensionKtTest {

    @Before
    fun setUp() {
    }

    @Test
    fun isNotNull() {
    }

    @Test
    fun elvis() {
    }

    @Test
    fun elvis1() {
    }

    @Test
    fun runNotNull() {
    }

    @Test
    fun runNotNull1() {
    }

    @Test
    fun alsoNotNull() {
        val test: String? = null
        val test1: String? = "test1"
        test.alsoNotNull {
            System.out.println("testName:$it")
        }.thenNull {
            println("test is null")
        }
        test1.alsoNotNull {
            Assert.assertEquals(it,"test1")
        }.thenNull {
            println("test1 is null")
        }
    }

    @Test
    fun thenNull() {
    }
}