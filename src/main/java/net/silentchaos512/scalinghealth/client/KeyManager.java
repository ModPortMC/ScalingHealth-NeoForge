package net.silentchaos512.scalinghealth.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.silentchaos512.scalinghealth.ScalingHealth;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = ScalingHealth.MOD_ID, value = Dist.CLIENT)
public class KeyManager {
    private static final KeyMapping.Category CATEGORY = new KeyMapping.Category(
            Identifier.fromNamespaceAndPath(ScalingHealth.MOD_ID, "main"));
    public static final KeyMapping TOGGLE_DIFF = new KeyMapping(
            "key.scalinghealth.difficultyMeter", GLFW.GLFW_KEY_Z, CATEGORY);

    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);
        event.register(TOGGLE_DIFF);
    }
}
