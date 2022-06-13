package com.kqp.inventorytabs.tabs.tab;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * Tab for the player's inventory.
 */
public class PlayerInventoryTab extends Tab {
    public PlayerInventoryTab() {
        super(getRenderItemStack());
    }

    @Override
    public void open() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.setScreen(new InventoryScreen(client.player));
    }

    @Override
    public boolean shouldBeRemoved() {
        return false;
    }

    @Override
    public Text getHoverText() {
        return Text.literal("Inventory");
    }

    @Override
    public String toString() {
        return "PLAYER INVENTORY TAB";
    }

    @Override
    public int getPriority() {
        return 100;
    }

    private static ItemStack getRenderItemStack() {
        ItemStack itemStack = new ItemStack(Blocks.PLAYER_HEAD);
        itemStack.getOrCreateNbt().putString("SkullOwner",
                MinecraftClient.getInstance().player.getGameProfile().getName());

        return itemStack;
    }
}
