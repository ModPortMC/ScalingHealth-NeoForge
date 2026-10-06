package net.silentchaos512.scalinghealth.client.gui;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.silentchaos512.scalinghealth.client.ClientHandler;
import net.silentchaos512.scalinghealth.config.SHConfig;
import net.silentchaos512.scalinghealth.utils.config.SHDifficulty;
import net.silentchaos512.lib.util.Anchor;
import org.joml.Matrix3x2fStack;

import javax.annotation.Nonnull;
import java.util.List;

public final class DebugOverlay {
    private static final String FLOAT_FORMAT = "%.5f";
    private static final float TEXT_SCALE = 0.75f;
    private static final int MARGIN = 5;
    private static final Identifier LAYER_ID = Identifier.fromNamespaceAndPath("scalinghealth", "debug_overlay");

    public static final DebugOverlay INSTANCE = new DebugOverlay();

    private DebugOverlay() { }

    public static void registerGuiLayer(RegisterGuiLayersEvent event) {
        event.registerAboveAll(LAYER_ID, INSTANCE::render);
    }

    private void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (isHidden()) return;

        List<String> lines = getDebugText();
        if (lines.isEmpty()) return;

        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int textWidth = lines.stream().mapToInt(font::width).max().orElse(0);
        int textHeight = lines.size() * font.lineHeight;
        float scale = getTextScale();
        int scaledWidth = (int) Math.ceil(textWidth * scale);
        int scaledHeight = (int) Math.ceil(textHeight * scale);
        Anchor anchor = getAnchorPoint();
        int x = anchor.getX(graphics.guiWidth(), scaledWidth, MARGIN);
        int y = anchor.getY(graphics.guiHeight(), scaledHeight, MARGIN);
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.scale(scale, scale);

        for (int i = 0; i < lines.size(); i++) {
            graphics.text(font, lines.get(i), (int) (x / scale), (int) (y / scale) + i * font.lineHeight, -1, true);
        }

        pose.popMatrix();
    }

    @Nonnull
    public List<String> getDebugText() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return ImmutableList.of();

        return ImmutableList.of(
                "Difficulty (/" + ClientHandler.maxDifficultyValue + ")",
                "- Mode=" + ClientHandler.areaMode.getName(),
                "- Player=" + String.format(FLOAT_FORMAT, ClientHandler.playerDifficulty),
                "- Server=" + String.format(FLOAT_FORMAT, ClientHandler.worldDifficulty),
                "- Area=" + String.format(FLOAT_FORMAT + " (x%.1f, ☽x%.1f)",
                        ClientHandler.areaDifficulty,
                        ClientHandler.locationMultiPercent / 100f,
                        SHDifficulty.lunarMultiplier(player.level(), player.blockPosition())),
                "Health",
                "- Health=" + String.format("%.5f / %.1f", player.getHealth(), player.getMaxHealth()),
                "- Regen=" + String.format("%ds", ClientHandler.regenTimer / 20)
        );
    }

    public float getTextScale() {
        return TEXT_SCALE;
    }

    public boolean isHidden() {
        return !SHConfig.SERVER.debugShowOverlay.get();
    }

    public Anchor getAnchorPoint() {
        return SHConfig.CLIENT.debugOverlayAnchor.get();
    }
}
