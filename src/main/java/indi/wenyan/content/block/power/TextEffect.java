package indi.wenyan.content.block.power;

import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(fluent = true)
public class TextEffect {
    final float rot;
    final String data;
    Vec3 pos;
    int remainTick = 20;

    public TextEffect(Vec3 pos, float rot, String data) {
        this.pos = pos;
        this.rot = rot;
        this.data = data;
    }

    public static List<TextEffect> randomSplash(String data, RandomSource random) {
        final float range = 1.3f;
        List<TextEffect> particles = new ArrayList<>();
        for (var c : data.toCharArray()) {
            Vec3 pos = new Vec3(random.triangle(0, range), random.triangle(0, range), random.triangle(0, range));
            particles.add(new TextEffect(pos, random.nextFloat() * 360, String.valueOf(c)));
        }
        return particles;
    }

    public Vec3 getPosition(float partialTick) {
        float time = 20 - remainTick + partialTick;
        return pos.scale(15 / (time + 15));
    }
}
