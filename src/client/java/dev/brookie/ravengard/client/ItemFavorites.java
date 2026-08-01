package dev.brookie.ravengard.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import dev.brookie.ravengard.RavengardMod;

// save favourites to disk
public final class ItemFavorites {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Set<String> FAVORITES = new LinkedHashSet<>();
	private static boolean loaded;

	private ItemFavorites() {
	}

	public static void ensureLoaded() {
		if (loaded) {
			return;
		}
		loaded = true;
		Path path = configPath();
		if (path == null || !Files.isRegularFile(path)) {
			return;
		}
		try {
			String json = Files.readString(path, StandardCharsets.UTF_8);
			JsonObject root = GSON.fromJson(json, JsonObject.class);
			if (root == null || !root.has("favorites")) {
				return;
			}
			JsonArray array = root.getAsJsonArray("favorites");
			FAVORITES.clear();
			for (JsonElement element : array) {
				FAVORITES.add(element.getAsString());
			}
			RavengardMod.LOGGER.info("Loaded {} favourite item(s)", FAVORITES.size());
		} catch (Exception e) {
			RavengardMod.LOGGER.warn("Failed to load favourites", e);
		}
	}

	public static boolean isFavorite(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return false;
		}
		ensureLoaded();
		return FAVORITES.contains(fingerprint(stack));
	}

	public static boolean toggle(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return false;
		}
		ensureLoaded();
		String id = fingerprint(stack);
		boolean nowFavorite;
		if (FAVORITES.contains(id)) {
			FAVORITES.remove(id);
			nowFavorite = false;
		} else {
			FAVORITES.add(id);
			nowFavorite = true;
		}
		save();
		return nowFavorite;
	}

	public static String fingerprint(ItemStack stack) {
		String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
		String name = stack.getHoverName().getString();
		List<String> lore = CrownLore.loreStrings(stack);
		StringBuilder raw = new StringBuilder();
		raw.append(itemId).append('\n').append(name);
		for (String line : lore) {
			raw.append('\n').append(line);
		}
		return sha256(raw.toString());
	}

	private static void save() {
		Path path = configPath();
		if (path == null) {
			return;
		}
		try {
			Files.createDirectories(path.getParent());
			JsonObject root = new JsonObject();
			JsonArray array = new JsonArray();
			for (String id : FAVORITES) {
				array.add(id);
			}
			root.add("favorites", array);
			Files.writeString(path, GSON.toJson(root), StandardCharsets.UTF_8);
		} catch (IOException e) {
			RavengardMod.LOGGER.warn("Failed to save favourites", e);
		}
	}

	private static Path configPath() {
		Minecraft client = Minecraft.getInstance();
		if (client == null || client.gameDirectory == null) {
			return null;
		}
		return client.gameDirectory.toPath().resolve("config").resolve("ravengard").resolve("favorites.json");
	}

	private static String sha256(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}
}
