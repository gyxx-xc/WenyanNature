package indi.wenyan.content.block.crafting_block;

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
    Vec3 oPos;
    Vec3 pos;
    int remainTick = 20;

    public TextEffect(Vec3 pos, float rot, String data) {
        this.oPos = pos;
        this.pos = pos;
        this.rot = rot;
        this.data = data;
    }

    public Vec3 getPosition(float partialTicks) {
        return oPos.lerp(pos, partialTicks);
    }

    public void setPos(Vec3 pos) {
        this.oPos = this.pos;
        this.pos = pos;
    }

    public static List<TextEffect> randomSplash(String data, RandomSource random) {
        final int range = 1;
        List<TextEffect> particles = new ArrayList<>();
        for (var c : data.toCharArray()) {
            Vec3 pos = new Vec3(random.triangle(0, range), random.triangle(0, range), random.triangle(0, range));
            particles.add(new TextEffect(pos, random.nextFloat() * 360, String.valueOf(c)));
        }
        return particles;
    }
}
