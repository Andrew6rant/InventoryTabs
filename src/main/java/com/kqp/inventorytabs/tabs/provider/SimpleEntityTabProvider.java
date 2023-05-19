package com.kqp.inventorytabs.tabs.provider;

import com.kqp.inventorytabs.tabs.tab.SimpleEntityTab;
import com.kqp.inventorytabs.tabs.tab.Tab;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleEntityTabProvider extends EntityTabProvider {
    private final Set<Identifier> entities = new HashSet<>();

    public SimpleEntityTabProvider() {
    }

    @Override
    public void addAvailableTabs(ClientPlayerEntity player, List<Tab> tabs) {
        super.addAvailableTabs(player, tabs);
        Set<SimpleEntityTab> tabsToRemove = new HashSet<>();
        List<SimpleEntityTab> entityTabs = tabs.stream().filter(tab -> tab instanceof SimpleEntityTab).map(tab -> (SimpleEntityTab) tab)
                .filter(tab -> entities.contains(tab.entityId)).toList();
        World world = player.getWorld();
    }

    @Override
    public boolean matches(Entity entity) {
        return entities.contains(new Identifier("minecraft:entity.minecraft.chest_minecart"));
    }

    public void addEntity(Identifier entityId) {
        entities.add(entityId);
    }

    @Override
    public Tab createTab(Entity entity) {
        return new SimpleEntityTab(entity);
    }
}
