package com.kqp.inventorytabs.init;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

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
            "techreborn:basic_machine_casing",
            "techreborn:advanced_machine_casing",
            "techreborn:industrial_machine_casing",
            "techreborn:creative_solar_panel",
            "techreborn:copper_cable",
            "techreborn:tin_cable",
            "techreborn:gold_cable",
            "techreborn:hv_cable",
            "techreborn:glassfiber_cable",
            "techreborn:insulated_copper_cable",
            "techreborn:insulated_gold_cable",
            "techreborn:insulated_hv_cable",
            "techreborn:superconductor_cable",
            "techreborn:resin_basin",
            "techreborn:dragon_egg_syphon",
            "techreborn:lightning_rod",
            "techreborn:water_mill",
            "techreborn:wind_mill",
            "techreborn:drain",
            "techreborn:lsu_storage",
            "techreborn:lv_transformer",
            "techreborn:mv_transformer",
            "techreborn:hv_transformer",
            "techreborn:ev_transformer",
            "techreborn:alarm",
            "techreborn:lamp_incandescent",
            "techreborn:lamp_led",
            "techreborn:computer_cube"
    );

    @ConfigEntry.Gui.Tooltip
    public List<String> includeTab = List.of();

    public boolean debugEnabled = false;
}
