package indi.wenyan.judou.api.language;

/// Translation keys for Wenyan type names.
public enum JudouTypeText implements JudouLocalizationEnum {
    JavacallHandler,
    Comparable,
    Function,
    Number,
    Object,
    ObjectType,
    Null,
    Package,
    BuiltinAsyncFunction,
    BuiltinFunction,
    BuiltinFuture,
    DictObject,
    DictObjectType,
    Bool,
    Double,
    Int,
    List,
    Iterator,
    String,
    Computable, CodeExecutor;

    @Override
    public String getTranslationKey() {
        return "type.wenyan_programming.judou." + name();
    }
}
