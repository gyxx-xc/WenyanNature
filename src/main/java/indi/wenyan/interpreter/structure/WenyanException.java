package indi.wenyan.interpreter.structure;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.antlr.v4.runtime.ParserRuleContext;

public class WenyanException extends RuntimeException {
    public WenyanException(String message) {
        super(message);
    }

    public WenyanException(String message, ParserRuleContext ctx) {
        super(ctx.getStart().getLine()+":"+ctx.getStart().getCharPositionInLine()+" "+ctx.getText()+"\n"+message);
    }

    public WenyanException(WenyanThrowException e, ParserRuleContext ctx) {
        super(ctx.getStart().getLine()+":"+ctx.getStart().getCharPositionInLine()+" "+ctx.getText()+"\n"+e.getMessage());
    }

    public static class WenyanNumberException extends WenyanThrowException {
        public WenyanNumberException(String message) {
            super(message);
        }
    }

    public static class WenyanDataException extends WenyanThrowException {
        public WenyanDataException(String message) {
            super(message);
        }
    }

    public static class WenyanVarException extends WenyanThrowException {
        public WenyanVarException(String message) {
            super(message);
        }
    }

    public static class WenyanTypeException extends WenyanThrowException {
        public WenyanTypeException(String message) {
            super(message);
        }
    }

    public static abstract class WenyanThrowException extends Exception {
        public WenyanThrowException(String message) {
            super(message);
        }
    }

    public static void handleException(Player player, String message) {
        System.out.println(message);
        player.displayClientMessage(Component.literal(message).withStyle(ChatFormatting.RED), true);
    }
}
