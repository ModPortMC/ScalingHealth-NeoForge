package net.silentchaos512.scalinghealth.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.loot.TableGlobalModifier;
import net.silentchaos512.scalinghealth.loot.conditions.EntityGroupCondition;
import net.silentchaos512.scalinghealth.utils.EntityGroup;

import java.util.concurrent.CompletableFuture;

public class LootModifierGen extends GlobalLootModifierProvider {
    public LootModifierGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ScalingHealth.MOD_ID);
    }

    @Override
    protected void start() {
//        this.add("boss", tableGlobalModifier,
//                new TableGlobalModifier(
//                        new ILootCondition[]{new EntityGroupCondition(EntityGroup.BOSS)},
//                        (TableLootEntry) TableLootEntry.builder(ScalingHealth.getId("bonus_drops/boss")).build()
//                ));
        this.add("hostile",
                new TableGlobalModifier(
                        new LootItemCondition[]{new EntityGroupCondition(EntityGroup.HOSTILE)},
                        IGlobalLootModifier.DEFAULT_PRIORITY,
                        MobTables.fromGroup(EntityGroup.HOSTILE)
                ));
        this.add("peaceful",
                new TableGlobalModifier(
                        new LootItemCondition[]{new EntityGroupCondition(EntityGroup.PEACEFUL)},
                        IGlobalLootModifier.DEFAULT_PRIORITY,
                        MobTables.fromGroup(EntityGroup.PEACEFUL)
                ));

        ChestLootTables.CHESTS.forEach(rl ->
                this.add(rl.identifier().getPath(),
                        new TableGlobalModifier(
                                new LootItemCondition[]{LootTableIdCondition.builder(rl.identifier()).build()},
                                IGlobalLootModifier.DEFAULT_PRIORITY,
                                ChestLootTables.VANILLA_TO_SH.get(rl)
                        )
                ));

    }
}
