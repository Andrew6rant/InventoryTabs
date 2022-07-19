package com.kqp.inventorytabs.init;

import com.kqp.inventorytabs.api.TabProviderRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

public class InventoryTabs implements ModInitializer {
    public static final String ID = "inventorytabs";
    static ConfigHolder<InventoryTabsConfig> inventoryTabsConfig;

    public static boolean isBigInvLoaded;
    public static boolean isPlayerExLoaded;
    public static boolean isLevelzLoaded;

    public static Identifier id(String path) {
        return new Identifier(ID, path);
    }

    @Override
    public void onInitialize() {
        inventoryTabsConfig = AutoConfig.register(InventoryTabsConfig.class, JanksonConfigSerializer::new);
        inventoryTabsConfig.registerSaveListener((configHolder, config) -> {
            TabProviderRegistry.init("save");
            return ActionResult.success(true);
        });
        ClientLoginConnectionEvents.INIT.register((handler, client) -> TabProviderRegistry.init("load"));
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> TabProviderRegistry.init("reload"));

        isBigInvLoaded = FabricLoader.getInstance().isModLoaded("biginv");
        isPlayerExLoaded = FabricLoader.getInstance().isModLoaded("playerex");
        isLevelzLoaded = FabricLoader.getInstance().isModLoaded("levelz");
    }

    public static InventoryTabsConfig getConfig() {
        return AutoConfig.getConfigHolder(InventoryTabsConfig.class).getConfig();
    }
}
