package indi.wenyan.interpreter_impl.value;

import indi.wenyan.interpreter.exec_interface.handler.WenyanInlineJavacall;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanThrowException;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.structure.values.IWenyanObject;
import indi.wenyan.interpreter.structure.values.IWenyanObjectType;
import indi.wenyan.interpreter.structure.values.IWenyanValue;
import indi.wenyan.interpreter.structure.values.IWenyanWarperValue;
import indi.wenyan.interpreter.structure.values.primitive.WenyanDouble;
import indi.wenyan.interpreter.structure.values.primitive.WenyanString;
import indi.wenyan.interpreter.utils.WenyanThreading;
import indi.wenyan.interpreter.utils.WenyanValues;
import indi.wenyan.interpreter_impl.WenyanMinecraftValues;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record WenyanVec3(Vec3 value) implements IWenyanWarperValue<Vec3>, IWenyanObject {
    public static final IWenyanObjectType OBJECT_TYPE = new Vec3ObjectType();
    public static final WenyanType<WenyanVec3> TYPE = new WenyanType<>("vec3", WenyanVec3.class);

    public WenyanVec3(Vec3i value) {
        this(new Vec3(value.getX(), value.getY(), value.getZ()));
    }

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T extends IWenyanValue> T casting(WenyanType<T> type) {
        if (type == WenyanString.TYPE) {
            return (T) WenyanValues.of(toString());
        }
        return null;
    }

    @Override
    public IWenyanValue getAttribute(String name) throws WenyanThrowException {
        return switch (name) {
            case "「上下」" -> WenyanValues.of(value.y);
            case "「東西」" -> WenyanValues.of(value.x);
            case "「南北」" -> WenyanValues.of(value.z);
            case "「長」" -> WenyanValues.of(value.length());
            case "「方長」" -> WenyanValues.of(value.lengthSqr());
            case "「偏移」" -> new WenyanInlineJavacall(
                    (self, args) -> {
                        if (args.size() == 1) {
                            return WenyanMinecraftValues.of(value.add(args.getFirst().as(TYPE).value));
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

    // store all static information
    public static class Vec3ObjectType implements IWenyanObjectType {
        public static final WenyanType<Vec3ObjectType> TYPE = new WenyanType<>("vec3_object_type", Vec3ObjectType.class);

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
        public IWenyanValue getAttribute(String name) throws WenyanThrowException {
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
        @WenyanThreading
        public IWenyanObject createObject(List<IWenyanValue> argsList) throws WenyanThrowException {
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
            ZERO = WenyanMinecraftValues.of(Vec3.ZERO);
            UP = WenyanMinecraftValues.of(new Vec3(0, 1, 0));
            DOWN = WenyanMinecraftValues.of(new Vec3(0, -1, 0));
            EAST = WenyanMinecraftValues.of(new Vec3(1, 0, 0));
            WEST = WenyanMinecraftValues.of(new Vec3(-1, 0, 0));
            SOUTH = WenyanMinecraftValues.of(new Vec3(0, 0, 1));
            NORTH = WenyanMinecraftValues.of(new Vec3(0, 0, -1));
        }

        @Override
        public WenyanType<?> type() {
            return TYPE;
        }
    }

    @Override
    public @NotNull String toString() {
        return "(" + WenyanValues.of(value().x()) + ", " +
                WenyanValues.of(value().y()) + ", " + new WenyanDouble(value().z()) + ")";
    }
}
