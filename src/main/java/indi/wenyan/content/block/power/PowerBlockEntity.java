package indi.wenyan.content.block.power;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter.exec_interface.handler.HandlerPackageBuilder;
import indi.wenyan.interpreter.exec_interface.handler.WenyanInlineJavacall;
import indi.wenyan.interpreter.structure.values.primitive.WenyanInteger;
import indi.wenyan.interpreter.utils.WenyanValues;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PowerBlockEntity extends AbstractModuleEntity {
    public static final int LARGE_PRIME = 1000000009;
    public static final int DISAPPEAR_TICK = 20;
    public final AtomicInteger power = new AtomicInteger(0);
    public final SecureRandom random;

    {
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Please report an issue to your Java platform as no strong SecureRandom implementation");
        }
    }

    public final List<Integer> generatedPower = new ArrayList<>(
            Collections.nCopies(DISAPPEAR_TICK, 0)
    );
    public int lastPower = 0;

    // in book: 意底*意底*...(天机) = 若干*数极 + 天意
    // a ^ b = ans mod p
    private int a;
    private int b;
    private int ans;

    @Getter
    private final boolean strong = false;

    @Getter
    public String basePackageName = "";

    @Getter
    public HandlerPackageBuilder.RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .nativeVariables(builder -> builder
                    .intFunction("「天根」", integers -> a)
                    .intFunction("「数极」", integers -> LARGE_PRIME)

                    .intFunction("「天机」", integers -> strong ? 0 : b)
                    .intFunction("「天意」", integers -> strong ? ans : 0)
                    .function("书", (WenyanInlineJavacall.BuiltinFunction) (self, args) -> {
                        int result = strong ? b : ans;
                        resetState();
                        if (args.getFirst().as(WenyanInteger.TYPE).value() == result) {
                            power.incrementAndGet();
                            return WenyanValues.of(true);
                        } else {
                            return WenyanValues.of(false);
                        }
                    })
            )
            .build();

    public PowerBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.POWER_BLOCK_ENTITY.get(), pos, blockState);
        resetState();
    }

    int n = 0;

    @Override
    public void tick(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
        if (!level.isClientSide()) {
            int firstPower = generatedPower.removeFirst();
            int lPower = power.addAndGet(-firstPower);
            generatedPower.addLast(lPower - lastPower + firstPower);
            lastPower = lPower;
            n = (n + 1) % 20;
            if (n == 0) {
                System.out.println(lPower);
            }
        }
    }

    private void resetState() {
        a = random.nextInt(2, LARGE_PRIME);
        b = random.nextInt(LARGE_PRIME);
        ans = fastPower(a, b);
    }

    private int fastPower(int a, int b) {
        long ans = 1;
        long a1 = a;
        while (b > 0) {
            if ((b & 1) == 1) {
                ans = (ans * a1) % LARGE_PRIME;
            }
            a1 = (a1 * a1) % LARGE_PRIME;
            b >>= 1;
        }
        return Math.toIntExact(ans);
    }

    // get the required power, and return the actual acquired power, not thread safe
    public int require(int acquire) {
        if (acquire < 0)
            throw new IllegalArgumentException("unreached");

        int got = 0;
        for (int i = 0; i < generatedPower.size(); i++) {
            int power = generatedPower.get(i);
            int acquirePower = Math.min(power, acquire);
            got += acquirePower;
            generatedPower.set(i, power - acquirePower);
        }
        power.addAndGet(-got);
        lastPower -= got;
        return got;
    }
}
