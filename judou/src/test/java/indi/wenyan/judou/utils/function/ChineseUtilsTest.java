package indi.wenyan.judou.utils.function;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChineseUtilsTest {

    @Test
    void toChinese() {
        assertEquals("一", ChineseUtils.toChinese(new BigInteger("1")));
        assertEquals("一", ChineseUtils.toChinese(1.0));
    }
}
