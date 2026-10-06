package net.silentchaos512.scalinghealth.objects;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.capability.DifficultyAffectedCapability;
import net.silentchaos512.scalinghealth.capability.DifficultySourceCapability;
import net.silentchaos512.scalinghealth.capability.PetHealthCapability;
import net.silentchaos512.scalinghealth.capability.PlayerDataCapability;
import net.silentchaos512.scalinghealth.loot.TableGlobalModifier;
import net.silentchaos512.scalinghealth.loot.conditions.EntityGroupCondition;
import net.silentchaos512.scalinghealth.loot.conditions.SHMobProperties;
import net.silentchaos512.scalinghealth.objects.item.DifficultyMutatorItem;
import net.silentchaos512.scalinghealth.objects.item.HealingItem;
import net.silentchaos512.scalinghealth.objects.item.HeartCrystal;
import net.silentchaos512.scalinghealth.objects.item.PowerCrystal;
import net.silentchaos512.scalinghealth.objects.potion.BandagedEffect;
import net.silentchaos512.scalinghealth.world.HeartCrystalPlacement;
import net.silentchaos512.scalinghealth.world.PowerCrystalPlacement;

public class Registration {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, ScalingHealth.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, ScalingHealth.MOD_ID);
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ScalingHealth.MOD_ID);
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ScalingHealth.MOD_ID);
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, ScalingHealth.MOD_ID);
    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLMS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ScalingHealth.MOD_ID);
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ScalingHealth.MOD_ID);
    private static final DeferredRegister<MapCodec<? extends LootItemCondition>> LOOT_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, ScalingHealth.MOD_ID);
    private static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, ScalingHealth.MOD_ID);
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ScalingHealth.MOD_ID);

    static {
        ATTACHMENTS.register("player_data", () -> PlayerDataCapability.INSTANCE);
        ATTACHMENTS.register("difficulty_affected", () -> DifficultyAffectedCapability.INSTANCE);
        ATTACHMENTS.register("difficulty_source", () -> DifficultySourceCapability.INSTANCE);
        ATTACHMENTS.register("pet_health", () -> PetHealthCapability.INSTANCE);
    }

    public static final DeferredHolder<Block, ? extends Block> HEART_CRYSTAL_ORE = registerOre("heart_crystal_ore");
    public static final DeferredHolder<Block, ? extends Block> DEEPLSATE_HEART_CRYSTAL_ORE = registerOre("deepslate_heart_crystal_ore");
    public static final DeferredHolder<Block, ? extends Block> POWER_CRYSTAL_ORE = registerOre("power_crystal_ore");
    public static final DeferredHolder<Block, ? extends Block> DEEPSLATE_POWER_CRYSTAL_ORE = registerOre("deepslate_power_crystal_ore");

    public static final DeferredHolder<Item, ? extends Item> HEART_CRYSTAL_ORE_ITEM = registerOreItem("heart_crystal_ore", HEART_CRYSTAL_ORE);
    public static final DeferredHolder<Item, ? extends Item> POWER_CRYSTAL_ORE_ITEM = registerOreItem("power_crystal_ore", POWER_CRYSTAL_ORE);
    public static final DeferredHolder<Item, ? extends Item> DEEPSLATE_HEART_CRYSTAL_ORE_ITEM = registerOreItem("deepslate_heart_crystal_ore", DEEPLSATE_HEART_CRYSTAL_ORE);
    public static final DeferredHolder<Item, ? extends Item> DEEPSLATE_POWER_CRYSTAL_ORE_ITEM = registerOreItem("deepslate_power_crystal_ore", DEEPSLATE_POWER_CRYSTAL_ORE);


    //Crystals
    public static final DeferredHolder<Item, ? extends Item> HEART_CRYSTAL = ITEMS.register("heart_crystal", () ->
            new HeartCrystal(itemProperties("heart_crystal").rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, ? extends Item> HEART_CRYSTAL_SHARD = ITEMS.register("heart_crystal_shard", () ->
            new Item(itemProperties("heart_crystal_shard")));
    public static final DeferredHolder<Item, ? extends Item> HEART_DUST = ITEMS.register("heart_dust", () ->
            new Item(itemProperties("heart_dust")));
    public static final DeferredHolder<Item, ? extends Item> POWER_CRYSTAL = ITEMS.register("power_crystal", () ->
            new PowerCrystal(itemProperties("power_crystal").rarity(Rarity.RARE)));
    public static final DeferredHolder<Item, ? extends Item> POWER_CRYSTAL_SHARD = ITEMS.register("power_crystal_shard", () ->
            new Item(itemProperties("power_crystal_shard")));

    //healing
    public static final DeferredHolder<Item, ? extends Item> BANDAGES = ITEMS.register("bandages", () ->
            new HealingItem(0.3f, 1, itemProperties("bandages")));
    public static final DeferredHolder<Item, ? extends Item> MEDKIT = ITEMS.register("medkit", () ->
            new HealingItem(0.7f, 4, itemProperties("medkit")));

    //difficulty hearts
    public static final DeferredHolder<Item, ? extends Item> CURSED_HEART = ITEMS.register("cursed_heart", () ->
            new DifficultyMutatorItem(DifficultyMutatorItem.Type.CURSED, itemProperties("cursed_heart").rarity(Rarity.EPIC)));
    public static final DeferredHolder<Item, ? extends Item> ENCHANTED_HEART = ITEMS.register("enchanted_heart", () ->
            new DifficultyMutatorItem(DifficultyMutatorItem.Type.ENCHANTED, itemProperties("enchanted_heart").rarity(Rarity.EPIC)));
    public static final DeferredHolder<Item, ? extends Item> CHANCE_HEART = ITEMS.register("chance_heart", () ->
            new DifficultyMutatorItem(DifficultyMutatorItem.Type.CHANCE, itemProperties("chance_heart").rarity(Rarity.EPIC)));

    public static final DeferredHolder<MobEffect, MobEffect> BANDAGED = EFFECTS.register("bandaged", () ->
            new BandagedEffect(MobEffectCategory.NEUTRAL, 0xf7dcad)
                    .addAttributeModifier(
                            Attributes.MOVEMENT_SPEED,
                            BandagedEffect.MODIFIER_ID,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                            amplifier -> BandagedEffect.SPEED_MODIFIER
                    ));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HEART_CRYSTAL_PARTICLE = PARTICLES.register("heart_crystal", () ->
            new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> POWER_CRYSTAL_PARTICLE = PARTICLES.register("power_crystal", () ->
            new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CURSED_HEART_PARTICLE = PARTICLES.register("cursed_heart", () ->
            new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ENCHANTED_HEART_PARTICLE = PARTICLES.register("enchanted_heart", () ->
            new SimpleParticleType(false));

    public static final DeferredHolder<SoundEvent, SoundEvent> CURSED_HEART_USE = makeSound("cursed_heart_use");
    public static final DeferredHolder<SoundEvent, SoundEvent> ENCHANTED_HEART_USE = makeSound("enchanted_heart_use");
    public static final DeferredHolder<SoundEvent, SoundEvent> HEART_CRYSTAL_USE = makeSound("heart_crystal_use");
    public static final DeferredHolder<SoundEvent, SoundEvent> PLAYER_DIED = makeSound("player_died");

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<TableGlobalModifier>> TABLE_INJECTOR =
            GLMS.register("table_loot_mod", () -> TableGlobalModifier.CODEC);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SH_TAB =
            TABS.register("scaling_health", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("scalinghealth.tab"))
                            .icon(() -> new ItemStack(HEART_CRYSTAL.get()))
                            .build());

    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<SHMobProperties>> MOB_PROPERTIES =
            LOOT_CONDITIONS.register(SHMobProperties.NAME.getPath(), () -> SHMobProperties.CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemCondition>, MapCodec<EntityGroupCondition>> ENTITY_GROUP =
            LOOT_CONDITIONS.register(EntityGroupCondition.NAME.getPath(), () -> EntityGroupCondition.CODEC);


    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<?>> HEART_CRYSTAL_PLACEMENT =
            PLACEMENT_MODIFIERS.register("heart_crystal_placement", () -> placement(HeartCrystalPlacement.CODEC));
    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<?>> POWER_CRYSTAL_PLACEMENT =
            PLACEMENT_MODIFIERS.register("power_crystal_placement", () -> placement(PowerCrystalPlacement.CODEC));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        PARTICLES.register(bus);
        EFFECTS.register(bus);
        SOUNDS.register(bus);
        GLMS.register(bus);
        TABS.register(bus);
        LOOT_CONDITIONS.register(bus);
        PLACEMENT_MODIFIERS.register(bus);
        ATTACHMENTS.register(bus);
        bus.addListener(Registration::buildCreativeTab);
    }

    public static void buildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == SH_TAB.getKey()) {
            event.accept(HEART_CRYSTAL_ORE_ITEM.get());
            event.accept(POWER_CRYSTAL_ORE_ITEM.get());
            event.accept(DEEPSLATE_HEART_CRYSTAL_ORE_ITEM.get());
            event.accept(DEEPSLATE_POWER_CRYSTAL_ORE_ITEM.get());
            event.accept(HEART_CRYSTAL.get());
            event.accept(HEART_CRYSTAL_SHARD.get());
            event.accept(HEART_DUST.get());
            event.accept(POWER_CRYSTAL.get());
            event.accept(POWER_CRYSTAL_SHARD.get());
            event.accept(BANDAGES.get());
            event.accept(MEDKIT.get());
            event.accept(CURSED_HEART.get());
            event.accept(ENCHANTED_HEART.get());
            event.accept(CHANCE_HEART.get());
        }
    }

    private static DeferredHolder<SoundEvent, SoundEvent> makeSound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(ScalingHealth.getId(name)));
    }

    private static DeferredHolder<Block, ? extends Block> registerOre(String name) {
        return BLOCKS.register(name, () ->
                new DropExperienceBlock(
                        UniformInt.of(1, 5),
                        blockProperties(name)
                                .mapColor(MapColor.STONE)
                                .instrument(NoteBlockInstrument.BASEDRUM)
                                .strength(3, 15)
                                .requiresCorrectToolForDrops()
                )
        );
    }

    private static DeferredHolder<Item, ? extends Item> registerOreItem(String name, DeferredHolder<Block, ? extends Block> ore) {
        return ITEMS.register(name, () -> new BlockItem(ore.get(), itemProperties(name)));
    }

    private static Block.Properties blockProperties(String name) {
        return Block.Properties.of().setId(ResourceKey.create(Registries.BLOCK, ScalingHealth.getId(name)));
    }

    private static Item.Properties itemProperties(String name) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ScalingHealth.getId(name)));
    }

    public static <P extends PlacementModifier> PlacementModifierType<P> placement(MapCodec<P> codec) {
        return () -> codec;
    }
}
