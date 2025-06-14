package indi.wenyan.content.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.utils.WenyanPackages;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

public class SelfPositionHandler implements JavacallHandler {
    private final Player holder;
    private final HandRunnerEntity runner;

    public SelfPositionHandler(Player holder, HandRunnerEntity runner) {
        this.holder = holder;
        this.runner = runner;
    }

    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] args) throws WenyanException.WenyanThrowException {
        Vec3 vec = runner.position().subtract(holder.position());
        return WenyanPackages.WenyanObjectTypes.VECTOR3.newObject(
                Arrays.asList(new WenyanNativeValue(WenyanType.DOUBLE, vec.y, true)
                        , new WenyanNativeValue(WenyanType.DOUBLE, vec.x, true)
                        , new WenyanNativeValue(WenyanType.DOUBLE, vec.z, true))
                        .toArray(WenyanNativeValue[]::new));
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
