package com.kqp.inventorytabs.tabs.render;

import java.awt.Rectangle;

import com.kqp.inventorytabs.init.InventoryTabs;
import com.kqp.inventorytabs.mixin.accessor.HandledScreenAccessor;
import com.kqp.inventorytabs.tabs.TabManager;
import com.kqp.inventorytabs.tabs.tab.Tab;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import static com.kqp.inventorytabs.init.InventoryTabs.*;

/**
 * Handles the rendering of tabs.
 */
@Environment(EnvType.CLIENT)
public class TabRenderer {
    private static final Identifier TABS_TEXTURE = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    private static final Identifier BUTTONS_TEXTURE = InventoryTabs.id("textures/gui/buttons.png");

    public static final int TAB_WIDTH = 28;
    public static final int TAB_HEIGHT = 32;
    public static final int BUTTON_WIDTH = 15;
    public static final int BUTTON_HEIGHT = 13;

    public final TabManager tabManager;

    private TabRenderInfo[] tabRenderInfos;

    private long pageTextRefreshTime;

    public TabRenderer(TabManager tabManager) {
        this.tabManager = tabManager;
    }

    public void renderBackground(MatrixStack matrices) {
        matrices.push();

        tabRenderInfos = getTabRenderInfos();

        for (int i = 0; i < tabRenderInfos.length; i++) {
            TabRenderInfo tabRenderInfo = tabRenderInfos[i];

            if (tabRenderInfo != null) {
                if (tabRenderInfo.tabReference != tabManager.currentTab) {
                    renderTab(matrices, tabRenderInfo);
                }
            }
        }
        matrices.pop();
    }

    public void renderForeground(MatrixStack matrices, double mouseX, double mouseY) {
        RenderSystem.setShaderTexture(0, TABS_TEXTURE);

        for (int i = 0; i < tabRenderInfos.length; i++) {
            TabRenderInfo tabRenderInfo = tabRenderInfos[i];

            if (tabRenderInfo != null) {
                if (tabRenderInfo.tabReference == tabManager.currentTab) {
                    renderTab(matrices, tabRenderInfo);
                }
            }
        }

        drawButtons(matrices, mouseX, mouseY);

        drawPageText(matrices);
    }

