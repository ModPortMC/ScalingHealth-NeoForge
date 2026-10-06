package net.silentchaos512.scalinghealth.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.utils.config.EnabledFeatures;
import net.silentchaos512.scalinghealth.utils.config.SHDifficulty;
import org.joml.Quaternionf;

/** Client render-state and geometry handling for blight flames. */
public final class BlightRenderEvent {
    private static final float FIRE_SCALE = 1.8F;
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            ScalingHealth.MOD_ID, "textures/entity/blightfire.png");
    private static final RenderType RENDER_TYPE = RenderTypes.entityCutout(TEXTURE);
    private static final ContextKey<BlightRenderData> BLIGHT_RENDER_DATA = new ContextKey<>(
            Identifier.fromNamespaceAndPath(ScalingHealth.MOD_ID, "blight_render_data"));

    public static final BlightRenderEvent INSTANCE = new BlightRenderEvent();

    private BlightRenderEvent() {}

    public static void addBlightRenderData(Mob mob, LivingEntityRenderState renderState) {
        if (EnabledFeatures.shouldRenderBlights() && SHDifficulty.affected(mob).isBlight()) {
            float yOffset = (float) (mob.getY() - mob.getBoundingBox().minY);
            renderState.setRenderData(BLIGHT_RENDER_DATA, new BlightRenderData(yOffset));
        } else {
            // Render-state instances are reused, so remove stale data when a mob stops being blighted.
            renderState.setRenderData(BLIGHT_RENDER_DATA, null);
        }
    }

    @SubscribeEvent
    public void renderBlight(RenderLivingEvent.Pre<?, ?, ?> event) {
        LivingEntityRenderState renderState = event.getRenderState();
        BlightRenderData data = renderState.getRenderData(BLIGHT_RENDER_DATA);
        if (data == null) return;

        float width = renderState.boundingBoxWidth * FIRE_SCALE;
        if (width <= 0.0F) return;

        float heightRatio = renderState.boundingBoxHeight / width;
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.scale(width, width, width);

        Quaternionf camera = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
        poseStack.mulPose(new Quaternionf(0.0F, camera.y, 0.0F, camera.w));
        poseStack.translate(0.0F, 0.0F, heightRatio * 0.02F);

        int frame = Math.floorMod(ClientTicks.ticksInGame(), 32);
        int light = renderState.lightCoords;
        float yOffset = data.yOffset();
        SubmitNodeCollector collector = event.getSubmitNodeCollector();
        collector.submitCustomGeometry(poseStack, RENDER_TYPE,
                (pose, vertices) -> drawFlames(pose, vertices, heightRatio, yOffset, frame, light));
        poseStack.popPose();
    }

    private static void drawFlames(PoseStack.Pose pose, VertexConsumer vertices,
                                   float heightRatio, float initialYOffset, int frame, int light) {
        float xOffset = 0.5F;
        float yOffset = initialYOffset;
        float zOffset = 0.0F;
        int layer = 0;

        while (heightRatio > 0.0F) {
            boolean swapU = layer % 2 == 0;
            float minU = swapU ? 0.5F : 0.0F;
            float minV = frame / 32.0F;
            float maxU = swapU ? 1.0F : 0.5F;
            float maxV = (frame + 1) / 32.0F;

            if (swapU) {
                float swap = maxU;
                maxU = minU;
                minU = swap;
            }

            vertex(vertices, pose, xOffset, -yOffset, zOffset, maxU, maxV, light);
            vertex(vertices, pose, -xOffset, -yOffset, zOffset, minU, maxV, light);
            vertex(vertices, pose, -xOffset, 1.4F - yOffset, zOffset, minU, minV, light);
            vertex(vertices, pose, xOffset, 1.4F - yOffset, zOffset, maxU, minV, light);

            heightRatio -= 0.45F;
            yOffset -= 0.45F;
            xOffset *= 0.9F;
            zOffset += 0.03F;
            ++layer;
        }
    }

    private static void vertex(VertexConsumer vertices, PoseStack.Pose pose,
                               float x, float y, float z, float u, float v, int light) {
        vertices.addVertex(pose, x, y, z)
                .setColor(0xFFFFFFFF)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    private record BlightRenderData(float yOffset) {}
}
