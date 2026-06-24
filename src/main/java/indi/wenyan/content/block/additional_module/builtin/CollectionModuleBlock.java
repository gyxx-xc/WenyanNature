package indi.wenyan.content.block.additional_module.builtin;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.api.exec.structure.RawHandlerPackage;
import indi.wenyan.judou.api.utils.WenyanValues;
import indi.wenyan.judou.api.values.IWenyanComparable;
import indi.wenyan.judou.api.values.IWenyanValue;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.judou.api.values.exception.WenyanUnreachedException;
import indi.wenyan.judou.api.values.primitive.WenyanList;
import indi.wenyan.setup.language.FunctionMetaText;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionModuleBlock extends AbstractFuluBlock {
    public static final String ID = "collection_module_block";

    public static final MapCodec<CollectionModuleBlock> CODEC = simpleCodec(CollectionModuleBlock::new);
    public static final String DEVICE_NAME = WenyanSymbol.CollectionModule;
    @Getter(lazy = true)
    private static final RawHandlerPackage execPackage = HandlerPackageBuilder.create()
            .nativeVariables(builder -> builder
                    .description(FunctionMetaText.CollectionModuleDisjoint.string())
                    .function(WenyanSymbol.CollectionModule$disjoint,
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var array1 = args.get(0).as(WenyanList.TYPE).value();
                                var array2 = args.get(1).as(WenyanList.TYPE).value();
                                return WenyanValues.of(Collections.disjoint(array1, array2));
                            })
                    .description(FunctionMetaText.CollectionModuleIntersection.string())
                    .function(WenyanSymbol.CollectionModule$intersection,
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var array1 = args.get(0).as(WenyanList.TYPE).value();
                                var array2 = args.get(1).as(WenyanList.TYPE).value();
                                var intersection = array1.stream().filter(array2::contains).toList();
                                return WenyanValues.of(intersection);
                            })
                    .description(FunctionMetaText.CollectionModuleDifference.string())
                    .function(WenyanSymbol.CollectionModule$difference,
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var array1 = args.get(0).as(WenyanList.TYPE).value();
                                var array2 = args.get(1).as(WenyanList.TYPE).value();
                                var difference = array1.stream().filter(e -> !array2.contains(e)).toList();
                                return WenyanValues.of(difference);
                            })
                    .description(FunctionMetaText.CollectionModuleReverse.string())
                    .function(WenyanSymbol.CollectionModule$reverse,
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var array = args.getFirst().as(WenyanList.TYPE).value();
                                Collections.reverse(array);
                                return WenyanValues.of(array);
                            })
                    .description(FunctionMetaText.CollectionModuleSort.string())
                    .function(WenyanSymbol.CollectionModule$sort,
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var array = args.getFirst().as(WenyanList.TYPE).value();
                                // converting to IWenyanComparable
                                List<IWenyanComparable> sorted = new ArrayList<>();
                                for (var item : array) {
                                    sorted.add(item.as(IWenyanComparable.TYPE));
                                }
                                try {
                                    sorted.sort((o1, o2) -> {
                                        try {
                                            return o1.compareTo(o2);
                                        } catch (WenyanException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                                } catch (RuntimeException e) {
                                    if (e.getCause() instanceof WenyanException we)
                                        throw we;
                                    throw new WenyanUnreachedException.WenyanUnexceptedException(e);
                                }
                                return WenyanValues.of(new ArrayList<>(sorted));
                            })
                    .description(FunctionMetaText.CollectionModuleContains.string())
                    .function(WenyanSymbol.CollectionModule$contains,
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var array = args.get(0).as(WenyanList.TYPE).value();
                                var element = args.get(1);
                                return WenyanValues.of(array.contains(element));
                            })
                    .description(FunctionMetaText.CollectionModuleMax.string())
                    .function(WenyanSymbol.CollectionModule$max,
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var array = args.getFirst().as(WenyanList.TYPE).value();
                                if (array.isEmpty()) {
                                    return WenyanValues.of(0);
                                }
                                IWenyanComparable max = null;
                                for (var item : array) {
                                    var comp = item.as(IWenyanComparable.TYPE);
                                    if (max == null || comp.compareTo(max) > 0) {
                                        max = comp;
                                    }
                                }
                                return max;
                            })
                    .description(FunctionMetaText.CollectionModuleMin.string())
                    .function(WenyanSymbol.CollectionModule$min,
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var array = args.getFirst().as(WenyanList.TYPE).value();
                                if (array.isEmpty()) {
                                    return WenyanValues.of(0);
                                }
                                IWenyanComparable min = null;
                                for (var item : array) {
                                    var comp = item.as(IWenyanComparable.TYPE);
                                    if (min == null || comp.compareTo(min) < 0) {
                                        min = comp;
                                    }
                                }
                                return min;
                            }))
            .build();

    public CollectionModuleBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<CollectionModuleBlock> codec() {
        return CODEC;
    }
}
