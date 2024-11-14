package indi.wenyan.interpreter.visitor;

import indi.wenyan.interpreter.utils.WenyanValue;

import java.util.Stack;

public class WenyanStack extends Stack<WenyanValue> {
    @Override
    public WenyanValue push(WenyanValue item) {
        return item == null ? null : super.push(item);
    }
}
