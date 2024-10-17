package me.sepdron.headcreator;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public final class HeadCreator {

    private static Constructor<? extends PlayerProfile> playerProfileConstructor;

    public static ItemStack createFromBase64(String b64) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        setBase64Skin(skull, b64);
        return skull;
    }

    public static void setBase64Skin(SkullMeta meta, String b64) {
        meta.setOwnerProfile(createProfile(b64));
    }
    public static void setBase64Skin(ItemStack item, String b64) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof SkullMeta skullMeta)) throw new IllegalArgumentException("Item does not have a skull meta");
        setBase64Skin(skullMeta, b64);
        item.setItemMeta(skullMeta);
    }

    private static void createProfileConstructor(String b64) {
        Server server = Bukkit.getServer();
        UUID id = createUUID(b64);
        PlayerProfile playerProfile = server.createPlayerProfile(id);
        try {
            playerProfileConstructor = playerProfile.getClass()
                    .getDeclaredConstructor(GameProfile.class);
            playerProfileConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static PlayerProfile createProfile(String b64) {
        if (playerProfileConstructor == null) createProfileConstructor(b64);
        GameProfile gameProfile = createGameProfile(b64);
        try {
            return playerProfileConstructor.newInstance(gameProfile);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    // Taken from SkullCreator
    private static GameProfile createGameProfile(String b64) {
        // random uuid based on the b64 string
        UUID id = createUUID(b64);
        GameProfile profile = new GameProfile(id, "Player");
        profile.getProperties().put("textures", new Property("textures", b64));
        return profile;
    }

    private static UUID createUUID(String b64) {
        return new UUID(
                b64.substring(b64.length() - 20).hashCode(),
                b64.substring(b64.length() - 10).hashCode()
        );
    }
}
