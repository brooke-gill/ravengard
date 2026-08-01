package dev.brookie.ravengard.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;

import org.jspecify.annotations.Nullable;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
	@Accessor("leftPos")
	int ravengard$getLeftPos();

	@Accessor("topPos")
	int ravengard$getTopPos();

	@Accessor("hoveredSlot")
	@Nullable Slot ravengard$getHoveredSlot();
}
