/*
 * Scaling Health
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.scalinghealth.client.gui.health;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.client.gui.TextureSlice;
import net.silentchaos512.scalinghealth.config.SHConfig;
import net.silentchaos512.lib.util.Color;
import net.silentchaos512.lib.util.MathUtils;

import java.util.List;
import org.joml.Matrix3x2fStack;

/**
 * Handles display of regular and absorption hearts.
 * The custom heart layer replaces the vanilla player-health layer when enabled.
 */
public final class HeartDisplayHandler {
    public static final HeartDisplayHandler INSTANCE = new HeartDisplayHandler();

    private static final int COLOR_CHANGE_PERIOD = 150;
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            ScalingHealth.MOD_ID, "textures/gui/hud.png");

    private static final TextureSlice TANK_SHINE = new TextureSlice(TEXTURE, 44, 0, 5, 5);
    private static final TextureSlice TANK_OUTLINE = new TextureSlice(TEXTURE,44, 5, 5, 5);
    private static final TextureSlice TANK_FULL = new TextureSlice(TEXTURE, 44, 10, 5, 5);
    private static final TextureSlice TANK_EMPTY = new TextureSlice(TEXTURE, 44, 15, 5, 5);

    private final HeartsInfo info = new HeartsInfo();

    private HeartDisplayHandler() {}

    public static void registerGuiLayer(RegisterGuiLayersEvent event) {
        event.wrapLayer(VanillaGuiLayers.PLAYER_HEALTH, vanillaLayer -> (graphics, deltaTracker) -> {
            if (INSTANCE.info.heartStyle.get() == HeartIconStyle.VANILLA) {
                vanillaLayer.render(graphics, deltaTracker);
                return;
            }

            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null || mc.options.hideGui || mc.gameMode == null || !mc.gameMode.canHurtPlayer()) {
                return;
            }

            INSTANCE.renderHearts(graphics, mc, player);
            if (mc.gameMode.getPlayerMode().isSurvival()) {
                INSTANCE.renderHealthText(graphics, mc, player);
            }
        });
    }

    private void renderHealthText(GuiGraphicsExtractor graphics, Minecraft mc, Player player) {
        if (SHConfig.CLIENT.healthTextStyle.get() != HealthTextStyle.DISABLED) {
            renderHealthText(mc, graphics, info.health, info.maxHealth,
                    -91 + SHConfig.CLIENT.healthTextOffsetX.get(),
                    -38 + SHConfig.CLIENT.healthTextOffsetY.get(),
                    SHConfig.CLIENT.healthTextStyle.get(),
                    SHConfig.CLIENT.healthTextColorStyle.get());
        }
        if (SHConfig.CLIENT.absorptionTextStyle.get() != HealthTextStyle.DISABLED && player.getAbsorptionAmount() > 0) {
            renderHealthText(mc, graphics, player.getAbsorptionAmount(), 0,
                    -91 + SHConfig.CLIENT.absorptionTextOffsetX.get(),
                    -49 + SHConfig.CLIENT.absorptionTextOffsetY.get(),
                    SHConfig.CLIENT.absorptionTextStyle.get(),
                    HealthTextColor.SOLID);
        }
    }

    private void renderHearts(GuiGraphicsExtractor graphics, Minecraft mc, Player player) {
        renderHeartsInternal(graphics, mc, player);
    }

    private void renderHeartsInternal(GuiGraphicsExtractor graphics, Minecraft mc, Player player) {
        info.update();

        float absorb = Mth.ceil(player.getAbsorptionAmount());

        final int left = info.scaledWindowWidth / 2 - 91;
        Gui gui = mc.gui;
        int top = info.scaledWindowHeight - gui.leftHeight;
        gui.leftHeight += info.rowsUsedInHud * info.rowHeight;
        if (info.rowHeight != 10)
            gui.leftHeight += 10 - info.rowHeight;

        // Draw vanilla hearts
        drawVanillaHearts(graphics, left, top);

        int potionOffset = info.hardcoreMode ? 27 : 0;

        // Draw extra hearts (only top 2 rows)
        int rowCount = info.getCustomHeartRowCount(info.healthInt);
        int maxHealthRows = info.getCustomHeartRowCount((int) player.getMaxHealth());

        for (int row = Math.max(0, rowCount - 2); row < rowCount; ++row) {
            int actualRow = info.getActualRow(row);
            int renderHearts = info.getHeartsInRows(actualRow);
            int rowColor = getColorForRow(row, false);

            // Draw the hearts
            int j;
            for (j = 0; j < renderHearts; ++j) {
                int y = info.offsetHeartPosY(j, top);
                blitWithColor(graphics,left + 8 * j, y, 0, potionOffset, 9, 9, rowColor);
            }
            boolean anythingDrawn = j > 0;

            // Half heart on the end?
            if (info.healthInt % 2 == 1 && renderHearts < 10) {
                int y = info.offsetHeartPosY(j, top);
                blitWithColor(graphics,left + 8 * renderHearts, y, 9, potionOffset, 9, 9, rowColor);
                anythingDrawn = true;
            }

            // Outline for last heart, to make seeing max health a little easier.
            if (SHConfig.CLIENT.lastHeartOutline.get() && anythingDrawn && row == maxHealthRows - 1) {
                // Get position of last partial/full heart
                j = (int) (Math.ceil(player.getMaxHealth() % 20f / 2f)) - 1;
                if (j < 0) j += 10;
                int y = info.offsetHeartPosY(j, top);
                int color = SHConfig.CLIENT.lastHeartOutlineColor.get();
                blitWithColor(graphics,left + 8 * j, y, 17, 9, 9, 9, color);
            }
        }

        for (int i = 0; i < 10 && i < Math.ceil(info.healthInt / 2f); ++i) {
            int y = info.offsetHeartPosY(i, top);
            // Effect hearts (poison, wither)
            if (showEffectHearts(player)) {
                int color = effectHeartColor(player);
                blitWithColor(graphics,left + 8 * i, y, 0, 54, 9, 9, color);
            }
            // Shiny glint on top of the hearts, a single white pixel in the upper left <3
            if (!info.hardcoreMode) {
                blitWithColor(graphics,left + 8 * i, y, 17, 0, 9, 9, 0xCCFFFFFF);
            }
        }

        // Tanks

        if (info.getMaxHeartTanks() > 0 && SHConfig.CLIENT.heartTanks.get()) {
            int tankRows = info.getHeartTankRowCount();
            int maxTankRows = info.getMaxHeartTankRowCount();

            for (int row = 0; row < maxTankRows; ++row) {
                int filledTanksInRow = info.getFilledHeartTanksInRow(row);
                int allTanksInRow = info.getAllHeartTanksInRow(row);
                int rowColor = getColorForRow(row, false);
                top -= 4;
                mc.gui.leftHeight += 4;

                // Draw tanks
                int x;
                for (x = 0; x < allTanksInRow; ++x) {
                    if (x < filledTanksInRow) {
                        TANK_FULL.blit(graphics, left + 4 * x, top, rowColor);
                    } else {
                        TANK_EMPTY.blit(graphics, left + 4 * x, top, 0xFFFFFF);
                    }
                }
                boolean anythingDrawn = x > 0;

                if (SHConfig.CLIENT.lastHeartOutline.get() && anythingDrawn && row == maxTankRows - 1) {
                    x = (int) (Math.ceil(allTanksInRow)) - 1;
                    if (x < 0) x += 20;
                    TANK_OUTLINE.blit(graphics, left + 4 * x, top, SHConfig.CLIENT.lastHeartOutlineColor.get());
                }
            }
        }

        // ==========================
        // Absorption hearts override
        // ==========================

        AbsorptionIconStyle absorptionIconStyle = SHConfig.CLIENT.absorptionIconStyle.get();
        if (absorptionIconStyle != AbsorptionIconStyle.VANILLA) {
            int absorbCeil = (int) Math.ceil(absorb);
            rowCount = (int) Math.ceil(absorb / 20);

            // Dark underlay for first row
            int texX = 17;
            int texY = absorptionIconStyle == AbsorptionIconStyle.SHIELD ? 45 : 54;
            for (int i = 0; i < 10 && i < absorb / 2; ++i) {
                int y = info.offsetAbsorptionPosY(i, top);
                blitWithColor(graphics,left + 8 * i, y, texX, texY, 9, 9, 0xFFFFFF);
            }

            // Draw the top two absorption rows, just the basic "hearts"
            texX = absorptionIconStyle == AbsorptionIconStyle.SHIELD ? 26 : 0;
            texY = absorptionIconStyle == AbsorptionIconStyle.SHIELD ? 0 : potionOffset;
            for (int i = Math.max(0, rowCount - 2); i < rowCount; ++i) {
                int renderHearts = Math.min((absorbCeil - 20 * i) / 2, 10);
                int rowColor = getColorForRow(i, true);
                boolean anythingDrawn;

                // Draw the hearts
                int x;
                for (x = 0; x < renderHearts; ++x) {
                    int y = info.offsetAbsorptionPosY(x, top);
                    blitWithColor(graphics,left + 8 * x, y, texX, texY, 9, 9, rowColor);
                }
                anythingDrawn = x > 0;

                // Half heart on the end?
                if (absorbCeil % 2 == 1 && renderHearts < 10) {
                    int y = info.offsetAbsorptionPosY(x, top);
                    blitWithColor(graphics,left + 8 * renderHearts, y, texX + 9, texY, 9, 9, rowColor);
                    anythingDrawn = true;
                }
            }

            // Add extra bits like outlines on top
            for (int i = 0; i < 10 && i < absorb / 2; ++i) {
                int y = info.offsetAbsorptionPosY(i, top);
                if (absorptionIconStyle == AbsorptionIconStyle.SHIELD) {
                    // Golden hearts in center (shield style only)
                    blitWithColor(graphics,left + 8 * i, y, 17, 36, 9, 9, 0xFFFFFF);
                } else if (absorptionIconStyle == AbsorptionIconStyle.GOLD_OUTLINE) {
                    // Golden outline
                    blitWithColor(graphics,left + 8 * i, y, 17, 27, 9, 9, 0xFFFFFF);
                }
                // Shiny glint on top, same as hearts.
                if (!info.hardcoreMode || absorptionIconStyle == AbsorptionIconStyle.SHIELD) {
                    blitWithColor(graphics,left + 8 * i, y, 17, 0, 9, 9, 0xCCFFFFFF);
                }
            }
        }
    }

    private void drawVanillaHearts(GuiGraphicsExtractor graphics, int left, int top) {
        int textureX = info.recentlyHurtHighlight ? 25 : 16;
        int textureY = 9 * (info.hardcoreMode ? 5 : 0);
        int margin = 16;

        float healthMax = Math.min(info.maxHealth, 20);
        float absorbRemaining = info.absorption;
        float healthTotal = info.healthInt + info.absorptionInt;

        int iStart = Mth.ceil((healthMax + (info.absorptionStyle.get() == AbsorptionIconStyle.VANILLA ? info.absorptionInt : 0)) / 2f) - 1;
        for (int i = iStart; i >= 0; --i) {
            int row = Mth.ceil((i + 1) / 10f) - 1;
            int x = left + i % 10 * 8;
            int y = info.offsetHeartPosY(i, top - row * info.rowHeight);

            blitWithColor(graphics, x, y, textureX, textureY, 9, 9, 0xFFFFFFFF);

            if (info.recentlyHurtHighlight) {
                if (i * 2 + 1 < info.previousHealthInt)
                    blitWithColor(graphics, x, y, margin + 54, textureY, 9, 9, 0xFFFFFFFF);
                else if (i * 2 + 1 == info.previousHealthInt)
                    blitWithColor(graphics, x, y, margin + 63, textureY, 9, 9, 0xFFFFFFFF);
            }

            if (absorbRemaining > 0f && info.absorptionStyle.get() == AbsorptionIconStyle.VANILLA) {
                if (MathUtils.doublesEqual(absorbRemaining, info.absorption) && MathUtils.doublesEqual(info.absorption % 2f, 1f)) {
                    blitWithColor(graphics, x, y, margin + 153, textureY, 9, 9, 0xFFFFFFFF);
                    absorbRemaining -= 1f;
                } else {
                    if (i * 2 + 1 < healthTotal)
                        blitWithColor(graphics, x, y, margin + 144, textureY, 9, 9, 0xFFFFFFFF);
                    absorbRemaining -= 2f;
                }
            } else {
                if (i * 2 + 1 < info.healthInt)
                    blitWithColor(graphics, x, y, margin + 36, textureY, 9, 9, 0xFFFFFFFF);
                else if (i * 2 + 1 == info.healthInt)
                    blitWithColor(graphics, x, y, margin + 45, textureY, 9, 9, 0xFFFFFFFF);
            }
        }
    }

    private void renderHealthText(Minecraft mc, GuiGraphicsExtractor graphics, float current, float max, int offsetX, int offsetY, HealthTextStyle style, HealthTextColor styleColor) {
        final float scale = (float) style.getScale();
        final int left = (int) ((info.scaledWindowWidth / 2 + offsetX) / scale);
        // The target health layer places the text at the same baseline used by the former Forge HUD.
        final int top = (int) ((info.scaledWindowHeight + offsetY + (1 / scale)) / scale);

        // Draw health string
        String healthString = style.textFor(current, max);
        Font fontRenderer = mc.font;
        int stringWidth = fontRenderer.width(healthString);
        int color;
        float divisor = max == 0 ? current : max;
        float healthFraction = divisor == 0
                ? 0
                : MathUtils.clamp(current / divisor, 0f, 1f);
        switch (styleColor) {
            case TRANSITION:
//                color = Color.HSBtoRGB(0.34f * current / divisor, 0.7f, 1.0f);
                color = Color.blend(
                        SHConfig.CLIENT.healthTextEmptyColor.get(),
                        SHConfig.CLIENT.healthTextFullColor.get(),
                        healthFraction);
                break;
            case PSYCHEDELIC:
                color = java.awt.Color.HSBtoRGB(
                        Math.floorMod(ClientTicks.ticksInGame(), COLOR_CHANGE_PERIOD) / (float) COLOR_CHANGE_PERIOD,
                        0.55f * healthFraction, 1.0f);
                break;
            case SOLID:
            default:
                color = SHConfig.CLIENT.healthTextFullColor.get();
                break;
        }
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.scale(scale, scale);
        graphics.text(fontRenderer, healthString, left - stringWidth - 2, top, toArgb(color));
        pose.popMatrix();
    }

    private void blitWithColor(GuiGraphicsExtractor graphics, int x, int y, int textureX, int textureY, int width, int height, int color) {
        int argb = toArgb(color);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, textureX, textureY,
                width, height, 256, 256, argb);
    }

    private static int toArgb(int color) {
        return (color >>> 24) == 0 ? 0xFF000000 | color : color;
    }

    private static int getColorForRow(int row, boolean absorption) {
        List<Integer> colors = absorption ? SHConfig.CLIENT.absorptionHeartColors.get() : SHConfig.CLIENT.heartColors.get();
        int index = SHConfig.CLIENT.heartColorLooping.get()
                ? row % colors.size()
                : MathUtils.clamp(row, 0, colors.size() - 1);
        return colors.get(index);
    }

    private static boolean showEffectHearts(Player player) {
        return player.hasEffect(MobEffects.POISON) || player.hasEffect(MobEffects.WITHER);
    }

    private static int effectHeartColor(Player player) {
        if (player.hasEffect(MobEffects.WITHER))
            return 0x663E47;
        if (player.hasEffect(MobEffects.POISON))
            return 0x4E9331;
        return 0xFFFFFF;
    }

}
