package dev.brookie.ravengard.client;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import dev.brookie.ravengard.client.mixin.AbstractContainerScreenAccessor;

public final class CrownOverlayRenderer {
	private static final int CROWN_TEXT_COLOR = 0xFFFFD700;
	private static final int FAVORITE_MARK_COLOR = 0xFFFF66AA;
	private static final int RED_HIGHLIGHT = 0x80FF0000;
	private static final int TEAL_HIGHLIGHT = 0x8000B8A0;
	private static final float TEXT_SCALE = 0.7F;
	private static final int SLOT_SIZE = 16;

	private CrownOverlayRenderer() {
	}

	// might not work on 26.2 since they changed graphics stuff - look into later
	public static void drawSlotOverlays(AbstractContainerScreen<?> screen, GuiGraphicsExtractor graphics) {
		AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) screen;
		int left = accessor.ravengard$getLeftPos();
		int top = accessor.ravengard$getTopPos();
		List<Slot> slots = screen.getMenu().slots;

		drawSwapHighlights(graphics, screen, left, top);
		drawRarityBorders(graphics, slots, left, top);
		drawItemMarkers(graphics, screen.getFont(), slots, left, top);
	}

	private static void drawRarityBorders(GuiGraphicsExtractor graphics, List<Slot> slots, int left, int top) {
		for (Slot slot : slots) {
			if (!slot.isActive() || !slot.hasItem()) {
				continue;
			}

			Optional<ItemRarity> rarity = ItemRarity.parse(slot.getItem());
			if (rarity.isEmpty()) {
				continue;
			}

			graphics.blit(
					RenderPipelines.GUI_TEXTURED,
					rarity.get().texture(),
					left + slot.x,
					top + slot.y,
					0.0F,
					0.0F,
					SLOT_SIZE,
					SLOT_SIZE,
					SLOT_SIZE,
					SLOT_SIZE);
		}
	}

	private static void drawSwapHighlights(GuiGraphicsExtractor graphics, AbstractContainerScreen<?> screen, int left, int top) {
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

		for (Slot slot : red) {
			fillSlot(graphics, left, top, slot, RED_HIGHLIGHT);
		}
		for (Slot slot : teal) {
			fillSlot(graphics, left, top, slot, TEAL_HIGHLIGHT);
		}
	}

	// dragged items (no associated slot)
	public static void drawCarriedItemMarkers(GuiGraphicsExtractor graphics, Font font, ItemStack stack, int x, int y) {
		drawItemCrown(graphics, font, stack, x, y);
		drawFavoriteMark(graphics, font, stack, x, y);
	}

	private static void drawItemMarkers(GuiGraphicsExtractor graphics, Font font, List<Slot> slots, int left, int top) {
		for (Slot slot : slots) {
			if (!slot.isActive() || !slot.hasItem()) {
				continue;
			}

			ItemStack stack = slot.getItem();
			drawItemCrown(graphics, font, stack, left + slot.x, top + slot.y);
			drawFavoriteMark(graphics, font, stack, left + slot.x, top + slot.y);
		}
	}

	private static void drawItemCrown(GuiGraphicsExtractor graphics, Font font, ItemStack stack, int x, int y) {
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

	private static void drawFavoriteMark(GuiGraphicsExtractor graphics, Font font, ItemStack stack, int x, int y) {
		if (!ItemFavorites.isFavorite(stack)) {
			return;
		}

		graphics.text(font, "*", x + 10, y + 9, FAVORITE_MARK_COLOR, true);
	}

	private static void fillSlot(GuiGraphicsExtractor graphics, int left, int top, Slot slot, int color) {
		int x0 = left + slot.x;
		int y0 = top + slot.y;
		graphics.fill(x0, y0, x0 + SLOT_SIZE, y0 + SLOT_SIZE, color);
	}
}
