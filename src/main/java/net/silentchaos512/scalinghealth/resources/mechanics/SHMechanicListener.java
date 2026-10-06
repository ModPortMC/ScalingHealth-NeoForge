package net.silentchaos512.scalinghealth.resources.mechanics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.silentchaos512.scalinghealth.ScalingHealth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@EventBusSubscriber(modid = ScalingHealth.MOD_ID)
public class SHMechanicListener extends SimplePreparableReloadListener<Map<Identifier, JsonElement>> {
    private static SHMechanicListener currentInstance = null;
    private static SHMechanicListener reloadingInstance = null;

    public static final Logger LOGGER = LogManager.getLogger("SHMechanicsListener");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final String FOLDER = "sh_mechanics";
    private static final FileToIdConverter FILES = FileToIdConverter.json(FOLDER);
    private SHMechanics shMechanics;

    public SHMechanicListener() {
        if (currentInstance == null)
            currentInstance = this;
        else
            reloadingInstance = this;
    }

    @Override
    protected Map<Identifier, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<Identifier, JsonElement> result = new HashMap<>();
        for (Map.Entry<Identifier, Resource> entry : FILES.listMatchingResources(resourceManager).entrySet()) {
            Identifier id = FILES.fileToId(entry.getKey());
            try (Reader reader = entry.getValue().openAsReader()) {
                result.put(id, GSON.fromJson(reader, JsonElement.class));
            } catch (IOException | JsonParseException | IllegalArgumentException e) {
                LOGGER.error("Couldn't parse mechanics data file '{}'", id, e);
            }
        }
        return result;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        Function<String, JsonElement> getter = file -> map.entrySet().stream()
                .filter(e -> e.getKey().getNamespace().equals(ScalingHealth.MOD_ID) && e.getKey().getPath().equals(file))
                .map(Map.Entry::getValue)
                .findAny().orElse(JsonNull.INSTANCE);

        var player = PlayerMechanics.CODEC.parse(JsonOps.INSTANCE, getter.apply(PlayerMechanics.FILE))
                .getOrThrow(prefix("PlayerMechanics: "));
        var item = ItemMechanics.CODEC.parse(JsonOps.INSTANCE, getter.apply(ItemMechanics.FILE))
                .getOrThrow(prefix("ItemMechanics: "));
        var mob = MobMechanics.CODEC.parse(JsonOps.INSTANCE, getter.apply(MobMechanics.FILE))
                .getOrThrow(prefix("MobMechanics: "));
        var difficulty = DifficultyMechanics.CODEC.parse(JsonOps.INSTANCE, getter.apply(DifficultyMechanics.FILE))
                .getOrThrow(prefix("DifficultyMechanics: "));
        var ds = DamageScalingMechanics.CODEC.parse(JsonOps.INSTANCE, getter.apply(DamageScalingMechanics.FILE))
                .getOrThrow(prefix("DamageScalingMechanics: "));
        this.shMechanics = new SHMechanics(player, item, mob, difficulty, ds);
        LOGGER.debug("Finished Parsing SH Config!");

        if (this == reloadingInstance) {
            currentInstance = this;
            reloadingInstance = null;
        }
    }

    static SHMechanics getInstance() {
        if (currentInstance == null)
            throw new RuntimeException("Tried to access SHMechanicsListener too early!");
        return currentInstance.shMechanics;
    }

    private static Function<String, RuntimeException> prefix(String pre) {
        return message -> {
            LOGGER.error(pre + message);
            return new IllegalStateException(pre + message);
        };
    }

    @SubscribeEvent
    public static void addListener(AddServerReloadListenersEvent event) {
        event.addListener(ScalingHealth.getId(FOLDER), new SHMechanicListener());
    }
}
