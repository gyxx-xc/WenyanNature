package indi.wenyan.interpreter.structure.values;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.content.handler.LocalCallHandler;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
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
            case "「「上下」」" -> new WenyanNativeValue(WenyanDouble.TYPE, vec3.y, true);
            case "「「東西」」" -> new WenyanNativeValue(WenyanDouble.TYPE, vec3.x, true);
            case "「「南北」」" -> new WenyanNativeValue(WenyanDouble.TYPE, vec3.z, true);
            case "「「長」」" -> new WenyanNativeValue(WenyanDouble.TYPE, vec3.length(), true);
            case "「「方長」」" -> new WenyanNativeValue(WenyanDouble.TYPE, vec3.lengthSqr(), true);
            case "「「偏移」」" -> new WenyanNativeValue(WenyanFunction.TYPE, new LocalCallHandler(
                    (self, args) -> {
                        if (args.size() == 1) {
                            var offsetValue = JavacallHandler.getArgs(args, new WenyanType[]{WenyanObject.TYPE}).getFirst();
                            if (offsetValue instanceof WenyanVec3Object offsetVec3) {
                                return new WenyanNativeValue(WenyanObject.TYPE,
                                        new WenyanVec3Object(vec3.add(offsetVec3.vec3)), true);
                            } else {
                                throw new WenyanException.WenyanVarException("Offset must be a Vec3 object");
                            }
                        } else if (args.size() == 3) {
                            var offset = JavacallHandler.getArgs(args, new WenyanType[]{WenyanDouble.TYPE, WenyanDouble.TYPE, WenyanDouble.TYPE});
                            return new WenyanNativeValue(WenyanObject.TYPE,
                                    new WenyanVec3Object(vec3.add((double) offset.get(0), (double) offset.get(1), (double) offset.get(2)))
                                    , true);
                        } else {
                            throw new WenyanException.WenyanVarException("args?");
                        }
                    }
            ), true);
            default -> throw new WenyanException("Unknown Vec3 attribute: " + name);
        };
    }

    @Override
    public void setVariable(String name, WenyanNativeValue value) {
        throw new WenyanException("Cannot set variable on Vec3 object: " + name);
    }

    // store all static information
    public static class Vec3ObjectType implements WenyanObjectType {
        public static final Vec3ObjectType INSTANCE = new Vec3ObjectType();

        public static final WenyanNativeValue ZERO;
        public static final WenyanNativeValue UP;
        public static final WenyanNativeValue DOWN;
        public static final WenyanNativeValue EAST;
        public static final WenyanNativeValue WEST;
        public static final WenyanNativeValue SOUTH;
        public static final WenyanNativeValue NORTH;

        private Vec3ObjectType() {}

        @Override
        public WenyanNativeValue getAttribute(String name) {
            return switch (name) {
                case "「「零」」" -> ZERO;
                case "「「上」」" -> UP;
                case "「「下」」" -> DOWN;
                case "「「東」」" -> EAST;
                case "「「西」」" -> WEST;
                case "「「南」」" -> SOUTH;
                case "「「北」」" -> NORTH;
                default -> throw new WenyanException("Unknown Vec3 static attribute: " + name);
            };
        }

        @Override
        public WenyanObject createObject(List<WenyanNativeValue> argsList) throws WenyanException.WenyanThrowException {
            if (argsList.size() == 1) {
                if (argsList.getFirst().as(WenyanObject.TYPE).getValue() instanceof WenyanVec3Object vec3Object) {
                    return vec3Object;
                } else {
                    throw new WenyanException.WenyanVarException("Expected a Vec3 object as argument");
                }
            } else {
                var args = JavacallHandler.getArgs(argsList, new WenyanType[]{WenyanDouble.TYPE, WenyanDouble.TYPE, WenyanDouble.TYPE});
                return new WenyanVec3Object(new Vec3((double) args.get(0), (double) args.get(1), (double) args.get(2)));
            }
        }

        static {
            ZERO = new WenyanNativeValue(WenyanObject.TYPE, new WenyanVec3Object(Vec3.ZERO), true);
            UP = new WenyanNativeValue(WenyanObject.TYPE, new WenyanVec3Object(new Vec3(0, 1, 0)), true);
            DOWN = new WenyanNativeValue(WenyanObject.TYPE, new WenyanVec3Object(new Vec3(0, -1, 0)), true);
            EAST = new WenyanNativeValue(WenyanObject.TYPE, new WenyanVec3Object(new Vec3(1, 0, 0)), true);
            WEST = new WenyanNativeValue(WenyanObject.TYPE, new WenyanVec3Object(new Vec3(-1, 0, 0)), true);
            SOUTH = new WenyanNativeValue(WenyanObject.TYPE, new WenyanVec3Object(new Vec3(0, 0, 1)), true);
            NORTH = new WenyanNativeValue(WenyanObject.TYPE, new WenyanVec3Object(new Vec3(0, 0, -1)), true);
        }
    }
}
