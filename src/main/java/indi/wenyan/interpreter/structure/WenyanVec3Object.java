package indi.wenyan.interpreter.structure;

import indi.wenyan.content.handler.JavacallHandler;
import net.minecraft.world.phys.Vec3;

import java.util.List;

// used as return new WenyanNativeValue(WenyanType.OBJECT, new WenyanVec3Object(vec3), true);
public class WenyanVec3Object implements WenyanObject {
    private final Vec3 vec3;
    public static final WenyanObjectType TYPE = new Vec3ObjectType();

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

    // store all static information
    public static class Vec3ObjectType implements WenyanObjectType {

        @Override
        public WenyanNativeValue getAttribute(String name) {
            throw new WenyanException("Vec3ObjectType does not have attributes: " + name);
        }

        @Override
        public WenyanObject createObject(List<WenyanNativeValue> argsList) throws WenyanException.WenyanThrowException {
            var args = JavacallHandler.getArgs(argsList, new WenyanType[]{WenyanType.DOUBLE, WenyanType.DOUBLE, WenyanType.DOUBLE});
            return new WenyanVec3Object(new Vec3((double) args.get(0), (double) args.get(1), (double) args.get(2)));
        }
    }
}
