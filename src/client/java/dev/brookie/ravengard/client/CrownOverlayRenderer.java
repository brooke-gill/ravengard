package dev.brookie.ravengard.client;

import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import dev.brookie.ravengard.client.mixin.AbstractContainerScreenAccessor;

public final class CrownOverlayRenderer {
	private static final int CROWN_TEXT_COLOR = 0xFFFFD700;
	private static final int FAVORITE_MARK_COLOR = 0xFFFF66AA;
	private static final int RED_HIGHLIGHT = 0x80FF0000;
	private static final int TEAL_HIGHLIGHT = 0x8000B8A0;
	private static final float TEXT_SCALE = 0.7F;

	private CrownOverlayRenderer() {
	}

	public static void drawItemCrown(GuiGraphicsExtractor graphics, Font font, ItemStack stack, int x, int y) {
		OptionalInt crowns = CrownLore.parse(stack);
		if (crowns.isEmpty()) {
			return;
		}

		String text = Integer.toString(crowns.getAsInt());
		graphics.pose().pushMatrix();
		graphics.pose().translate(x, y);
		graphics.pose().scale(TEXT_SCALE, TEXT_SCALE);
		graphics.text(font, text, 1, 1, CROWN_TEXT_COLOR, true);
		graphics.pose().popMatrix();
	}

	public static void drawFavoriteMark(GuiGraphicsExtractor graphics, Font font, ItemStack stack, int x, int y) {
		if (!ItemFavorites.isFavorite(stack)) {
			return;
		}

		graphics.text(font, "*", x + 10, y + 9, FAVORITE_MARK_COLOR, true);
	}

	public static void drawSwapHighlights(AbstractContainerScreen<?> screen, GuiGraphicsExtractor graphics) {
		if (!SwapAdvisor.hasExternalSlots(screen.getMenu())) {
			return;
		}

		List<SwapAdvisor.Swap> swaps = SwapAdvisor.findProfitableSwaps(screen.getMenu());
		if (swaps.isEmpty()) {
			return;
		}

		Set<Slot> red = new HashSet<>();
		Set<Slot> teal = new HashSet<>();
		for (SwapAdvisor.Swap swap : swaps) {
			Slot inv = swap.inventorySlot();
			
			if (inv != null && inv.hasItem()) {
				red.add(inv);
			}
			teal.add(swap.containerSlot());
		}

		AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) screen;
		int left = accessor.ravengard$getLeftPos();
		int top = accessor.ravengard$getTopPos();

		for (Slot slot : red) {
			fillSlot(graphics, left, top, slot, RED_HIGHLIGHT);
		}
		for (Slot slot : teal) {
			fillSlot(graphics, left, top, slot, TEAL_HIGHLIGHT);
		}
	}

	private static void fillSlot(GuiGraphicsExtractor graphics, int left, int top, Slot slot, int color) {
		int x0 = left + slot.x;
		int y0 = top + slot.y;
		graphics.fill(x0, y0, x0 + 16, y0 + 16, color);
	}
}
