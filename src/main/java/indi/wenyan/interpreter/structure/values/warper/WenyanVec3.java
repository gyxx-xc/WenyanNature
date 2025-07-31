package indi.wenyan.interpreter.structure.values.warper;

import indi.wenyan.content.handler.WenyanBuiltinFunction;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanObjectType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.IWenyanWarperValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public record WenyanVec3(Vec3 value) implements IWenyanWarperValue<Vec3>, IWenyanObject {
    public static final IWenyanObjectType OBJECT_TYPE = new Vec3ObjectType();
    public static final WenyanType<WenyanVec3> TYPE = new WenyanType<>("vec3", WenyanVec3.class);

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public IWenyanValue getAttribute(String name) {
        return switch (name) {
            case "「上下」" -> new WenyanDouble(value.y);
            case "「東西」" -> new WenyanDouble(value.x);
            case "「南北」" -> new WenyanDouble(value.z);
            case "「長」" -> new WenyanDouble(value.length());
            case "「方長」" -> new WenyanDouble(value.lengthSqr());
            case "「偏移」" -> new WenyanBuiltinFunction(
                    (self, args) -> {
                        if (args.size() == 1) {
                            return new WenyanVec3(value.add(args.getFirst().as(TYPE).value));
                        } else if (args.size() == 3) {
                            return new WenyanVec3(value.add(
                                    args.get(0).as(WenyanDouble.TYPE).value(),
                                    args.get(1).as(WenyanDouble.TYPE).value(),
                                    args.get(2).as(WenyanDouble.TYPE).value()));
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
                case "「零」" -> ZERO;
                case "「上」" -> UP;
                case "「下」" -> DOWN;
                case "「東」" -> EAST;
                case "「西」" -> WEST;
                case "「南」" -> SOUTH;
                case "「北」" -> NORTH;
                default -> throw new WenyanException("Unknown Vec3 static attribute: " + name);
            };
        }

        @Override
        public IWenyanObject createObject(List<IWenyanValue> argsList) throws WenyanException.WenyanThrowException {
            if (argsList.size() == 1) {
                return argsList.getFirst().as(WenyanVec3.TYPE);
            } else {
                return new WenyanVec3(new Vec3(
                        argsList.get(0).as(WenyanDouble.TYPE).value(),
                        argsList.get(1).as(WenyanDouble.TYPE).value(),
                        argsList.get(2).as(WenyanDouble.TYPE).value()));
            }
        }

        static {
            ZERO = new WenyanVec3(Vec3.ZERO);
            UP = new WenyanVec3(new Vec3(0, 1, 0));
            DOWN = new WenyanVec3(new Vec3(0, -1, 0));
            EAST = new WenyanVec3(new Vec3(1, 0, 0));
            WEST = new WenyanVec3(new Vec3(-1, 0, 0));
            SOUTH = new WenyanVec3(new Vec3(0, 0, 1));
            NORTH = new WenyanVec3(new Vec3(0, 0, -1));
        }

        @Override
        public WenyanType<?> type() {
            return TYPE;
        }
    }
}
