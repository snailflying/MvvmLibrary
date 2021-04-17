package com.theone.framework.ext

import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

/**
 * @Author zhiqiang
 * @Date 2019-06-19
 * @Description
 */
class Number_ExtKtTest {

    @Test
    fun formatNumber() {
        System.out.println("value= 0.12345678901234567890=${0.12345678901234567890.formatNumber()}")
        System.out.println("value= 1.12345678901234567890=${1.12345678901234567890.formatNumber()}")
        System.out.println("value= 12.12345678901234567890=${12.12345678901234567890.formatNumber()}")
        System.out.println("value= 123.12345678901234567890=${123.12345678901234567890.formatNumber()}")
        System.out.println("value= 1234.12345678901234567890=${1234.12345678901234567890.formatNumber()}")
        System.out.println("value= 12345.12345678901234567890=${12345.12345678901234567890.formatNumber()}")
        System.out.println("value= 123456.12345678901234567890=${123456.12345678901234567890.formatNumber()}")
        System.out.println("value= 1234567.12345678901234567890=${1234567.12345678901234567890.formatNumber()}")
        System.out.println("value= 12345678.12345678901234567890=${12345678.12345678901234567890.formatNumber()}")
        System.out.println("value= 123456789.12345678901234567890=${123456789.12345678901234567890.formatNumber()}")
        System.out.println("value= 1234567890.12345678901234567890=${1234567890.12345678901234567890.formatNumber()}")
        System.out.println("value= 12345678901.12345678901234567890=${12345678901.12345678901234567890.formatNumber()}")
        System.out.println("value= 123456789012.12345678901234567890=${123456789012.12345678901234567890.formatNumber()}")
        System.out.println("value= 1234567890123.12345678901234567890=${1234567890123.12345678901234567890.formatNumber()}")
        System.out.println("value= 12345678901234.12345678901234567890=${12345678901234.12345678901234567890.formatNumber()}")
        System.out.println("value= 123456789012345.12345678901234567890=${123456789012345.12345678901234567890.formatNumber()}")
    }



    @Test
    fun decimalFormat() {
        assertEquals("1,234,567,890.1234567891", BigDecimal("1234567890.12345678912").formatNumber())
        assertEquals("1,234,567,890", BigDecimal("1234567890.00").formatNumber())
        assertEquals("1234567890", BigDecimal("1234567890.00").formatNumber(false))
        assertEquals("1234567890.1234567891", BigDecimal("1234567890.123456789123").formatNumber(false))
        assertEquals("1.1",1.10.formatNumber())
        assertEquals("1",1.00.formatNumber())
    }

    @Test
    fun multiDecimalFormat(){
        assertEquals("203.22", BigDecimal("203.223").formatNumber(decimalNum = 2))
        assertEquals("203.22", BigDecimal("203.223456").formatNumber(decimalNum = 2))
        assertEquals("203.3", BigDecimal("203.3").formatNumber(decimalNum = 2))
        assertEquals("203", BigDecimal("203").formatNumber(decimalNum = 2))
        assertEquals("2,222,222", BigDecimal("2222222").formatNumber(decimalNum = 2))
        assertEquals("2,222,222.33", BigDecimal("2222222.333").formatNumber(decimalNum = 2))
        assertEquals("2,222,222.12345", BigDecimal("2222222.12345678").formatNumber(decimalNum = 5))
        assertEquals("2,222,222.12345", BigDecimal("2222222.12345123").formatNumber(decimalNum = 5))
        assertEquals("2,222,222.12345678", BigDecimal("2222222.1234567890").formatNumber(decimalNum = 8))
        assertEquals("2,222,222.1234567891", BigDecimal("2222222.1234567891123").formatNumber(decimalNum = null))
        assertEquals("2,222,222.123456789", BigDecimal("2222222.1234567890999").formatNumber(decimalNum = null))

    }
}