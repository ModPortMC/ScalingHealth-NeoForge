package net.silentchaos512.scalinghealth.utils.serialization;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.config.SHConfig;
import net.silentchaos512.scalinghealth.utils.EntityGroup;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public record DifficultyMobEffect(Holder<MobEffect> effect, int level, int minDifficulty,
                                  double durationMinutes) {
    private static final Marker MARKER = MarkerManager.getMarker("DifficultyMobEffects");

    public static final Codec<DifficultyMobEffect> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(DifficultyMobEffect::effect),
                    SerializationUtils.positiveInt().fieldOf("level").forGetter(e -> e.level),
                    SerializationUtils.positiveInt().fieldOf("minDifficulty").forGetter(e -> e.minDifficulty),
                    SerializationUtils.positiveDouble().fieldOf("durationInMinutes").forGetter(e -> e.durationMinutes)
            ).apply(inst, DifficultyMobEffect::new)
    );

    public void apply(LivingEntity e, double difficulty) {
        if (difficulty >= minDifficulty) {
            e.addEffect(new MobEffectInstance(effect, (int) (durationMinutes * 60 * 20), level - 1));
            if (ScalingHealth.LOGGER.isDebugEnabled() && SHConfig.SERVER.debugMobPotionEffects.get()) {
                ScalingHealth.LOGGER.debug(MARKER, "Applied effect {}, level {} for {}min to {} ({})",
                        BuiltInRegistries.MOB_EFFECT.getKey(effect.value()), level, durationMinutes, e.getScoreboardName(), BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()));
            }
        }
    }

    public void tryApply(LivingEntity e, double difficulty) {
        if (ScalingHealth.RANDOM.nextDouble() < EntityGroup.from(e).getPotionChance())
            apply(e, difficulty);
    }
}
