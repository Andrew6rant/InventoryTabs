package com.kqp.inventorytabs.tabs.provider;

import com.kqp.inventorytabs.tabs.tab.InventoryTab;
import com.kqp.inventorytabs.tabs.tab.Tab;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InventoryTabProvider implements TabProvider {
    private static final Set<Identifier> inventoryItems = new HashSet<>();

    @Override
    public void addAvailableTabs(ClientPlayerEntity player, List<Tab> tabs) {
        Set<Item> itemSet = inventoryItems.stream().map(Registry.ITEM::get).collect(Collectors.toSet());
        for (Item item : itemSet) {
            if (player.inventory.contains(new ItemStack(item))) {
                Tab tab = new InventoryTab(item);
                if (tabs.stream().filter(c -> c instanceof InventoryTab).noneMatch(c -> ((InventoryTab) c).itemId == item)) {
                    tabs.add(tab);
                }
            }
        }
    }

    public void addItem(Identifier blockId) {
        inventoryItems.add(blockId);
    }

    public Set<Identifier> getItemIds() {
        return inventoryItems;
    }

    public static Set<Item> getItems() {
        return inventoryItems.stream().map(Registry.ITEM::get).collect(Collectors.toSet());
    }

}
