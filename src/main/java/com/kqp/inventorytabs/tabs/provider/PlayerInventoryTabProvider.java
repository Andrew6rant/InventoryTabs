package com.kqp.inventorytabs.tabs.provider;

import java.util.List;

import com.kqp.inventorytabs.tabs.tab.PlayerInventoryTab;
import com.kqp.inventorytabs.tabs.tab.Tab;

import net.minecraft.client.network.ClientPlayerEntity;

public class PlayerInventoryTabProvider implements TabProvider {
    @Override
    public void addAvailableTabs(ClientPlayerEntity player, List<Tab> tabs) {
        for (Tab tab : tabs) {
            if (tab instanceof PlayerInventoryTab) {
                return;
            }
        }
        tabs.add(new PlayerInventoryTab());
    }
}
