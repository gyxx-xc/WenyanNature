package indi.wenyan.interpreter_impl.value;

import indi.wenyan.interpreter_impl.WenyanMinecraftValues;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.WenyanType;
import indi.wenyan.judou.api.language.JudouExceptionText;
import indi.wenyan.judou.api.runtime.IWenyanRunner;
import indi.wenyan.judou.api.utils.WenyanValues;
import indi.wenyan.judou.api.values.*;
import indi.wenyan.judou.api.values.primitive.WenyanDouble;
import indi.wenyan.judou.api.values.primitive.WenyanString;
import indi.wenyan.setup.language.TypeText;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public record WenyanVec3(Vec3 value) implements IWenyanWarperValue<Vec3>, IWenyanObject, IWenyanComputable {
    public static final WenyanType<WenyanVec3> TYPE = new WenyanType<>(TypeText.Vec3.string(), WenyanVec3.class);

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
    public IWenyanValue getAttribute(String name) throws WenyanException {
        return switch (name) {
            case WenyanSymbol.VECTOR_Y -> WenyanValues.of(value.y);
            case WenyanSymbol.VECTOR_X -> WenyanValues.of(value.x);
            case WenyanSymbol.VECTOR_Z -> WenyanValues.of(value.z);
            case WenyanSymbol.VECTOR_LENGTH -> WenyanValues.of(value.length());
            case WenyanSymbol.VECTOR_LENGTH_SQR -> WenyanValues.of(value.lengthSqr());
            case WenyanSymbol.VECTOR_OFFSET -> WenyanValues.of(
                    (_, args) -> {
                        if (args.size() == 1) {
                            return WenyanMinecraftValues.of(value.add(args.getFirst().as(TYPE).value));
                        } else if (args.size() == 3) {
                            return new WenyanVec3(value.add(
                                    args.get(0).as(WenyanDouble.TYPE).value(),
                                    args.get(1).as(WenyanDouble.TYPE).value(),
                                    args.get(2).as(WenyanDouble.TYPE).value()));
                        } else {
                            throw new WenyanException.WenyanVarException(JudouExceptionText.ArgsNumWrong.string(3, args.size()));
                        }
                    }
            );
            default -> throw new WenyanException(JudouExceptionText.NoAttribute.string(name));
        };
    }

    @Override
    public IWenyanValue add(IWenyanValue other) throws WenyanException {
        return new WenyanVec3(value.add(other.as(TYPE).value));
    }

    @Override
    public IWenyanValue subtract(IWenyanValue other) throws WenyanException {
        return new WenyanVec3(value.subtract(other.as(TYPE).value));
    }

    @Override
    public IWenyanValue multiply(IWenyanValue other) throws WenyanException {
        return WenyanValues.of(value.dot(other.as(TYPE).value));
    }

    @Override
    public IWenyanValue divide(IWenyanValue other) throws WenyanException {
        throw new WenyanException(JudouExceptionText.OperationNotSupported.string());
    }

    @Override
    public @NotNull String toString() {
        return "(" + WenyanValues.of(value().x()) + ", " +
                WenyanValues.of(value().y()) + ", " + WenyanValues.of(value().z()) + ")";
    }

    // store all static information
    public enum Vec3ObjectType implements IWenyanObjectType {
        INSTANCE;

        public static final WenyanType<Vec3ObjectType> TYPE = new WenyanType<>(TypeText.Vec3ObjectType.string(), Vec3ObjectType.class);

        public static final IWenyanValue ZERO;
        public static final IWenyanValue UP;
        public static final IWenyanValue DOWN;
        public static final IWenyanValue EAST;
        public static final IWenyanValue WEST;
        public static final IWenyanValue SOUTH;
        public static final IWenyanValue NORTH;

        @Override
        public IWenyanValue getAttribute(String name) throws WenyanException {
            return switch (name) {
                case WenyanSymbol.VECTOR_TYPE_ZERO -> ZERO;
                case WenyanSymbol.VECTOR_TYPE_UP -> UP;
                case WenyanSymbol.VECTOR_TYPE_DOWN -> DOWN;
                case WenyanSymbol.VECTOR_TYPE_EAST -> EAST;
                case WenyanSymbol.VECTOR_TYPE_WEST -> WEST;
                case WenyanSymbol.VECTOR_TYPE_SOUTH -> SOUTH;
                case WenyanSymbol.VECTOR_TYPE_NORTH -> NORTH;
                default -> throw new WenyanException(JudouExceptionText.NoAttribute.string(name));
            };
        }

        @Override
        public void callWithReturn(@Nullable IWenyanValue self, IWenyanRunner thread, List<IWenyanValue> argsList,
                                   Consumer<IWenyanValue> onReturn) throws WenyanException {
            IWenyanObject result;
            if (argsList.size() == 1) {
                result = argsList.getFirst().as(WenyanVec3.TYPE);
            } else {
                result = new WenyanVec3(new Vec3(
                        argsList.get(0).as(WenyanDouble.TYPE).value(),
                        argsList.get(1).as(WenyanDouble.TYPE).value(),
                        argsList.get(2).as(WenyanDouble.TYPE).value()));
            }
            onReturn.accept(result);
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
}
