package co.yappuworld.global.util

import co.yappuworld.global.util.StringUtils.isPhoneNumber
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringUtilsTest {

    @Test
    fun `핸드폰 번호는 통과`() {
        assertTrue { "010-1234-5678".isPhoneNumber() }
    }

    @Test
    fun `핸드폰 번호 아닌 애들 잡아낸다`() {
        listOf(
            "010-1234-567",
            "010-1234-56789",
            "01012345678",
            "010-1234-5678-",
            "010-1234-5678a",
            "010-1234-a678"
        ).forEach {
            assertFalse { it.isPhoneNumber() }
        }
    }
}
