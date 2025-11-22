package com.fyxeinc.ffnutrition;

import com.fyxeinc.ffnutrition.config.FFNutritionConfigCommon;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameType;

import static com.fyxeinc.ffnutrition.FFNutritionMod.getNutritionData;

public class FFNutritionDecay
{
    public static void decayNutrition(ServerLevel serverLevel)
    {
        boolean creativeDecay = FFNutritionConfigCommon.CREATIVE_DECAY_NUTRITION.getAsBoolean();
        for (ServerPlayer player : serverLevel.players())
        {
            GameType gameMode = player.gameMode.getGameModeForPlayer();
            if (shouldDecay(gameMode, creativeDecay))
            {
                NutritionData data = getNutritionData(player);
                double defaultDecay = FFNutritionConfigCommon.DECAY_DEFAULT.get();
                //List<Double> categoryDecay = new ArrayList<>(FFNutritionConfig.DECAY_BY_CATEGORY.get()); // TODO
                data.addToAllCategories(-defaultDecay);

                FFNutritionMod.updatePlayerHealth(player);
            }
        }
    }

    public static void decayHunger(ServerLevel serverLevel)
    {
        //FFNutritionMod.LOGGER.info("Hunger was Ticked.");
        boolean creativeDecay = FFNutritionConfigCommon.CREATIVE_DECAY_HUNGER.getAsBoolean();
        int amountHunger = FFNutritionConfigCommon.HUNGER_DECAY_AMOUNT.getAsInt();
        int amountSaturation = FFNutritionConfigCommon.SATURATION_DECAY_AMOUNT.getAsInt();
        for (ServerPlayer player : serverLevel.players())
        {
            GameType gameMode = player.gameMode.getGameModeForPlayer();
            if (shouldDecay(gameMode, creativeDecay))
            {
                FoodData foodData = player.getFoodData();
                foodData.setFoodLevel(Math.max(0, foodData.getFoodLevel() - amountHunger));
                foodData.setSaturation(Math.max(0, foodData.getSaturationLevel() - amountSaturation));
            }
        }
    }

    private static boolean shouldDecay(GameType gameMode, boolean creativeDecay)
    {
        return switch (gameMode)
        {
            case SURVIVAL, ADVENTURE -> true;
            case CREATIVE -> creativeDecay;
            case null -> false;
            case SPECTATOR -> false;
        };
    }


}
