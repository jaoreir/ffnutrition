package com.fyxeinc.ffnutrition;

import com.fyxeinc.ffnutrition.config.FFNutritionConfigCommon;
import com.fyxeinc.ffnutrition.data.FFNutritionItemDataLoader;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fyxeinc.ffnutrition.FFNutritionDataAttachment.NUTRITION_DATA;
import static com.fyxeinc.ffnutrition.FFNutritionMod.getNutritionData;

/**
 * Handles all NeoForge events for FF Nutrition.
 */
@EventBusSubscriber(modid = FFNutritionMod.MODID)
public class FFNutritionEvents
{

    private static final Map<Item, double[]> itemNutritionCache = new HashMap<>();
    private static double[] fallbackItemNutrition;
    private static boolean hasCachedItems = false;

    private static int NutritionTickCurrent = 0;
    private static int HungerTickCurrent = 0;

    public static void cacheItemNutritionInfo(boolean force)
    {
        if (hasCachedItems && !force)
        {
            return;
        }
        List<String> nutritionCategories = FFNutritionItemDataLoader.getNutritionCategories();
        final double nutritionModifier = FFNutritionConfigCommon.NUTRITION_MODIFIER.get();
        final double saturationModifier = FFNutritionConfigCommon.SATURATION_MODIFIER.get();
        for (Map.Entry<Item, double[]> entry : FFNutritionItemDataLoader.getItemNutritionMap().entrySet())
        {
//            String[] parts = entry.split("=");
//            if (parts.length != 2)
//            {
//                // TODO - throw error, item not setup properly
//                continue;
//            }
//            String itemId = parts[0];
//            Item foundItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(parts[0]));
//            FoodProperties foodProperties = null;
//            if (foundItem != null)
//            {
//                foodProperties = foundItem.getDefaultInstance().get(DataComponents.FOOD);
//            }
//
//            String[] values = parts[1].split(",");
////            if (values.length != nutritionCategories.size())
////            {
////                // TODO - throw error, not enough entries for each item
////                continue;
////            }
//            double[] nutrition = new double[nutritionCategories.size()];
//            try
//            {
//                for (int i = 0; i < nutritionCategories.size(); i++)
//                {
//                    if (values.length > i )
//                    {
//                        double parsedDouble = Double.parseDouble(values[i]);
//                        if (foodProperties != null)
//                        {
//                            double nutritionPart = parsedDouble * foodProperties.nutrition() * nutritionModifier;
//                            double saturationPart = parsedDouble * foodProperties.saturation() * saturationModifier;
//                            nutrition[i] = nutritionPart + saturationPart;
//                        }
//                        else
//                        {
//                            nutrition[i] = parsedDouble;
//                        }
//                    }
//                    else
//                    {
//                        // TODO - error about incorrect food item?
//                        nutrition[i] = 0.0;
//                    }
//                }
//            }
//            catch (NumberFormatException e)
//            {
//                // TODO - error about incorrect number syntax
//                continue;
//            }

            // TODO - category fixups
            FoodProperties foodProperties = null;
            if (entry.getKey() != null)
            {
                foodProperties = entry.getKey().getDefaultInstance().get(DataComponents.FOOD);
            }

            double[] nutritionValues = new double[entry.getValue().length];

            for (int i = 0; i < entry.getValue().length; i++)
            {
                if (foodProperties != null)
                {
                    double nutritionPart = entry.getValue()[i] * foodProperties.nutrition() * nutritionModifier;
                    double saturationPart = entry.getValue()[i] * foodProperties.saturation() * saturationModifier;
                    nutritionValues[i] = nutritionPart + saturationPart;
                }
                else
                {
                    nutritionValues[i] = entry.getValue()[i];
                }
            }
            itemNutritionCache.put(entry.getKey(), nutritionValues);
        }


        String[] values = FFNutritionConfigCommon.ITEM_NUTRITION_FALLBACK.get().split(",");
        double[] fallbackNutrition = new double[nutritionCategories.size()];
        try
        {
            for (int i = 0; i < nutritionCategories.size(); i++)
            {
                if (values.length > i)
                {
                    fallbackNutrition[i] = Double.parseDouble(values[i]);
                }
                else
                {
                    fallbackNutrition[i] = 0.0;
                }
            }
        }
        catch (NumberFormatException e)
        {
            // TODO - error about incorrect number syntax
        }
        fallbackItemNutrition = fallbackNutrition;

        hasCachedItems = true;
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(FFNutritionItemDataLoader.INSTANCE);
    }

