package net.silentchaos512.scalinghealth.datagen;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.objects.Registration;

public class BlockStateGen extends ModelProvider {
    public BlockStateGen(PackOutput output) {
        super(output, ScalingHealth.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        simpleBlock(blockModels, Registration.HEART_CRYSTAL_ORE.get());
        simpleBlock(blockModels, Registration.POWER_CRYSTAL_ORE.get());
        simpleBlock(blockModels, Registration.DEEPLSATE_HEART_CRYSTAL_ORE.get());
        simpleBlock(blockModels, Registration.DEEPSLATE_POWER_CRYSTAL_ORE.get());

        itemModels.generateFlatItem(Registration.BANDAGES.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.CHANCE_HEART.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.CURSED_HEART.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.ENCHANTED_HEART.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.HEART_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.HEART_CRYSTAL_SHARD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.HEART_DUST.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.MEDKIT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.POWER_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.POWER_CRYSTAL_SHARD.get(), ModelTemplates.FLAT_ITEM);
    }

    private static void simpleBlock(BlockModelGenerators models, net.minecraft.world.level.block.Block block) {
        models.createTrivialCube(block);
        models.registerSimpleItemModel(block, ModelLocationUtils.getModelLocation(block));
    }
}
