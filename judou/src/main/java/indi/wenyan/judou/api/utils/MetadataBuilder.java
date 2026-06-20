package indi.wenyan.judou.api.utils;

import indi.wenyan.judou.api.exec.structure.WenyanMetadata;
import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class MetadataBuilder<T> implements IMetadataBuilder<T> {
    @Getter private final Map<String, WenyanMetadata> metadata = new HashMap<>();
    private MutableWenyanMetadata pendingMetadata = new MutableWenyanMetadata();

    private final T parentBuilder;

    public MetadataBuilder(T parentBuilder) {
        this.parentBuilder = parentBuilder;
    }

    @Override
    public T meta(WenyanMetadata metadata) {
        pendingMetadata.fromImmutable(metadata);
        return parentBuilder;
    }

    @Override
    public T description(String description) {
        pendingMetadata.description = description;
        return parentBuilder;
    }

    public void withCurrentMetadata(String name) {
        if (pendingMetadata.getDescription() != null) {
            getMetadata().put(name, pendingMetadata.toImmutable());
            pendingMetadata = new MutableWenyanMetadata();
        }
    }

    @Data
    private static class MutableWenyanMetadata {
        String description;

        @Nullable WenyanMetadata toImmutable() {
            if (description == null) return null;
            return new WenyanMetadata(description);
        }

        void fromImmutable(WenyanMetadata metadata) {
            this.description = metadata.description();
        }
    }
}