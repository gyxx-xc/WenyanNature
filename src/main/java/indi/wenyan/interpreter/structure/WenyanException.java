package indi.wenyan.interpreter.structure;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.interpreter.utils.WenyanThreading;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Base exception class for Wenyan interpreter errors
 */
public class WenyanException extends WenyanThrowException {
    public WenyanException(String message) {
        super(message);
    }

    public WenyanException(String message, ParserRuleContext ctx) {
        super(ctx.getStart().getLine() + ":" + ctx.getStart().getCharPositionInLine() + " " + ctx.getText() + "\n" + message);
    }

    public WenyanException(WenyanThrowException e, ParserRuleContext ctx) {
        super(ctx.getStart().getLine() + ":" + ctx.getStart().getCharPositionInLine() + " " + ctx.getText() + "\n" + e.getMessage());
    }

    public static class WenyanUnreachedException extends WenyanException {
        public WenyanUnreachedException() {
            super("unreached, please report an issue");
        }
    }

    /**
     * Exception for numerical errors
     */
    public static class WenyanNumberException extends WenyanThrowException {
        public WenyanNumberException(String message) {
            super(message);
        }
    }

    /**
     * Exception for data handling errors
     */
    public static class WenyanDataException extends WenyanThrowException {
        public WenyanDataException(String message) {
            super(message);
        }
    }

    /**
     * Exception for variable errors
     */
    public static class WenyanVarException extends WenyanThrowException {
        public WenyanVarException(String message) {
            super(message);
        }
    }

    /**
     * Exception for type errors
     */
    public static class WenyanTypeException extends WenyanThrowException {
        public WenyanTypeException(String message) {
            super(message);
        }
    }

    /**
     * Exception for code validation errors
     */
    public static class WenyanCheckerError extends WenyanThrowException {
        public WenyanCheckerError(String message) {
            super(message);
        }
    }

    /**
     * Handles an exception by displaying it to the player
     *
     * @param player  Player to notify
     * @param message Error message
     */
    @WenyanThreading
    public static void handleException(Player player, String message) {
        WenyanProgramming.LOGGER.error(message);
        // TODO: thread safety check
        player.displayClientMessage(Component.literal(message).withStyle(ChatFormatting.RED), true);
    }
}
