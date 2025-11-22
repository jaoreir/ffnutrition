package com.fyxeinc.ffnutrition.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class FFNutritionConfigCommon
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Nutrition decay
    public static final ModConfigSpec.BooleanValue USE_NUTRITION_DECAY = BUILDER
        .comment("Should nutrition score decay overtime?")
        .define("useNutritionDecay", true);

    public static final ModConfigSpec.IntValue NUTRITION_DECAY_TICKS = BUILDER
        .comment("The number of ticks it takes for nutrition score decay to apply.")
        .defineInRange("nutritionDecayTicks", 1200, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue DECAY_DEFAULT = BUILDER
        .comment("Amount of nutrition decay for non specified category.")
        .defineInRange("decayDefault", 0.00083, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue CREATIVE_DECAY_NUTRITION = BUILDER
        .comment("Should nutrition score decay in creative mode?")
        .define("creativeDecayNutrition", false);

    // Nutrition score
    public static final ModConfigSpec.DoubleValue INITIAL_SCORE = BUILDER
        .comment("Initial nutrition score for a player.")
        .defineInRange("initialScore", 2.0 / 7.0 /*Starts at 10 health*/, 0, 100.0);

    // Health affected by nutrition score
    public static final ModConfigSpec.IntValue MAX_HEALTH = BUILDER
        .comment("Player health at maximum nutrition score.")
        .defineInRange("maxHealth", 20, 2, 1024);

    public static final ModConfigSpec.IntValue MIN_HEALTH = BUILDER
        .comment("Player health at minimum nutrition score")
        .defineInRange("minHealth", 6, 2, 1024);

    // Item nutrition
    public static final ModConfigSpec.DoubleValue NUTRITION_MODIFIER = BUILDER
        .comment("Determines how much a food item’s nutrition level affects the nutrition score.")
        .defineInRange("nutritionModifier", 4.0, 0.0, 100.0);

    public static final ModConfigSpec.DoubleValue SATURATION_MODIFIER = BUILDER
        .comment("Determines how much a food item’s saturation level affects the nutrition score.")
        .defineInRange("saturationModifier", 2.0, 0.0, 100.0);

    public static final ModConfigSpec.BooleanValue USE_ITEM_NUTRITION_FALLBACK = BUILDER
        .comment(
            "Should items with no nutrition value use the fallback? Non specific Items will give no nutrition if this is false. However, if this is true, items that can be used may cause unintended nutrition gain.")
        .define("useItemNutritionFallback", false);

    public static final ModConfigSpec.ConfigValue<String> ITEM_NUTRITION_FALLBACK = BUILDER
        .comment("The default nutrition amounts to use for unspecified items.")
        .define("defaultNutritionFallback", "1.0,1.0,1.0,1.0,1.0");

    // Additional hunger decay
    public static final ModConfigSpec.BooleanValue USE_HUNGER_DECAY = BUILDER
        .comment("Should additional hunger and saturation decay be applied over time?")
        .define("useHungerDecay", true);

    public static final ModConfigSpec.IntValue HUNGER_DECAY_TICKS = BUILDER
        .comment("The amount of ticks it takes for hunger decay to apply.")
        .defineInRange("hungerDecayTicks", 1200, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue HUNGER_DECAY_AMOUNT = BUILDER
        .comment("The amount of food level to reduce on hunger decay.")
        .defineInRange("hungerDecayAmount", 1, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SATURATION_DECAY_AMOUNT = BUILDER
        .comment("The amount of saturation to reduce on hunger decay.")
        .defineInRange("saturationDecayAmount", 1, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue CREATIVE_DECAY_HUNGER = BUILDER
        .comment("Should additional hunger decay be applied in creative mode?")
        .define("creativeDecayHunger", false);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
