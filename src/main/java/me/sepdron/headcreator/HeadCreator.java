package me.sepdron.headcreator;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R1.profile.CraftPlayerProfile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.util.UUID;

public final class HeadCreator {

    public static ItemStack createFromBase64(String b64) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        assert meta != null;
        meta.setOwnerProfile(createProfile(b64));
        skull.setItemMeta(meta);
        return skull;
    }

    private static PlayerProfile createProfile(String b64) {
        GameProfile gameProfile = createGameProfile(b64);
        return new CraftPlayerProfile(gameProfile);
    }

    // Taken from SkullCreator
    private static GameProfile createGameProfile(String b64) {
        // random uuid based on the b64 string
        UUID id = new UUID(
                b64.substring(b64.length() - 20).hashCode(),
                b64.substring(b64.length() - 10).hashCode()
        );
        GameProfile profile = new GameProfile(id, "Player");
        profile.getProperties().put("textures", new Property("textures", b64));
        return profile;
    }
}
