package indi.wenyan.content.checker.checker.challenge;

import indi.wenyan.content.checker.ValueAnswerChecker;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;

public class Ex2Checker extends ValueAnswerChecker {
    public Ex2Checker(RandomSource random) {
        super(random);
    }

    @Override
    public void init() {
        super.init();
        String expr = generateExpression();
        setVariable(0, WenyanValues.of(expr));

        boolean isMatching = checkMatching(expr);
        // FIXME: may change to const
        ans = WenyanValues.of(isMatching ? "YES" : "NO");
    }

    private String generateExpression() {
        StringBuilder sb = new StringBuilder();
        int length = random.nextInt(100) + 10;
        int leftParens = 0;
        int openParens = 0;

        boolean wantValid = random.nextBoolean();

        for (int i = 0; i < length; i++) {
            int type = random.nextInt(4);
            if (type == 0 && leftParens < 19 && (wantValid || random.nextBoolean())) {
                sb.append('(');
                leftParens++;
                openParens++;
            } else if (type == 1 && openParens > 0 && (wantValid || random.nextBoolean())) {
                sb.append(')');
                openParens--;
            } else if (type == 2) {
                sb.append((char) ('a' + random.nextInt(26)));
            } else {
                char[] ops = { '+', '-', '*', '/' };
                sb.append(ops[random.nextInt(4)]);
            }
        }

        if (wantValid) {
            while (openParens > 0) {
                sb.append(')');
                openParens--;
            }
        } else {
            if (random.nextBoolean()) {
                if (leftParens < 19) {
                    sb.append('(');
                } else {
                    sb.append(')');
                }
            } else {
                sb.append(')');
            }
        }

        sb.append('@');
        return sb.toString();
    }

    private boolean checkMatching(String a) {
        int balance = 0;
        for (int i = 0; i < a.length(); i++) {
            char c = a.charAt(i);
            if (c == '(') {
                balance++;
            } else if (c == ')') {
                balance--;
                if (balance < 0) {
                    return false;
                }
            }
        }
        return balance == 0;
    }
}
