package indi.wenyan.setup.datagen;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.setup.Registration;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;

/**
 * Provider for generating particle descriptions during data generation.
 * Maps particle types to their texture resources.
 */
public class ModParticleDescriptionProvider extends ParticleDescriptionProvider {

    /**
     * Constructs a new particle description provider.
     * @param output The pack output for particle description generation
     * @param fileHelper Helper for accessing existing files
     */
    protected ModParticleDescriptionProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, fileHelper);
    }

    @Override
    protected void addDescriptions() {
        sprite(Registration.COMMUNICATION_PARTICLES.get(),
                ResourceLocation.fromNamespaceAndPath(WenyanProgramming.MODID, "communication"));
    }
}
