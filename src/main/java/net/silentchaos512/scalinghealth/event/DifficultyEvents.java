package net.silentchaos512.scalinghealth.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.capability.DifficultyAffectedCapability;
import net.silentchaos512.scalinghealth.capability.DifficultySourceCapability;
import net.silentchaos512.scalinghealth.capability.PetHealthCapability;
import net.silentchaos512.scalinghealth.capability.PlayerDataCapability;
import net.silentchaos512.scalinghealth.config.SHConfig;
import net.silentchaos512.scalinghealth.objects.Registration;
import net.silentchaos512.scalinghealth.utils.config.EnabledFeatures;
import net.silentchaos512.scalinghealth.utils.config.SHDifficulty;
import net.silentchaos512.scalinghealth.utils.config.SHMobs;
import net.silentchaos512.scalinghealth.utils.config.SHPlayers;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Game-bus lifecycle hooks for attachment-backed gameplay data. */
@EventBusSubscriber(modid = ScalingHealth.MOD_ID)
public final class DifficultyEvents {
    public static final Marker MARKER = MarkerManager.getMarker("Difficulty");
    private static final Set<UUID> ENTITIES_DRINKING_MILK = new HashSet<>();

    private DifficultyEvents() {
    }

    /**
     * Materializes only the attachments that the Forge predicates would have attached.
     * Calling {@code getData} here gives the holder a stable value without any provider
     * map or Forge-private capability state.
     */
    @SubscribeEvent
    public static void onEntityConstructing(EntityEvent.EntityConstructing event) {
        Entity entity = event.getEntity();
        if (entity instanceof Mob mob && SHMobs.allowsDifficultyChanges(mob))
            mob.getData(DifficultyAffectedCapability.INSTANCE);
        if (entity instanceof Player player) {
            player.getData(PlayerDataCapability.INSTANCE);
            if (EnabledFeatures.difficultyEnabled())
                player.getData(DifficultySourceCapability.INSTANCE);
        }
        if (EnabledFeatures.petBonusHpEnabled() && entity instanceof TamableAnimal pet)
            pet.getData(PetHealthCapability.INSTANCE);
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel server
                && server.dimension().equals(Level.OVERWORLD)
                && SHConfig.SERVER.enableDifficulty.get()) {
            server.getData(DifficultySourceCapability.INSTANCE);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onMilkUseStart(LivingEntityUseItemEvent.Start event) {
        if (!event.isCanceled() && !event.getEntity().level().isClientSide()
                && event.getItem().is(Items.MILK_BUCKET)) {
            ENTITIES_DRINKING_MILK.add(event.getEntity().getUUID());
        }
    }

    @SubscribeEvent
    public static void onMilkUseStop(LivingEntityUseItemEvent.Stop event) {
        clearMilkUse(event.getEntity(), event.getItem());
    }

    @SubscribeEvent
    public static void onMilkUseFinish(LivingEntityUseItemEvent.Finish event) {
        clearMilkUse(event.getEntity(), event.getItem());
    }

    @SubscribeEvent
    public static void onBandagedEffectRemove(MobEffectEvent.Remove event) {
        Entity entity = event.getEntity();
        if (!entity.level().isClientSide() && ENTITIES_DRINKING_MILK.contains(entity.getUUID())
                && event.getEffect().value() == Registration.BANDAGED.value()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void clearMilkUseOnDeath(LivingDeathEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            ENTITIES_DRINKING_MILK.remove(event.getEntity().getUUID());
        }
    }

    private static void clearMilkUse(LivingEntity entity, ItemStack item) {
        if (!entity.level().isClientSide() && item.is(Items.MILK_BUCKET)) {
            ENTITIES_DRINKING_MILK.remove(entity.getUUID());
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity living) || living.level().isClientSide())
            return;
        if (living.level() instanceof ServerLevel server && server.getServer().isSingleplayer()
                && server.players().isEmpty())
            return;

        if (living instanceof Mob mob && mob.hasData(DifficultyAffectedCapability.INSTANCE))
            mob.getData(DifficultyAffectedCapability.INSTANCE).tick(mob);

        if (living instanceof TamableAnimal pet && pet.isTame()
                && pet.hasData(PetHealthCapability.INSTANCE))
            pet.getData(PetHealthCapability.INSTANCE).tick(pet);

        if (living instanceof Player player && player.level().getGameTime() % 20 == 0
                && player.hasData(DifficultySourceCapability.INSTANCE))
            player.getData(DifficultySourceCapability.INSTANCE)
                    .addDifficulty((float) SHDifficulty.changePerSecond());
    }

    @SubscribeEvent
    public static void onMobDeath(LivingDeathEvent event) {
        LivingEntity killed = event.getEntity();
        if (event.getSource() == null || killed.level().isClientSide())
            return;

        Entity source = event.getSource().getEntity();
        if (source instanceof Player player) {
            SHDifficulty.applyKillMutator(killed, player);
        } else if (source instanceof TamableAnimal pet && pet.isTame()
                && pet.getOwner() instanceof Player owner) {
            SHDifficulty.applyKillMutator(killed, owner);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (level.isClientSide() || level.getGameTime() % 20 != 0
                || !(level instanceof ServerLevel server)
                || !server.hasData(DifficultySourceCapability.INSTANCE))
            return;

        server.getData(DifficultySourceCapability.INSTANCE)
                .addDifficulty((float) SHDifficulty.changePerSecond());
    }

    /**
     * Attachment copying is performed by NeoForge: serializable attachments copy on
     * End-return and the player/source types opt into death copying. This handler is
     * deliberately mutation-only, so a clone is never copied twice and death modifiers
     * run after the attachment copy in the source event order.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath())
            return;

        Player clone = event.getEntity();
        if (clone.hasData(PlayerDataCapability.INSTANCE)) {
            var data = clone.getData(PlayerDataCapability.INSTANCE);
            data.updateStats(clone);
            int newCrystals = SHPlayers.getCrystalsAfterDeath(clone);
            notifyOfChanges(clone, "heart crystal(s)", data.getHeartCrystals(), newCrystals);
            data.setHeartCrystals(clone, newCrystals);
        }

        if (clone.hasData(DifficultySourceCapability.INSTANCE)) {
            var source = clone.getData(DifficultySourceCapability.INSTANCE);
            float newDifficulty = (float) SHDifficulty.getDifficultyAfterDeath(clone);
            notifyOfChanges(clone, "difficulty", source.getDifficulty(), newDifficulty);
            source.setDifficulty(newDifficulty);
        }
    }

    private static void notifyOfChanges(Player player, String valueName, float oldValue, float newValue) {
        float diff = newValue - oldValue;
        String line = String.format("%s %.2f %s", diff > 0 ? "gained" : "lost", diff, valueName);
        if (diff != 0)
            player.sendSystemMessage(Component.translatable(line));
        ScalingHealth.LOGGER.info("Player {}", line);
    }
}
