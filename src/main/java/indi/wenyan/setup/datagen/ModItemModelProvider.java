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
        basicItem(Registration.HAND_RUNNER.get());
        basicItem(Registration.HAND_RUNNER_1.get());
        basicItem(Registration.HAND_RUNNER_2.get());
        basicItem(Registration.HAND_RUNNER_3.get());

        basicItem(Registration.BAMBOO_PAPER.get());
        basicItem(Registration.CLOUD_PAPER.get());
        basicItem(Registration.DRAGON_PAPER.get());
        basicItem(Registration.FROST_PAPER.get());
        basicItem(Registration.PHOENIX_PAPER.get());
        basicItem(Registration.STAR_PAPER.get());

        basicItem(Registration.ARCANE_INK.get());
        basicItem(Registration.BAMBOO_INK.get());
        basicItem(Registration.CELESTIAL_INK.get());
        basicItem(Registration.LUNAR_INK.get());
        basicItem(Registration.CINNABAR_INK.get());
        basicItem(Registration.STARLIGHT_INK.get());

        withExistingParent(Registration.ADDITIONAL_PAPER_BLOCK.getId().getPath(),
                modLoc("block/runner_block"));
    }
}
