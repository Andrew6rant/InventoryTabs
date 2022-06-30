package com.kqp.inventorytabs.api;

import java.util.*;

import com.kqp.inventorytabs.init.InventoryTabs;
import com.kqp.inventorytabs.tabs.provider.*;

import net.minecraft.block.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Registry for tab providers.
 */
public class TabProviderRegistry {
    private static final Logger LOGGER = LogManager.getLogger("InventoryTabs");
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


    public static void init(String configMsg) {
        LOGGER.info("InventoryTabs: Attempting to "+configMsg+" config...");
        Set<String> invalidSet = new HashSet<>();
        if (InventoryTabs.getConfig().debugEnabled) {
            LOGGER.warn("InventoryTabs: DEBUG ENABLED");
        }
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
        configRemove();
        configAdd();
        //modCompatAdd();
        LOGGER.info(configMsg.equals("save") ? "InventoryTabs: Config saved!": "InventoryTabs: Config "+configMsg+"ed!");
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

    private static void configRemove() {
        for (String excluded_tab : InventoryTabs.getConfig().excludeTab) {
            if (InventoryTabs.getConfig().debugEnabled) {
                LOGGER.info("Excluding: " + excluded_tab);
            }
            removeSimpleBlock(new Identifier(excluded_tab));
        }
    }
    private static void configAdd() {
        for (String included_tab : InventoryTabs.getConfig().includeTab) {
            if (InventoryTabs.getConfig().debugEnabled) {
                LOGGER.info("Including: " + included_tab);
            }
            registerSimpleBlock(new Identifier(included_tab));
        }
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
        if (InventoryTabs.getConfig().debugEnabled) {
            LOGGER.info("Registering: " + block);
        }
        SIMPLE_BLOCK_TAB_PROVIDER.addBlock(block);
    }

    /**
     * Used to register a block identifier with the simple block tab provider.
     *
     * @param blockId
     */
    public static void registerSimpleBlock(Identifier blockId) {
        if (InventoryTabs.getConfig().debugEnabled) {
            LOGGER.info("Registering: " + blockId);
        }
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
