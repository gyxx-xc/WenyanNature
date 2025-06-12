package indi.wenyan.interpreter.utils;

import indi.wenyan.content.handler.LocalCallHandler;
import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.*;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

@Deprecated
class ConstructorBuilder {
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
                    WenyanObject self = (WenyanObject) args[0].casting(WenyanType.OBJECT).getValue();
                    for (int i = 0; i < variable.size(); i++) {
                        self.setVariable(variable.get(i), args[i + 1]);
                    }
                    return WenyanValue.NULL;
                });
    }
}
