package indi.wenyan.interpreter.structure;

import indi.wenyan.interpreter.utils.WenyanDataParser;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

// used as return new WenyanNativeValue(WenyanType.OBJECT, new WenyanVec3Object(vec3), true);
public class WenyanVec3Object implements WenyanObject {
    private final Vec3 vec3;
    private final WenyanObjectType objectType = new Vec3ObjectType();
    // TODO
//    public static final WenyanNativeValue CONSTRUCTOR = new WenyanNativeValue(WenyanType.FUNCTION,
//            new WenyanNativeValue.FunctionSign(WenyanDataParser.CONSTRUCTOR_ID,
//                    new WenyanType[]{WenyanType.DOUBLE, WenyanType.DOUBLE, WenyanType.DOUBLE},
//                    args -> {
//                        if (args.size() != 3)
//                            throw new WenyanException("Vec3 constructor requires 3 arguments: x, y, z");
//                        return new WenyanNativeValue(WenyanType.OBJECT, new WenyanVec3Object(new Vec3(
//                                args.get(0).casting(WenyanType.DOUBLE).getValue(),
//                                args.get(1).casting(WenyanType.DOUBLE).getValue(),
//                                args.get(2).casting(WenyanType.DOUBLE).getValue())), true);
//                    }), true);

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
    public @Nullable WenyanObjectType getObjectType() {
        return objectType;
    }

    // store all static information
    static class Vec3ObjectType implements WenyanObjectType {
        @Override
        public @Nullable WenyanObjectType getParent() {
            return null;
        }

        @Override
        public WenyanNativeValue getFunction(String id) {
//            switch (id) {
//                case WenyanDataParser.ITER_ID ->
//            }
            return null;
        }

        @Override
        public WenyanNativeValue getStaticVariable(String id) {
            throw new WenyanException("Vec3 does not support static variables: " + id);
        }
    }
}
