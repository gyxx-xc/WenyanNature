package indi.wenyan.interpreter.structure;

import net.minecraft.network.chat.Component;

import java.util.Stack;

public class WenyanThread {
    public final Stack<WenyanRuntime> runtimes = new Stack<>();
    public WenyanThread() {
    }

    public void add(WenyanRuntime runtime) {
        runtimes.push(runtime);
    }
    public WenyanRuntime cur() {
        return runtimes.peek();
    }
    public void ret() {
        runtimes.pop();
    }

    public WenyanValue getGlobalVariable(String id) {
        WenyanValue value = null;
        for (int i = runtimes.size()-1; i >= 0; i --) {
            if (runtimes.get(i).variables.containsKey(id)) {
                value = runtimes.get(i).variables.get(id);
                break;
            }
        }
        if (value == null)
            throw new WenyanException(Component.translatable("error.wenyan_nature.variable_not_found_").getString()+id);
        return value;
    }
}
