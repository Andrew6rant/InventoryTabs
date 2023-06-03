package com.kqp.inventorytabs.tabs.tab;

import com.kqp.inventorytabs.mixin.accessor.ScreenAccessor;
import com.kqp.inventorytabs.tabs.render.TabRenderInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Base interface for tabs.
 */
@Environment(EnvType.CLIENT)
public abstract class Tab {
    private final ItemStack renderItemStack;

    protected Tab(ItemStack renderItemStack) {
        this.renderItemStack = renderItemStack;
    }

    /**
     * Fires whenever the tab is clicked.
     */
    public abstract void open();

    /**
     * Returns true if the tab should stop being displayed. Should be synced up with
     * the provider that provides this tab.
     *
     * @return
     */
    public abstract boolean shouldBeRemoved();

    /**
     * Returns the first line of text that's displayed when hovering over the tab.
     *
     * @return
     */
    public abstract Text getHoverText();

    /**
     * Returns the full text that's displayed when hovering over the tab,
     * this includes the text of a sign that is attached to the container.
     *
     * @return
     */
    public List<Text> getFullHoverText() {
        ArrayList<Text> firstHoverText = new ArrayList<>(1);
        firstHoverText.add(getHoverText());
        return firstHoverText;
    }

    /**
     * Called when the screen associated with the tab is closed.
     */
    public void onClose() {
    }

    /**
     * Returns the tab's priority when being displayed. The player's inventory is at
     * 100.
     *
     * @return
     */
    public int getPriority() {
        return 0;
    }

    /**
     * Renders the tab's icon
     *
     * @param matrices      MatrixStack
     * @param tabRenderInfo TabRenderInfo
     * @param currentScreen HandledScreen
     */
    @Environment(EnvType.CLIENT)
    public void renderTabIcon(MatrixStack matrices, TabRenderInfo tabRenderInfo, HandledScreen<?> currentScreen) {
        ItemRenderer itemRenderer = ((ScreenAccessor) currentScreen).getItemRenderer();
        TextRenderer textRenderer = ((ScreenAccessor) currentScreen).getTextRenderer();
        matrices.push();
        matrices.translate(0, 0, 100.0F);
        // RenderSystem.enableRescaleNormal();
        itemRenderer.renderInGuiWithOverrides(matrices, renderItemStack, tabRenderInfo.itemX, tabRenderInfo.itemY);
        itemRenderer.renderGuiItemOverlay(matrices, textRenderer, renderItemStack, tabRenderInfo.itemX, tabRenderInfo.itemY);
        matrices.pop();
    }
}
