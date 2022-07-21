package com.kqp.inventorytabs.tabs.tab;

import com.kqp.inventorytabs.mixin.accessor.ScreenAccessor;
import com.kqp.inventorytabs.tabs.render.TabRenderInfo;
import com.kqp.inventorytabs.util.ChestUtil;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.kqp.inventorytabs.util.ChestUtil.getOtherChestBlockPos;

/**
 * Tab for chests
 */
public class ChestTab extends SimpleBlockTab {
    ItemStack itemStack;
    public ChestTab(Identifier blockId, BlockPos blockPos) {
        super(blockId, blockPos);
        this.itemStack = new ItemStack(Registry.BLOCK.get(blockId));
    }

    @Override
    public boolean shouldBeRemoved() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (ChestBlock.isChestBlocked(player.world, blockPos)) {
            return true;
        }

        return super.shouldBeRemoved();
    }

    @Override
    public Text getHoverText() {
        if (itemStack.hasCustomName()) {
            return itemStack.getName();
        }
        return super.getHoverText();
    }

    @Override
    public void renderTabIcon(MatrixStack matrices, TabRenderInfo tabRenderInfo, HandledScreen<?> currentScreen) {
        ItemStack itemStack = getItemFrame();
        ItemRenderer itemRenderer = ((ScreenAccessor) currentScreen).getItemRenderer();
        TextRenderer textRenderer = ((ScreenAccessor) currentScreen).getTextRenderer();
        itemRenderer.zOffset = 100.0F;
        itemRenderer.renderInGuiWithOverrides(itemStack, tabRenderInfo.itemX, tabRenderInfo.itemY);
        itemRenderer.renderGuiItemOverlay(textRenderer, itemStack, tabRenderInfo.itemX, tabRenderInfo.itemY);
        itemRenderer.zOffset = 0.0F;
    }

    public ItemStack getItemFrame() {
        World world = MinecraftClient.getInstance().player.world;
        itemStack = new ItemStack(world.getBlockState(blockPos).getBlock());
        BlockPos doubleChestPos = ChestUtil.isDouble(world, blockPos) ? getOtherChestBlockPos(world, blockPos) : blockPos;
        Box box = new Box(blockPos, doubleChestPos);
        double x = box.minX;    double y = box.minY;    double z = box.minZ;
        double x1 = box.maxX;   double y1 = box.maxY;   double z1 = box.maxZ;
        List<ItemFrameEntity> list1 = world.getNonSpectatingEntities(ItemFrameEntity.class, new Box(x-0.8, y, z, x1+1.8, y1+0.8, z1+0.8));
        List<ItemFrameEntity> list2 = world.getNonSpectatingEntities(ItemFrameEntity.class, new Box(x, y, z-0.8, x1+0.8, y1+0.8, z1+1.8));
        List<ItemFrameEntity> list3 = world.getNonSpectatingEntities(ItemFrameEntity.class, new Box(x, y-0.8, z, x1+0.8, y1+1.8, z1+0.8));
        List<ItemFrameEntity> list = new ArrayList<>();
        Stream.of(list1, list2, list3).forEach(list::addAll);
        if (!list.isEmpty()) {
            itemStack = list.get(0).getHeldItemStack();
        }
        return itemStack;
    }
}
