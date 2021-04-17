package com.theone.mvvm

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*
import java.util.regex.Pattern

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val url: String = "https://www.lalala.com/acs/v1/ac_shows/13liu3456789/get_encrypt?key=vall"
        val pattern = Pattern.compile(url)
        val matcher = pattern.matcher(".*acs/v1/ac_shows/(.*)/get_encrypt.*")
        matcher.find()
        val showId = matcher.group(1)
        assertEquals(showId, "13liu3456789")

    }
}
