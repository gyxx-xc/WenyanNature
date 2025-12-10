package indi.wenyan.interpreter.structure;

import indi.wenyan.content.handler.IExecCallHandler;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import lombok.Value;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * Represents the context for calling Java code from Wenyan
 */
@Accessors(fluent = true)
@Value
public class JavacallContext {
    /** The Wenyan value acting as 'this' */
    IWenyanValue self;

    /** Arguments passed to the call */
    List<IWenyanValue> args;

    /** The thread executing the call */
    WenyanThread thread;

    /** Handler for execution */
    IExecCallHandler handler;

    /** Player who triggered the call */
    @Deprecated
    Player holder;
}
