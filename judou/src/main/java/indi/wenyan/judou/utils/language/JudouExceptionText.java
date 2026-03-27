package indi.wenyan.judou.utils.language;

public enum JudouExceptionText implements JudouLocalizationEnum {
    ArgsNumWrong,                       // 謬：參數數需%d得%d
    ArgsNumWrongRange,                  // 謬：參數數需%d至%d得%d
    NoAttribute,                        // 謬：無屬性%s
    StackEmpty,                         // 謬：棧空
    StackIndexOutOfBounds,              // 謬：棧索引越界
    RecursionDepthTooDeep,              // 謬：遞歸深度過深
    SetValueToNonLeftValue,             // 謬：設值於非左值
    InvalidArgumentType,                // 謬：無效參數類
    CannotCast,                         // 謬：不可轉%s為%s
    InvalidDataType,                    // 謬：無效資料類
    FunctionDoesNotHaveReferences,      // 謬：術無引
    CannotCreateObject,                 // 謬：不可造物
    OperationNotSupported,              // 謬：操作未支
    IntegerOverflow,                    // 謬：整數溢
    DivisionByZero,                     // 謬：除零
    LineError,                          // 謬：行%d:%d %s\n伴%s
    DebugInfoNotFound,                  // 謬：無除錯資訊於索引%d
    VariableNameDuplicate,              // 謬：變量名稱重複
    UnknownOperator,                    // 謬：未知算子
    UnknownPreposition,                 // 謬：未知介詞
    FunctionNameDoesNotMatch,           // 謬：術名不符
    VerificationFailed,                 // 謬：驗證敗
    TooManyVariables,                   // 謬：變數過多
    VariablesNotPositive,               // 謬：變數非正
    VariablesNotMatch,                  // 謬：變數不符
    InvalidNumber,                      // 謬：無效數
    InvalidFloatNumber,                 // 謬：無效分數
    InvalidBoolValue,                   // 謬：無效爻
    UnexpectedCharacter,                // 謬：意外字元
    IndexOutOfBounds,                   // 謬：索引越界
    TooManyThreads,                     // 謬：線程過多
    RunningTooSlow,                     // 謬：運行過慢
    Unreached,                          // 未知错误，请提交issue
    ;

    @Override
    public String getTranslationKey() {
        return "error.wenyan_programming.judou." + name();
    }
}
