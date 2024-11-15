package indi.wenyan.interpreter.structure;

import java.util.Stack;

public class WenyanStack extends Stack<WenyanValue> {
    @Override
    public WenyanValue push(WenyanValue item) {return item == null ? null : super.push(item);}
}
