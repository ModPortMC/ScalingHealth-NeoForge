package net.silentchaos512.scalinghealth.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.objects.Registration;

import java.util.concurrent.CompletableFuture;

public class Recipes extends RecipeProvider.Runner {
    public Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            protected void buildRecipes() {
                fullBlockRecipe(output, Registration.HEART_CRYSTAL.get(), Registration.HEART_CRYSTAL_SHARD.get());
                fullBlockRecipe(output, Registration.POWER_CRYSTAL.get(), Registration.POWER_CRYSTAL_SHARD.get());

                shapeless(RecipeCategory.MISC, Registration.HEART_DUST.get(), 24)
                        .requires(Registration.HEART_CRYSTAL.get())
                        .unlockedBy("cobblestone", has(Items.COBBLESTONE))
                        .group(ScalingHealth.MOD_ID)
                        .save(output);

                shaped(RecipeCategory.COMBAT, Registration.BANDAGES.get(), 2)
                        .pattern("xxx")
                        .pattern("yyy")
                        .define('x', Items.PAPER)
                        .define('y', Registration.HEART_DUST.get())
                        .unlockedBy("cobblestone", has(Items.COBBLESTONE))
                        .showNotification(false)
                        .group(ScalingHealth.MOD_ID)
                        .save(output);

                shaped(RecipeCategory.COMBAT, Registration.MEDKIT.get(), 2)
                        .pattern("aba")
                        .pattern("cdc")
                        .pattern("eee")
                        .define('a', Registration.HEART_DUST.get())
                        .define('b', Tags.Items.INGOTS_IRON)
                        .define('c', Registration.BANDAGES.get())
                        .define('d', Items.GLISTERING_MELON_SLICE)
                        .define('e', Items.TERRACOTTA)
                        .unlockedBy("cobblestone", has(Items.COBBLESTONE))
                        .showNotification(false)
                        .group(ScalingHealth.MOD_ID)
                        .save(output);
            }

            private void fullBlockRecipe(RecipeOutput recipeOutput, ItemLike result, ItemLike ingredient) {
                shaped(RecipeCategory.MISC, result)
                        .pattern("xxx")
                        .pattern("xxx")
                        .pattern("xxx")
                        .define('x', ingredient)
                        .unlockedBy("cobblestone", has(Items.COBBLESTONE))
                        .group(ScalingHealth.MOD_ID)
                        .showNotification(false)
                        .save(recipeOutput);
            }
        };
    }

    @Override
    public String getName() {
        return "Scaling Health Recipes";
    }
}
