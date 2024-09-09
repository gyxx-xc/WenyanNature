package indi.wenyan.network;

import indi.wenyan.WenyanNature;
import indi.wenyan.item.WenyanHandRunner;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ProgramTextServerPayloadHandler {
    public static void handleRunnerTextPacket(final RunnerTextPacket packet, final IPayloadContext context) {
        Player player = context.player();
        WenyanHandRunner runner = ((WenyanHandRunner)player.getInventory().items.get(packet.slot()).getItem());
        runner.pages = packet.pages();
        runner.test();
    }
}
