package indi.wenyan.interpreter.runtime;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;

import java.util.Deque;
import java.util.LinkedList;

public class WenyanStack {
    private final Deque<WenyanNativeValue> stack = new LinkedList<>();
    public static final int MAX_SIZE = 64;

    public void push(WenyanNativeValue item) {
        if (item.type() != WenyanType.NULL)
            stack.addLast(item);
        while (stack.size() > MAX_SIZE) {
            stack.removeFirst();
        }
    }

    public WenyanNativeValue peek() {
        int len = stack.size();
        if (len == 0) {
            throw new WenyanException("error.wenyan_nature.stack_empty");
        }
        return stack.getLast();
    }

    public WenyanNativeValue pop() {
        int len = stack.size();
        if (len == 0) {
            throw new WenyanException("error.wenyan_nature.stack_empty");
        }
        return stack.removeLast();
    }

    public void clear() {
        stack.clear();
    }

    public int size() {
        return stack.size();
    }

    public WenyanNativeValue get(int index) {
        if (index < 0 || index >= stack.size()) {
            throw new WenyanException("error.wenyan_nature.stack_index_out_of_bounds");
        }
        return ((LinkedList<WenyanNativeValue>) stack).get(stack.size() - index - 1);
    }
}
