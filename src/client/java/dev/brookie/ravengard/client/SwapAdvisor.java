package dev.brookie.ravengard.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

// greedy
public final class SwapAdvisor {
	public record Swap(Slot inventorySlot, Slot containerSlot, int inventoryValue, int containerValue) {
	}

	public record ValuedSlot(Slot slot, int value) {
	}

	private SwapAdvisor() {
	}

	public static boolean hasExternalSlots(AbstractContainerMenu menu) {
		for (Slot slot : menu.slots) {
			if (!(slot.container instanceof Inventory)) {
				return true;
			}
		}
		return false;
	}

	public static List<Swap> findProfitableSwaps(AbstractContainerMenu menu) {
		List<ValuedSlot> inventory = new ArrayList<>();
		List<ValuedSlot> container = new ArrayList<>();
		List<Slot> emptyStorage = new ArrayList<>();
		int occupiedStorage = 0;

		for (Slot slot : menu.slots) {
			if (slot.container instanceof Inventory) {
				int containerSlot = slot.getContainerSlot();
				// don't check armour
				if (containerSlot < 0 || containerSlot >= Inventory.INVENTORY_SIZE) {
					continue;
				}
				if (!slot.hasItem()) {
					emptyStorage.add(slot);
					continue;
				}
				occupiedStorage++;
				ItemStack stack = slot.getItem();
				// ignore favourites
				if (ItemFavorites.isFavorite(stack)) {
					continue;
				}
				inventory.add(new ValuedSlot(slot, CrownLore.valueOrZero(stack)));
			} else if (slot.hasItem()) {
				container.add(new ValuedSlot(slot, CrownLore.valueOrZero(slot.getItem())));
			}
		}

		// and don't forget the pesky cursor
		ItemStack carried = menu.getCarried();
		if (!carried.isEmpty()) {
			occupiedStorage++;
			if (!ItemFavorites.isFavorite(carried)) {
				inventory.add(new ValuedSlot(null, CrownLore.valueOrZero(carried)));
			}
		}

		// air worth nothing
		int free = Inventory.INVENTORY_SIZE - occupiedStorage;
		for (int i = 0; i < free && i < emptyStorage.size(); i++) {
			inventory.add(new ValuedSlot(emptyStorage.get(i), 0));
		}

		inventory.sort(Comparator.comparingInt(ValuedSlot::value));
		container.sort(Comparator.comparingInt(ValuedSlot::value).reversed());

		List<Swap> swaps = new ArrayList<>();
		int limit = Math.min(inventory.size(), container.size());
		for (int i = 0; i < limit; i++) {
			ValuedSlot inv = inventory.get(i);
			ValuedSlot chest = container.get(i);
			if (chest.value() <= inv.value()) {
				break;
			}
			swaps.add(new Swap(inv.slot(), chest.slot(), inv.value(), chest.value()));
		}
		return swaps;
	}
}
