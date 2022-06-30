package com.kqp.inventorytabs.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kqp.inventorytabs.init.InventoryTabs;
import com.kqp.inventorytabs.tabs.provider.*;

import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Registry for tab providers.
 */
public class TabProviderRegistry {
    private static final Map<Identifier, TabProvider> TAB_PROVIDERS = new HashMap<>();

    public static final PlayerInventoryTabProvider PLAYER_INVENTORY_TAB_PROVIDER = (PlayerInventoryTabProvider) register(
            InventoryTabs.id("player_inventory_tab_provider"), new PlayerInventoryTabProvider());
    public static final SimpleBlockTabProvider SIMPLE_BLOCK_TAB_PROVIDER = (SimpleBlockTabProvider) register(
            InventoryTabs.id("simple_block_tab_provider"), new SimpleBlockTabProvider());
    public static final ChestTabProvider CHEST_TAB_PROVIDER = (ChestTabProvider) register(
            InventoryTabs.id("chest_tab_provider"), new ChestTabProvider());
    public static final EnderChestTabProvider ENDER_CHEST_TAB_PROVIDER = (EnderChestTabProvider) register(
            InventoryTabs.id("ender_chest_tab_provider"), new EnderChestTabProvider());
    public static final ShulkerBoxTabProvider SHULKER_BOX_TAB_PROVIDER = (ShulkerBoxTabProvider) register(
            InventoryTabs.id("shulker_box_tab_provider"), new ShulkerBoxTabProvider());
    //public static final CraftingTableTabProvider CRAFTING_TABLE_TAB_PROVIDER = (CraftingTableTabProvider) register(
    //        InventoryTabs.id("crafting_table_tab_provider"), new CraftingTableTabProvider());
    public static final LecternTabProvider LECTERN_TAB_PROVIDER = (LecternTabProvider) register(
            InventoryTabs.id("lectern_tab_provider"), new LecternTabProvider());
    public static final InventoryTabProvider INVENTORY_TAB_PROVIDER = (InventoryTabProvider) register(
            InventoryTabs.id("inventory_tab_provider"), new InventoryTabProvider());


    public static void init() {
        Registry.BLOCK.forEach(block -> {
            if (block instanceof BlockEntityProvider) {
                if (block instanceof AbstractChestBlock) {
                    registerChest(block);
                } else if (!(block instanceof AbstractBannerBlock) && !(block instanceof AbstractSignBlock) && !(block instanceof AbstractSkullBlock) && !(block instanceof BeehiveBlock) && !(block instanceof BellBlock) && !(block instanceof CampfireBlock) && !(block instanceof ConduitBlock) && !(block instanceof DaylightDetectorBlock) && !(block instanceof EndGatewayBlock) && !(block instanceof EndPortalBlock) && !(block instanceof JukeboxBlock) && !(block instanceof PistonExtensionBlock) && !(block instanceof SculkCatalystBlock) && !(block instanceof SculkSensorBlock) && !(block instanceof SculkShriekerBlock) && !(block instanceof SpawnerBlock)) {
                    registerSimpleBlock(block);
                }
            } else if ((block instanceof CraftingTableBlock && !(block instanceof FletchingTableBlock)) || block instanceof AnvilBlock || block instanceof CartographyTableBlock || block instanceof GrindstoneBlock || block instanceof LoomBlock || block instanceof StonecutterBlock) {
                registerSimpleBlock(block);
            }
        });
        modCompatRemove();
        //modCompatAdd();
    }

    private static void modCompatAdd() {
        registerInventoryTab(new Identifier("onastick", "crafting_table_on_a_stick"));
        registerInventoryTab(new Identifier("onastick", "smithing_table_on_a_stick"));
        registerInventoryTab(new Identifier("onastick", "cartography_table_on_a_stick"));
        registerInventoryTab(new Identifier("onastick", "anvil_on_a_stick"));
        registerInventoryTab(new Identifier("onastick", "loom_on_a_stick"));
        registerInventoryTab(new Identifier("onastick", "grindstone_on_a_stick"));
        registerInventoryTab(new Identifier("onastick", "stonecutter_on_a_stick"));

        registerInventoryTab(new Identifier("craftingpad", "craftingpad"));
    }

