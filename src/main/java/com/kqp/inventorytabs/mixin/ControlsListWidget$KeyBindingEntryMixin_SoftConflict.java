package com.kqp.inventorytabs.mixin;

import java.util.Objects;

import com.kqp.inventorytabs.init.InventoryTabsClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.client.gui.screen.options.ControlsListWidget;
import net.minecraft.client.options.KeyBinding;
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
	
	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;setMessage(Lnet/minecraft/text/Text;)V"))
	public Text setMessage(Text text) {
		TextColor c = text.getStyle().getColor();
		if(c != null && c.getRgb() == Objects.requireNonNull(Formatting.RED.getColorValue())) {
			if(this.binding == InventoryTabsClient.NEXT_TAB_KEY_BIND) {
				text = text.copy().formatted(Formatting.GOLD);
			}
		}
		return text;
	}
}
