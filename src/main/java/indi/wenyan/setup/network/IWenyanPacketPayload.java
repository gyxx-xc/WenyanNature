package indi.wenyan.setup.network;

import indi.wenyan.WenyanProgramming;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public interface IWenyanPacketPayload extends CustomPacketPayload {
    static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> createType(String name) {
        return new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(WenyanProgramming.MODID, name));
    }
}
