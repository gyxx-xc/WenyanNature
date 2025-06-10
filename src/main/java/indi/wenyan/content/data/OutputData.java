package indi.wenyan.content.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record OutputData(String output) {
    public static final Codec<OutputData> CODEC = Codec.STRING.xmap(
            OutputData::new,
            OutputData::output
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, OutputData> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8, OutputData::output, OutputData::new);
}
