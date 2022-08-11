package com.kqp.inventorytabs.tabs.tab;

import com.kqp.inventorytabs.mixin.accessor.ScreenAccessor;
import com.kqp.inventorytabs.tabs.render.TabRenderInfo;
import com.kqp.inventorytabs.util.EntityUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import java.util.Objects;

public class SimpleEntityTab extends Tab {
    public final Vec3d entityPos;
    public final Identifier entityId;
    public final Entity entity;

    public SimpleEntityTab(Entity entity) {
        super(new ItemStack(Registry.ITEM.get(new Identifier("minecraft", "barrier"))));
        this.entity = entity;
        this.entityPos = entity.getPos();
        this.entityId = EntityType.getId(entity.getType());
    }

    @Override
    public void open() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(player != null) {
            Hand hand = player.getActiveHand();
            if(hand == null){
                hand = Hand.MAIN_HAND;
            }
            MinecraftClient.getInstance().interactionManager.interactEntity(player, entity, hand);
        }
    }

    @Override
    public boolean shouldBeRemoved() {
        if (entity.removed) {
            return true;
        }
        return entityPos.distanceTo(MinecraftClient.getInstance().player.getPos()) > 5;
    }

    @Override
    public Text getHoverText() {
        return entity.getName();
    }

    @Override
    public void renderTabIcon(MatrixStack matrices, TabRenderInfo tabRenderInfo, HandledScreen<?> currentScreen) {
        ItemStack itemStack = getItemStack();
        ItemRenderer itemRenderer = ((ScreenAccessor) currentScreen).getItemRenderer();
        TextRenderer textRenderer = ((ScreenAccessor) currentScreen).getTextRenderer();
        itemRenderer.zOffset = 100.0F;
        itemRenderer.renderInGuiWithOverrides(itemStack, tabRenderInfo.itemX, tabRenderInfo.itemY);
        itemRenderer.renderGuiItemOverlay(textRenderer, itemStack, tabRenderInfo.itemX, tabRenderInfo.itemY);
        itemRenderer.zOffset = 0.0F;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SimpleEntityTab tab = (SimpleEntityTab) o;
        return Objects.equals(entityId, tab.entityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId);
    }

    public ItemStack getItemStack() {
        ItemStack pickBlockResult = EntityUtil.getEntityPickResult(entity);
        return pickBlockResult != null && !pickBlockResult.isEmpty() ? pickBlockResult : new ItemStack(Registry.ITEM.get(new Identifier("minecraft", "barrier")));
    }
}
