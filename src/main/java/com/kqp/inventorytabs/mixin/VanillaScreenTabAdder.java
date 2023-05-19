package com.kqp.inventorytabs.mixin;

import java.util.HashSet;
import java.util.Set;

import com.kqp.inventorytabs.init.InventoryTabsClient;
import com.kqp.inventorytabs.interf.TabManagerContainer;
import com.kqp.inventorytabs.tabs.TabManager;
import com.kqp.inventorytabs.tabs.render.TabRenderingHints;
import com.kqp.inventorytabs.tabs.tab.SimpleBlockTab;
import com.kqp.inventorytabs.tabs.tab.Tab;
import com.kqp.inventorytabs.util.ChestUtil;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class VanillaScreenTabAdder extends Screen implements TabRenderingHints {
    private static final boolean isBRBLoaded = FabricLoader.getInstance().isModLoaded("brb"); // Better Recipe Book compat
    
    protected VanillaScreenTabAdder(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void initRestoreStack(CallbackInfo callbackInfo) {
        MinecraftClient client = MinecraftClient.getInstance();
        TabManager tabManager = ((TabManagerContainer) client).getTabManager();
        if (tabManager.screenOpenedViaTab()) {
            tabManager.restoreCursorStack(client.interactionManager, client.player, ((HandledScreen<?>) (Object) this).getScreenHandler());
            tabManager.tabOpenedRecently = true; // Preserve value for later
        }
    }
    
    @Inject(method = "init", at = @At("RETURN"))
    private void initTabRenderer(CallbackInfo callbackInfo) {
        if (InventoryTabsClient.screenSupported(this)) {
            MinecraftClient client = MinecraftClient.getInstance();
            TabManager tabManager = ((TabManagerContainer) client).getTabManager();

            tabManager.onScreenOpen((HandledScreen<?>) (Object) this);

            Tab tabOpened = null;

            if ((Object) this instanceof InventoryScreen) {
                tabOpened = tabManager.tabs.get(0);
            } else if (!tabManager.screenOpenedViaTab()) { // Consumes flag
                // If the screen was NOT opened via tab,
                // check what block player is looking at for context

                if (client.crosshairTarget instanceof BlockHitResult) {
                    BlockHitResult blockHitResult = (BlockHitResult) client.crosshairTarget;
                    BlockPos blockPos = blockHitResult.getBlockPos();

                    Set<BlockPos> matchingBlockPositions = new HashSet<>();
                    matchingBlockPositions.add(blockPos);

                    // For double chests
                    World world = client.player.getWorld();
                    if (world.getBlockState(blockPos).getBlock() instanceof ChestBlock) {
                        if (ChestUtil.isDouble(world, blockPos)) {
                            matchingBlockPositions.add(ChestUtil.getOtherChestBlockPos(world, blockPos));
                        }
                    }

                    for (int i = 0; i < tabManager.tabs.size(); i++) {
                        Tab tab = tabManager.tabs.get(i);

                        if (tab instanceof SimpleBlockTab) {
                            if (matchingBlockPositions.contains(((SimpleBlockTab) tab).blockPos)) {
                                tabOpened = tab;
                                break;
                            }
                        }
                    }
                }
            }

            if (tabOpened != null) {
                tabManager.onOpenTab(tabOpened);
            }
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    protected void drawBackgroundTabs(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (InventoryTabsClient.screenSupported(this)) {
            if (!screenDoesDumbBlock()) {
                MinecraftClient client = MinecraftClient.getInstance();
                TabManager tabManager = ((TabManagerContainer) client).getTabManager();

                tabManager.tabRenderer.renderBackground(context);
            }
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    protected void drawForegroundTabs(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (InventoryTabsClient.screenSupported(this)) {
            MinecraftClient client = MinecraftClient.getInstance();
            TabManager tabManager = ((TabManagerContainer) client).getTabManager();

            tabManager.tabRenderer.renderForeground(context, mouseX, mouseY);
            tabManager.tabRenderer.renderHoverTooltips(context, mouseX, mouseY);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> callbackInfo) {
        if (InventoryTabsClient.screenSupported(this)) {
            TabManager tabManager = ((TabManagerContainer) MinecraftClient.getInstance()).getTabManager();

            if (tabManager.mouseClicked(mouseX, mouseY, button)) {
                callbackInfo.setReturnValue(true);
            }
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> callbackInfo) {
        if (InventoryTabsClient.screenSupported(this)) {
            TabManager tabManager = ((TabManagerContainer) MinecraftClient.getInstance()).getTabManager();

            if (tabManager.keyPressed(keyCode, scanCode, modifiers)) {
                callbackInfo.setReturnValue(true);
            }
        }
    }

    @Override
    public int getTopRowXOffset() {
        if (!isBRBLoaded) {
            HandledScreen<?> screen = (HandledScreen<?>) (Object) this;
            if (screen instanceof InventoryScreen) {
                if (((InventoryScreen) screen).getRecipeBookWidget().isOpen()) {
                    return 77;
                }
            } else if (screen instanceof AbstractFurnaceScreen) {
                if (((AbstractFurnaceScreen<?>) screen).recipeBook.isOpen()) {
                    return 77;
                }
            } else if (screen instanceof CraftingScreen) {
                if (((CraftingScreen) screen).getRecipeBookWidget().isOpen()) {
                    return 77;
                }
            }
        }
        return 0;
    }

    @Override
    public int getBottomRowXOffset() {
        return getTopRowXOffset();
    }

    @Override
    public int getBottomRowYOffset() {
        return screenNeedsOffset() ? -1 : 0;
    }
    
    private boolean screenDoesDumbBlock() {
        HandledScreen<?> screen = (HandledScreen<?>) (Object) this;

        return screen instanceof CartographyTableScreen || screen instanceof LoomScreen
                || screen instanceof StonecutterScreen;
    }

    private boolean screenNeedsOffset() {
        HandledScreen<?> screen = (HandledScreen<?>) (Object) this;

        return screen instanceof ShulkerBoxScreen || screen instanceof GenericContainerScreen;
    }
}
