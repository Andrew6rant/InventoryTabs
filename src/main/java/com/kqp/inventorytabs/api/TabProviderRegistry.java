package com.kqp.inventorytabs.api;

import java.util.*;

import com.kqp.inventorytabs.init.InventoryTabs;
import com.kqp.inventorytabs.interf.TabManagerContainer;
import com.kqp.inventorytabs.tabs.provider.*;

import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.tag.TagContainers;
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
    public static final SimpleEntityTabProvider ENTITY_TAB_PROVIDER = (SimpleEntityTabProvider) register(
            InventoryTabs.id("entity_tab_provider"), new SimpleEntityTabProvider());
    public static final SimpleBlockTabProvider SIMPLE_BLOCK_TAB_PROVIDER = (SimpleBlockTabProvider) register(
            InventoryTabs.id("simple_block_tab_provider"), new SimpleBlockTabProvider());
    public static final ChestTabProvider CHEST_TAB_PROVIDER = (ChestTabProvider) register(
            InventoryTabs.id("chest_tab_provider"), new ChestTabProvider());
    public static final EnderChestTabProvider ENDER_CHEST_TAB_PROVIDER = (EnderChestTabProvider) register(
            InventoryTabs.id("ender_chest_tab_provider"), new EnderChestTabProvider());
    public static final ShulkerBoxTabProvider SHULKER_BOX_TAB_PROVIDER = (ShulkerBoxTabProvider) register(
            InventoryTabs.id("shulker_box_tab_provider"), new ShulkerBoxTabProvider());
    public static final UniqueTabProvider UNIQUE_TAB_PROVIDER = (UniqueTabProvider) register(
            InventoryTabs.id("crafting_table_tab_provider"), new UniqueTabProvider());
    public static final LecternTabProvider LECTERN_TAB_PROVIDER = (LecternTabProvider) register(
            InventoryTabs.id("lectern_tab_provider"), new LecternTabProvider());
    public static final InventoryTabProvider INVENTORY_TAB_PROVIDER = (InventoryTabProvider) register(
            InventoryTabs.id("inventory_tab_provider"), new InventoryTabProvider());

    public static void init(String configMsg) {
        LOGGER.info("InventoryTabs: Attempting to "+configMsg+" config...");
        if (InventoryTabs.getConfig().debugEnabled) {
            LOGGER.warn("InventoryTabs: DEBUG ENABLED");
        }
        Set<String> invalidSet = new HashSet<>();
        Set<String> tagSet = new HashSet<>();
        Set<String> blockSet = new HashSet<>();
        for (String overrideEntry : InventoryTabs.getConfig().excludeTab) {
            if (overrideEntry.startsWith("#")) {
                tagSet.add(overrideEntry.trim().substring(1));
            } else {
                blockSet.add(overrideEntry);
            }
        }
        Registry.BLOCK.forEach(block -> {
            if (block instanceof BlockEntityProvider) {
                if (block instanceof AbstractChestBlock) {
                    registerChest(block);
                } else if (!(block instanceof AbstractBannerBlock) && !(block instanceof AbstractSignBlock) && !(block instanceof AbstractSkullBlock) && !(block instanceof BeehiveBlock) && !(block instanceof BedBlock) && !(block instanceof BellBlock) && !(block instanceof CampfireBlock) && !(block instanceof ComparatorBlock) && !(block instanceof ConduitBlock) && !(block instanceof DaylightDetectorBlock) && !(block instanceof EndGatewayBlock) && !(block instanceof EndPortalBlock) && !(block instanceof JukeboxBlock) && !(block instanceof PistonExtensionBlock) && !(block instanceof SpawnerBlock)) {
                    registerSimpleBlock(block);
                }
            } else if (block instanceof CraftingTableBlock && !(block instanceof FletchingTableBlock) || block instanceof AnvilBlock || block instanceof CartographyTableBlock || block instanceof GrindstoneBlock || block instanceof LoomBlock || block instanceof StonecutterBlock) {
                registerUniqueBlock(block);
            }
            configRemove(block, tagSet, invalidSet);
        });
        configRemove(blockSet);
        configAdd();
        registerEntity(new Identifier("minecraft:entity.minecraft.chest_minecart"));

        MinecraftClient client = MinecraftClient.getInstance();
        TabManagerContainer tabManagerContainer = (TabManagerContainer) client;
        tabManagerContainer.getTabManager().removeTabs();
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

    public static boolean isValid(String overrideEntry, String[] splitEntry, Set<String> invalidSet) {
        if (splitEntry.length != 2) {
            invalidSet.add(overrideEntry);
            return false;
        }
        return true;
    }

    private static void configRemove(Set<String> blockSet) {
        for (String overrideEntry : blockSet) {
            if (InventoryTabs.getConfig().debugEnabled) {
                LOGGER.info("Excluding: " + overrideEntry);
            }
            removeSimpleBlock(new Identifier(overrideEntry));
        }
    }

    private static void configRemove(Block block, Set<String> tagSet, Set<String> invalidSet) {
        for (String overrideEntry : tagSet) {
            String[] splitEntry = overrideEntry.split(":"); // split into two parts: tag id, item name
            if (isValid(overrideEntry, splitEntry, invalidSet)) {
                var blocks = TagContainers.method_29223().method_30215();
                Collection<Identifier> blockStream = blocks.method_30206(block);
                for (Identifier tagKey : blockStream) {
                    if (block.isIn(blocks.method_30213(new Identifier(splitEntry[0], splitEntry[1])))) {
                        removeSimpleBlock(block);
                        if (InventoryTabs.getConfig().debugEnabled) {
                            LOGGER.info("Excluding: " + block);
                        }
                    }
                }
            }
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
        if (InventoryTabs.getConfig().debugEnabled) {
            LOGGER.info("Registering: " + block);
        }
        CHEST_TAB_PROVIDER.addChestBlock(block);
    }

    public static void registerUniqueBlock(Block block) {
        if (InventoryTabs.getConfig().debugEnabled) {
            LOGGER.info("Registering: " + block);
        }
        UNIQUE_TAB_PROVIDER.addUniqueBlock(block);
    }

    public static void registerEntity(Identifier entityId) {
        if (InventoryTabs.getConfig().debugEnabled) {
            LOGGER.info("Registering: " + entityId);
        }
        ENTITY_TAB_PROVIDER.addEntity(entityId);
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