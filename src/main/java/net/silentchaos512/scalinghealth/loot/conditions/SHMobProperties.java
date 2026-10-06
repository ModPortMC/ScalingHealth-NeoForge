package net.silentchaos512.scalinghealth.loot.conditions;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.capability.IDifficultyAffected;
import net.silentchaos512.scalinghealth.utils.config.SHDifficulty;

public class SHMobProperties implements LootItemCondition {
    public static final Identifier NAME = ScalingHealth.getId("mob_properties");

    private record DifficultyRange(float min, float max) {}

    private static final Codec<DifficultyRange> DIFFICULTY_RANGE_CODEC = Codec.<DifficultyRange, Float>either(
            RecordCodecBuilder.<DifficultyRange>create(instance -> instance.group(
                    Codec.FLOAT.optionalFieldOf("min", 0f).forGetter(DifficultyRange::min),
                    Codec.FLOAT.optionalFieldOf("max", Float.MAX_VALUE).forGetter(DifficultyRange::max)
            ).apply(instance, DifficultyRange::new)),
            Codec.FLOAT
    ).xmap(
            value -> value.map(range -> range, scalar -> new DifficultyRange(scalar, scalar)),
            range -> range.min() == range.max() ? Either.right(range.min()) : Either.left(range)
    );

    public static final MapCodec<SHMobProperties> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LootContext.EntityTarget.CODEC.fieldOf("entity").forGetter(condition -> condition.target),
            Codec.BOOL.optionalFieldOf("is_blight", false).forGetter(condition -> condition.isBlight),
            DIFFICULTY_RANGE_CODEC.optionalFieldOf("difficulty", new DifficultyRange(0, Float.MAX_VALUE))
                    .forGetter(condition -> new DifficultyRange(condition.minDifficulty, condition.maxDifficulty))
    ).apply(instance, (target, isBlight, range) ->
            new SHMobProperties(target, isBlight, range.min(), range.max())));

    private final LootContext.EntityTarget target;
    private final boolean isBlight;
    private final float minDifficulty;
    private final float maxDifficulty;

    public SHMobProperties(LootContext.EntityTarget target, boolean isBlight, float minDifficulty, float maxDifficulty) {
        this.target = target;
        this.isBlight = isBlight;
        this.minDifficulty = minDifficulty;
        this.maxDifficulty = maxDifficulty;
    }

    public static LootItemCondition.Builder builder(LootContext.EntityTarget target, boolean isBlight, float minDifficulty, float maxDifficulty) {
        return () -> new SHMobProperties(target, isBlight, minDifficulty, maxDifficulty);
    }

    @Override
    public MapCodec<SHMobProperties> codec() {
        return CODEC;
    }

    @Override
    public boolean test(LootContext lootContext) {
        Entity entity = lootContext.getOptionalParameter(this.target.contextParam());
        if (entity instanceof Mob) {
            IDifficultyAffected affected = SHDifficulty.affected(entity);
            float difficulty = affected.getDifficulty();
            return difficulty >= this.minDifficulty
                    && difficulty <= this.maxDifficulty
                    && (!this.isBlight || affected.isBlight());
        }
        return false;
    }
}
