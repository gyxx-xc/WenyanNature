package indi.wenyan.judou.compiler;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import indi.wenyan.judou.api.utils.UtilManager;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum WenyanPreprocessor {
    ;
    private final static Pattern PATTERN = Pattern.compile("「「.*?」」|「.*?」");

    public static @NotNull String preprocess(String sourceCode) {
        if (!UtilManager.getConfig().convertCode())
            return sourceCode;

        Matcher matcher = PATTERN.matcher(sourceCode);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        while (matcher.find()) {
            // Convert text before the current bracket
            String beforeBracket = sourceCode.substring(lastEnd, matcher.start());
            result.append(ZhConverterUtil.toTraditional(beforeBracket));
            // Preserve text inside the bracket
            result.append(matcher.group());
            lastEnd = matcher.end();
        }

        // Convert remaining text after the last bracket
        String remaining = sourceCode.substring(lastEnd);
        result.append(ZhConverterUtil.toTraditional(remaining));

        return result.toString();
    }
}