    private static void modCompatRemove() {
        removeSimpleBlock(new Identifier("tiered", "reforging_station"));

        removeSimpleBlock(new Identifier("techreborn", "basic_machine_casing"));
        removeSimpleBlock(new Identifier("techreborn", "advanced_machine_casing"));
        removeSimpleBlock(new Identifier("techreborn", "industrial_machine_casing"));
        removeSimpleBlock(new Identifier("techreborn", "creative_solar_panel"));
        removeSimpleBlock(new Identifier("techreborn", "copper_cable"));
        removeSimpleBlock(new Identifier("techreborn", "tin_cable"));
        removeSimpleBlock(new Identifier("techreborn", "gold_cable"));
        removeSimpleBlock(new Identifier("techreborn", "hv_cable"));
        removeSimpleBlock(new Identifier("techreborn", "glassfiber_cable"));
        removeSimpleBlock(new Identifier("techreborn", "insulated_copper_cable"));
        removeSimpleBlock(new Identifier("techreborn", "insulated_gold_cable"));
        removeSimpleBlock(new Identifier("techreborn", "insulated_hv_cable"));
        removeSimpleBlock(new Identifier("techreborn", "superconductor_cable"));
        removeSimpleBlock(new Identifier("techreborn", "resin_basin"));
        removeSimpleBlock(new Identifier("techreborn", "dragon_egg_syphon"));
        removeSimpleBlock(new Identifier("techreborn", "lightning_rod"));
        removeSimpleBlock(new Identifier("techreborn", "water_mill"));
        removeSimpleBlock(new Identifier("techreborn", "wind_mill"));
        removeSimpleBlock(new Identifier("techreborn", "drain"));
        removeSimpleBlock(new Identifier("techreborn", "lsu_storage"));
        removeSimpleBlock(new Identifier("techreborn", "lv_transformer"));
        removeSimpleBlock(new Identifier("techreborn", "mv_transformer"));
        removeSimpleBlock(new Identifier("techreborn", "hv_transformer"));
        removeSimpleBlock(new Identifier("techreborn", "ev_transformer"));
        removeSimpleBlock(new Identifier("techreborn", "alarm"));
        removeSimpleBlock(new Identifier("techreborn", "lamp_incandescent"));
        removeSimpleBlock(new Identifier("techreborn", "lamp_led"));
        removeSimpleBlock(new Identifier("techreborn", "computer_cube"));

    }

    public static void registerInventoryTab(Identifier itemId) {
        INVENTORY_TAB_PROVIDER.addItem(itemId);
    }

    /**
     * Used to register a block with the simple block tab provider.
     *
     * @param block
     */
    public static void registerSimpleBlock(Block block) {
        //System.out.println("Registering simple block: " + block);
        SIMPLE_BLOCK_TAB_PROVIDER.addBlock(block);
    }

    /**
     * Used to register a block identifier with the simple block tab provider.
     *
     * @param blockId
     */
    public static void registerSimpleBlock(Identifier blockId) {
        SIMPLE_BLOCK_TAB_PROVIDER.addBlock(blockId);
    }

    public static void removeSimpleBlock(Block block) {
        SIMPLE_BLOCK_TAB_PROVIDER.removeBlock(block);
    }
    public static void removeSimpleBlock(Identifier blockId) {
        SIMPLE_BLOCK_TAB_PROVIDER.removeBlock(blockId);
    }

    /**
     * Used to register a chest with the chest tab provider.
     *
     * @param block
     */
    public static void registerChest(Block block) {
        CHEST_TAB_PROVIDER.addChestBlock(block);
    }

    /**
     * Used to register a chest with the chest tab provider.
     *
     * @param blockId
     */
    public static void registerChest(Identifier blockId) {
        CHEST_TAB_PROVIDER.addChestBlock(blockId);
    }

    public static TabProvider register(Identifier id, TabProvider tabProvider) {
        TAB_PROVIDERS.put(id, tabProvider);

        return tabProvider;
    }

    public static List<TabProvider> getTabProviders() {
        return new ArrayList<>(TAB_PROVIDERS.values());
    }
}
