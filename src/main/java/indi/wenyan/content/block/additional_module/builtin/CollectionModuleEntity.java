package indi.wenyan.content.block.additional_module.builtin;

import indi.wenyan.content.block.additional_module.AbstractModuleEntity;
import indi.wenyan.interpreter.structure.values.IWenyanComparable;
import indi.wenyan.interpreter.structure.values.WenyanPackage;
import indi.wenyan.interpreter.structure.values.warper.WenyanList;
import indi.wenyan.interpreter.utils.WenyanPackageBuilder;
import indi.wenyan.interpreter.utils.WenyanValues;
import indi.wenyan.setup.Registration;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionModuleEntity extends AbstractModuleEntity {
    @Getter
    private final String basePackageName = "「集」";

    @Getter
    private final WenyanPackage execPackage = WenyanPackageBuilder.create()
            .function("「無交」", (self, args) -> {
                var array1 = args.get(0).as(WenyanList.TYPE).value();
                var array2 = args.get(1).as(WenyanList.TYPE).value();
                return WenyanValues.of(Collections.disjoint(array1, array2));
            })
            .function("「交集」", (self, args) -> {
                var array1 = args.get(0).as(WenyanList.TYPE).value();
                var array2 = args.get(1).as(WenyanList.TYPE).value();
                var intersection = array1.stream().filter(array2::contains).toList();
                return WenyanValues.of(intersection);
            })
            .function("「差集」", (self, args) -> {
                var array1 = args.get(0).as(WenyanList.TYPE).value();
                var array2 = args.get(1).as(WenyanList.TYPE).value();
                var difference = array1.stream().filter(e -> !array2.contains(e)).toList();
                return WenyanValues.of(difference);
            })
            .function("「反轉」", (self, args) -> {
                var array = args.getFirst().as(WenyanList.TYPE).value();
                Collections.reverse(array);
                return WenyanValues.of(array);
            })
            .function("「排序」", (self, args) -> {
                var array = args.getFirst().as(WenyanList.TYPE).value();
                // converting to IWenyanComparable
                List<IWenyanComparable> sorted = new ArrayList<>();
                for (var item : array) {
                    sorted.add(item.as(IWenyanComparable.TYPE));
                }
                sorted.sort((o1, o2) -> {
                    try {
                        return o1.compareTo(o2);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                return WenyanValues.of(new ArrayList<>(sorted));
            })
            .function("「包含」", (self, args) -> {
                var array = args.get(0).as(WenyanList.TYPE).value();
                var element = args.get(1);
                return WenyanValues.of(array.contains(element));
            })
            .function("「最大」", (self, args) -> {
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
            .function("「最小」", (self, args) -> {
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
            })
            // code here
            .build();


    public CollectionModuleEntity(BlockPos pos, BlockState blockState) {
        super(Registration.COLLECTION_MODULE_ENTITY.get(), pos, blockState);
    }
}