    @SubscribeEvent
    private static void server_OnServerSetup(FMLDedicatedServerSetupEvent event)
    {
        cacheItemNutritionInfo(false);
    }

    @SubscribeEvent
    private static void server_OnLevelTickPost(LevelTickEvent.Post event)
    {
        if (!(event.getLevel() instanceof ServerLevel serverLevel))
        {
            return;
        }
        if (FFNutritionConfigCommon.USE_NUTRITION_DECAY.get())
        {
            NutritionTickCurrent += 1;
            if (NutritionTickCurrent > FFNutritionConfigCommon.NUTRITION_DECAY_TICKS.getAsInt())
            {
                NutritionTickCurrent = 0;
                FFNutritionDecay.decayNutrition(serverLevel);
            }
        }
        if (FFNutritionConfigCommon.USE_HUNGER_DECAY.get())
        {
            HungerTickCurrent += 1;
            if (HungerTickCurrent > FFNutritionConfigCommon.HUNGER_DECAY_TICKS.getAsInt())
            {
                HungerTickCurrent = 0;
                FFNutritionDecay.decayHunger(serverLevel);
            }
        }
    }

    @SubscribeEvent
    private static void server_OnDatapackSyncEvent(OnDatapackSyncEvent event)
    {
        Player player = event.getPlayer();
        if (!(player instanceof ServerPlayer))
        {
            return;
        }
        FFNutritionMod.server_LoadPlayerData(player);
    }

    @SubscribeEvent
    private static void server_OnPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        if(!(event.getEntity() instanceof ServerPlayer player)){
            return;
        }
        FFNutritionMod.updatePlayerHealth(player);
    }

    @SubscribeEvent
    private static void server_OnPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer))
        {
            return;
        }
        FFNutritionMod.server_SavePlayerData(player);
    }

    @SubscribeEvent
    private static void server_OnLivingEntityUseItemEventFinish(LivingEntityUseItemEvent.Finish event)
    {
        Player player = null;
        if (event.getEntity() instanceof Player)
        {
            player = (Player) event.getEntity();
        }

        if (player == null || !(player instanceof ServerPlayer))
        {
            return;
        }

        cacheItemNutritionInfo(false);

        ItemStack itemStack = event.getItem();
        Item item = itemStack.getItem();

        //itemStack.getComponents().get(DataComponents.FOOD). todo
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        String idString = id.toString();

        NutritionData data = getNutritionData(player);

        if (itemNutritionCache.containsKey(item))
        {
            double[] orderedNutritionInfo = itemNutritionCache.get(item);

            data.addValuesToOrderedCategories(orderedNutritionInfo);
        }
        else if (FFNutritionConfigCommon.USE_ITEM_NUTRITION_FALLBACK.get())
        {
            data.addValuesToOrderedCategories(fallbackItemNutrition);
        }
    }

//    @SubscribeEvent
//    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event)
//    {
//        Level level = event.getLevel();
//        BlockPos pos = event.getPos();
//
//        BlockState blockState = level.getBlockState(pos);
//        Block block = blockState.getBlock();
//
//        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
//        String idString = blockId.toString();
//    }

    @SubscribeEvent
    public static void server_OnEntityJoin(EntityJoinLevelEvent event)
    {
        if (!(event.getEntity() instanceof ServerPlayer player))
        {
            return;
        }

        // Ensure the attachment exists (creates default if absent)
        player.getData(NUTRITION_DATA);
    }

}
