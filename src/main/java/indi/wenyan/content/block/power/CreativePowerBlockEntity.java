package indi.wenyan.content.block.power;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.api.exec.structure.RawHandlerPackage;
import indi.wenyan.judou.api.utils.ChineseUtils;
import indi.wenyan.judou.api.values.WenyanNull;
import indi.wenyan.judou.api.values.primitive.WenyanInteger;
import indi.wenyan.setup.definitions.WenyanBlocks;
import indi.wenyan.setup.language.FunctionMetaText;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;

@ParametersAreNonnullByDefault
public class CreativePowerBlockEntity extends AbstractModuleEntity {
    @Getter
    public String basePackageName = WenyanSymbol.POWER;

    @Getter
    private final Deque<TextEffect> textEffects = new ArrayDeque<>();

    private int power = 100;
    private int lifetime = 0;
    private int nextUpdate = 0;

    @Getter
    public RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .description(FunctionMetaText.POWER_UP.string())
            .handler(WenyanSymbol.POWER_UP, request -> {
                power = request.args().getFirst().as(WenyanInteger.TYPE).value();
                return WenyanNull.NULL;
            })
            .description(FunctionMetaText.POWER_ANS.string())
            .handler(WenyanSymbol.POWER_ANS, request -> {
                power = request.args().getFirst().as(WenyanInteger.TYPE).value();
                return WenyanNull.NULL;
            })
            .build();

    public CreativePowerBlockEntity(BlockPos pos, BlockState blockState) {
        super(WenyanBlocks.CREATIVE_POWER_BLOCK_ENTITY.get(), pos, blockState);
    }

    // get the required power, and return the actual acquired power, not thread safe
    @Deprecated
    public int require(int acquire) {
        return Math.min(acquire, power);
    }

    @SuppressWarnings("unused")
    public void tick(Level level, BlockPos pos, BlockState state, RandomSource random) {
        lifetime++;
        if (lifetime > nextUpdate) {
            String data = ChineseUtils.toChinese(BigInteger.valueOf(random.nextInt(1000 * power)));
            textEffects.addAll(TextEffect.randomSplash(data, random));
            nextUpdate = random.nextInt(1, 5);
            lifetime = 0;
        }

        for (var textEffect : textEffects) {
            if (textEffect.remainTick > 0) {
                textEffect.remainTick--;
            } else {
                textEffects.removeFirstOccurrence(textEffect);
            }
        }
    }
}
