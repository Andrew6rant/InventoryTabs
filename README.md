# InventoryTabs
Adds tabs to access nearby blocks without leaving your inventory. Completely client-side. Requires Cloth Config API.

<table><tr><td width="34.5%">

![Inventory Tabs showcase](https://raw.githubusercontent.com/Andrew6rant/inventorytabs/1.18.x/.github/resources/InventoryTabs.png)

</td><td width="65.5%">

![Showcase GIF](https://raw.githubusercontent.com/Andrew6rant/inventorytabs/1.18.x/.github/resources/Tabs_showcase.gif)

</td></tr></table>

Also has [BigInv](https://github.com/SollyW/BigInv) and [PlayerEx](https://www.curseforge.com/minecraft/mc-mods/playerex) support!

<table><tr><td width="36.5%">

![BigInv support](https://raw.githubusercontent.com/Andrew6rant/inventorytabs/1.18.x/.github/resources/BigInv_compat.png)

</td><td width="33.5%">

![PlayerEx support](https://user-images.githubusercontent.com/57331134/175751311-6edb68b4-82f4-4104-ba45-38139303536c.png)

</td></tr></table>

New in 0.8.1: Chest icons and hover text can be changed by nearby item frames!

![InventoryTabs-nearby-item-frames](https://user-images.githubusercontent.com/57331134/180107483-11429056-8e99-4529-ad7d-7091032fb9aa.gif)

New in 0.9.0: Entity support!

<table><tr><td width="38%">

![Chest Minecart showcase](https://user-images.githubusercontent.com/57331134/180368210-2510a9a3-0efb-49c9-9a1b-66b137739bf9.gif)

</td><td width="32%">

![Villager inventory showcase](https://user-images.githubusercontent.com/57331134/180335139-9db9e488-9e1d-467f-9877-13db22173453.png)

</td></tr></table>


## Devs
### Importing
To add **Inventory Tabs** to your project, you need to add ``https://jitpack.io`` as a repo and ``com.github.Andrew6rant:InventoryTabs:inventorytabs-(mod version)-(Minecraft version)`` as a dependency. For example:
```
repositories {
	maven {
		url "https://jitpack.io"
	}
}

dependencies {
	modImplementation "com.github.Andrew6rant:InventoryTabs:inventorytabs-0.6.1-1.19.x"
}
```

See the releases page for available versions.

### Adding Custom Tabs
There are multiple ways to add custom tabs.

#### Simple Block Tabs
A "simple block tab" is a tab that is opened via interaction with a block. If your block falls under this category, adding a tab is as easy as passing your block to ``TabProviderRegistry#registerSimpleBlock``.

#### Chest Tabs
Chest tabs are tabs belonging to chests that are able to double up along the horizontal axis. To register your chest, pass the block reference to ``TabProviderRegistry#registerChest``. Chests that do not match vanilla chest behavior should not use this method.

#### Custom Tabs
The first step to adding a custom tab is creating a class that implements the ``Tab`` interface. This represents the tab that players will see, and it also controls what happens when the tab is clicked.

The next step is to register a ``TabProvider`` using the ``TabProviderRegistry``. ``TabProvider`` objects are called every tick while a screen is open in order to populate the list of tabs available to the player. The list is not managed by the mod, so be sure to check for duplicates yourself. The ``GenericBlockTabProvider`` class is provided, and serves as a basis for adding tabs for blocks that open handled screens (it also checks for duplicates for you). Feel free to see the ``EnderChestTabProvider`` and ``ShulkerBoxTabProvider`` classes for implementation details.

Your handled screen needs to update the ``TabManager``, which you can grab a reference of by calling ``TabManager#getInstance``. In the ``init`` method (not your constructor), you **must** call ``TabManager$#onScreenOpen``. After doing that, you need to do a check to see if your screen was opened via tab or other means. If it **wasn't** opened via tab (check by calling ``TabManager#screenOpenedViaTab``), call ``TabManager#onOpenTab`` with your ``Tab`` object. See ``VanillaScreenTabAdder`` for more details on how to do this. You can also adjust the y-axis positioning of the bottom row of tabs by setting ``TabRenderer#bottomRowYOffset`` (there is a reference to a ``TabRenderer`` object in the ``TabManager``).

Finally, there are some methods to call for rendering and managing the tabs. In the ``render`` method before anything is drawn, call ``TabRenderer#renderBackground``. In the ``drawBackground``, call ``TabRenderer#renderForeground`` and ``TabRenderer#renderHoverTooltips``. In the ``mouseClicked`` method, call ``TabManager#mouseClicked``.

If your screen's GUI dynamically changes (in the case of a recipe book opening), you can implement the ``TabRenderingHints`` interface and offset the top and bottom rows however you like.

## Credits
This 1.18 and 1.19 port is based on LiamMCW's fork of the original mod by cakewhip. Full credits can be found at https://github.com/Andrew6rant/inventorytabs/graphs/contributors.
