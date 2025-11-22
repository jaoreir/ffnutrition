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
            .defineInRange("initialScore", 0, 0, 100.0);

    public static final ModConfigSpec.IntValue MAX_HEALTH = BUILDER
            .comment("Player health at maximum nutrition score.")
            .defineInRange("maxHealth", 20, 2, 1024);

    public static final ModConfigSpec.IntValue MIN_HEALTH = BUILDER
            .comment("Player health at minimum nutrition score")
            .defineInRange("minHealth", 6, 2, 1024);

    // Item nutrition
    public static final ModConfigSpec.DoubleValue NUTRITION_MODIFIER = BUILDER
            .comment("Determines how much a food item’s nutrition level affects the nutrition score.")
            .defineInRange("nutritionModifier", 4.0, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue SATURATION_MODIFIER = BUILDER
            .comment("Determines how much a food item’s saturation level affects the nutrition score.")
            .defineInRange("saturationModifier", 2.0, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue USE_ITEM_NUTRITION_FALLBACK = BUILDER
            .comment("Should items with no nutrition value use the fallback? Non specific Items will give no nutrition if this is false. However, if this is true, items that can be used may cause unintended nutrition gain.")
            .define("useItemNutritionFallback", false);

    public static final ModConfigSpec.ConfigValue<String> ITEM_NUTRITION_FALLBACK = BUILDER
            .comment("The default nutrition amounts to use for unspecified items.")
            .define("defaultNutritionFallback", "10.0,10.0,10.0,10.0,10.0");

//    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_NUTRITION_CATEGORIES = BUILDER
//            .comment("""
//                    List of item nutrition categories.
//                    Format: "A,B,C,D,E,etc."
//                    Example: "protein,carbs,fat,vitamin,mineral"
//                    """)
//            .defineListAllowEmpty("itemNutritionCategories", List.of(
//                    "meat",
//                    "fruit",
//                    "veg",
//                    "carbs",
//                    "sweets"
//            ), obj -> obj instanceof String);
//    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_NUTRITION = BUILDER
//            .comment("""
//                    List of item nutrition entries. These will be multiplied by the item's hunger and satiation values.
//                    "How much of each category is this item made of?"
//                    Format: "modid:item_name=category,category,category,category,category" (ordered by how categories are defined)
//                    Example: "minecraft:carrot=0.0,0.0,1.0,0.0,0.0" (for default categories of meat,fruit,veg,carbs,sweets)
//                    """)
//            .defineListAllowEmpty("itemNutrition", List.of(
//                    "minecraft:apple=0.0,1.0,0.0,0.0,0.0",
//                    "minecraft:beef=1.0,0.0,0.0,0.0,0.0",
//                    "minecraft:beetroot=0.0,0.0,1.0,0.0,0.0",
//                    "minecraft:beetroot_soup=0.0,0.0,0.8,0.2,0.0",
//                    "minecraft:bread=0.0,0.0,0.0,1.0,0.0",
//                    "minecraft:cake=0.0,0.0,0.0,0.7,0.3",
//                    "minecraft:carrot=0.0,0.0,1.0,0.0,0.0",
//                    "minecraft:chicken=1.0,0.0,0.0,0.0,0.0",
//                    "minecraft:chorus_fruit=0.0,1.0,0.0,0.0,0.0",
//                    "minecraft:cod=1.0,0.0,0.0,0.0,0.0",
//                    "minecraft:cooked_beef=1.0,0.0,0.0,0.0,0.0",
//                    "minecraft:cooked_chicken=1.0,0.0,0.0,0.0,0.0",
//                    "minecraft:cooked_cod=1.0,0.0,0.0,0.0,0.0",
//                    "minecraft:cooked_mutton=1.0,0.0,0.0,0.0,0.0",
//                    "minecraft:cooked_porkchop=1.0,0.0,0.0,0.0,0.0",
//                    "minecraft:cooked_rabbit=1.0,0.0,0.0,0.0,0.0",
//                    "minecraft:cooked_salmon=1.0,0.0,0.0,0.0,0.0",
//                    "minecraft:cookie=0.0,0.0,0.0,0.5,0.5",
//                    "minecraft:dried_kelp=0.0,0.0,0.0,1.0,0.0",
//                    "minecraft:enchanted_golden_apple=0.5,1.0,0.5,0.5,0.5",
//                    "minecraft:glow_berries=0.0,1.0,0.0,0.0,0.0",
//                    "minecraft:golden_apple=0.1,1.0,0.1,0.1,0.1",
//                    "minecraft:golden_carrot=0.1,0.1,1.0,0.1,0.1",
//                    "minecraft:honey_bottle=0.0,1.0,0.0,0.0,0.0",
//                    "minecraft:melon_slice=0.0,1.0,0.0,0.0,0.0",
//                    "minecraft:mushroom_stew=0.0,0.0,0.7,0.3,0.0",
//                    "minecraft:pufferfish=0.5,0.0,0.0,0.0,0.5",
//                    "minecraft:pumpkin_pie=0.0,0.0,0.2,0.2,0.6",
//                    "minecraft:poisonous_potato=0.0,0.0,0.0,0.8,0.2",
//                    "minecraft:baked_potato=0.0,0.0,0.0,1.0,0.0",
//                    "minecraft:potato=0.0,0.0,0.0,1.0,0.0",
//                    "minecraft:rotten_flesh=0.9,0.0,0.0,0.0,0.1",
//                    "minecraft:rabbit_stew=0.5,0.0,0.5,0.0,0.0",
//                    "minecraft:mutton=1.0,0.0,0.0,0.0,0.0",
//                    "minecraft:porkchop=1.0,0.0,0.0,0.0,0.0",
//                    "minecraft:rabbit=1.0,0.0,0.0,0.0,0.0",
//                    "minecraft:salmon=1.0,0.0,0.0,0.0,0.0",
//                    "minecraft:spider_eye=0.0,0.0,0.0,0.0,1.0",
//                    "minecraft:steak=1.0,0.0,0.0,0.0,0.0",
//                    "minecraft:suspicious_stew=0.0,0.0,0.8,0.0,0.2",
//                    "minecraft:sweet_berries=0.0,1.0,0.0,0.0,0.0",
//                    "minecraft:tropical_fish=1.0,0.0,0.0,0.0,0.0",
//                    "minecraft:honey_bottle=0.0,0.0,0.0,0.0,1.0",
//                    "minecraft:milk_bucket=0.0,0.0,0.0,0.5,0.0",
//                    "minecraft:ominous_bottle=0.5,0.0,0.0,0.0,0.5",
//                    // --- Farmer's Delight foods & drinks (append these) ---
//                    "farmersdelight:apple_cider=0.0,1.0,0.0,0.0,0.0",
//                    "farmersdelight:apple_pie=0.0,0.6,0.0,0.4,0.0",
//                    "farmersdelight:bacon_and_eggs=0.8,0.0,0.0,0.2,0.0",
//                    "farmersdelight:bacon_sandwich=0.6,0.0,0.0,0.4,0.0",
//                    "farmersdelight:baked_cod_stew=0.8,0.0,0.0,0.2,0.0",
//                    "farmersdelight:beef_patty=1.0,0.0,0.0,0.0,0.0",
//                    "farmersdelight:beef_stew=0.8,0.0,0.1,0.1,0.0",
//                    "farmersdelight:bone_broth=0.8,0.0,0.2,0.0,0.0",
//                    "farmersdelight:cabbage=0.0,0.0,1.0,0.0,0.0",
//                    "farmersdelight:cabbage_leaf=0.0,0.0,1.0,0.0,0.0",
//                    "farmersdelight:cabbage_rolls=0.0,0.0,0.8,0.2,0.0",
//                    "farmersdelight:chicken_soup=0.7,0.0,0.2,0.1,0.0",
//                    "farmersdelight:chocolate_pie=0.0,0.0,0.0,0.5,0.5",
//                    "farmersdelight:cod_roll=0.8,0.0,0.0,0.2,0.0",
//                    "farmersdelight:cooked_bacon=1.0,0.0,0.0,0.0,0.0",
//                    "farmersdelight:cooked_chicken_cuts=1.0,0.0,0.0,0.0,0.0",
//                    "farmersdelight:chicken_cuts=1.0,0.0,0.0,0.0,0.0",
//                    "farmersdelight:cooked_cod_slice=1.0,0.0,0.0,0.0,0.0",
//                    "farmersdelight:cooked_mutton_chops=1.0,0.0,0.0,0.0,0.0",
//                    "farmersdelight:cooked_rice=0.0,0.0,0.0,1.0,0.0",
//                    "farmersdelight:cooked_salmon_slice=1.0,0.0,0.0,0.0,0.0",
//                    "farmersdelight:egg_sandwich=0.2,0.0,0.2,0.6,0.0",
//                    "farmersdelight:fish_stew=0.8,0.0,0.0,0.2,0.0",
//                    "farmersdelight:fried_egg=0.3,0.0,0.1,0.6,0.0",
//                    "farmersdelight:fried_rice=0.0,0.0,0.2,0.8,0.0",
//                    "farmersdelight:fruit_salad=0.0,0.6,0.3,0.1,0.0",
//                    "farmersdelight:glow_berry_custard=0.0,0.2,0.0,0.2,0.6",
//                    "farmersdelight:grilled_salmon=1.0,0.0,0.0,0.0,0.0",
//                    "farmersdelight:ham=1.0,0.0,0.0,0.0,0.0",
//                    "farmersdelight:hamburger=0.8,0.0,0.0,0.2,0.0",
//                    "farmersdelight:honey_glazed_ham=0.7,0.0,0.0,0.1,0.2",
//                    "farmersdelight:minced_beef=1.0,0.0,0.0,0.0,0.0",
//                    "farmersdelight:mixed_salad=0.1,0.4,0.4,0.1,0.0",
//                    "farmersdelight:mushroom_rice=0.0,0.0,0.6,0.4,0.0",
//                    "farmersdelight:mutton_chops=1.0,0.0,0.0,0.0,0.0",
//                    "farmersdelight:mutton_wrap=0.7,0.0,0.1,0.2,0.0",
//                    "farmersdelight:nether_salad=0.0,0.4,0.5,0.1,0.0",
//                    "farmersdelight:nethers_delight=0.0,0.2,0.6,0.2,0.0",
//                    "farmersdelight:noodle_soup=0.0,0.0,0.5,0.5,0.0",
//                    "farmersdelight:onion=0.0,0.0,1.0,0.0,0.0",
//                    "farmersdelight:pasta_with_meatballs=0.4,0.0,0.1,0.5,0.0",
//                    "farmersdelight:potato=0.0,0.0,0.0,1.0,0.0",
//                    "farmersdelight:pumpkin_slices=0.0,0.0,0.3,0.7,0.0",
//                    "farmersdelight:pumpkin_soup=0.0,0.0,0.7,0.3,0.0",
//                    "farmersdelight:raw_bacon=1.0,0.0,0.0,0.0,0.0",
//                    "farmersdelight:raw_chicken_cuts=1.0,0.0,0.0,0.0,0.0",
//                    "farmersdelight:raw_cod_slice=1.0,0.0,0.0,0.0,0.0",
//                    "farmersdelight:raw_salmon_slice=1.0,0.0,0.0,0.0,0.0",
//                    "farmersdelight:rice=0.0,0.0,0.0,1.0,0.0",
//                    "farmersdelight:rice_roll_medley=0.2,0.0,0.4,0.4,0.0",
//                    "farmersdelight:roast_chicken=1.0,0.0,0.0,0.0,0.0",
//                    "farmersdelight:roasted_mutton_chops=1.0,0.0,0.0,0.0,0.0",
//                    "farmersdelight:salmon_roll=0.8,0.0,0.0,0.2,0.0",
//                    "farmersdelight:shepherds_pie=0.6,0.0,0.2,0.2,0.0",
//                    "farmersdelight:smoked_ham=1.0,0.0,0.0,0.0,0.0",
//                    "farmersdelight:squid_ink_pasta=0.0,0.0,0.2,0.8,0.0",
//                    "farmersdelight:stuffed_pumpkin=0.2,0.0,0.4,0.3,0.1",
//                    "farmersdelight:sweet_berry_cheesecake=0.0,0.3,0.0,0.3,0.4",
//                    "farmersdelight:tomato=0.0,0.0,1.0,0.0,0.0",
//                    "farmersdelight:tomato_sauce=0.0,0.2,0.5,0.3,0.0",
//                    "farmersdelight:vegetable_noodles=0.0,0.0,0.4,0.6,0.0",
//                    "farmersdelight:vegetable_soup=0.0,0.0,0.7,0.3,0.0",
//                    "farmersdelight:fish_roll=0.8,0.0,0.0,0.2,0.0"
//
//            ), obj -> obj instanceof String);

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
