package net.silentchaos512.scalinghealth;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.silentchaos512.scalinghealth.command.ModCommands;
import net.silentchaos512.scalinghealth.config.SHConfig;
import net.silentchaos512.scalinghealth.network.Network;
import net.silentchaos512.scalinghealth.objects.Registration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod(ScalingHealth.MOD_ID)
public class ScalingHealth {
    public static final String MOD_ID = "scalinghealth";
    public static final String MOD_NAME = "Scaling Health";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final Random RANDOM = new Random();

    public ScalingHealth(IEventBus modbus, ModContainer modContainer) {

        Registration.register(modbus);

        SHConfig.register(modContainer);
        Network.init();

        modbus.addListener(this::reloadConfig);

        NeoForge.EVENT_BUS.addListener(this::registerCommandsEvent);
    }

    private void registerCommandsEvent(RegisterCommandsEvent event) {
        ModCommands.registerAll(event);
    }

    private void reloadConfig(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.CLIENT)
            SHConfig.CLIENT.reload();
    }

    public static Identifier getId(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
