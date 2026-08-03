package dev.brookie.ravengard.client;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

// use crown glyph from resource pack
public final class CrownLore {
	private static final Pattern CROWN_PATTERN = Pattern.compile(
			"(?:👑\\s*)?([\\d,]+)\\s*Crowns?",
			Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private CrownLore() {
	}

	public static OptionalInt parse(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return OptionalInt.empty();
		}

		for (String line : loreStrings(stack)) {
			OptionalInt value = parseLine(line);
			if (value.isPresent()) {
				return value;
			}
		}

		return OptionalInt.empty();
	}

	// no lore means $0
	public static int valueOrZero(ItemStack stack) {
		return parse(stack).orElse(0);
	}

	public static List<String> loreStrings(ItemStack stack) {
		List<String> lines = new ArrayList<>();
		ItemLore lore = stack.get(DataComponents.LORE);
		if (lore != null) {
			for (Component component : lore.lines()) {
				lines.add(component.getString());
			}
		}
		return lines;
	}

	public static OptionalInt parseLine(String line) {
		if (line == null || line.isEmpty()) {
			return OptionalInt.empty();
		}

		String cleaned = stripFormatting(line).trim();
		Matcher matcher = CROWN_PATTERN.matcher(cleaned);
		if (!matcher.find()) {
			return OptionalInt.empty();
		}

		try {
			int value = Integer.parseInt(matcher.group(1).replace(",", ""));
			return OptionalInt.of(value);
		} catch (NumberFormatException e) {
			return OptionalInt.empty();
		}
	}

	static String stripFormatting(String text) {
		StringBuilder out = new StringBuilder(text.length());
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '§' && i + 1 < text.length()) {
				i++;
				continue;
			}
			out.append(c);
		}
		return out.toString();
	}
}
