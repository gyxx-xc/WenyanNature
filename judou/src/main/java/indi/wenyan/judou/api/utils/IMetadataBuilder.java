package indi.wenyan.judou.api.utils;

import indi.wenyan.judou.api.exec.structure.WenyanMetadata;

@SuppressWarnings("unused")
public sealed interface IMetadataBuilder<T> permits MetadataBuilder {
    T meta(WenyanMetadata metadata);

    T description(String description);
}
