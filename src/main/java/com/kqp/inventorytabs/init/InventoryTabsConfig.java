package com.kqp.inventorytabs.init;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Config(name = "inventory_tabs")
public class InventoryTabsConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean doSightChecksFlag = true;
    @ConfigEntry.Gui.Tooltip
    public boolean rotatePlayer = false;

    @ConfigEntry.Gui.Tooltip
    public List<String> excludeTab = Arrays.asList(
            "tiered:reforging_station",
            "#techreborn:block_entities_without_inventories",
            "#inventorytabs:mod_compat_blacklist"
    );

    @ConfigEntry.Gui.Tooltip
    public List<String> includeTab = Arrays.asList();

    public boolean debugEnabled = false;
}
