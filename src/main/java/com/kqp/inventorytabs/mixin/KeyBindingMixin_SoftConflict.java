package com.kqp.inventorytabs.mixin;

import java.util.Map;
import java.util.Objects;

import com.kqp.inventorytabs.init.InventoryTabsClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

@Mixin(KeyBinding.class)
public class KeyBindingMixin_SoftConflict {
	@Shadow @Final private static Map<String, KeyBinding> KEYS_BY_ID;
	
	@Shadow private InputUtil.Key boundKey;
	
	@Shadow private int timesPressed;
	
	@Inject(method = "onKeyPressed", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/KeyBinding;timesPressed:I"),
			locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci, KeyBinding binding) {
		Screen screen = MinecraftClient.getInstance().currentScreen;
		if(binding == InventoryTabsClient.NEXT_TAB_KEY_BIND && !InventoryTabsClient.screenSupported(screen)) {
			for(KeyBinding value : KEYS_BY_ID.values()) {
				KeyBindingMixin_SoftConflict self = (KeyBindingMixin_SoftConflict) (Object) value;
				InputUtil.Key bound = self.boundKey;
				if(Objects.equals(bound, key)) {
					self.timesPressed++;
					ci.cancel();
					return;
				}
			}
		}
	}
}
