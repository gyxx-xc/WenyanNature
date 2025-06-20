package indi.wenyan.setup.datagen;

import indi.wenyan.content.item.paper.*;
import indi.wenyan.setup.Registration;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static indi.wenyan.WenyanNature.MODID;


public class ModItemModelProvider extends ItemModelProvider {
    public static final String BAMBOO_PAPER_ID = bamboo_paper.item_ID;
    public static final String CLOUD_PAPER_ID = cloud_paper.item_ID;
    public static final String DRAGON_PAPER_ID = dragon_paper.item_ID;
    public static final String FROST_PAPER_ID = frost_paper.item_ID;
    public static final String PHOENIX_PAPER_ID = phoenix_paper.item_ID;
    public static final String STAR_PAPER_ID = star_paper.item_ID;

    public ModItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.basicItem(Registration.HAND_RUNNER.get());
        this.basicItem(Registration.HAND_RUNNER_1.get());
        this.basicItem(Registration.HAND_RUNNER_2.get());
        this.basicItem(Registration.HAND_RUNNER_3.get());
//Paper
        this.singleTexture(BAMBOO_PAPER_ID, ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0", ResourceLocation.fromNamespaceAndPath(MODID,"item/paper/" + BAMBOO_PAPER_ID));
        this.singleTexture(CLOUD_PAPER_ID, ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0", ResourceLocation.fromNamespaceAndPath(MODID,"item/paper/" + CLOUD_PAPER_ID));
        this.singleTexture(DRAGON_PAPER_ID, ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0", ResourceLocation.fromNamespaceAndPath(MODID,"item/paper/" + DRAGON_PAPER_ID));
        this.singleTexture(FROST_PAPER_ID, ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0", ResourceLocation.fromNamespaceAndPath(MODID,"item/paper/" + FROST_PAPER_ID));
        this.singleTexture(PHOENIX_PAPER_ID, ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0", ResourceLocation.fromNamespaceAndPath(MODID,"item/paper/" + PHOENIX_PAPER_ID));
        this.singleTexture(STAR_PAPER_ID, ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0", ResourceLocation.fromNamespaceAndPath(MODID,"item/paper/" + STAR_PAPER_ID));

    }
}
