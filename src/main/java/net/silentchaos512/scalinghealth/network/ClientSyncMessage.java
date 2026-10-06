package net.silentchaos512.scalinghealth.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.silentchaos512.scalinghealth.ScalingHealth;

public class ClientSyncMessage implements CustomPacketPayload {
    public static final Type<ClientSyncMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ScalingHealth.MOD_ID, "client_sync"));
    public static final StreamCodec<FriendlyByteBuf, ClientSyncMessage> CODEC = StreamCodec.ofMember(
            ClientSyncMessage::toBytes,
            ClientSyncMessage::fromBytes
    );

    public float playerDifficulty;
    public float worldDifficulty;
    public float areaDifficulty;
    public int regenTimer;
    public int locationMultiPercent;
    public int experienceLevel;

    public ClientSyncMessage() {}

    public ClientSyncMessage(float playerDifficulty, float worldDifficulty, float areaDifficulty, int regenTimer, double locationMultiplier, int xp) {
        this.playerDifficulty = playerDifficulty;
        this.worldDifficulty = worldDifficulty;
        this.areaDifficulty = areaDifficulty;
        this.regenTimer = regenTimer;
        this.locationMultiPercent = (int) (100 * locationMultiplier);
        this.experienceLevel = xp;
    }

    public static ClientSyncMessage fromBytes(FriendlyByteBuf buf) {
        ClientSyncMessage msg = new ClientSyncMessage();
        msg.playerDifficulty = buf.readFloat();
        msg.worldDifficulty = buf.readFloat();
        msg.areaDifficulty = buf.readFloat();
        msg.regenTimer = buf.readVarInt();
        msg.locationMultiPercent = buf.readVarInt();
        msg.experienceLevel = buf.readVarInt();
        return msg;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(playerDifficulty);
        buf.writeFloat(worldDifficulty);
        buf.writeFloat(areaDifficulty);
        buf.writeVarInt(regenTimer);
        buf.writeVarInt(locationMultiPercent);
        buf.writeVarInt(experienceLevel);
    }

    @Override
    public Type<ClientSyncMessage> type() {
        return TYPE;
    }
}
