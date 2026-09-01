package io.hxneyw.bitterballenvillagercompat;

import com.mojang.logging.LogUtils;
import io.hxneyw.bitterballenvillagercompat.config.BVCConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mod(BitterballenVillagerCompat.MOD_ID)
public final class BitterballenVillagerCompat {
    public static final String MOD_ID = "bitterballen_villager_compat";
    private static final String BITTERBALLEN_NAMESPACE = "create_bic_bit";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Object STATE_LOCK = new Object();
    private static final Map<Item, BaselineState> BASELINE_STATES = new HashMap<>();
    private static boolean initialized;

    public BitterballenVillagerCompat() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BVCConfig.SPEC);
        modBus.addListener(this::onCommonSetup);
        modBus.addListener(this::onLoadComplete);
        modBus.addListener(this::onConfigReloading);
        MinecraftForge.EVENT_BUS.addListener(this::onServerAboutToStart);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(BitterballenVillagerCompat::initialize);
    }

    private void onLoadComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(BitterballenVillagerCompat::applyConfiguredState);
    }

    private void onConfigReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == BVCConfig.SPEC) {
            applyConfiguredState();
        }
    }

    private void onServerAboutToStart(ServerAboutToStartEvent event) {
        applyConfiguredState();
    }

    private static void initialize() {
        synchronized (STATE_LOCK) {
            captureBaselines();
            initialized = true;
            applyConfiguredStateLocked();
        }
    }

    private static void applyConfiguredState() {
        synchronized (STATE_LOCK) {
            if (!initialized) {
                captureBaselines();
                initialized = true;
            }

            applyConfiguredStateLocked();
        }
    }

    private static void captureBaselines() {
        for (String itemPath : BVCConfig.FOODS.keySet()) {
            Item item = resolveItem(itemPath);
            if (item == null || BASELINE_STATES.containsKey(item)) {
                continue;
            }

            BASELINE_STATES.put(
                    item,
                    new BaselineState(
                            Villager.FOOD_POINTS.get(item),
                            Villager.WANTED_ITEMS.contains(item)
                    )
            );
        }
    }

    private static void applyConfiguredStateLocked() {
        captureBaselines();

        if (!BVCConfig.ENABLED.get()) {
            restoreBaselines();
            LOGGER.info("Create: Bitterballen villager food compatibility is disabled. Restored the pre-BVC villager food state.");
            return;
        }

        Map<Item, Integer> foodPointsMap = new HashMap<>(Villager.FOOD_POINTS);
        Set<Item> wantedItems = new HashSet<>(Villager.WANTED_ITEMS);
        int appliedFoods = 0;

        for (Map.Entry<String, BVCConfig.FoodConfig> entry : BVCConfig.FOODS.entrySet()) {
            Item item = resolveItem(entry.getKey());
            if (item == null) {
                continue;
            }

            BVCConfig.FoodConfig config = entry.getValue();
            int foodPoints = config.foodPoints().get();
            boolean allowPickup = config.allowPickup().get();

            if (foodPoints > 0) {
                foodPointsMap.put(item, foodPoints);
            } else {
                foodPointsMap.remove(item);
            }

            if (allowPickup) {
                wantedItems.add(item);
            } else {
                wantedItems.remove(item);
            }

            appliedFoods++;
        }

        Villager.FOOD_POINTS = Map.copyOf(foodPointsMap);
        Villager.WANTED_ITEMS = Set.copyOf(wantedItems);

        LOGGER.info("Create: Bitterballen villager compatibility applied to {} configured food items.", appliedFoods);
    }

    private static void restoreBaselines() {
        Map<Item, Integer> foodPointsMap = new HashMap<>(Villager.FOOD_POINTS);
        Set<Item> wantedItems = new HashSet<>(Villager.WANTED_ITEMS);

        for (Map.Entry<Item, BaselineState> entry : BASELINE_STATES.entrySet()) {
            Item item = entry.getKey();
            BaselineState baseline = entry.getValue();

            if (baseline.foodPoints() == null) {
                foodPointsMap.remove(item);
            } else {
                foodPointsMap.put(item, baseline.foodPoints());
            }

            if (baseline.wanted()) {
                wantedItems.add(item);
            } else {
                wantedItems.remove(item);
            }
        }

        Villager.FOOD_POINTS = Map.copyOf(foodPointsMap);
        Villager.WANTED_ITEMS = Set.copyOf(wantedItems);
    }

    private static Item resolveItem(String itemPath) {
        ResourceLocation id = new ResourceLocation(BITTERBALLEN_NAMESPACE, itemPath);
        if (!ForgeRegistries.ITEMS.containsKey(id)) {
            LOGGER.warn("Create: Bitterballen item {} was not found. Skipping its villager compatibility entry.", id);
            return null;
        }

        return ForgeRegistries.ITEMS.getValue(id);
    }

    private record BaselineState(Integer foodPoints, boolean wanted) {
    }
}
