package net.silentchaos512.scalinghealth.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.silentchaos512.scalinghealth.ScalingHealth;

public class ClientBlightMessage implements CustomPacketPayload {
    public static final Type<ClientBlightMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ScalingHealth.MOD_ID, "client_blight"));
    public static final StreamCodec<FriendlyByteBuf, ClientBlightMessage> CODEC = StreamCodec.ofMember(
            ClientBlightMessage::encode,
            ClientBlightMessage::decode
    );

    public int entityId;

    public ClientBlightMessage(int parent) {
        this.entityId = parent;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(entityId);
    }

    public static ClientBlightMessage decode(FriendlyByteBuf buffer) {
        return new ClientBlightMessage(buffer.readInt());
    }

    @Override
    public Type<ClientBlightMessage> type() {
        return TYPE;
    }

}
