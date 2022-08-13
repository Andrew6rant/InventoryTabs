package com.kqp.inventorytabs.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;

public class EntityUtil {
    /** Currently hard-coded, see {@link net.minecraft.client.MinecraftClient} */
    public static ItemStack getEntityPickResult(Entity entity){
        if (entity instanceof PaintingEntity) {
            return new ItemStack(Items.PAINTING);
        } else if (entity instanceof LeashKnotEntity) {
            return new ItemStack(Items.LEAD);
        } else if (entity instanceof ItemFrameEntity) {
            ItemFrameEntity itemFrameEntity = (ItemFrameEntity)entity;
            ItemStack itemStack2 = itemFrameEntity.getHeldItemStack();
            return itemStack2.isEmpty() ? new ItemStack(Items.ITEM_FRAME) : itemStack2.copy();
        } else if (entity instanceof AbstractMinecartEntity) {
            Item item;
            AbstractMinecartEntity abstractMinecartEntity = (AbstractMinecartEntity)entity;
            switch (abstractMinecartEntity.getMinecartType()) {
                case FURNACE: {
                    item = Items.FURNACE_MINECART;
                    break;
                }
                case CHEST: {
                    item = Items.CHEST_MINECART;
                    break;
                }
                case TNT: {
                    item = Items.TNT_MINECART;
                    break;
                }
                case HOPPER: {
                    item = Items.HOPPER_MINECART;
                    break;
                }
                case COMMAND_BLOCK: {
                    item = Items.COMMAND_BLOCK_MINECART;
                    break;
                }
                default: {
                    item = Items.MINECART;
                }
            }
            return new ItemStack(item);
        } else if (entity instanceof BoatEntity) {
            return new ItemStack(((BoatEntity)entity).asItem());
        } else if (entity instanceof ArmorStandEntity) {
            return new ItemStack(Items.ARMOR_STAND);
        } else if (entity instanceof EndCrystalEntity) {
            return new ItemStack(Items.END_CRYSTAL);
        } else {
            SpawnEggItem spawnEggItem = SpawnEggItem.forEntity(entity.getType());
            if (spawnEggItem == null) {
                return ItemStack.EMPTY;
            }
            return new ItemStack(spawnEggItem);
        }
    }
}
