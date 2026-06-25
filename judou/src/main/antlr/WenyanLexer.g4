lexer grammar WenyanLexer;

POST_MOD_MATH_OP         : '所餘幾何';
AND_STMT                 : '中無陰乎';
OR_STMT                  : '中有陽乎';
AND                      : '且' ;
OR                       : '或' ;
NEQ                      : '不等於';
LTE                      : '不大於';
GTE                      : '不小於';
EQ                       : '等於';
GT                       : '大於';
LT                       : '小於';


CONTINUE_                : '乃止是遍' ;
BREAK_                   : '乃止' ;
DATA_ID_LAST             : '其' ;

RETURN_NULL              : '乃歸空無' | '乃歸';
RETURN_LAST              : '乃得矣' ;
RETURN                   : '乃得' ;

ASSIGN_RIGHT_NULL        : '不復存矣';
ASSIGN_RIGHT_END         : '是矣' ;
ASSIGN_RIGHT             : '今' ;

IF_                      : '若' ;
ELIF                     : '或若' ;
ELSE_                    : '若非' ;

FOR_WHILE_SART           : '恆為是';
FOR_ARR_BELONG           : '中之' ;
FOR_ENUM_START           : '為是';
FOR_ARR_START            : '凡' ;
FOR_ENUM_TIMES           : '遍' ;

FUNCTION_ARGS_START      : '欲行是術';
FUNCTION_ARGS_GET        : '必先得' ;
FUNCTION_BODY_START      : '是術曰' | '乃行是術曰';
FUNCTION_DEFINE_END      : '之术也' | '之術也';
FUNCTION_GET_ARGS        : '取' ;

OBJECT_BODY_START        : '其物如是' ;
OBJECT_DEFINE_END        : '之物也' ;
OBJECT_STATIC_DECLARE    : '物之' ;

IMPORT_START             : '吾嘗觀' ;
IMPORT_PACKAGE           : '之書' ;
FROM_IMPORT              : '方悟' ;
FROM_IMPORT_END          : '之義' ;

LOCAL_DECLARE_OP         : '吾有' ;
GLOBAL_DECLARE_OP        : '今有' ;
ASYNC_DECLARE_OP         : '同有' ;
DEFINE_CLOSURE           : '是謂' ;
NEED                     : '需' ;

FOR_IF_END               : '云云' | '是也' | '也' ;

NAMING                   : '名之' ;
ASSIGNING                : '予之以' ;
ASSIGN_LEFT              : '昔之' ;
DECLARE_HAVE             : '有' ;

PREPOSITION_LEFT         : '於';
PREPOSITION_RIGHT        : '以' ;

CALLING_FUNCTION         : '施' ;
CREATE_OBJECT            : '造' ;
EXTENDS                  : '繼';

ZHE                      : '者' ;
FU                       : '夫' ;
YUE                      : '曰' ;
ZHI                      : '之' ;

NUM_TYPE                 : '數';
LIST_TYPE                : '列' ;
STRING_TYPE              : '言' ;
BOOL_TYPE                : '爻' ;
FUNCTION_TYPE            : '術';
OBJECT_TYPE              : '物' ;

ADD                      : '加' ;
SUB                      : '減';
MUL                      : '乘' ;
DIV                      : '除' ;
UNARY_OP                 : '變';

ARRAY_COMBINE_OP         : '銜';
ARRAY_ADD_OP             : '充' ;
WRITE_KEY_FUNCTION       : '書';
AWAIT_KEY_FUNCTION       : '待' ;
FLUSH                    : '噫' ;

SELF                     : '己' ;
PARENT                   : '父' ;
LONG                     : '長';

STRING_LITERAL           : '「「' .*? '」」' ;
IDENTIFIER               : '「' .*? '」' ;

FLOAT_NUM                : INT_NUM FLOAT_NUM_DIVISION (INT_NUM FLOAT_NUM_KEYWORDS)+ ;
fragment
FLOAT_NUM_DIVISION       : '又';
fragment
FLOAT_NUM_KEYWORDS       : '分'|'釐'|'毫'|'絲'|'忽'|'微'|'纖'|'沙'|'塵'|'埃'|'渺'|'漠' ;

INT_NUM                  : INT_NUM_SIGN? INT_NUM_KEYWORDS+ ;
fragment
INT_NUM_SIGN             : '負' | '负' ;
fragment
INT_NUM_KEYWORDS         : '〇'|'零'|'一'|'二'|'三'|'四'|'五'|'六'|'七'|'八'|'九'
                         | '十'|'百'|'千'|'万'|'萬'|'亿'|'億'|'兆'|'京'|'垓'|'秭'|'穰'|'穣'|'沟'|'溝'|'涧'|'澗'|'正'|'载'|'載'|'极'|'極';


BOOL_VALUE               : '陰'|'陽';

COMMENT                  : ('注曰'|'疏曰'|'批曰') WS? STRING_LITERAL -> channel(HIDDEN) ;
WS                       : ([ \t\r\n]|'。'|'、'|'，'|'　')+ -> channel(HIDDEN) ;
