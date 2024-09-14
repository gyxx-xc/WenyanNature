package indi.wenyan.datagen;

import indi.wenyan.WenyanNature;
import indi.wenyan.setup.Registration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Objects;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.basicItem(Registration.HAND_RUNNER.get());
    }

    public ResourceLocation getResourceLocation(Item item){
        return Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item));
    }
}
