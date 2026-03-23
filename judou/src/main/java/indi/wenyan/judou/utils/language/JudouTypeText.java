package indi.wenyan.judou.utils.language;

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
    ;

    @Override
    public String getTranslationKey() {
        return "type.wenyan_programming.judou." + name();
    }
}
