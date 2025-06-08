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

public class WenyanStatementTest {

    public static final Map<String, Boolean> codes = new HashMap<>() {{
        put("", true);
        put("夫", false);
        put("夫二十一億四千七百四十八萬三千六百四十七", true);
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
        put("夫「a」之二十一億四千七百四十八萬三千六百四十七", true);
        put("夫己之「a」", true);
        put("夫「a」之「「a」」之「「a」」之「「b」」之一之「「a」」", true);

        put("吾有一數曰一", true);
        put("吾有二十一億四千七百四十八萬三千六百四十七數", false);
        put("吾有一數曰之", true);
        put("吾有一數曰陽", true);
        put("吾有一數曰一曰一", false);
        put("吾有一數曰陽曰「a」", false);
        put("吾有一百數", true);
        put("吾有一百數曰一", false);
        put("吾有一百數曰一曰一", false);
        put("吾有一列曰一", true);
        put("吾有一言曰一", true);
        put("吾有一爻曰一", true);

        put("有數一", true);
        put("有數之之一", true);
        put("有數之之「a 」", true);

        put("名之曰「a 」", true);
        put("名之曰「a 」之一", false);
        put("名之曰己之一", false);
        put("名之曰己之「「a 」」", true);

        put("吾有一列曰一書之", true);
        put("吾有一列書之", true);

        put("除一於一", true);
        put("除一於「「a 」」所餘幾何", true);
        put("夫一二中有陽乎", false);
        put("夫一 二中有陽乎", true);
        put("夫陰陽中有陽乎", true);
        put("昔之「「a 」」者今一是矣", true);
        put("昔之「a 」者今一是矣", true);
        put("昔之「a 」之長者今一是矣", true);
        put("昔之「a 」之一者今一是矣", true);
        put("昔之「a 」之「「a 」」者今一是矣", true);
        put("昔之「「a 」」者 不復存矣", true);

        put("加一以一", true);
        put("加之以其", true);
        put("加之之一以之之二", true);
        put("減一於之之二", true);
        put("減一於之之二以一以一於一", true);
        put("施加於一以一以一", true);
        put("加一", true);
        put("施「加」於一", true);
        put("施加", true);
        put("造「a」", true);
        put("造「a」於一", true);
        put("造加", true);
        put("銜一以一", true);

        put("取一以施「a 」", true);
        put("取一以施「a 」之一", true);
        put("取一以施「a 」之「「a 」」", true);
        put("取二十一億四千七百四十八萬三千六百四十七以施「「a 」」", false);


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
