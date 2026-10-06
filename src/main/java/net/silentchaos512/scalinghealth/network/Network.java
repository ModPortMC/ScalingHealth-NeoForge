package net.silentchaos512.scalinghealth.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.silentchaos512.scalinghealth.ScalingHealth;

/** Registration for Scaling Health's play-phase, server-to-client payloads. */
@EventBusSubscriber(modid = ScalingHealth.MOD_ID)
public final class Network {
    private static final String PROTOCOL_VERSION = "3";

    private Network() {}

    /**
     * Kept for source compatibility with the mod constructor. Payloads are now
     * registered through {@link RegisterPayloadHandlersEvent} on the mod bus.
     */
    public static void init() {}

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(ClientSyncMessage.TYPE, ClientSyncMessage.CODEC);
        registrar.playToClient(ClientLoginMessage.TYPE, ClientLoginMessage.CODEC);
        registrar.playToClient(ClientBlightMessage.TYPE, ClientBlightMessage.CODEC);
        registrar.playToClient(SHMechanicsPacket.TYPE, SHMechanicsPacket.CODEC);
    }
}
