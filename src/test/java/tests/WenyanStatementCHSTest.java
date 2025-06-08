package tests;

import indi.wenyan.interpreter.compiler.WenyanBytecode;
import indi.wenyan.interpreter.compiler.WenyanCompilerEnvironment;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.compiler.visitor.WenyanMainVisitor;
import indi.wenyan.interpreter.compiler.visitor.WenyanVisitor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class WenyanStatementCHSTest {

    public static final Map<String, Boolean> codes = new HashMap<>() {{
        put("", true);
        put("夫", false);
        put("夫二十一亿四千七百四十八万三千六百四十七", true);
        put("夫一", true);
        put("夫二", true);
        put("夫「「a」」", true);
        put("夫一又一分", true);
        put("夫其", true);
        put("夫之", true);
        put("夫「a」", true);
        put("夫己", true);
        put("夫之之「「a」」", true);
        put("夫之之一", true);
        put("夫「a」之一", true);
        put("夫一之「「a」」", true);
        put("夫「a」之二十一亿四千七百四十八万三千六百四十七", true);
        put("夫己之「a」", true);
        put("夫「a」之「「a」」之「「a」」之「「b」」之一之「「a」」", true);

        put("吾有一数曰一", true);
        put("吾有二十一亿四千七百四十八万三千六百四十七数", false);
        put("吾有一数曰之", true);
        put("吾有一数曰阳", true);
        put("吾有一数曰一曰一", false);
        put("吾有一数曰阳曰「a」", false);
        put("吾有一百数", true);
        put("吾有一百数曰一", false);
        put("吾有一百数曰一曰一", false);
        put("吾有一列曰一", true);
        put("吾有一言曰一", true);
        put("吾有一爻曰一", true);

        put("有数一", true);
        put("有数之之一", true);
        put("有数之之「a 」", true);

        put("名之曰「a 」", true);
        put("名之曰「a 」之一", false);
        put("名之曰己之一", false);
        put("名之曰己之「「a 」」", true);

        put("吾有一列曰一书之", true);
        put("吾有一列书之", true);

        put("除一于一", true);
        put("除一于「「a 」」所余几何", true);
        put("夫一二中有阳乎", false);
        put("夫一 二中有阳乎", true);
        put("夫阴阳中有阳乎", true);
        put("昔之「「a 」」者今一是矣", true);
        put("昔之「a 」者今一是矣", true);
        put("昔之「a 」之长者今一是矣", true);
        put("昔之「a 」之一者今一是矣", true);
        put("昔之「a 」之「「a 」」者今一是矣", true);
        put("昔之「「a 」」者 不复存矣", true);

        put("加一以一", true);
        put("加之以其", true);
        put("加之之一以之之二", true);
        put("减一于之之二", true);
        put("减一于之之二以一以一于一", true);
        put("施加于一以一以一", true);
        put("加一", true);
        put("施「加」于一", true);
        put("施加", true);
        put("造「a」", true);
        put("造「a」于一", true);
        put("造加", true);
        put("衔一以一", true);

        put("取一以施「a 」", true);
        put("取一以施「a 」之一", true);
        put("取一以施「a 」之「「a 」」", true);
        put("取二十一亿四千七百四十八万三千六百四十七以施「「a 」」", false);


    }};

    static Stream<Arguments> codeProvider() {
        return codes.entrySet().stream()
                .map(e -> Arguments.of(e.getKey(), e.getValue()));
    }

    @ParameterizedTest
    @MethodSource("codeProvider")
    public void testVisitor(String code, boolean pass) {
        try {
            visit(code);
        } catch (NoClassDefFoundError | WenyanException e) {
            if (pass) {
                throw e;
            } else {
                return;
            }
        }
        if (!pass) {
            throw new AssertionError("Expected failure for code: " + code);
        }
    }

    public static void visit(String code) {
        WenyanVisitor visitor = new WenyanMainVisitor(new WenyanCompilerEnvironment(new WenyanBytecode()));
        visitor.visit(WenyanVisitor.program(code));
    }
}
