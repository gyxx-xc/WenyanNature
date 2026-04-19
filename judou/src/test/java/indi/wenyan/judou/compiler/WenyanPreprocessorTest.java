package indi.wenyan.judou.compiler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WenyanPreprocessorTest {
    @Test
    void testConvertedCode() {
        assertEquals("", WenyanPreprocessor.preprocess(""));
        assertEquals("創作具有藝術性與互動性的手機應用，例如動態畫板或音樂可視化工具", WenyanPreprocessor.preprocess("创作具有艺术性与互动性的手机应用，例如动态画板或音乐可视化工具"));
        assertEquals("創作具有藝術性與互動性的手機應用，「例如动态画板或音乐可视化工具」", WenyanPreprocessor.preprocess("创作具有艺术性与互动性的手机应用，「例如动态画板或音乐可视化工具」"));
        assertEquals("創作具有藝術性與互動性的手機應用，「「例如动态画板或音乐可视化工具」」", WenyanPreprocessor.preprocess("创作具有艺术性与互动性的手机应用，「「例如动态画板或音乐可视化工具」」"));
        assertEquals("創作", WenyanPreprocessor.preprocess("創作"));
        assertEquals("創作a", WenyanPreprocessor.preprocess("创作a"));
        assertEquals("創作\u0001", WenyanPreprocessor.preprocess("创作\u0001"));
    }
}
