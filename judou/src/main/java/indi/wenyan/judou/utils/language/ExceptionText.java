package indi.wenyan.judou.utils.language;

public enum ExceptionText implements JudouLocalizationEnum {
    VariableNotFound("variable not found %s"),
    FunctionNotFound("error.wenyan_programming.function_not_found_ %s"),
    StackEmpty("error.wenyan_programming.stack_empty"),
    StackIndexOutOfBounds("error.wenyan_programming.stack_index_out_of_bounds"),
    RecursionDepthTooDeep("递归深度过深"),
    SetValueToNonLeftValue("error.wenyan_programming.set_value_to_non_left_value"),
    InvalidDataType("error.wenyan_programming.invalid_data_type"),
    NumberOfArgumentsDoesNotMatch("error.wenyan_programming.number_of_arguments_does_not_match"),
    FunctionDoesNotHaveReferences("error.wenyan_programming.function_does_not_have_references"),
    UnknownPackageAttribute("Unknown package attribute: %s"),
    CannotCreateObjectFromPackage("Cannot create an object from a package"),
    IntegerOverflow("Integer overflow"),
    DivisionByZero("Division by zero"),
    ModuloByZero("Modulo by zero"),
    ParameterError("参数错误"),
    StringSubtractNotSupported(""),
    StringMultiplyNotSupported(""),
    StringDivideNotSupported(""),
    ;

    // STUB: the string of old system
    @Deprecated(forRemoval = true)
    final String originText;

    ExceptionText(String originText) {
        this.originText = originText;
    }

    @Override
    public String getTranslationKey() {
        return name();
    }
}