    private void drawButtons(MatrixStack matrices, double mouseX, double mouseY) {
        HandledScreen<?> currentScreen = tabManager.getCurrentScreen();

        RenderSystem.setShaderTexture(0, BUTTONS_TEXTURE);

        int width = ((HandledScreenAccessor) currentScreen).getBackgroundWidth();
        int height = ((HandledScreenAccessor) currentScreen).getBackgroundHeight();
        int oX = (currentScreen.width - width) / 2;
        int oY = (currentScreen.height - height) / 2;

        // Drawing back button
        int x = oX - BUTTON_WIDTH - 4;
        x += ((TabRenderingHints) currentScreen).getTopRowXOffset();
        int y = oY - 16;
        y += ((TabRenderingHints) currentScreen).getTopRowYOffset();
        boolean hovered = new Rectangle(x, y, BUTTON_WIDTH, BUTTON_HEIGHT).contains(mouseX, mouseY);
        int u = 0;
        u += tabManager.canGoBackAPage() && hovered ? BUTTON_WIDTH * 2 : 0;
        int v = tabManager.canGoBackAPage() ? 0 : 13;
        currentScreen.drawTexture(matrices, x, y, u, v, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Drawing forward button
        x = oX + width + 4;
        x += ((TabRenderingHints) currentScreen).getTopRowXOffset();
        y = oY - 16;
        y += ((TabRenderingHints) currentScreen).getTopRowYOffset();
        hovered = new Rectangle(x, y, BUTTON_WIDTH, BUTTON_HEIGHT).contains(mouseX, mouseY);
        u = 15;
        u += tabManager.canGoForwardAPage() && hovered ? BUTTON_WIDTH * 2 : 0;
        v = tabManager.canGoForwardAPage() ? 0 : 13;
        currentScreen.drawTexture(matrices, x, y, u, v, BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    private void drawPageText(MatrixStack matrices) {
        if (tabManager.getMaxPages() > 1 && pageTextRefreshTime > 0) {
            // RenderSystem.pushMatrix();
            // TODO: Figure out rendering

            int color = 0xFFFFFFFF;

            if (pageTextRefreshTime <= 20) {
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                // RenderSystem.disableAlphaTest();
                RenderSystem.defaultBlendFunc();
                RenderSystem.colorMask(true, true, true, true);
                float transparency = pageTextRefreshTime / 20F;

                color &= 0x00FFFFFF;
                color = ((int) (0xFF * transparency) << 24) | color;
            }

            HandledScreen<?> currentScreen = tabManager.getCurrentScreen();
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            int height = ((HandledScreenAccessor) currentScreen).getBackgroundHeight();
            int oX = currentScreen.width;
            int oY = (currentScreen.height - height) / 2;

            String text = (tabManager.currentPage + 1) + " / " + (tabManager.getMaxPages() + 1);
            int x = (oX - textRenderer.getWidth(text)) / 2;
            int y = oY - 34;

            MinecraftClient.getInstance().textRenderer.draw(matrices, text, x, y, color);

            // RenderSystem.popMatrix();
        }
    }

    private void renderTab(MatrixStack matrices, TabRenderInfo tabRenderInfo) {
        HandledScreen<?> currentScreen = tabManager.getCurrentScreen();

        RenderSystem.setShaderTexture(0, TABS_TEXTURE);
        currentScreen.drawTexture(matrices, tabRenderInfo.x, tabRenderInfo.y, tabRenderInfo.texU, tabRenderInfo.texV,
                tabRenderInfo.texW, tabRenderInfo.texH);

        tabRenderInfo.tabReference.renderTabIcon(matrices, tabRenderInfo, currentScreen);
    }

    public void renderHoverTooltips(MatrixStack matrices, double mouseX, double mouseY) {
        for (int i = 0; i < tabRenderInfos.length; i++) {
            TabRenderInfo tabRenderInfo = tabRenderInfos[i];

            if (tabRenderInfo != null) {
                Rectangle itemRec = new Rectangle(tabRenderInfo.itemX, tabRenderInfo.itemY, 16, 16);

                if (itemRec.contains(mouseX, mouseY)) {
                    tabManager.getCurrentScreen().renderTooltip(matrices, tabRenderInfo.tabReference.getFullHoverText(),
                            (int) mouseX, (int) mouseY);
                }
            }
        }
    }

    public TabRenderInfo[] getTabRenderInfos() {
        HandledScreen<?> currentScreen = tabManager.getCurrentScreen();

        int maxRowLength = tabManager.getMaxRowLength();
        int numVisibleTabs;
        if(isBigInvLoaded) {
            numVisibleTabs = (maxRowLength * 2) + 5;
        } else if (isPlayerExLoaded) {
            numVisibleTabs = (maxRowLength * 2) - 3;
        } else if (isLevelzLoaded) {
            numVisibleTabs = (maxRowLength * 2) - 2;
        }else {
            numVisibleTabs = maxRowLength * 2;
        }
        int startingIndex = tabManager.currentPage * numVisibleTabs;

        TabRenderInfo[] tabRenderInfo = new TabRenderInfo[numVisibleTabs];

        int x = (currentScreen.width - ((HandledScreenAccessor) currentScreen).getBackgroundWidth()) / 2;
        int y = (currentScreen.height - ((HandledScreenAccessor) currentScreen).getBackgroundHeight()) / 2;

        for (int i = 0; i < numVisibleTabs; i++) {
            if (startingIndex + i < tabManager.tabs.size()) {
                // Setup basic info
                Tab tab = tabManager.tabs.get(startingIndex + i);
                boolean topRow = i < maxRowLength;
                if(isPlayerExLoaded) {
                    topRow = i < maxRowLength - 3;
                } else if(isLevelzLoaded) {
                    topRow = i < maxRowLength - 2;
                }
                boolean selected = tab == tabManager.currentTab;

                // Create tab info object
                TabRenderInfo tabInfo = new TabRenderInfo();
                tabInfo.tabReference = tab;
                tabInfo.index = startingIndex + i;

                // Calc x value
                tabInfo.x = x + i * (TAB_WIDTH + 1);
                if (!topRow) {
                    tabInfo.x -= maxRowLength * (TAB_WIDTH + 1);
                }

                // Calc y value
                if (topRow) {
                    tabInfo.y = y - 28;
                } else {
                    if(isBigInvLoaded) {
                        tabInfo.y = y + ((HandledScreenAccessor) currentScreen).getBackgroundHeight() + 32;
                    } else {
                        tabInfo.y = y + ((HandledScreenAccessor) currentScreen).getBackgroundHeight() - 4;
                    }
                }

                // Calc texture dimensions
                tabInfo.texW = 28;
                tabInfo.texH = 32;

                // Calc texture U
                if (i == 0 || i == maxRowLength) {
                    tabInfo.texU = 0;
                } else {
                    tabInfo.texU = 28;
                }

                // Calc texture V
                if (topRow) {
                    if (selected) {
                        tabInfo.texV = 32;
                    } else {
                        tabInfo.texV = 0;
                    }
                } else {
                    if (selected) {
                        tabInfo.texV = 96;
                    } else {
                        tabInfo.texV = 64;
                    }
                }

                // Calc item position
                if (topRow) {
                    tabInfo.itemX = tabInfo.x + 6;
                    tabInfo.itemY = tabInfo.y + 8;
                } else {
                    tabInfo.itemX = tabInfo.x + 6;
                    tabInfo.itemY = tabInfo.y + 6;
                }

                // Apply rendering hints
                if (currentScreen instanceof TabRenderingHints) {
                    if (topRow) {
                        if(isPlayerExLoaded) {
                            tabInfo.x += ((TabRenderingHints) currentScreen).getTopRowXOffset() + 87;
                            tabInfo.itemX += ((TabRenderingHints) currentScreen).getTopRowXOffset() + 87;
                        } else if(isLevelzLoaded) {
                            tabInfo.x += ((TabRenderingHints) currentScreen).getTopRowXOffset() + 54;
                            tabInfo.itemX += ((TabRenderingHints) currentScreen).getTopRowXOffset() + 54;
                        }else {
                            tabInfo.x += ((TabRenderingHints) currentScreen).getTopRowXOffset();
                            tabInfo.itemX += ((TabRenderingHints) currentScreen).getTopRowXOffset();
                        }
                        tabInfo.y += ((TabRenderingHints) currentScreen).getTopRowYOffset();
                        tabInfo.itemY += ((TabRenderingHints) currentScreen).getTopRowYOffset();
                    } else {
                        if(isBigInvLoaded) {
                            tabInfo.x += ((TabRenderingHints) currentScreen).getBottomRowXOffset() - 145;
                            tabInfo.itemX += ((TabRenderingHints) currentScreen).getBottomRowXOffset() - 145;
                        } else if(isPlayerExLoaded) {
                            tabInfo.x += ((TabRenderingHints) currentScreen).getBottomRowXOffset() + 86;
                            tabInfo.itemX += ((TabRenderingHints) currentScreen).getBottomRowXOffset() + 86;
                        } else if(isLevelzLoaded) {
                            tabInfo.x += ((TabRenderingHints) currentScreen).getBottomRowXOffset() + 60;
                            tabInfo.itemX += ((TabRenderingHints) currentScreen).getBottomRowXOffset() + 60;
                        }else {
                            tabInfo.x += ((TabRenderingHints) currentScreen).getBottomRowXOffset();
                            tabInfo.itemX += ((TabRenderingHints) currentScreen).getBottomRowXOffset();
                        }
                        tabInfo.y += ((TabRenderingHints) currentScreen).getBottomRowYOffset();
                        tabInfo.itemY += ((TabRenderingHints) currentScreen).getBottomRowYOffset();
                    }
                }

                tabRenderInfo[i] = tabInfo;
            }
        }

        return tabRenderInfo;
    }

    public void update() {
        pageTextRefreshTime = Math.max(pageTextRefreshTime - 1, 0);
    }

    public void resetPageTextRefreshTime() {
        pageTextRefreshTime = 60;
    }
}
