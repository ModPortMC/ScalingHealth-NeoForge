package net.silentchaos512.scalinghealth.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.silentchaos512.scalinghealth.objects.item.DifficultyMutatorItem;
import net.silentchaos512.scalinghealth.objects.item.PowerCrystal;
import net.silentchaos512.scalinghealth.utils.config.EnabledFeatures;

public class TableGlobalModifier extends LootModifier {
   public static final MapCodec<TableGlobalModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> codecStart(instance)
           .and(ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("table").forGetter(modifier -> modifier.table))
           .apply(instance, TableGlobalModifier::new));

   private final ResourceKey<LootTable> table;

   public TableGlobalModifier(LootItemCondition[] conditions, int priority, ResourceKey<LootTable> table) {
      super(conditions, priority);
      this.table = table;
   }

   @Override
   public MapCodec<? extends IGlobalLootModifier> codec() {
      return CODEC;
   }

   @SuppressWarnings("deprecation")
   @Override
   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
      context.getResolver().lookupOrThrow(Registries.LOOT_TABLE).get(this.table).ifPresent(extraTable ->
              extraTable.value().getRandomItemsRaw(context, LootTable.createStackSplitter(context.getLevel(), generatedLoot::add)));
      generatedLoot.forEach(stack -> {
         if ((EnabledFeatures.powerCrystalEnabled() && stack.getItem() instanceof PowerCrystal)
                 || (!EnabledFeatures.difficultyEnabled() && stack.getItem() instanceof DifficultyMutatorItem)) {
            stack.setCount(0);
         }
      });
      return generatedLoot;
   }
}
