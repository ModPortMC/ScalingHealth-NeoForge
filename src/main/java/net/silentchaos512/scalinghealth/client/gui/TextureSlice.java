package net.silentchaos512.scalinghealth.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class TextureSlice {
    private final int texU;
    private final int texV;
    private final int width;
    private final int height;
    private final Identifier texture;

    public TextureSlice(Identifier texture, int u, int v, int width, int height) {
        this.texU = u;
        this.texV = v;
        this.width = width;
        this.height = height;
        this.texture = texture;
    }

    public void blit(GuiGraphicsExtractor graphics, int x, int y, int color) {
        int argb = (color >>> 24) == 0 ? 0xFF000000 | color : color;
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, texU, texV,
                width, height, 256, 256, argb);
    }
}
