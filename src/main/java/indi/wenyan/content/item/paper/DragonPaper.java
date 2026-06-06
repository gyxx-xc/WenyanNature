package indi.wenyan.content.item.paper;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSongs;

public class DragonPaper extends Item {
    public static final String ID =  "dragon_paper";

    public DragonPaper(Item.Properties properties) {
        super(properties
                .jukeboxPlayable(JukeboxSongs.LAVA_CHICKEN)
        );
    }
}
