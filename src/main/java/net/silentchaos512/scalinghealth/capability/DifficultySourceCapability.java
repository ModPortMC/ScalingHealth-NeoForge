package net.silentchaos512.scalinghealth.capability;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.silentchaos512.scalinghealth.utils.config.SHDifficulty;

public class DifficultySourceCapability implements IDifficultySource, ValueIOSerializable {
    public static final AttachmentType<DifficultySourceCapability> INSTANCE = AttachmentType
            .serializable(DifficultySourceCapability::new)
            .copyOnDeath()
            .build();

    private static final String NBT_DIFFICULTY = "Difficulty";

    private float difficulty;
    private boolean exempt = false;
    private static volatile float clientWorldDifficulty;

    public static float getClientWorldDifficulty() {
        return clientWorldDifficulty;
    }

    public static void setClientWorldDifficulty(float difficulty) {
        clientWorldDifficulty = difficulty;
    }

    @Override
    public float getDifficulty() {
        return exempt ? 0 : difficulty;
    }

    @Override
    public void setDifficulty(float value) {
        difficulty = (float) SHDifficulty.clamp(value);
    }

    @Override
    public void setExempt(boolean exempt){
        this.exempt = exempt;
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putFloat(NBT_DIFFICULTY, difficulty);
    }

    @Override
    public void deserialize(ValueInput input) {
        difficulty = input.getFloatOr(NBT_DIFFICULTY, 0);
    }
}
