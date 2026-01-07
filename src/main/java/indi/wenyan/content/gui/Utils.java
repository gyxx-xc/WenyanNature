package indi.wenyan.content.gui;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum Utils {;
    public record BoxInformation(int top, int left, int bottom, int right) {
        public int horizontal() {
            return left + right;
        }

        public int vertical() {
            return top + bottom;
        }
    }

}
