package indi.wenyan.content.data;

import com.mojang.serialization.Codec;
import lombok.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ProgramCodeData(@NonNull String code) {
    public static final Codec<ProgramCodeData> CODEC = Codec.STRING.xmap(
            ProgramCodeData::new,
            ProgramCodeData::code
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ProgramCodeData> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ProgramCodeData::code, ProgramCodeData::new);
}
