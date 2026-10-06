package net.silentchaos512.scalinghealth.client;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.client.gui.DebugOverlay;
import net.silentchaos512.scalinghealth.client.gui.difficulty.DifficultyMeter;
import net.silentchaos512.scalinghealth.client.gui.health.HeartDisplayHandler;
import net.silentchaos512.scalinghealth.objects.Registration;
import net.silentchaos512.lib.util.Color;
import com.google.common.reflect.TypeToken;

@EventBusSubscriber(modid = ScalingHealth.MOD_ID, value = Dist.CLIENT)
public class ClientInit {
    //A bit weird, but works.
    static {
        NeoForge.EVENT_BUS.register(DifficultyMeter.INSTANCE);
        NeoForge.EVENT_BUS.register(BlightRenderEvent.INSTANCE);
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        HeartDisplayHandler.registerGuiLayer(event);
        DifficultyMeter.registerGuiLayer(event);
        DebugOverlay.registerGuiLayer(event);
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        ImmutableMap.of(
                Registration.HEART_CRYSTAL_PARTICLE.get(), Color.FIREBRICK,
                Registration.POWER_CRYSTAL_PARTICLE.get(), Color.ROYALBLUE,
                Registration.CURSED_HEART_PARTICLE.get(), Color.REBECCAPURPLE,
                Registration.ENCHANTED_HEART_PARTICLE.get(), Color.ANTIQUEWHITE
        ).forEach((type, color) -> event.registerSpriteSet(type, sprites -> factory(color, sprites)));
    }

    @SubscribeEvent
    public static void registerRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        event.registerEntityModifier(
                new TypeToken<LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>>() {},
                (entity, state) -> {
                    if (entity instanceof Mob mob) {
                        BlightRenderEvent.addBlightRenderData(mob, state);
                    }
                });
    }

    private static ParticleProvider<SimpleParticleType> factory(Color color, SpriteSet sprites) {
        return new ColoredParticle.Factory(color, sprites);
    }
}
