package indi.wenyan.content.block.additional_module.builtin;

import com.mojang.serialization.MapCodec;
import indi.wenyan.content.block.AbstractFuluBlock;
import indi.wenyan.interpreter_impl.HandlerPackageBuilder;
import indi.wenyan.interpreter_impl.WenyanSymbol;
import indi.wenyan.judou.exec_interface.RawHandlerPackage;
import indi.wenyan.judou.structure.WenyanException;
import indi.wenyan.judou.structure.WenyanUnreachedException;
import indi.wenyan.judou.structure.values.IWenyanComparable;
import indi.wenyan.judou.structure.values.IWenyanValue;
import indi.wenyan.judou.structure.values.primitive.WenyanList;
import indi.wenyan.judou.utils.WenyanValues;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionModuleBlock extends AbstractFuluBlock {
    public static final String ID = "collection_module_block";

    public static final MapCodec<CollectionModuleBlock> CODEC = simpleCodec(CollectionModuleBlock::new);
    public static final String DEVICE_NAME = WenyanSymbol.var("CollectionModule");
    public static final RawHandlerPackage PACKAGE = HandlerPackageBuilder.create()
            .nativeVariables(builder -> builder
                    .function(WenyanSymbol.var("CollectionModule.disjoint"),
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var array1 = args.get(0).as(WenyanList.TYPE).value();
                                var array2 = args.get(1).as(WenyanList.TYPE).value();
                                return WenyanValues.of(Collections.disjoint(array1, array2));
                            })
                    .function(WenyanSymbol.var("CollectionModule.intersection"),
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var array1 = args.get(0).as(WenyanList.TYPE).value();
                                var array2 = args.get(1).as(WenyanList.TYPE).value();
                                var intersection = array1.stream().filter(array2::contains).toList();
                                return WenyanValues.of(intersection);
                            })
                    .function(WenyanSymbol.var("CollectionModule.difference"),
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var array1 = args.get(0).as(WenyanList.TYPE).value();
                                var array2 = args.get(1).as(WenyanList.TYPE).value();
                                var difference = array1.stream().filter(e -> !array2.contains(e)).toList();
                                return WenyanValues.of(difference);
                            })
                    .function(WenyanSymbol.var("CollectionModule.reverse"),
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var array = args.getFirst().as(WenyanList.TYPE).value();
                                Collections.reverse(array);
                                return WenyanValues.of(array);
                            })
                    .function(WenyanSymbol.var("CollectionModule.sort"),
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
                    .function(WenyanSymbol.var("CollectionModule.contains"),
                            (IWenyanValue _, List<IWenyanValue> args) -> {
                                var array = args.get(0).as(WenyanList.TYPE).value();
                                var element = args.get(1);
                                return WenyanValues.of(array.contains(element));
                            })
                    .function(WenyanSymbol.var("CollectionModule.max"),
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
                    .function(WenyanSymbol.var("CollectionModule.min"),
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
