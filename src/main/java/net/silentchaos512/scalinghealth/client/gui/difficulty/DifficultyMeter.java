package net.silentchaos512.scalinghealth.client.gui.difficulty;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.client.ClientHandler;
import net.silentchaos512.scalinghealth.client.KeyManager;
import net.silentchaos512.scalinghealth.config.SHConfig;
import net.silentchaos512.scalinghealth.utils.config.EnabledFeatures;
import net.silentchaos512.scalinghealth.utils.mode.AreaDifficultyMode;
import net.silentchaos512.scalinghealth.utils.mode.AreaDifficultyModes;
import net.silentchaos512.lib.util.Anchor;
import org.joml.Matrix3x2fStack;

public final class DifficultyMeter {
    public static final DifficultyMeter INSTANCE = new DifficultyMeter();

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            ScalingHealth.MOD_ID, "textures/gui/hud.png");
    private static final Identifier LAYER_ID = Identifier.fromNamespaceAndPath(
            ScalingHealth.MOD_ID, "difficulty_meter");

    private int lastDifficultyDisplayed = -100;
    private int lastAreaDifficultyDisplayed = -100;
    private int lastUpdateTime = Integer.MIN_VALUE;
    private boolean keyDown;

    private DifficultyMeter() {}

    public static void registerGuiLayer(RegisterGuiLayersEvent event) {
        event.registerAboveAll(LAYER_ID, INSTANCE::extractRenderState);
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent.Pre event) {
        if (Minecraft.getInstance().level == null || !EnabledFeatures.difficultyEnabled()) {
            return;
        }

        this.keyDown = KeyManager.TOGGLE_DIFF.isDown();
    }

    private void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (!EnabledFeatures.difficultyEnabled()) return;

        Minecraft mc = Minecraft.getInstance();
        if (!mc.getDebugOverlay().showDebugScreen()) return;

        DifficultyMeterShow showMode = SHConfig.CLIENT.difficultyMeterShow.get();
        if (showMode == DifficultyMeterShow.NEVER) return;

        Player player = mc.player;
        if (player == null) return;

        final double maxDifficulty = ClientHandler.maxDifficultyValue;
        if (maxDifficulty <= 0) return;

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();

        AreaDifficultyMode areaMode = ClientHandler.areaMode;
        int preClampAreaDifficulty = (int) ClientHandler.areaDifficulty;
        int areaDifficulty = Mth.clamp(preClampAreaDifficulty, 0, (int) maxDifficulty);
        int difficulty = areaMode == AreaDifficultyModes.ServerWide.INSTANCE
                ? areaDifficulty
                : (int) ClientHandler.playerDifficulty;
        int currentTime = ClientTicks.ticksInGame();
        if (lastUpdateTime == Integer.MIN_VALUE) {
            lastUpdateTime = currentTime;
        }
        int timeSinceLastUpdate = currentTime - lastUpdateTime;

        if (difficulty != lastDifficultyDisplayed) {
            lastDifficultyDisplayed = difficulty;
            lastUpdateTime = currentTime;
        }
        if (areaDifficulty < lastAreaDifficultyDisplayed - 10 ||
                (areaDifficulty > lastAreaDifficultyDisplayed + 10 && timeSinceLastUpdate > 1200)) {
            lastAreaDifficultyDisplayed = areaDifficulty;
            lastUpdateTime = currentTime;
        }

        if (showMode != DifficultyMeterShow.ALWAYS && !keyDown &&
                currentTime - lastUpdateTime >= 20 * SHConfig.CLIENT.difficultyMeterShowTime.get()) {
            return;
        }

        Anchor anchor = SHConfig.CLIENT.difficultyMeterAnchor.get();
        int posX = anchor.getX(width, 66, 5) + SHConfig.CLIENT.difficultyMeterOffsetX.get();
        int posY = anchor.getY(height, 14, 5) + SHConfig.CLIENT.difficultyMeterOffsetY.get();

        blitWithColor(graphics, posX, posY, 190, 0, 66, 14, 0xFFFFFFFF);

        int barLength = (int) (60 * areaDifficulty / maxDifficulty);
        blitWithColor(graphics, posX + 3, posY + 5, 193, 19, barLength, 6, 0xFFFFFFFF);

        barLength = (int) (60 * difficulty / maxDifficulty);
        blitWithColor(graphics, posX + 3, posY + 3, 193, 17, barLength, 2, 0xFFFFFFFF);

        final float textScale = SHConfig.CLIENT.difficultyMeterTextScale.get().floatValue();
        if (textScale > 0) {
            Matrix3x2fStack pose = graphics.pose();
            pose.pushMatrix();
            pose.scale(textScale, textScale);
            graphics.text(
                    mc.font,
                    Component.translatable("misc.scalinghealth.difficultyMeterText"),
                    (int) (posX / textScale + 4),
                    (int) (posY / textScale - 9),
                    0xFFFFFFFF,
                    true);

            String str = String.format("%d", areaDifficulty);
            int strWidth = mc.font.width(str);
            graphics.text(
                    mc.font,
                    str,
                    (int) (posX / textScale + 104 - strWidth),
                    (int) (posY / textScale - 9),
                    0xFFAAAAAA,
                    true);
            pose.popMatrix();
        }
    }

    private static void blitWithColor(GuiGraphicsExtractor graphics, int x, int y, int textureX,
                                      int textureY, int width, int height, int color) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, textureX, textureY,
                width, height, 256, 256, color);
    }
}
