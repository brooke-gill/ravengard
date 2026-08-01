package dev.brookie.ravengard.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import dev.brookie.ravengard.client.mixin.AbstractContainerScreenAccessor;

public final class FavouriteToggle {
	private FavouriteToggle() {
	}

	public static void toggleHovered() {
		Minecraft client = Minecraft.getInstance();
		if (!(client.gui.screen() instanceof AbstractContainerScreen<?> screen)) {
			return;
		}

		Slot hovered = ((AbstractContainerScreenAccessor) screen).ravengard$getHoveredSlot();
		if (hovered == null || !hovered.hasItem()) {
			chat(client, "hover over an item");
			return;
		}

		ItemStack stack = hovered.getItem();
		boolean nowFavorite = ItemFavorites.toggle(stack);
		String name = stack.getHoverName().getString();
		if (nowFavorite) {
			chat(client, "favourited: " + name);
		} else {
			chat(client, "unfavourited: " + name);
		}
	}

	private static void chat(Minecraft client, String message) {
		if (client.player != null) {
			client.player.sendSystemMessage(Component.literal(message));
		}
	}
}
