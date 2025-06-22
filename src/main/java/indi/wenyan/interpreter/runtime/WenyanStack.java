package indi.wenyan.interpreter.runtime;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.WenyanValue;
import indi.wenyan.interpreter.structure.values.WenyanNativeValue1;
import indi.wenyan.interpreter.structure.values.WenyanNull;

import java.util.Deque;
import java.util.LinkedList;

public class WenyanStack {
    private final Deque<WenyanValue> stack = new LinkedList<>();
    public static final int MAX_SIZE = 64;

    public void push(WenyanNativeValue1 item) {
        if (item.type() != WenyanNull.TYPE)
            stack.addLast(item);
        while (stack.size() > MAX_SIZE) {
            stack.removeFirst();
        }
    }

    public WenyanValue peek() {
        int len = stack.size();
        if (len == 0) {
            throw new WenyanException("error.wenyan_nature.stack_empty");
        }
        return stack.getLast();
    }

    public WenyanValue pop() {
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

    public WenyanValue get(int index) {
        if (index < 0 || index >= stack.size()) {
            throw new WenyanException("error.wenyan_nature.stack_index_out_of_bounds");
        }
        return ((LinkedList<WenyanValue>) stack).get(stack.size() - index - 1);
    }
}
