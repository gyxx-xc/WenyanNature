package indi.wenyan.content.checker.checker.challenge;

import indi.wenyan.content.checker.ValueAnswerChecker;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.utils.WenyanValues;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Ex1Checker extends ValueAnswerChecker {
    private IWenyanValue ans;

    public Ex1Checker(RandomSource random) {
        super(random);
    }

    private static class Node {
        char val;
        Node left, right;
    }

    private @Nullable Node buildRandomTree(int n) {
        if (n == 0) return null;
        Node root = new Node();
        int leftSize = random.nextInt(n);
        int rightSize = n - 1 - leftSize;
        root.left = buildRandomTree(leftSize);
        root.right = buildRandomTree(rightSize);
        return root;
    }

    private void getPreOrder(Node node, StringBuilder sb) {
        if (node == null) return;
        sb.append(node.val);
        getPreOrder(node.left, sb);
        getPreOrder(node.right, sb);
    }

    private void getPostOrder(Node node, StringBuilder sb) {
        if (node == null) return;
        getPostOrder(node.left, sb);
        getPostOrder(node.right, sb);
        sb.append(node.val);
    }

    private void assignLabels(Node node, List<Character> chars, int[] idx) {
        if (node == null) return;
        node.val = chars.get(idx[0]++);
        assignLabels(node.left, chars, idx);
        assignLabels(node.right, chars, idx);
    }

    @Override
    public void init() {
        super.init();

        int n = random.nextInt(25) + 2; // 2 to 26 nodes
        Node root = buildRandomTree(n);

        List<Character> chars = new ArrayList<>();
        for (char c = 'a'; c <= 'z'; c++) {
            chars.add(c);
        }
        // Shuffle the characters
        for (int i = chars.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars.get(i);
            chars.set(i, chars.get(j));
            chars.set(j, temp);
        }

        assignLabels(root, chars, new int[]{0});

        StringBuilder preOrder = new StringBuilder();
        getPreOrder(root, preOrder);
        StringBuilder postOrder = new StringBuilder();
        getPostOrder(root, postOrder);

        String str1 = preOrder.toString();
        String str2 = postOrder.toString();

        setVariable(0, WenyanValues.of(str1));
        setVariable(1, WenyanValues.of(str2));

        int ansCount = 0;
        for (int i = 0; i < str1.length() - 1; i++) {
            for (int j = 1; j < str2.length(); j++) {
                if (str1.charAt(i) == str2.charAt(j) && str1.charAt(i + 1) == str2.charAt(j - 1)) {
                    ansCount++;
                }
            }
        }

        ans = WenyanValues.of(1L << ansCount);
    }

    @Override
    public void accept(IWenyanValue value) throws WenyanException {
        try {
            if (IWenyanValue.equals(value, ans)) {
                setResult(ResultStatus.ANSWER_CORRECT);
            } else {
                setResult(ResultStatus.WRONG_ANSWER);
            }
        } catch (WenyanException e) {
            setResult(ResultStatus.WRONG_ANSWER);
            throw new WenyanException(e.getMessage());
        }
    }
}
