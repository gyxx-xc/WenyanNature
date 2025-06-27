package indi.wenyan.interpreter.runtime;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanNull;

import java.util.Deque;
import java.util.LinkedList;

public class WenyanStack {
    private final Deque<IWenyanValue> stack = new LinkedList<>();
    public static final int MAX_SIZE = 64;

    public void push(IWenyanValue item) {
        if (item.type() != WenyanNull.TYPE)
            stack.addLast(item);
        while (stack.size() > MAX_SIZE) {
            stack.removeFirst();
        }
    }

    public IWenyanValue peek() {
        int len = stack.size();
        if (len == 0) {
            throw new WenyanException("error.wenyan_programming.stack_empty");
        }
        return stack.getLast();
    }

    public IWenyanValue pop() {
        int len = stack.size();
        if (len == 0) {
            throw new WenyanException("error.wenyan_programming.stack_empty");
        }
        return stack.removeLast();
    }

    public void clear() {
        stack.clear();
    }

    public int size() {
        return stack.size();
    }

    public IWenyanValue get(int index) {
        if (index < 0 || index >= stack.size()) {
            throw new WenyanException("error.wenyan_programming.stack_index_out_of_bounds");
        }
        return ((LinkedList<IWenyanValue>) stack).get(stack.size() - index - 1);
    }
}
