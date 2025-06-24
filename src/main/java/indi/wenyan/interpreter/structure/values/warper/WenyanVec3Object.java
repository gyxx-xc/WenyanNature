package indi.wenyan.interpreter.structure.values.warper;

import indi.wenyan.content.handler.IJavacallHandler;
import indi.wenyan.content.handler.LocalCallHandler;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanObjectType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public record WenyanVec3Object(Vec3 vec3) implements IWenyanObject {
    public static final IWenyanObjectType OBJECT_TYPE = new Vec3ObjectType();
    public static final WenyanType<WenyanVec3Object> TYPE = new WenyanType<>("vec3", WenyanVec3Object.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public IWenyanValue getAttribute(String name) {
        return switch (name) {
            case "「「上下」」" -> new WenyanDouble(vec3.y);
            case "「「東西」」" -> new WenyanDouble(vec3.x);
            case "「「南北」」" -> new WenyanDouble(vec3.z);
            case "「「長」」" -> new WenyanDouble(vec3.length());
            case "「「方長」」" -> new WenyanDouble(vec3.lengthSqr());
            case "「「偏移」」" -> new LocalCallHandler(
                    (self, args) -> {
                        if (args.size() == 1) {
                            return new WenyanVec3Object(vec3.add(args.getFirst().as(TYPE).vec3));
                        } else if (args.size() == 3) {
                            var offset = IJavacallHandler.getArgs(args, new WenyanType[]{WenyanDouble.TYPE, WenyanDouble.TYPE, WenyanDouble.TYPE});
                            return new WenyanVec3Object(vec3.add((double) offset.get(0), (double) offset.get(1), (double) offset.get(2)));
                        } else {
                            throw new WenyanException.WenyanVarException("args?");
                        }
                    }
            );
            default -> throw new WenyanException("Unknown Vec3 attribute: " + name);
        };
    }

    @Override
    public void setVariable(String name, IWenyanValue value) {
        throw new WenyanException("Cannot set variable on Vec3 object: " + name);
    }

    // store all static information
    public static class Vec3ObjectType implements IWenyanObjectType {
        public static final WenyanType<Vec3ObjectType> TYPE = new WenyanType<>("vec3_object_type", Vec3ObjectType.class);
        public static final Vec3ObjectType INSTANCE = new Vec3ObjectType();

        public static final IWenyanValue ZERO;
        public static final IWenyanValue UP;
        public static final IWenyanValue DOWN;
        public static final IWenyanValue EAST;
        public static final IWenyanValue WEST;
        public static final IWenyanValue SOUTH;
        public static final IWenyanValue NORTH;

        private Vec3ObjectType() {
        }

        @Override
        public IWenyanValue getAttribute(String name) {
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
        public IWenyanObject createObject(List<IWenyanValue> argsList) throws WenyanException.WenyanThrowException {
            if (argsList.size() == 1) {
                // TODO
                if (argsList.getFirst().as(WenyanVec3Object.TYPE) instanceof WenyanVec3Object vec3Object) {
                    return vec3Object;
                } else {
                    throw new WenyanException.WenyanVarException("Expected a Vec3 object as argument");
                }
            } else {
                var args = IJavacallHandler.getArgs(argsList, new WenyanType[]{WenyanDouble.TYPE, WenyanDouble.TYPE, WenyanDouble.TYPE});
                return new WenyanVec3Object(new Vec3((double) args.get(0), (double) args.get(1), (double) args.get(2)));
            }
        }

        static {
            ZERO = new WenyanVec3Object(Vec3.ZERO);
            UP = new WenyanVec3Object(new Vec3(0, 1, 0));
            DOWN = new WenyanVec3Object(new Vec3(0, -1, 0));
            EAST = new WenyanVec3Object(new Vec3(1, 0, 0));
            WEST = new WenyanVec3Object(new Vec3(-1, 0, 0));
            SOUTH = new WenyanVec3Object(new Vec3(0, 0, 1));
            NORTH = new WenyanVec3Object(new Vec3(0, 0, -1));
        }

        @Override
        public WenyanType<?> type() {
            return TYPE;
        }
    }
}
