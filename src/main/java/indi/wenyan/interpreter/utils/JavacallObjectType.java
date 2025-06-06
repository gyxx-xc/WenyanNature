package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanObject;
import indi.wenyan.interpreter.structure.WenyanObjectType;
import indi.wenyan.interpreter.structure.WenyanValue;

/**
 * This class is a warper for WenyanObjectType, which use javacall as the constructor.
 * This make the Object can be created by java in an easier way.
 */
public class JavacallObjectType extends WenyanObjectType {
    private final JavacallHandler constructor;

    public JavacallObjectType(WenyanObjectType parent, String name, JavacallHandler constructor) {
        super(parent, name);
        if (!constructor.isLocal())
            throw new RuntimeException("cannot use JavacallObjectType with non-local constructor");
        this.constructor = constructor;
        functions.put(WenyanDataParser.CONSTRUCTOR_ID, new WenyanValue(WenyanValue.Type.FUNCTION,
                new WenyanValue.FunctionSign(WenyanDataParser.CONSTRUCTOR_ID,
                        new WenyanValue.Type[0],
                        new JavaCallCodeWarper(constructor)), true));
    }

    public WenyanValue newObject(WenyanValue[] args) throws WenyanException.WenyanThrowException {
        WenyanValue object = new WenyanValue(WenyanValue.Type.OBJECT,
                new WenyanObject(this), true);
        WenyanValue[] newArgs = new WenyanValue[args.length + 1];
        newArgs[0] = object;
        System.arraycopy(args, 0, newArgs, 1, args.length);
        constructor.handle(newArgs);
        return object;
    }

    public JavacallObjectType addStatic(String name, WenyanValue[] args) {
        try {
            staticVariable.put(name, newObject(args));
        } catch (WenyanException.WenyanThrowException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public JavacallObjectType addStatic(String name, WenyanValue value) {
        staticVariable.put(name, value);
        return this;
    }
}
