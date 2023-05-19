package com.kqp.inventorytabs.mixin;

import java.util.Objects;

import com.kqp.inventorytabs.init.InventoryTabsClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

/**
 * The 'Tab' keybinding conflicts with the multiplayer player list keybind, but since you can only see the player list when outside the inventory
 * anyways, the conflict can be soft and not hard.
 */
@Mixin(ControlsListWidget.KeyBindingEntry.class)
public class ControlsListWidget$KeyBindingEntryMixin_SoftConflict {
	@Shadow @Final private KeyBinding binding;
	
	@ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/MutableText;formatted(Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/MutableText;"))
	public Formatting setBracketsColor(Formatting color) {
		if (this.binding == InventoryTabsClient.NEXT_TAB_KEY_BIND && color.equals(Formatting.RED)) {
			return Formatting.GOLD;
		}

		return color;
	}

	@ModifyArg(index = 4, method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"))
	public int setIndicatorColor(int color) {
		if (this.binding == InventoryTabsClient.NEXT_TAB_KEY_BIND) {
			return Formatting.GOLD.getColorValue() | 0xFF000000;
		}

		return color;
	}
}
