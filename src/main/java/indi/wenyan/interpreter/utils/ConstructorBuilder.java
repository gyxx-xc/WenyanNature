package indi.wenyan.interpreter.utils;

import indi.wenyan.interpreter.handler.LocalCallHandler;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanObject;
import indi.wenyan.interpreter.structure.WenyanValue;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ConstructorBuilder {
    private final List<String> variable = new ArrayList<>();

    public static ConstructorBuilder builder() {
        return new ConstructorBuilder();
    }

    public ConstructorBuilder var(String name) {
        variable.add(name);
        return this;
    }

    public JavacallHandler makeConstructor() {
        return new LocalCallHandler(args -> {
                    if (args.length != variable.size() + 1) {
                        throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
                    }
                    WenyanObject self = (WenyanObject) args[0].casting(WenyanValue.Type.OBJECT).getValue();
                    for (int i = 0; i < variable.size(); i++) {
                        self.variable.put(variable.get(i), args[i + 1]);
                    }
                    return WenyanValue.NULL;
                });
    }
}
