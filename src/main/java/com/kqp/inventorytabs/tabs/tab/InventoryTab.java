package com.kqp.inventorytabs.tabs.tab;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class InventoryTab extends Tab {
    public final Item itemId;
    public InventoryTab(Item itemId) {
        super(new ItemStack(itemId));
        this.itemId = itemId;
    }

    @Override
    public void open() {
        System.out.println("TESTING: Opening inventory tab");
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        World world = MinecraftClient.getInstance().world;
        System.out.println("Player: "+player);
        System.out.println("World: "+world);
        System.out.println("Item: "+itemId);
        System.out.println("ItemStack: "+new ItemStack(itemId));
        System.out.println("Active hand: "+player.getActiveHand());
        Item item = new ItemStack(itemId).getItem();
        item.use(world, player, player.getActiveHand());
        //itemId.use(world, player, player.getActiveHand());
    }

    @Override
    public boolean shouldBeRemoved() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        return (player == null || !player.inventory.contains(new ItemStack(itemId)));
    }

    @Override
    public Text getHoverText() {
        return new LiteralText(itemId.getName().getString());
    }
}
