package indi.wenyan.interpreter.structure;

public class WenyanNull implements WenyanValue{
    @Override
    public WenyanType type() {
        return WenyanType.NULL;
    }
}
