package indi.wenyan.interpreter.structure;

import java.util.Stack;

public class WenyanStack extends Stack<WenyanValue> {
    @Override
    public WenyanValue push(WenyanValue item) {return  item.getType() == WenyanValue.Type.NULL ? null : super.push(item);}
}
