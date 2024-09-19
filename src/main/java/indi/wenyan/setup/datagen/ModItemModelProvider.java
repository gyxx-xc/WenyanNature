package indi.wenyan.setup.datagen;

import indi.wenyan.setup.Registration;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.basicItem(Registration.HAND_RUNNER.get());
        this.basicItem(Registration.HAND_RUNNER_1.get());
        this.basicItem(Registration.HAND_RUNNER_2.get());
        this.basicItem(Registration.HAND_RUNNER_3.get());
    }

}
