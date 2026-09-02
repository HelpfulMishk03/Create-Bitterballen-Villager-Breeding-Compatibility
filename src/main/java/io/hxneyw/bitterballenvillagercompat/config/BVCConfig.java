package io.hxneyw.bitterballenvillagercompat.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.LinkedHashMap;
import java.util.Map;

public final class BVCConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final Map<String, FoodConfig> FOODS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        LinkedHashMap<String, FoodConfig> foods = new LinkedHashMap<>();

        builder.push("villager_food");

        ENABLED = builder
                .comment("Enables Create: Bitterballen villager breeding and food-sharing compatibility.")
                .define("enabled", true);

        builder.pop();

        builder.push("foods");

        registerFood(builder, foods, "aged_cheese", 4);
        registerFood(builder, foods, "aged_cheese_wedge", 2);
        registerFood(builder, foods, "bitterballen", 2);
        registerFood(builder, foods, "cheese_souffle", 2);
        registerFood(builder, foods, "chocolate_glazed_stroopwafel", 2);
        registerFood(builder, foods, "churros", 2);
        registerFood(builder, foods, "coated_churros", 2);
        registerFood(builder, foods, "cooked_herring", 2);
        registerFood(builder, foods, "eggball", 2);
        registerFood(builder, foods, "enderball", 2);
        registerFood(builder, foods, "fries", 2);
        registerFood(builder, foods, "frikandel", 2);
        registerFood(builder, foods, "frikandel_sandwich", 3);
        registerFood(builder, foods, "ketchup_topped_frikandel_sandwich", 3);
        registerFood(builder, foods, "ketchup_topped_kroket_sandwich", 3);
        registerFood(builder, foods, "kroket", 2);
        registerFood(builder, foods, "kroket_sandwich", 3);
        registerFood(builder, foods, "kruidnoten", 1);
        registerFood(builder, foods, "mayonnaise_ketchup_topped_frikandel_sandwich", 4);
        registerFood(builder, foods, "mayonnaise_ketchup_topped_kroket_sandwich", 4);
        registerFood(builder, foods, "mayonnaise_topped_frikandel_sandwich", 3);
        registerFood(builder, foods, "mayonnaise_topped_kroket_sandwich", 3);
        registerFood(builder, foods, "oliebollen", 2);
        registerFood(builder, foods, "coated_oliebollen", 2);
        registerFood(builder, foods, "raw_herring", 1);
        registerFood(builder, foods, "roasted_sunflower_seeds", 1);
        registerFood(builder, foods, "speculaas", 1);
        registerFood(builder, foods, "stamppot_bowl", 4);
        registerFood(builder, foods, "stroopwafel", 2);
        registerFood(builder, foods, "sunflower_seeds", 1);
        registerFood(builder, foods, "unripe_cheese", 4);
        registerFood(builder, foods, "unripe_cheese_wedge", 2);
        registerFood(builder, foods, "waxed_aged_cheese", 4);
        registerFood(builder, foods, "waxed_unripe_cheese", 4);
        registerFood(builder, foods, "waxed_young_cheese", 4);
        registerFood(builder, foods, "wrapped_chocolate_glazed_stroopwafel", 2);
        registerFood(builder, foods, "wrapped_churros", 2);
        registerFood(builder, foods, "wrapped_coated_churros", 2);
        registerFood(builder, foods, "wrapped_fries", 2);
        registerFood(builder, foods, "wrapped_ketchup_topped_fries", 3);
        registerFood(builder, foods, "wrapped_mayonnaise_ketchup_topped_fries", 3);
        registerFood(builder, foods, "wrapped_mayonnaise_topped_fries", 3);
        registerFood(builder, foods, "wrapped_stroopwafel", 2);
        registerFood(builder, foods, "young_cheese", 4);
        registerFood(builder, foods, "young_cheese_wedge", 2);

        builder.pop();

        FOODS = Map.copyOf(foods);
        SPEC = builder.build();
    }

    private static void registerFood(ForgeConfigSpec.Builder builder, Map<String, FoodConfig> foods, String itemPath, int defaultFoodPoints) {
        builder.push(itemPath);

        ForgeConfigSpec.IntValue foodPoints = builder
                .comment("Food points this item contributes toward villager breeding and food sharing. Set to 0 to disable breeding-food value for this item.")
                .defineInRange("foodPoints", defaultFoodPoints, 0, 64);

        ForgeConfigSpec.BooleanValue allowPickup = builder
                .comment("Allows villagers to pick up this item from the ground using normal villager item-pickup behavior.")
                .define("allowPickup", true);

        builder.pop();

        foods.put(itemPath, new FoodConfig(foodPoints, allowPickup));
    }

    private BVCConfig() {
    }

    public record FoodConfig(ForgeConfigSpec.IntValue foodPoints, ForgeConfigSpec.BooleanValue allowPickup) {
    }
}
