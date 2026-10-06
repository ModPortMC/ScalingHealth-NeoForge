package net.silentchaos512.scalinghealth.objects.item;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.capability.IPlayerData;
import net.silentchaos512.scalinghealth.capability.PetHealthCapability;
import net.silentchaos512.scalinghealth.resources.mechanics.SHMechanics;
import net.silentchaos512.scalinghealth.utils.ParticleUtils;
import net.silentchaos512.scalinghealth.utils.SoundUtils;
import net.silentchaos512.scalinghealth.utils.config.SHPlayers;

import java.util.function.Consumer;

public abstract class StatBoosterItem extends Item {
    public StatBoosterItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flagIn) {
        tooltip.accept(Component.translatable(this.getDescriptionId() + ".desc"));
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand handIn) {
        ItemStack stack = player.getItemInHand(handIn);
        final boolean statIncreaseAllowed = isStatIncreaseAllowed(player);
        final int levelRequirement = getLevelCost(player);

        // Does player have enough XP?
        if (player.experienceLevel < levelRequirement) {
            if (world.isClientSide()) {
                String translationKey = "item.scalinghealth.stat_booster.notEnoughXP";
                player.sendSystemMessage(Component.translatable(translationKey, levelRequirement));
            }
            return InteractionResult.PASS;
        }

        if (!world.isClientSide()) {
            // May be used as a healing item even if there is no stat increase
            final boolean consumed = shouldConsume(player);
            if (consumed) {
                extraConsumeEffect(player);
            }

            // End here if stat increases are not allowed
            if (!statIncreaseAllowed) {
                return useAsConsumable(world, player, stack, levelRequirement, consumed);
            }

            // Increase stat, consume item
            return useAsStatIncreaseItem(player, stack, levelRequirement);
        }
        else if(shouldConsume(player) || isStatIncreaseAllowed(player))
            spawnParticlesAndPlaySound(player);

        return InteractionResult.SUCCESS;
    }

    public void increasePetHp(Player player, TamableAnimal pet, ItemStack stack){
        //check config

        int levelRequirement = getLevelCost(player);
        if (player.experienceLevel < levelRequirement) {
            String translationKey = "item.scalinghealth.stat_booster.notEnoughXP";
            player.sendSystemMessage(Component.translatable(translationKey, levelRequirement));
            return;
        }

        pet.getData(PetHealthCapability.INSTANCE)
                .addHealth(SHMechanics.getMechanics().mobMechanics().pets().petsHealthCrystalGain(), pet);
        stack.shrink(1);
        consumeLevels(player, levelRequirement);
        player.awardStat(Stats.ITEM_USED.get(this));
    }

    protected abstract int getLevelCost(Player player);

    protected abstract boolean isStatIncreaseAllowed(Player player);

    protected abstract boolean shouldConsume(Player player);

    protected abstract void extraConsumeEffect(Player player);

    protected abstract void increaseStat(Player player);

    protected abstract ParticleOptions getParticleType();

    protected abstract SoundEvent getSoundEffect();

    private InteractionResult useAsConsumable(Level world, Player player, ItemStack stack, int levelRequirement, boolean consumed) {
        if (consumed) {
            world.playSound(null, player.blockPosition(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS,
                    0.5f, 1 + 0.1f * (float) ScalingHealth.RANDOM.nextGaussian());
            stack.shrink(1);
            consumeLevels(player, levelRequirement);
            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private InteractionResult useAsStatIncreaseItem(Player player, ItemStack stack, int levelRequirement) {
        increaseStat(player);
        stack.shrink(1);
        consumeLevels(player, levelRequirement);
        player.awardStat(Stats.ITEM_USED.get(this));
        IPlayerData.sendUpdatePacketTo(player);
        return InteractionResult.SUCCESS;
    }

    private void spawnParticlesAndPlaySound(Player player) {
        ScalingHealth.LOGGER.debug("StatBoosterItem effect!");
        ParticleUtils.spawn(getParticleType(), 40, player);
        SoundUtils.play(player, getSoundEffect());
    }

    private static void consumeLevels(Player player, int amount) {
        player.giveExperienceLevels(-amount);
        SHPlayers.getPlayerData(player).updateStats(player);
    }
}
