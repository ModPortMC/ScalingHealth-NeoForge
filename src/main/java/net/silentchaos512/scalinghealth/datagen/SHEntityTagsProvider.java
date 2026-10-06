package net.silentchaos512.scalinghealth.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.resources.tags.EntityTags;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.entity.EntityType.*;

public class SHEntityTagsProvider extends EntityTypeTagsProvider {
   public SHEntityTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
      super(output, lookupProvider, ScalingHealth.MOD_ID);
   }

   @Override
   protected void addTags(HolderLookup.Provider provider) {
      this.tag(EntityTags.BLIGHT_EXEMPT).add(BAT, CAT, CHICKEN, COD, COW, DONKEY, FOX, HORSE, MOOSHROOM, MULE,
              OCELOT, PARROT, PIG, RABBIT, SALMON, SHEEP, TROPICAL_FISH, TURTLE, VILLAGER, WANDERING_TRADER);
      this.tag(EntityTags.DIFFICULTY_EXEMPT).add(VILLAGER, WANDERING_TRADER);
   }
}
