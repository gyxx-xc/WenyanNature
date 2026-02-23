package indi.wenyan.setup.network;

import indi.wenyan.WenyanProgramming;
import indi.wenyan.setup.definitions.WyRegistration;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

public record FloatNotePacket(String name, boolean locked) implements CustomPacketPayload {
    public static final Type<FloatNotePacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, "float_note_rename"));

    public static final StreamCodec<ByteBuf, FloatNotePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            FloatNotePacket::name,
            ByteBufCodecs.BOOL,
            FloatNotePacket::locked,
            FloatNotePacket::new
    );

    public static final IPayloadHandler<FloatNotePacket> HANDLER = (packet, context) -> {
        if (context.flow().isServerbound()) {
            ItemStack stack = context.player().getItemInHand(InteractionHand.MAIN_HAND);
            stack.set(DataComponents.CUSTOM_NAME, Component.literal(packet.name()));
            stack.set(WyRegistration.NOTE_LOCK_DATA.get(), packet.locked());
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
