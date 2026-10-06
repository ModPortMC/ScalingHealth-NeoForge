package net.silentchaos512.scalinghealth.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.silentchaos512.scalinghealth.utils.ModifierHandler;
import net.silentchaos512.scalinghealth.utils.config.SHDifficulty;
import net.silentchaos512.scalinghealth.utils.config.SHPlayers;

public class PlayerDataCapability implements IPlayerData, ValueIOSerializable {
    public static final AttachmentType<PlayerDataCapability> INSTANCE = AttachmentType
            .serializable(PlayerDataCapability::new)
            .copyOnDeath()
            .build();

    private static final String NBT_HEART_CRYSTALS = "HeartCrystals";
    private static final String NBT_POWER_CRYSTALS = "PowerCrystals";

    private boolean afk;
    private int timeAfk;
    private BlockPos lastPos;

    private int heartCrystals;
    private int powerCrystals;

    @Override
    public int getBonusHearts(Player player) {
        return SHPlayers.clampedHpFromHeartCrystals(getHeartCrystals()) + SHPlayers.fullHeartsFromXp(player.experienceLevel);
    }

    @Override
    public int getHeartCrystals() {
        return heartCrystals;
    }

    @Override
    public void setHeartCrystals(Player player, int amount) {
        heartCrystals = SHPlayers.clampExtraHearts(amount);
        ModifierHandler.setMaxHealth(player, getModifiedHealth(player), AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public int getPowerCrystals() {
        return powerCrystals;
    }

    @Override
    public void setPowerCrystalCount(Player player, int amount) {
        powerCrystals = SHPlayers.clampPowerCrystals(amount);
        ModifierHandler.addAttackDamage(player, getAttackDamageModifier(), AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public void updateStats(Player player) {
        ModifierHandler.setMaxHealth(player, getModifiedHealth(player), AttributeModifier.Operation.ADD_VALUE);
        ModifierHandler.addAttackDamage(player, getAttackDamageModifier(), AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public void tick(Player player) {
        if (player.level().getGameTime() % 20 == 0 && !player.level().isClientSide()) {
            checkPlayerIdle(player);

            if (player instanceof ServerPlayer)
                IPlayerData.sendUpdatePacketTo(player);
        }
    }

    private void checkPlayerIdle(Player player) {
        if (SHDifficulty.areaDifficulty(player.level(), player.blockPosition()) >= SHDifficulty.maxValue()) return;

        if (player.blockPosition().equals(lastPos)) {
            timeAfk++;
        } else {
            afk = false;
            timeAfk = 0;
        }

        lastPos = player.blockPosition();
        if (timeAfk > SHDifficulty.timeBeforeAfk()) {
            if (!afk) {
                afk = true;
                if (SHDifficulty.afkMessage())
                    player.sendSystemMessage(Component.translatable("misc.scalinghealth.afkmessage"));
            }
        }

        if (afk) {
            IDifficultySource data = SHDifficulty.source(player);
            float changePerSec = (float) SHDifficulty.changePerSecond();
            // Since last second we added changePerSec difficulty, subtract the idle portion.
            data.addDifficulty(-changePerSec * (float) (1 - SHDifficulty.idleModifier()));
        }
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putInt(NBT_HEART_CRYSTALS, heartCrystals);
        output.putInt(NBT_POWER_CRYSTALS, powerCrystals);
    }

    @Override
    public void deserialize(ValueInput input) {
        heartCrystals = input.getIntOr(NBT_HEART_CRYSTALS, 0);
        powerCrystals = input.getIntOr(NBT_POWER_CRYSTALS, 0);
    }
}
