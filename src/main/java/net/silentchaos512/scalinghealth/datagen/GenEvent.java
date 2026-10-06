package net.silentchaos512.scalinghealth.datagen;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.silentchaos512.scalinghealth.ScalingHealth;

@EventBusSubscriber(modid = ScalingHealth.MOD_ID)
public class GenEvent {
    @SubscribeEvent
    public static void gatherServerData(GatherDataEvent.Server event) {
        var output = event.getGenerator().getPackOutput();
        event.createDatapackRegistryObjects(WorldGenGenerator.BUILDER);
        var lookupProvider = event.getLookupProvider();

        event.addProvider(new Recipes(output, lookupProvider));
        event.addProvider(new LootTablesGen(output, lookupProvider));
        event.addProvider(new SHEntityTagsProvider(output, lookupProvider));
        event.addProvider(new SHBlockTagsProvider(output, lookupProvider));
        event.addProvider(new LootModifierGen(output, lookupProvider));
    }

    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        var output = event.getGenerator().getPackOutput();
        event.addProvider(new EnglishLocalization(output));
        event.addProvider(new BlockStateGen(output));
    }
}
