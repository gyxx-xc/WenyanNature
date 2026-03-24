package indi.wenyan.judou.structure;

import indi.wenyan.judou.structure.values.*;
import indi.wenyan.judou.structure.values.primitive.WenyanBoolean;
import indi.wenyan.judou.structure.values.primitive.WenyanList;
import indi.wenyan.judou.structure.values.primitive.WenyanString;
import lombok.Getter;

public enum ParsableType {
    NUMBER(IWenyanNumber.TYPE),
    STRING(WenyanString.TYPE),
    BOOLEAN(WenyanBoolean.TYPE),
    NULL(WenyanNull.TYPE),
    OBJECT(IWenyanObject.TYPE),
    FUNCTION(IWenyanFunction.TYPE),
    LIST(WenyanList.TYPE),
    OBJECT_TYPE(IWenyanObjectType.TYPE);

    @Getter
    private final WenyanType<?> type;

    ParsableType(WenyanType<?> type) {
        this.type = type;
    }
}
