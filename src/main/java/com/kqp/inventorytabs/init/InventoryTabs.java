package com.kqp.inventorytabs.init;

import com.kqp.inventorytabs.api.TabProviderRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.Identifier;

public class InventoryTabs implements ModInitializer {
    public static final String ID = "inventorytabs";

    public static Identifier id(String path) {
        return new Identifier(ID, path);
    }

    @Override
    public void onInitialize() {
        AutoConfig.register(InventoryTabsConfig.class, JanksonConfigSerializer::new);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> TabProviderRegistry.init());
    }

    public static InventoryTabsConfig getConfig() {
        return AutoConfig.getConfigHolder(InventoryTabsConfig.class).getConfig();
    }
}
