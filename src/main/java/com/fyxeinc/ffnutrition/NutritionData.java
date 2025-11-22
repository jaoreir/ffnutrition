package com.fyxeinc.ffnutrition;

import com.fyxeinc.ffnutrition.config.FFNutritionConfigCommon;
import com.fyxeinc.ffnutrition.data.FFNutritionItemDataLoader;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;

/**
 * Stores per-player nutrition data.
 */
public class NutritionData implements INBTSerializable<CompoundTag>
{
    private static final double NUTRITION_CATEGORY_VALUE_MAX = 100.0;

    private double nutritionScore;
    private final Map<String, Double> nutritionValueCollection = new HashMap<>();

    public NutritionData()
    {
        double currentScore = 0.0;

        int maxEntries = FFNutritionItemDataLoader.getNutritionCategories().size();
        for (String entry : FFNutritionItemDataLoader.getNutritionCategories())
        {
            double categoryScore = FFNutritionConfigCommon.INITIAL_SCORE.get();
            nutritionValueCollection.put(entry, categoryScore);
            currentScore += categoryScore;
        }
        nutritionScore = currentScore / (NUTRITION_CATEGORY_VALUE_MAX * maxEntries);
    }

    public Map<String, Double> getNutritionValueCollection()
    {
        return nutritionValueCollection;
    }

    public double getNutritionScore()
    {
        return nutritionScore;
    }

    public long evalMaxHealth()
    {
        double score = getNutritionScore();
        double min = FFNutritionConfigCommon.MIN_HEALTH.get();
        double max = Math.max(min, FFNutritionConfigCommon.MAX_HEALTH.get());
        double health = min + (max - min) * score;
        return Math.round(health / 2.0) * 2L;
    }

    public double getCategoryValue(String category)
    {
        return nutritionValueCollection.getOrDefault(category, 0.0);
    }

//    public void addValuesToOrderedCategories(ItemStack stack, double[] nutritionInfo)
//    {
//        double[] scaledNutritionInfo = new double[nutritionInfo.length];
//
//        FoodProperties foodProperties = stack.getComponents().get(DataComponents.FOOD);
//        if (foodProperties == null)
//        {
//            // TODO - log, fallback
//            addValuesToOrderedCategories(nutritionInfo);
//            return;
//        }
//        float saturation = foodProperties.saturation();
//        float nutrition = foodProperties.nutrition();
//
//        int i = 0;
//        for(double entry : nutritionInfo)
//        {
//            scaledNutritionInfo[i] = entry;
//            i++;
//        }
//
//        addValuesToOrderedCategories(scaledNutritionInfo);
//    }

    public void addValuesToOrderedCategories(double[] nutritionInfo)
    {
        List<String> nutritionCategories = new ArrayList<>(FFNutritionItemDataLoader.getNutritionCategories());
        for (int i = 0; i < nutritionCategories.size(); i++)
        {
            if (i >= nutritionInfo.length)
            {
                // TODO - warning
                return;
            }
            String entry = nutritionCategories.get(i);
            addToCategory(entry, nutritionInfo[i], false);
        }
        nutritionScore = computeNutritionScore();
    }

    public void addToAllCategories(double amount)
    {
        if (amount == 0.0)
        {
            return;
        }

        for (String entry : nutritionValueCollection.keySet())
        {
            addToCategory(entry, amount, false);
        }
        nutritionScore = computeNutritionScore();
    }

    public void addToCategory(String category, double amount, boolean computeScore)
    {
        if (amount == 0.0)
        {
            return;
        }

        if (nutritionValueCollection.containsKey(category))
        {
            double newValue = nutritionValueCollection.getOrDefault(category, 0.0) + amount;

            newValue = Math.clamp(newValue, 0.0, NUTRITION_CATEGORY_VALUE_MAX);

            nutritionValueCollection.put
                (
                    category,
                    newValue
                );
        } else
        {
            // key not found, add category?
        }

        if (computeScore)
        {
            nutritionScore = computeNutritionScore();
        }
    }

    public void setCategory(String category, double amount)
    {
        if (nutritionValueCollection.containsKey(category))
        {

            amount = Math.clamp(amount, 0.0, NUTRITION_CATEGORY_VALUE_MAX);

            nutritionValueCollection.put
                (
                    category,
                    amount
                );
        } else
        {
            // key not found, add category?
        }
        nutritionScore = computeNutritionScore();
    }

    public void setAllCategories(double amount)
    {
        for (String entry : nutritionValueCollection.keySet())
        {
            nutritionValueCollection.put(entry, amount);
        }
        nutritionScore = computeNutritionScore();
    }

    public double computeNutritionScore()
    {
        double currentScore = 0.0;
        int maxEntries = 0;
        for (String entry : nutritionValueCollection.keySet())
        {
            double categoryScore = nutritionValueCollection.getOrDefault(entry, 0.0);
            currentScore += categoryScore;
            maxEntries += 1;
        }
        return currentScore / (NUTRITION_CATEGORY_VALUE_MAX * maxEntries);
    }

    public void saveToNBT(CompoundTag tag)
    {
        for (String entry : nutritionValueCollection.keySet())
        {
            double categoryScore = nutritionValueCollection.getOrDefault(entry, 0.0);
            tag.putDouble(entry, categoryScore);
        }
    }

    public void loadFromNBT(CompoundTag tag)
    {
        for (String entry : nutritionValueCollection.keySet())
        {
            double categoryScore = tag.getDouble(entry);

            nutritionValueCollection.put
                (
                    entry,
                    categoryScore
                );
        }
    }

    public static final StreamCodec<FriendlyByteBuf, NutritionData> STREAM_CODEC =
        StreamCodec.of(
            (buf, data) ->
            { // encode
                Map<String, Double> map = data.getNutritionValueCollection();
                buf.writeInt(map.size()); // write map size
                for (Map.Entry<String, Double> entry : map.entrySet())
                {
                    buf.writeUtf(entry.getKey()); // write key
                    buf.writeDouble(entry.getValue()); // write value
                }
            },
            buf ->
            { // decode
                int size = buf.readInt();
                Map<String, Double> map = new HashMap<>();
                for (int i = 0; i < size; i++)
                {
                    String key = buf.readUtf();
                    double value = buf.readDouble();
                    map.put(key, value);
                }
                NutritionData data = new NutritionData();
                data.setNutritionValueCollection(map);
                return data;
            }
        );

    public void setNutritionValueCollection(Map<String, Double> map)
    {
        if (map == null)
        {
            this.nutritionValueCollection.clear();
            return;
        }

        this.nutritionValueCollection.clear();
        this.nutritionValueCollection.putAll(map);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider)
    {
        CompoundTag tag = new CompoundTag();

        // Save each category and its score
        for (Map.Entry<String, Double> entry : nutritionValueCollection.entrySet())
        {
            tag.putDouble(entry.getKey(), entry.getValue());
        }

        // Also save the overall nutrition score
        tag.putDouble("NutritionScore", nutritionScore);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag)
    {
        nutritionValueCollection.clear();

        // Rebuild categories from config to ensure valid keys
        for (String category : FFNutritionItemDataLoader.getNutritionCategories())
        {
            double value = tag.contains(category)
                ? tag.getDouble(category)
                : FFNutritionConfigCommon.INITIAL_SCORE.get();

            value = Math.clamp(value, 0.0, NUTRITION_CATEGORY_VALUE_MAX);
            nutritionValueCollection.put(category, value);
        }

        nutritionScore = tag.contains("NutritionScore")
            ? tag.getDouble("NutritionScore")
            : computeNutritionScore();
    }
}
