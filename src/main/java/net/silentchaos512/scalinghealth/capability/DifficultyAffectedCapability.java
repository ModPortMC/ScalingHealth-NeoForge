package net.silentchaos512.scalinghealth.capability;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.config.SHConfig;
import net.silentchaos512.scalinghealth.event.DifficultyEvents;
import net.silentchaos512.scalinghealth.utils.MobDifficultyHandler;
import net.silentchaos512.scalinghealth.utils.config.SHDifficulty;
import net.silentchaos512.scalinghealth.utils.config.SHMobs;

public class DifficultyAffectedCapability implements IDifficultyAffected, ValueIOSerializable {
    public static final AttachmentType<DifficultyAffectedCapability> INSTANCE = AttachmentType
            .serializable(DifficultyAffectedCapability::new)
            .build();

    private static final String NBT_BLIGHT = "Blight";
    private static final String NBT_DIFFICULTY = "Difficulty";

    private float difficulty;
    private boolean blight;
    private boolean processed;

    @Override
    public float getDifficulty() {
        return difficulty;
    }

    @Override
    public void setDifficulty(Mob mob) {
        difficulty = (float) ((Math.random()*(0.1)+0.95) * SHDifficulty.areaDifficulty(mob.level(), mob.blockPosition()));
    }

    @Override
    public void forceDifficulty(float diff) {
        difficulty = diff;
    }

    @Override
    public boolean isBlight() {
        return blight;
    }

    @Override
    public void setIsBlight(boolean value) {
        blight = value;
    }

    @Override
    public void setProcessed(boolean value) {
        this.processed = value;
    }

    @Override
    public void tick(Mob entity) {
        if (!processed && entity.isAlive() && entity.tickCount > 2) {
            setDifficulty(entity);
            MobDifficultyHandler.process(entity, this);
            processed = true;

            if (ScalingHealth.LOGGER.isDebugEnabled() && SHConfig.SERVER.debugLogEntitySpawns.get()) {
                ScalingHealth.LOGGER.debug(DifficultyEvents.MARKER, "Processed {} -> difficulty={}, isBlight={}", entity, difficulty, blight);
            }
        }
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putBoolean(NBT_BLIGHT, blight);
        output.putFloat(NBT_DIFFICULTY, difficulty);
    }

    @Override
    public void deserialize(ValueInput input) {
        blight = input.getBooleanOr(NBT_BLIGHT, false);
        difficulty = input.getFloatOr(NBT_DIFFICULTY, 0);
    }
}
