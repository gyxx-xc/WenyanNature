package tests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

public class WenyanBlockTest {
    public static List<String> getTestCases() {
        return List.of(
                """
                        夫一
                        夫二
                        取二以施加
                        加之以之
                        """,
                """
                        吾有一列名之曰「a 」
                        充「a 」以「a 」以一
                        夫「a 」之一之一之二
                        書之
                        """,
                """
                        吾有一物。名之曰「a」。其物如是。
                        	物之「「a 」」者。數曰一。
                        是謂「a」之物也。
                        
                        吾有一物繼「a」。名之曰「b」。其物如是。
                        	物之「「b 」」者。言曰「「女」」。
                        是謂「b」之物也。
                        
                        書「a」之「「a 」」
                        書「b」之「「b 」」
                        昔之「b」之「「b 」」者今一是矣
                        書「a」之「「a 」」
                        書「b」之「「b 」」
                        """,
                """
                        吾有一物。名之曰「a」。其物如是。
                        	物之造者術
                        	是術曰。
                        		夫一
                        		名之曰己之「「a 」」
                        	是謂造之術也。
                        是謂「a」之物也。
                        
                        吾有一物繼「a」。名之曰「b」。其物如是。
                        是謂「b」之物也。
                        
                        造「a」名之曰「a 」
                        施「a」名之曰「a1 」
                        造「b」名之曰「b 」
                        施「b」名之曰「b1 」
                        
                        書「a 」之「「a 」」
                        書「a1 」之「「a 」」
                        書「b 」之「「a 」」
                        書「b1 」之「「a 」」
                        
                        昔之「b 」之「「a 」」者今二是矣
                        
                        書「a 」之「「a 」」
                        書「b 」之「「a 」」
                        """,
                """
                        吾有一物。名之曰「a」。其物如是。
                        	物之造者術是術曰。
                        		夫一名之曰己之「「a 」」
                        	是謂造之術也。
                        	物之「「f 」」者術是術曰
                        		夫一名之曰己之「「f 」」
                        	是謂「「f 」」之術也
                        是謂「a」之物也。
                        吾有一物繼「a」。名之曰「b」。其物如是。是謂「b」之物也。
                        造「a」名之曰「a 」
                        造「b」名之曰「b 」
                        
                        書「a 」之「「f 」」
                        書「b 」之「「f 」」
                        施「a 」之「「f 」」
                        書「a 」之「「f 」」
                        書「b 」之「「f 」」
                        """,
                """
                        吾有一物。名之曰「a」。其物如是。
                        	物之造者術是術曰。
                        		夫一名之曰己之「「a 」」
                        	是謂造之術也。
                        	物之「「f 」」者術是術曰
                        		書「「f 」」
                        	是謂「「f 」」之術也
                        	物之「「f1 」」者術是術曰
                            	書「「f1 」」
                            是謂「「f1 」」之術也
                        是謂「a」之物也。
                        吾有一物繼「a」。名之曰「b」。其物如是。是謂「b」之物也。
                        造「a」名之曰「a 」
                        造「b」名之曰「b 」
                        
                        施「a 」之「「f 」」
                        施「a」之「「f1 」」
                        施「a 」之「「f1 」」
                        """,
                """
                        吾有一物。名之曰「a」。其物如是。
                        	物之造者術是術曰。
                        		夫一名之曰己之「「a 」」
                        	是謂造之術也。
                        是謂「a」之物也。
                        
                        吾有一物繼「a」。名之曰「b」。其物如是。
                        	物之造者術是術曰。
                        		施父之造
                        		夫二名之曰己之「「a 」」
                        		夫一名之曰己之「「b 」」
                        	是謂造之術也。
                        是謂「b」之物也。
                        
                        造「a」名之曰「a 」
                        施「a」名之曰「a1 」
                        造「b」名之曰「b 」
                        施「b」名之曰「b1 」
                        
                        書「a 」之「「a 」」
                        書「a1 」之「「a 」」
                        書「b 」之「「a 」」
                        書「b1 」之「「a 」」
                        書「b 」之「「b 」」
                        書「b1 」之「「b 」」
                        
                        昔之「b 」之「「a 」」者今三是矣
                        
                        書「a 」之「「a 」」
                        書「b 」之「「a 」」
                        """);
    }

    @ParameterizedTest
    @MethodSource("getTestCases")
    public void testWenyanBlock(String s) {
//        WenyanProgram program = new WenyanProgram(s, WenyanPackages.WENYAN_BASIC_PACKAGES, null);
//        program.run();
//        while (program.isRunning()) {
//            program.step();
//            program.handle();
//        }
    }
}
