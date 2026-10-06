package net.silentchaos512.scalinghealth.resources.mechanics;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.client.MechanicsHandler;
import net.silentchaos512.scalinghealth.network.ClientLoginMessage;
import net.silentchaos512.scalinghealth.network.SHMechanicsPacket;
import net.silentchaos512.scalinghealth.utils.config.SHDifficulty;

@EventBusSubscriber(modid = ScalingHealth.MOD_ID)
public record SHMechanics(PlayerMechanics playerMechanics, ItemMechanics itemMechanics, MobMechanics mobMechanics, DifficultyMechanics difficultyMechanics, DamageScalingMechanics damageScalingMechanics) {
    public static final SHMechanics DEFAULT = new SHMechanics(
            PlayerMechanics.DEFAULT, ItemMechanics.DEFAULT, MobMechanics.DEFAULT, DifficultyMechanics.DEFAULT, DamageScalingMechanics.DEFAULT
    );

    public static SHMechanics fromNetwork(FriendlyByteBuf buffer) {
        return new SHMechanics(
                buffer.readLenientJsonWithCodec(PlayerMechanics.CODEC),
                buffer.readLenientJsonWithCodec(ItemMechanics.CODEC),
                buffer.readLenientJsonWithCodec(MobMechanics.CODEC),
                buffer.readLenientJsonWithCodec(DifficultyMechanics.CODEC),
                buffer.readLenientJsonWithCodec(DamageScalingMechanics.CODEC)
        );
    }

    public static SHMechanics getMechanics() {
        // Keep physical-side selection: an integrated server is physically a
        // client, which is how the original implementation selected mechanics.
        return FMLEnvironment.getDist() == Dist.CLIENT
                ? ClientMechanicsAccess.get()
                : SHMechanicListener.getInstance();
    }

    private static final class ClientMechanicsAccess {
        private static SHMechanics get() {
            return MechanicsHandler.getClientMechanics();
        }
    }

    @SubscribeEvent
    public static void syncMechanics(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) {
            PacketDistributor.sendToPlayer(event.getPlayer(), new SHMechanicsPacket(SHMechanicListener.getInstance()));
            PacketDistributor.sendToPlayer(event.getPlayer(), new ClientLoginMessage(SHDifficulty.areaMode(), (float) SHDifficulty.maxValue()));
        }
        else {
            for (ServerPlayer player : event.getPlayerList().getPlayers()) {
                PacketDistributor.sendToPlayer(player, new SHMechanicsPacket(SHMechanicListener.getInstance()));
                PacketDistributor.sendToPlayer(player, new ClientLoginMessage(SHDifficulty.areaMode(), (float) SHDifficulty.maxValue()));
            }
        }
    }
}
