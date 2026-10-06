package net.silentchaos512.scalinghealth.network;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.utils.mode.AreaDifficultyMode;

public class ClientLoginMessage implements CustomPacketPayload {
    public static final Type<ClientLoginMessage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ScalingHealth.MOD_ID, "client_login"));
    public static final StreamCodec<FriendlyByteBuf, ClientLoginMessage> CODEC = StreamCodec.ofMember(
            ClientLoginMessage::toBytes,
            ClientLoginMessage::fromBytes
    );

    public AreaDifficultyMode areaMode;
    public float maxDifficultyValue;

    public ClientLoginMessage() {}

    public ClientLoginMessage(AreaDifficultyMode areaMode, float maxDifficultyValue) {
        this.areaMode = areaMode;
        this.maxDifficultyValue = maxDifficultyValue;
    }

    public static ClientLoginMessage fromBytes(FriendlyByteBuf buf) {
        ClientLoginMessage msg = new ClientLoginMessage();
        try {
            msg.areaMode = AreaDifficultyMode.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(buf.readUtf()))
                    .getOrThrow(error -> new DecoderException("Failed to decode JSON: " + error));
        } catch (Exception e) {
            throw new RuntimeException("Failed to receive difficulty mode packet!", e);
        }
        msg.maxDifficultyValue = buf.readFloat();
        return msg;
    }

    public void toBytes(FriendlyByteBuf buf) {
        try {
            buf.writeJsonWithCodec(AreaDifficultyMode.CODEC, areaMode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send difficulty mode packet!", e);
        }
        buf.writeFloat(maxDifficultyValue);
    }

    @Override
    public Type<ClientLoginMessage> type() {
        return TYPE;
    }
}
