package indi.wenyan.interpreter.structure;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanDataParser;

/**
 * This class is a warper for WenyanObjectType, which use javacall as the constructor.
 * This make the Object can be created by java in an easier way.
 */
@Deprecated
public class JavacallObjectType extends WenyanDictObjectType {
    private final JavacallHandler constructor;

    public JavacallObjectType(WenyanObjectType parent, String name, JavacallHandler constructor) {
        super(parent, name);
        if (!constructor.isLocal())
            throw new RuntimeException("cannot use JavacallObjectType with non-local constructor");
        this.constructor = constructor;
        addFunction(WenyanDataParser.CONSTRUCTOR_ID, new WenyanNativeValue(WenyanType.FUNCTION,
                new WenyanNativeValue.FunctionSign(WenyanDataParser.CONSTRUCTOR_ID,
                        new WenyanType[0],
                        constructor), true));
    }

    public WenyanNativeValue newObject(WenyanNativeValue[] args) throws WenyanException.WenyanThrowException {
        WenyanNativeValue object = new WenyanNativeValue(WenyanType.OBJECT,
                new WenyanDictObject(this), true);
        WenyanNativeValue[] newArgs = new WenyanNativeValue[args.length + 1];
        newArgs[0] = object;
        System.arraycopy(args, 0, newArgs, 1, args.length);
        constructor.handle(newArgs);
        return object;
    }

    public JavacallObjectType addStatic(String name, WenyanNativeValue[] args) {
        try {
            addStaticVariable(name, newObject(args));
        } catch (WenyanException.WenyanThrowException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public JavacallObjectType addStatic(String name, WenyanNativeValue value) {
        addStaticVariable(name, value);
        return this;
    }
}
