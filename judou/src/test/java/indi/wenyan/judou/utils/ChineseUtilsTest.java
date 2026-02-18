package indi.wenyan.judou.utils;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChineseUtilsTest {

    @Test
    void toChinese() {
        assertEquals("一", ChineseUtils.toChinese(new BigInteger("1")));
        assertEquals("一", ChineseUtils.toChinese(1.0));
    }

    @Test
    void toTranditionalCode() {
        assertEquals("", ChineseUtils.toTranditionalCode(""));
        assertEquals("創作具有藝術性與互動性的手機應用，例如動態畫板或音樂可視化工具", ChineseUtils.toTranditionalCode("创作具有艺术性与互动性的手机应用，例如动态画板或音乐可视化工具"));
        assertEquals("創作具有藝術性與互動性的手機應用，「例如动态画板或音乐可视化工具」", ChineseUtils.toTranditionalCode("创作具有艺术性与互动性的手机应用，「例如动态画板或音乐可视化工具」"));
        assertEquals("創作具有藝術性與互動性的手機應用，「「例如动态画板或音乐可视化工具」」", ChineseUtils.toTranditionalCode("创作具有艺术性与互动性的手机应用，「「例如动态画板或音乐可视化工具」」"));
        assertEquals("創作", ChineseUtils.toTranditionalCode("創作"));
        assertEquals("創作a", ChineseUtils.toTranditionalCode("创作a"));
        assertEquals("創作\u0001", ChineseUtils.toTranditionalCode("创作\u0001"));
    }
}
