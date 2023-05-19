package com.kqp.inventorytabs.tabs.tab;

import com.kqp.inventorytabs.mixin.accessor.ScreenAccessor;
import com.kqp.inventorytabs.tabs.render.TabRenderInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import java.util.Objects;

public class SimpleEntityTab extends Tab {
    public final Vec3d entityPos;
    public final Identifier entityId;
    public final Entity entity;

    public SimpleEntityTab(Entity entity) {
        super(new ItemStack(Registries.ITEM.get(new Identifier("minecraft", "barrier"))));
        this.entity = entity;
        this.entityPos = entity.getPos();
        this.entityId = EntityType.getId(entity.getType());
    }

    @Override
    public void open() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        MinecraftClient.getInstance().interactionManager.interactEntity(player, entity, player.getActiveHand());
    }

    @Override
    public boolean shouldBeRemoved() {
        if (entity.isRemoved()) {
            return true;
        }
        return entityPos.distanceTo(MinecraftClient.getInstance().player.getPos()) > 5;
    }

    @Override
    public Text getHoverText() {
        return entity.getName();
    }

    @Override
    public void renderTabIcon(DrawContext context, TabRenderInfo tabRenderInfo, HandledScreen<?> currentScreen) {
        ItemStack itemStack = getItemStack();
        TextRenderer textRenderer = ((ScreenAccessor) currentScreen).getTextRenderer();
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 100.0F);
        context.drawItem(itemStack, tabRenderInfo.itemX, tabRenderInfo.itemY);
        context.drawItemInSlot(textRenderer, itemStack, tabRenderInfo.itemX, tabRenderInfo.itemY);
        context.getMatrices().pop();
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
        return entity.getPickBlockStack() != null ? entity.getPickBlockStack() : new ItemStack(Registries.ITEM.get(new Identifier("minecraft", "barrier")));
    }
}
