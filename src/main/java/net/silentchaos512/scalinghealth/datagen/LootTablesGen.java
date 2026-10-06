package net.silentchaos512.scalinghealth.datagen;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.data.PackOutput;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class LootTablesGen extends LootTableProvider {
    public static final List<SubProviderEntry> PROVIDERS = ImmutableList.of(
            new SubProviderEntry(BlockTables::new, LootContextParamSets.BLOCK),
            new SubProviderEntry(lookup -> new ChestLootTables(), LootContextParamSets.CHEST),
            new SubProviderEntry(MobTables::new, LootContextParamSets.ENTITY)
    );

    public LootTablesGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Set.of(), PROVIDERS, lookupProvider);
    }
}
