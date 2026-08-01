package dev.brookie.ravengard.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import dev.brookie.ravengard.client.CrownOverlayRenderer;

// this is boring
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
	@Inject(method = "extractContents", at = @At("RETURN"))
	private void ravengard$drawSwapHighlights(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickProgress, CallbackInfo ci) {
		CrownOverlayRenderer.drawSwapHighlights((AbstractContainerScreen<?>) (Object) this, graphics);
	}
}
