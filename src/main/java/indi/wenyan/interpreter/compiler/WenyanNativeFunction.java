package indi.wenyan.interpreter.compiler;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.*;
import indi.wenyan.interpreter.utils.WenyanDataParser;
import net.minecraft.network.chat.Component;

import java.util.List;

public record WenyanNativeFunction(List<WenyanType> argTypes, WenyanBytecode bytecode) implements WenyanFunction {
    @Override
    public void call(WenyanNativeValue self, WenyanThread thread,
                     List<WenyanNativeValue> argsList)
            throws WenyanException.WenyanThrowException {
        if (argTypes().size() != argsList.size())
            throw new WenyanException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
        for (int i = 0; i < argsList.size(); i++) {
            argsList.get(i).casting(argTypes().get(i));
        }

        WenyanRuntime newRuntime = new WenyanRuntime(bytecode);
        if (self != null) {
            newRuntime.setVariable(WenyanDataParser.SELF_ID, self);
            newRuntime.setVariable(WenyanDataParser.PARENT_ID, new WenyanNativeValue(WenyanType.OBJECT_TYPE,
                    ((WenyanDictObject) self.getValue()).getObjectType().getParent(), true));
        }
        // STUB: assume the first n id is the args
        for (int i = 0; i < argsList.size(); i++)
            newRuntime.setVariable(bytecode.getIdentifier(i), WenyanNativeValue.varOf(argsList.get(i)));
        thread.call(newRuntime);
    }
}
