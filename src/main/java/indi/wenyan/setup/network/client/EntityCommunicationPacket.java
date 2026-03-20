package indi.wenyan.setup.network.client;

import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record EntityCommunicationPacket() {

    public static final IPayloadHandler<CommunicationLocationPacket> HANDLER = (packet, context) -> {
//        context.e
    };

}
