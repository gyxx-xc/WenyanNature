package indi.wenyan.interpreter.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanValue;
import indi.wenyan.interpreter.utils.JavacallHandler;
import indi.wenyan.interpreter.utils.WenyanPackages;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

public class SelfPositionHandler extends JavacallHandler {
    private final Player holder;
    private final HandRunnerEntity runner;

    public SelfPositionHandler(Player holder, HandRunnerEntity runner) {
        this.holder = holder;
        this.runner = runner;
    }

    @Override
    public WenyanValue handle(WenyanValue[] args) throws WenyanException.WenyanThrowException {
        Vec3 vec = runner.position().subtract(holder.position());
        return WenyanPackages.WenyanObjectTypes.VECTOR3.newObject(
                Arrays.asList(new WenyanValue(WenyanValue.Type.DOUBLE, vec.y, true)
                        , new WenyanValue(WenyanValue.Type.DOUBLE, vec.x, true)
                        , new WenyanValue(WenyanValue.Type.DOUBLE, vec.z, true))
                        .toArray(WenyanValue[]::new));
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
