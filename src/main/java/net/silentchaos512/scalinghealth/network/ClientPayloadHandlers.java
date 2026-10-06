package net.silentchaos512.scalinghealth.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.client.ClientHandler;
import net.silentchaos512.scalinghealth.client.MechanicsHandler;
import net.silentchaos512.scalinghealth.utils.config.SHDifficulty;

/** Client-only handlers for the server-to-client play payloads. */
@EventBusSubscriber(modid = ScalingHealth.MOD_ID, value = Dist.CLIENT)
public final class ClientPayloadHandlers {
    private ClientPayloadHandlers() {}

    @SubscribeEvent
    public static void register(RegisterClientPayloadHandlersEvent event) {
        event.register(ClientSyncMessage.TYPE, HandlerThread.MAIN,
                (message, context) -> ClientHandler.handleSyncMessage(message));
        event.register(ClientLoginMessage.TYPE, HandlerThread.MAIN,
                (message, context) -> ClientHandler.handleLoginMessage(message));
        event.register(ClientBlightMessage.TYPE, HandlerThread.MAIN,
                (message, context) -> applyBlight(message));
        event.register(SHMechanicsPacket.TYPE, HandlerThread.MAIN,
                (message, context) -> MechanicsHandler.setClientMechanics(message.shMechanics()));
    }

    public static void applyBlight(ClientBlightMessage message) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        Entity entity = level.getEntity(message.entityId);
        if (entity instanceof Mob) {
            SHDifficulty.affected(entity).setIsBlight(true);
        }
    }
}
