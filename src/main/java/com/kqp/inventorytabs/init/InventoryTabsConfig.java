package com.kqp.inventorytabs.init;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "inventory_tabs")
public class InventoryTabsConfig implements ConfigData {
    public boolean doSightChecksFlag = true;
    public boolean rotatePlayer = false;
    public boolean targetAllScreenHandledBlocks = false;
}
