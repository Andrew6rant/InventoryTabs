package com.kqp.inventorytabs.tabs.provider;

import com.kqp.inventorytabs.tabs.tab.Tab;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public abstract class EntityTabProvider implements TabProvider {
    public static final int SEARCH_DISTANCE = 5;
    @Override
    public void addAvailableTabs(ClientPlayerEntity player, List<Tab> tabs) {
        World world = player.world;
        List<Entity> entityList = world.getNonSpectatingEntities(Entity.class, new Box(player.getBlockPos().getX()-SEARCH_DISTANCE, player.getBlockPos().getY()-SEARCH_DISTANCE, player.getBlockPos().getZ()-SEARCH_DISTANCE, player.getBlockPos().getX()+SEARCH_DISTANCE, player.getBlockPos().getY()+SEARCH_DISTANCE, player.getBlockPos().getZ()+SEARCH_DISTANCE));

        for (Entity entity : entityList) {
            if (!(entity instanceof PlayerEntity) && ((entity instanceof Inventory) || (entity instanceof InventoryChangedListener))) {
                if (matches(entity)) {
                    boolean add = false;

                    Vec3d playerHead = player.getPos().add(0D, player.getEyeHeight(player.getPose()), 0D);
                    Vec3d blockVec = new Vec3d(entity.getX() + 0.5D, entity.getY() + 0.5D,
                            entity.getZ() + 0.5D);

                    if (blockVec.subtract(playerHead).lengthSquared() <= SEARCH_DISTANCE * SEARCH_DISTANCE) {
                        add = true;
                    }


                    if (add) {
                        Tab tab = createTab(entity);

                        if (!tabs.contains(tab)) {
                            tabs.add(tab);
                        }
                    }
                }
            }
        }
    }
    /**
     * Checks to see if block at passsed block position matches criteria.
     *
     * @param entity
     * @return
     */
    public abstract boolean matches(Entity entity);

    /**
     * Method to create tabs.
     *
     * @param entity
     * @return
     */
    public abstract Tab createTab(Entity entity);
}