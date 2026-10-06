package net.silentchaos512.scalinghealth.loot.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.utils.EntityGroup;

import java.util.Locale;

public class EntityGroupCondition implements LootItemCondition {
   public static final Identifier NAME = ScalingHealth.getId("entity_group_condition");

   public static final MapCodec<EntityGroupCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
           Codec.STRING.xmap(EntityGroup::from, group -> group.name().toLowerCase(Locale.ROOT))
                   .fieldOf("entity_group").forGetter(condition -> condition.group)
   ).apply(instance, EntityGroupCondition::new));

   private final EntityGroup group;

   public EntityGroupCondition(EntityGroup group) {
      this.group = group;
   }

   /**
    * Tests the mob group of this mob, and tests that a damage source is present.
    *
    * This prevents a block loot table triggered by an explosion from being treated as
    * a mob drop just because the loot context has a this-entity value.
    */
   @Override
   public boolean test(LootContext context) {
      Entity entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
      return context.hasParameter(LootContextParams.DAMAGE_SOURCE)
              && entity instanceof LivingEntity living
              && EntityGroup.from(living, true) == this.group;
   }

   @Override
   public MapCodec<EntityGroupCondition> codec() {
      return CODEC;
   }
}
