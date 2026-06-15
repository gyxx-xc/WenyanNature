package indi.wenyan.setup.datagen.advancement;

import indi.wenyan.setup.datagen.advancement.node.RunnerAdvancements;
import net.minecraft.data.advancements.AdvancementSubProvider;

import java.util.List;

public enum AdvancementProvider {
    ;

    public static List<AdvancementSubProvider> createSubProviders() {
        return List.of(
                new RunnerAdvancements()
        );
    }
}
