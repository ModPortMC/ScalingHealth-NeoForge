package net.silentchaos512.scalinghealth.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.resources.mechanics.*;

public record SHMechanicsPacket(SHMechanics shMechanics) implements CustomPacketPayload {
    public static final Type<SHMechanicsPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ScalingHealth.MOD_ID, "mechanics"));
    public static final StreamCodec<FriendlyByteBuf, SHMechanicsPacket> CODEC = StreamCodec.ofMember(
            SHMechanicsPacket::encode,
            SHMechanicsPacket::decode
    );

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeJsonWithCodec(PlayerMechanics.CODEC, shMechanics.playerMechanics());
        buffer.writeJsonWithCodec(ItemMechanics.CODEC, shMechanics.itemMechanics());
        buffer.writeJsonWithCodec(MobMechanics.CODEC, shMechanics.mobMechanics());
        buffer.writeJsonWithCodec(DifficultyMechanics.CODEC, shMechanics.difficultyMechanics());
        buffer.writeJsonWithCodec(DamageScalingMechanics.CODEC, shMechanics.damageScalingMechanics());
    }

    public static SHMechanicsPacket decode(FriendlyByteBuf buffer) {
        return new SHMechanicsPacket(SHMechanics.fromNetwork(buffer));
    }

    @Override
    public Type<SHMechanicsPacket> type() {
        return TYPE;
    }

}
