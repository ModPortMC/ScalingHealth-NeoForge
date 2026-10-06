package net.silentchaos512.scalinghealth.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.objects.Registration;

import java.util.concurrent.CompletableFuture;

public class SHBlockTagsProvider extends BlockTagsProvider {
    public SHBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ScalingHealth.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(Registration.HEART_CRYSTAL_ORE.get())
                .add(Registration.POWER_CRYSTAL_ORE.get())
                .add(Registration.DEEPLSATE_HEART_CRYSTAL_ORE.get())
                .add(Registration.DEEPSLATE_POWER_CRYSTAL_ORE.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(Registration.HEART_CRYSTAL_ORE.get())
                .add(Registration.POWER_CRYSTAL_ORE.get())
                .add(Registration.DEEPLSATE_HEART_CRYSTAL_ORE.get())
                .add(Registration.DEEPSLATE_POWER_CRYSTAL_ORE.get());
    }
}
