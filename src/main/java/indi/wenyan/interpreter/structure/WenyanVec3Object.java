package indi.wenyan.interpreter.structure;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

// used as return new WenyanNativeValue(WenyanType.OBJECT, new WenyanVec3Object(vec3), true);
public class WenyanVec3Object implements WenyanObject {
    private final Vec3 vec3;

    public WenyanVec3Object(Vec3 vec3) {
        this.vec3 = vec3;
    }

    @Override
    public WenyanNativeValue getAttribute(String name) {
        return switch (name) {
            case "「「上下」」" -> new WenyanNativeValue(WenyanType.DOUBLE, vec3.y, true);
            case "「「東西」」" -> new WenyanNativeValue(WenyanType.DOUBLE, vec3.x, true);
            case "「「南北」」" -> new WenyanNativeValue(WenyanType.DOUBLE, vec3.z, true);
            default -> throw new WenyanException("Unknown Vec3 attribute: " + name);
        };
    }

    @Override
    public void setVariable(String name, WenyanNativeValue value) {
        throw new WenyanException("Cannot set variable on Vec3 object: " + name);
    }

    @Override
    public @Nullable WenyanObjectType getParent() {
        return null;
    }
}
