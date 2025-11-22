package com.fyxeinc.ffnutrition;

import com.fyxeinc.ffnutrition.config.FFNutritionConfigCommon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.fyxeinc.ffnutrition.FFNutritionDataAttachment.NUTRITION_DATA;

/**
 * Runs on both client and serv
 */
@Mod(FFNutritionMod.MODID)
public class FFNutritionMod
{
    public static final String MODID = "ffnutrition";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public FFNutritionMod(IEventBus modEventBus, ModContainer container)
    {
        LOGGER.info("FFNutrition: Initialize - Start.");

        //FFNutritionCommands.register();
        //FFNutritionEvents.register();

        //NeoForge.EVENT_BUS.addListener(FFNutritionEvents.class);
        //FFNutritionAttributes.ATTRIBUTES.register(modEventBus);

        LOGGER.info("FFNutrition: register attachment types.");
        FFNutritionDataAttachment.ATTACHMENT_TYPES.register(modEventBus);

        //container.registerConfig(ModConfig.Type.SERVER, FFNutritionConfigServer.SPEC);
        //container.registerConfig(ModConfig.Type.CLIENT, FFNutritionConfigClient.SPEC);
        LOGGER.info("FFNutrition: register config - common.");
        container.registerConfig(ModConfig.Type.COMMON, FFNutritionConfigCommon.SPEC);
        LOGGER.info("FFNutrition: Initialize - End");
    }

    public static NutritionData getNutritionData(LivingEntity entity)
    {
        return entity.getData(NUTRITION_DATA);
    }

    public static void updatePlayerHealth(ServerPlayer player)
    {
        NutritionData data = getNutritionData(player);
        long maxHealth = calculateMaxHealth(data);
        AttributeInstance maxHealthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        maxHealthAttribute.setBaseValue(maxHealth);

        // Clamp player health in case Max Health becomes lower than current player health
        double realMaxHealth = maxHealthAttribute.getValue();
        if (player.getHealth() > realMaxHealth)
        {
            player.setHealth((float) realMaxHealth);
        }
    }

    private static long calculateMaxHealth(NutritionData data)
    {
        double score = data.getNutritionScore();
        double min = FFNutritionConfigCommon.MIN_HEALTH.get();
        double max = Math.max(min, FFNutritionConfigCommon.MAX_HEALTH.get());
        double health = min + (max - min) * score;
        return Math.round(health / 2.0) * 2L;
    }


    public static void server_SavePlayerData(Player player)
    {
        if (!(player instanceof ServerPlayer serverPlayer))
        {
            return;
        }
        CompoundTag tag = new CompoundTag();
        getNutritionData(player).saveToNBT(tag);
        player.getPersistentData().put("ffnutrition_data", tag);
    }

    public static void server_LoadPlayerData(Player player)
    {
        if (!(player instanceof ServerPlayer serverPlayer))
        {
            return;
        }
        if (player.getPersistentData().contains("ffnutrition_data"))
        {
            CompoundTag tag = player.getPersistentData().getCompound("ffnutrition_data");
            getNutritionData(player).loadFromNBT(tag);
        }
    }

}
