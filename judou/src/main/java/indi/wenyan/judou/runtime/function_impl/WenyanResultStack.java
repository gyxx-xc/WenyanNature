package indi.wenyan.judou.runtime.function_impl;

import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanThrowException;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.WenyanNull;
import indi.wenyan.judou.utils.WenyanThreading;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

/**
 * A specialized stack implementation for Wenyan values with a size limit.
 * Null values are not pushed to the stack.
 */
@WenyanThreading
public class WenyanResultStack {
    /** Underlying data structure for the stack */
    private final Deque<IWenyanValue> stack = new ArrayDeque<>();

    /** Maximum allowed size for the stack */
    public static final int MAX_SIZE = 64;

    /**
     * Pushes an item onto the stack if it's not null.
     * Removes oldest items if stack exceeds maximum size.
     *
     * @param item The value to push
     */
    public void push(IWenyanValue item) {
        if (item.type() != WenyanNull.TYPE)
            stack.addLast(item);
        while (stack.size() > MAX_SIZE) {
            stack.removeFirst();
        }
    }

    /**
     * Returns the top item without removing it.
     *
     * @return The top item
     * @throws WenyanException if the stack is empty
     */
    public IWenyanValue peek() throws WenyanThrowException {
        int len = stack.size();
        if (len == 0) {
            throw new WenyanException("error.wenyan_programming.stack_empty");
        }
        return stack.getLast();
    }

    /**
     * Removes and returns the top item.
     *
     * @return The removed top item
     * @throws WenyanException if the stack is empty
     */
    public IWenyanValue pop() throws WenyanThrowException {
        int len = stack.size();
        if (len == 0) {
            throw new WenyanException("error.wenyan_programming.stack_empty");
        }
        return stack.removeLast();
    }

    /**
     * Removes all items from the stack.
     */
    public void clear() {
        stack.clear();
    }

    /**
     * Returns the number of items in the stack.
     *
     * @return The stack size
     */
    public int size() {
        return stack.size();
    }

    /**
     * Gets an item at a specific index from the top (0-indexed).
     *
     * @param index The index from the top (0 is top)
     * @return The item at the specified index
     * @throws WenyanException if index is out of bounds
     */
    public IWenyanValue get(int index) throws WenyanThrowException {
        if (index < 0 || index >= stack.size()) {
            throw new WenyanException("error.wenyan_programming.stack_index_out_of_bounds");
        }
        return ((LinkedList<IWenyanValue>) stack).get(stack.size() - index - 1);
    }
}
