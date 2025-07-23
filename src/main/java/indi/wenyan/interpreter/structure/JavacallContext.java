package indi.wenyan.interpreter.structure;

import indi.wenyan.content.handler.IExecCallHandler;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import lombok.Value;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.player.Player;

import java.util.List;

@Accessors(fluent = true)
@Value
public class JavacallContext {
    IWenyanValue self;
    List<IWenyanValue> args;
    WenyanThread thread;
    IExecCallHandler handler;
    Player holder;
}
