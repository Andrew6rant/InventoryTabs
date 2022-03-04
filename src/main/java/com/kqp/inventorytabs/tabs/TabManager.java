package com.kqp.inventorytabs.tabs;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.kqp.inventorytabs.api.TabProviderRegistry;
import com.kqp.inventorytabs.init.InventoryTabsClient;
import com.kqp.inventorytabs.interf.TabManagerContainer;
import com.kqp.inventorytabs.mixin.accessor.HandledScreenAccessor;
import com.kqp.inventorytabs.tabs.render.TabRenderInfo;
import com.kqp.inventorytabs.tabs.render.TabRenderer;
import com.kqp.inventorytabs.tabs.render.TabRenderingHints;
import com.kqp.inventorytabs.tabs.tab.Tab;
import com.kqp.inventorytabs.util.MouseUtil;

import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.sound.SoundEvents;

/**
 * Manages everything related to tabs.
 */
@Environment(EnvType.CLIENT)
public class TabManager {
    public final List<Tab> tabs;
    public Tab currentTab;

    private HandledScreen<?> currentScreen;
    public int currentPage = 0;
    public boolean tabOpenedRecently;

    public final TabRenderer tabRenderer;

    public TabManager() {
        this.tabs = new ArrayList<>();
        this.tabRenderer = new TabRenderer(this);
    }

    public void update() {
        refreshAvailableTabs();

        tabRenderer.update();
    }

    public void setCurrentTab(Tab tab) {
        this.currentTab = tab;
    }

    private void refreshAvailableTabs() {
        // Remove old ones
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).shouldBeRemoved()) {
                tabs.remove(i);
                i--;
            }
        }

        // Add new tabs
        TabProviderRegistry.getTabProviders().forEach(tabProvider -> {
            tabProvider.addAvailableTabs(MinecraftClient.getInstance().player, tabs);
        });

        // Sort
        tabs.sort(
                Comparator.comparing(Tab::getPriority).reversed().thenComparing(tab -> tab.getHoverText().getString()));
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int guiWidth = ((HandledScreenAccessor) currentScreen).getBackgroundWidth();
            int guiHeight = ((HandledScreenAccessor) currentScreen).getBackgroundHeight();
            int x = (currentScreen.width - guiWidth) / 2;
            int y = (currentScreen.height - guiHeight) / 2;

            if (mouseX > x && mouseX < x + guiWidth && mouseY > y && mouseY < y + guiHeight) {
                return false;
            }

            // Check back button
            if (new Rectangle(x - TabRenderer.BUTTON_WIDTH - 4 + ((TabRenderingHints) currentScreen).getTopRowXOffset(), y - 16, TabRenderer.BUTTON_WIDTH,
                    TabRenderer.BUTTON_HEIGHT).contains(mouseX, mouseY)) {
                if (canGoBackAPage()) {
                    setCurrentPage(currentPage - 1);
                    playClick();

                    return true;
                }
            }

            // Check forward button
            if (new Rectangle(x + guiWidth + 4 + ((TabRenderingHints) currentScreen).getTopRowXOffset(), y - 16, TabRenderer.BUTTON_WIDTH, TabRenderer.BUTTON_HEIGHT)
                    .contains(mouseX, mouseY)) {
                if (canGoForwardAPage()) {
                    setCurrentPage(currentPage + 1);
                    playClick();

                    return true;
                }
            }

            TabRenderInfo[] tabRenderInfos = tabRenderer.getTabRenderInfos();

            for (int i = 0; i < tabRenderInfos.length; i++) {
                TabRenderInfo tabRenderInfo = tabRenderInfos[i];

                if (tabRenderInfo != null) {
                    if (tabRenderInfo.tabReference != currentTab) {
                        Rectangle rect = new Rectangle(tabRenderInfo.x, tabRenderInfo.y, tabRenderInfo.texW,
                                tabRenderInfo.texH);

                        if (rect.contains(mouseX, mouseY)) {
                            onTabClick(tabRenderInfo.tabReference);

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (InventoryTabsClient.NEXT_TAB_KEY_BIND.matchesKey(keyCode, scanCode)) {
            int currentTabIndex = tabs.indexOf(currentTab);

            if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(),
                    GLFW.GLFW_KEY_LEFT_SHIFT)) {
                if (currentTabIndex > 0) {
                    onTabClick(tabs.get(currentTabIndex - 1));
                } else {
                    onTabClick(tabs.get(tabs.size() - 1));
                }
                return true;
            } else {
                if (currentTabIndex < tabs.size() - 1) {
                    onTabClick(tabs.get(currentTabIndex + 1));
                } else {
                    onTabClick(tabs.get(0));
                }

                return true;
            }
        }

        return false;
    }

    public void onScreenOpen(HandledScreen<?> screen) {
        refreshAvailableTabs();

        setCurrentScreen(screen);
        MouseUtil.tryPop();
    }

    public void onTabClick(Tab tab) {
        // Push current mouse position
        // This is to persist mouse position across screens
        MouseUtil.push();

        // Set tab open flag
        tabOpenedRecently = true;

        // Close any handled screens
        // This fixes the inventory desync issue
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player.currentScreenHandler != null) {
            client.getNetworkHandler()
                    .sendPacket(new CloseHandledScreenC2SPacket(client.player.currentScreenHandler.syncId));
        }

        // Open new tab
        onOpenTab(tab);
        tab.open();
    }

    public void onOpenTab(Tab tab) {
        if (currentTab != null && currentTab != tab) {
            currentTab.onClose();
        }

        setCurrentTab(tab);
        setCurrentPage(pageOf(tab));
    }

    public int pageOf(Tab tab) {
        int index = tabs.indexOf(tab);
        if(FabricLoader.getInstance().isModLoaded("biginv")) {
            return index / (getMaxRowLength() * 2 + 5);
        } else {
            return index / (getMaxRowLength() * 2);
        }
    }

    public int getMaxRowLength() {
        int guiWidth = ((HandledScreenAccessor) currentScreen).getBackgroundWidth();
        int maxRowLength = guiWidth / (TabRenderer.TAB_WIDTH + 1);

        return maxRowLength;
    }

    public void setCurrentScreen(HandledScreen<?> screen) {
        this.currentScreen = screen;
    }

    public HandledScreen<?> getCurrentScreen() {
        return currentScreen;
    }

    public void setCurrentPage(int page) {
        if (page > 0 && tabs.size() < getMaxRowLength() * 2) {
            System.err.println("Not enough tabs to paginate, ignoring");

            return;
        }

        if (this.currentPage != page) {
            tabRenderer.resetPageTextRefreshTime();
        }

        this.currentPage = page;
    }

    public boolean screenOpenedViaTab() {
        if (tabOpenedRecently) {
            tabOpenedRecently = false;

            return true;
        }

        return false;
    }

    public int getMaxPages() {
        if(FabricLoader.getInstance().isModLoaded("biginv")) {
            return tabs.size() / (getMaxRowLength() * 2 + 6);
        } else {
            return tabs.size() / (getMaxRowLength() * 2 + 1);
        }
    }

    public boolean canGoBackAPage() {
        return currentPage != 0;
    }

    public boolean canGoForwardAPage() {
        return currentPage < getMaxPages();
    }

    public static TabManager getInstance() {
        return ((TabManagerContainer) MinecraftClient.getInstance()).getTabManager();
    }

    public static void playClick() {
        MinecraftClient.getInstance().getSoundManager()
                .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}

