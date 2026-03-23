package indi.wenyan.setup.network.server;

import indi.wenyan.setup.definitions.WyRegistration;
import indi.wenyan.setup.network.IServerboundPacket;
import indi.wenyan.setup.network.IWenyanPacketPayload;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record FloatNotePacket(String name, boolean locked) implements IServerboundPacket {
    public static final Type<FloatNotePacket> TYPE =
            IWenyanPacketPayload.createType("float_note_rename");

    public static final StreamCodec<RegistryFriendlyByteBuf, FloatNotePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            FloatNotePacket::name,
            ByteBufCodecs.BOOL,
            FloatNotePacket::locked,
            FloatNotePacket::new
    );

    @Override
    public void handleOnServer(ServerPlayer player) {
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(name()));
        stack.set(WyRegistration.NOTE_LOCK_DATA.get(), locked());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
