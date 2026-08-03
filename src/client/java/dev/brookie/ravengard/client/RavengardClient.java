package dev.brookie.ravengard.client;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ExtractItemDecorationsCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;

import dev.brookie.ravengard.RavengardMod;

// overlay
public final class RavengardClient implements ClientModInitializer {
	private static KeyMapping favouriteKey;

	@Override
	public void onInitializeClient() {
		ItemFavorites.ensureLoaded();

		favouriteKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.ravengard.favourite",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_K,
				KeyMapping.Category.MISC));

		ExtractItemDecorationsCallback.EVENT.register((graphics, font, stack, x, y) -> {
			Minecraft client = Minecraft.getInstance();
			if (!(client.gui.screen() instanceof AbstractContainerScreen<?> screen)) {
				return;
			}
			// include dragged part
			if (screen.getMenu().getCarried() != stack) {
				return;
			}
			CrownOverlayRenderer.drawCarriedItemMarkers(graphics, font, stack, x, y);
		});

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (!(screen instanceof AbstractContainerScreen<?>)) {
				return;
			}

			ScreenKeyboardEvents.allowKeyPress(screen).register((scr, keyEvent) -> {
				if (!favouriteKey.matches(keyEvent)) {
					return true;
				}
				FavouriteToggle.toggleHovered();
				return false;
			});
		});

		RavengardMod.LOGGER.info("Ravengard Crown Overlay initialized");
	}
}
