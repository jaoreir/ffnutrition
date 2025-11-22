package com.fyxeinc.ffnutrition;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Map;

@EventBusSubscriber(modid = FFNutritionMod.MODID)
public class FFNutritionCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {

        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
            Commands.literal("ffnutrition")
                .then(Commands.literal("getNutritionDataVague")
                    .then(Commands.argument("target", EntityArgument.player())
                        .executes(context ->
                        {
                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                            NutritionData data = FFNutritionMod.getNutritionData(target);

                            StringBuilder line = new StringBuilder();
                            if (data.getNutritionScore() >= 0.8) {
                                line.append("You're quite satiated.");
                            } else if (data.getNutritionScore() >= 0.5) {
                                line.append("You're decently satiated.");
                            } else {
                                line.append("You should diversify your diet.");
                            }

                            boolean hasNeeded = false;
                            int index = 0;
                            for (Map.Entry<String, Double> entry : data.getNutritionValueCollection().entrySet()) {
                                String key = entry.getKey();
                                Double value = entry.getValue();
                                if (value <= 40.0) {
                                    if (!hasNeeded) {
                                        hasNeeded = true;
                                        line.append(" You're lacking ");
                                    }
                                    if (index == data.getNutritionValueCollection().size() - 1) {
                                        line.append(key).append(".");
                                    } else {
                                        line.append(key).append(", ");
                                    }

                                }
                                index++;
                            }
                            final String message = line.toString();
                            context.getSource().sendSuccess(() -> Component.literal(message), false);
                            return 1;
                        })
                    )
                )
        );

        dispatcher.register(
            Commands.literal("ffnutrition")
                .then(Commands.literal("getNutritionData")
                    .then(Commands.argument("target", EntityArgument.player())
                        .requires(source -> source.hasPermission(2))
                        .executes(context ->
                        {
                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                            NutritionData data = FFNutritionMod.getNutritionData(target);
                            context.getSource()
                                .sendSuccess(() -> Component.literal(
                                        String.format("Nutrition Score: %.6f. (Health=%d)",
                                            data.getNutritionScore(), data.evalMaxHealth())),
                                    false);

                            int index = 0;
                            for (Map.Entry<String, Double> entry : data.getNutritionValueCollection().entrySet()) {
                                index += 1;
                                String key = entry.getKey();
                                Double value = entry.getValue();
                                int newIndex = index;
                                //System.out.println(key + " = " + value);
                                context.getSource()
                                    .sendSuccess(() -> Component.literal(
                                        String.format("%d) %s: %.6f", newIndex, key, value)), false);
                            }
                            return 1;
                        })
                    )
                )
        );

        dispatcher.register(
            Commands.literal("ffnutrition")
                .then(Commands.literal("maxAllNutrients")
                    .then(Commands.argument("target", EntityArgument.player())
                        .requires(source -> source.hasPermission(2))
                        .executes(context ->
                        {
                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                            NutritionData data = FFNutritionMod.getNutritionData(target);
                            data.setAllCategories(100.0);
                            context.getSource()
                                .sendSuccess(
                                    () -> Component.literal("Fully satiated player: " + target.getName().getString()),
                                    false);

                            return 1;
                        })
                    )
                )
        );

        dispatcher.register(
            Commands.literal("ffnutrition")
                .then(Commands.literal("minAllNutrients")
                    .then(Commands.argument("target", EntityArgument.player())
                        .requires(source -> source.hasPermission(2))
                        .executes(context ->
                        {
                            ServerPlayer target = EntityArgument.getPlayer(context, "target");
                            NutritionData data = FFNutritionMod.getNutritionData(target);
                            data.setAllCategories(0);
                            FFNutritionMod.updatePlayerHealth(target);
                            context.getSource()
                                .sendSuccess(
                                    () -> Component.literal("Fully unsatiated player: " + target.getName().getString()),
                                    false);

                            return 1;
                        })
                    )
                )
        );

        dispatcher.register(
            Commands.literal("ffnutrition")
                .then(Commands.literal("setAllNutrients")
                    .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                            .requires(source -> source.hasPermission(2))
                            .executes(context ->
                            {
                                double value = DoubleArgumentType.getDouble(context, "value");

                                ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                NutritionData data = FFNutritionMod.getNutritionData(target);
                                data.setAllCategories(value);
                                FFNutritionMod.updatePlayerHealth(target);
                                context.getSource()
                                    .sendSuccess(() -> Component.literal(
                                        "Set stationation of player: " + target.getName()
                                            .getString() + " to : " + String.format("%.6f", value)), false);

                                return 1;
                            })
                        )
                    )
                )
        );

        dispatcher.register(
            Commands.literal("ffnutrition")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("addToCategory")
                    .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("category", StringArgumentType.string())
                            .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                .requires(source -> source.hasPermission(2))
                                .executes(context ->
                                {
                                    ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                    String category = StringArgumentType.getString(context, "category");
                                    double value = DoubleArgumentType.getDouble(context, "value");

                                    NutritionData data = FFNutritionMod.getNutritionData(target);

                                    data.addToCategory(category, value, true);
                                    FFNutritionMod.updatePlayerHealth(target);

                                    // Feedback to the command sender
                                    context.getSource().sendSuccess(
                                        () -> Component.literal(
                                            "Added " + String.format("%.6f", value) + " to category '" + category +
                                                "' for player " + target.getName()
                                                .getString() + ". New total: " + String.format("%.6f",
                                                data.getCategoryValue(category))
                                        ),
                                        false
                                    );
                                    return 1;
                                })
                            )
                        )
                    )
                )
        );

        dispatcher.register(
            Commands.literal("ffnutrition")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("setCategory")
                    .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("category", StringArgumentType.string())
                            .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                .requires(source -> source.hasPermission(2))
                                .executes(context ->
                                {
                                    ServerPlayer target = EntityArgument.getPlayer(context, "target");
                                    String category = StringArgumentType.getString(context, "category");
                                    double value = DoubleArgumentType.getDouble(context, "value");

                                    NutritionData data = FFNutritionMod.getNutritionData(target);

                                    data.addToCategory(category, value, true);
                                    FFNutritionMod.updatePlayerHealth(target);

                                    // Feedback to the command sender
                                    context.getSource().sendSuccess(
                                        () -> Component.literal(
                                            "Set category " + category + " to '" + String.format("%.6f", value) +
                                                "' for player " + target.getName()
                                                .getString() + ". New total: " + String.format("%.6f",
                                                data.getCategoryValue(category))
                                        ),
                                        false
                                    );
                                    return 1;
                                })
                            )
                        )
                    )
                )
        );
    }
}