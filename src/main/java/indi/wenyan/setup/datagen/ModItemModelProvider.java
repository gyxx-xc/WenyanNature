package indi.wenyan.setup.datagen;

import indi.wenyan.content.item.ink.*;
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

    public static final String ARCANE_INK_ID = arcane_ink.item_ID;
    public static final String BAMBOO_INK_ID = bamboo_ink.item_ID;
    public static final String CELESTIAL_INK_ID = celestial_ink.item_ID;
    public static final String CINNABAR_INK_ID = cinnabar_ink.item_ID;
    public static final String LUNAR_INK_ID = lunar_ink.item_ID;
    public static final String STARLIGHT_INK_ID = starlight_ink.item_ID;

    public ModItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(Registration.HAND_RUNNER.get());
        basicItem(Registration.HAND_RUNNER_1.get());
        basicItem(Registration.HAND_RUNNER_2.get());
        basicItem(Registration.HAND_RUNNER_3.get());
        //Paper
        singleTexture(BAMBOO_PAPER_ID, ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0", ResourceLocation.fromNamespaceAndPath(MODID,"item/paper/" + BAMBOO_PAPER_ID));
        singleTexture(CLOUD_PAPER_ID, ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0", ResourceLocation.fromNamespaceAndPath(MODID,"item/paper/" + CLOUD_PAPER_ID));
        singleTexture(DRAGON_PAPER_ID, ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0", ResourceLocation.fromNamespaceAndPath(MODID,"item/paper/" + DRAGON_PAPER_ID));
        singleTexture(FROST_PAPER_ID, ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0", ResourceLocation.fromNamespaceAndPath(MODID,"item/paper/" + FROST_PAPER_ID));
        singleTexture(PHOENIX_PAPER_ID, ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0", ResourceLocation.fromNamespaceAndPath(MODID,"item/paper/" + PHOENIX_PAPER_ID));
        singleTexture(STAR_PAPER_ID, ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0", ResourceLocation.fromNamespaceAndPath(MODID,"item/paper/" + STAR_PAPER_ID));
        //INK
        singleTexture(ARCANE_INK_ID, ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0", ResourceLocation.fromNamespaceAndPath(MODID,"item/ink/" + ARCANE_INK_ID));
        singleTexture(BAMBOO_INK_ID, ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0", ResourceLocation.fromNamespaceAndPath(MODID,"item/ink/" + BAMBOO_INK_ID));
        singleTexture(CELESTIAL_INK_ID, ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0", ResourceLocation.fromNamespaceAndPath(MODID,"item/ink/" + CELESTIAL_INK_ID));
        singleTexture(CINNABAR_INK_ID, ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0", ResourceLocation.fromNamespaceAndPath(MODID,"item/ink/" + CINNABAR_INK_ID));
        singleTexture(LUNAR_INK_ID, ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0", ResourceLocation.fromNamespaceAndPath(MODID,"item/ink/" + LUNAR_INK_ID));
        singleTexture(STARLIGHT_INK_ID, ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0", ResourceLocation.fromNamespaceAndPath(MODID,"item/ink/" + STARLIGHT_INK_ID));
    }
}
