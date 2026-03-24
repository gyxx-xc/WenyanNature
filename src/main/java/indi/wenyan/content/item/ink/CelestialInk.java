package indi.wenyan.content.item.ink;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSongs;

public class CelestialInk extends Item {
    public static final String ID = "celestial_ink";

    public CelestialInk(Properties properties) {
        super(properties
                .jukeboxPlayable(JukeboxSongs.CAT)
        );
    }
}
