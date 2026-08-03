package dev.brookie.ravengard.client;

import java.util.Locale;
import java.util.Optional;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import dev.brookie.ravengard.RavengardMod;

// glyphs from resource pack
public enum ItemRarity {
	UNCOMMON('\uE21C', "uncommon"),
	RARE('\uE218', "rare"),
	EPIC('\uE208', "epic"),
	LEGENDARY('\uE211', "legendary");

	private final char glyph;
	private final Identifier texture;

	ItemRarity(char glyph, String textureName) {
		this.glyph = glyph;
		this.texture = Identifier.fromNamespaceAndPath(
				RavengardMod.MOD_ID,
				"textures/gui/rarity/" + textureName + ".png");
	}

	public Identifier texture() {
		return texture;
	}

	public static Optional<ItemRarity> parse(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return Optional.empty();
		}

		ItemRarity best = null;
		best = consider(best, stack.getHoverName().getString());
		for (String line : CrownLore.loreStrings(stack)) {
			best = consider(best, line);
		}
		return Optional.ofNullable(best);
	}

	private static ItemRarity consider(ItemRarity current, String text) {
		if (text == null || text.isEmpty()) {
			return current;
		}

		ItemRarity fromGlyph = fromGlyphs(text);
		if (fromGlyph != null && isHigher(fromGlyph, current)) {
			current = fromGlyph;
		}

		ItemRarity fromWord = fromWords(text);
		if (fromWord != null && isHigher(fromWord, current)) {
			current = fromWord;
		}

		return current;
	}

	private static ItemRarity fromGlyphs(String text) {
		ItemRarity best = null;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			for (ItemRarity rarity : values()) {
				if (c == rarity.glyph && isHigher(rarity, best)) {
					best = rarity;
				}
			}
		}
		return best;
	}

	private static ItemRarity fromWords(String text) {
		String cleaned = CrownLore.stripFormatting(text).toLowerCase(Locale.ROOT);
		if (cleaned.contains("legendary")) {
			return LEGENDARY;
		}
		if (cleaned.contains("epic")) {
			return EPIC;
		}
		if (cleaned.contains("uncommon")) {
			return UNCOMMON;
		}
		if (cleaned.contains("rare")) {
			return RARE;
		}
		return null;
	}

	private static boolean isHigher(ItemRarity candidate, ItemRarity current) {
		return current == null || candidate.ordinal() > current.ordinal();
	}
}
