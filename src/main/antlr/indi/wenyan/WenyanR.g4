grammar WenyanR;

@header{package indi.wenyan;}

program                     : statement* EOF;

statement                   : candy_statement
                            | expr_statement
                            | object_statement
                            | control_statement
                            | import_statement
                            ;

candy_statement             : declare_write_candy_statement; // make the candy first

control_statement           : if_statement
                            | for_statement
                            | flush_statement
                            | return_statement
                            | break_
                            | continue_
                            ;

expr_statement              : declare_statement
                            | init_declare_statement
                            | reference_statement
                            | define_statement

                            | assign_statement
                            | boolean_algebra_statement
                            | mod_math_statement

                            | function_define_statement
                            | function_call_statement
                            ;

data                        : data_type=(STRING_LITERAL|BOOL_VALUE|FLOAT_NUM|INT_NUM)            # data_primary
                            | DATA_ID_LAST                                                       # id_last
                            | IDENTIFIER                                                         # id
                            | data ZHI p=(STRING_LITERAL|IDENTIFIER|INT_NUM|DATA_ID_LAST|LONG)   # data_child
                            ;

reference_statement         : FU data ;
declare_statement           : declare_op INT_NUM type (YUE d+=data)* ;
init_declare_statement      : DECLARE_HAVE type data ;
define_statement            : NAMING (YUE d+=IDENTIFIER)+ ;

declare_write_candy_statement : declare_statement WRITE_KEY_FUNCTION ZHI ;

mod_math_statement          : DIV (data|ZHI) pp=(PREPOSITION_LEFT|PREPOSITION_RIGHT) data POST_MOD_MATH_OP ;
boolean_algebra_statement   : FU data data op=(AND | OR) ;
assign_statement            : ASSIGN_LEFT data ZHE ASSIGN_RIGHT data ASSIGN_RIGHT_END   # assign_data_statement
                            | ASSIGN_LEFT data ZHE ASSIGN_RIGHT ASSIGN_RIGHT_NULL       # assign_null_statement;

function_define_statement   : LOCAL_DECLARE_OP INT_NUM FUNCTION_TYPE NAMING YUE IDENTIFIER
                              (FUNCTION_ARGS_START FUNCTION_ARGS_GET (args+=INT_NUM type (YUE id+=IDENTIFIER)+)+)?
                              FUNCTION_BODY_START statement* DEFINE_CLOSURE IDENTIFIER FUNCTION_DEFINE_END ;

function_call_statement     : CALLING_FUNCTION (data|key_function)
                              (preposition (args+=data|ZHI))?
                              (preposition args+=data)*                         # function_pre_call
                            | key_function (data|ZHI)
                              (pp+=(PREPOSITION_LEFT|PREPOSITION_RIGHT) data)*  # key_function_call
                            | FUNCTION_GET_ARGS INT_NUM PREPOSITION_RIGHT CALLING_FUNCTION
                              (data|key_function)                               # function_post_call
                            ;

flush_statement             : FLUSH ;

if_statement                : IF_ if_expression ZHE if_+=statement* (ELSE_ else_+=statement*)? FOR_IF_END ;
if_expression               : data                  # if_data
                            | data if_logic_op data # if_logic ;

for_statement               : FOR_ARR_START data FOR_ARR_BELONG IDENTIFIER statement* FOR_IF_END  # for_arr_statement
                            | FOR_ENUM_START data FOR_ENUM_TIMES statement* FOR_IF_END            # for_enum_statement
                            | FOR_WHILE_SART statement* FOR_IF_END                                # for_while_statement
                            ;

return_statement            : RETURN data                     # return_data_statement
                            | RETURN_LAST                     # return_last_statement
                            | RETURN_NULL                     # return_void_statement
                            ;

object_statement            : LOCAL_DECLARE_OP INT_NUM '物' define_statement (object_define_statement)? ;
object_define_statement     : '其物如是' ('物之' STRING_LITERAL ZHE type YUE data)+ DEFINE_CLOSURE IDENTIFIER '之物也' ;
import_statement            : '吾嘗觀' STRING_LITERAL '之書' ('方悟' IDENTIFIER+ '之義')? ;

if_logic_op                 : op=(EQ|NEQ|LTE|GTE|GT|LT) ;

key_function                : op=(
                              ADD | SUB | MUL | DIV
                            | UNARY_OP
                            | ARRAY_COMBINE_OP
                            | ARRAY_ADD_OP
                            | WRITE_KEY_FUNCTION
                            ) ;

type                        : NUM_TYPE|LIST_TYPE|STRING_TYPE|BOOL_TYPE ;

break_                      : BREAK_ ;
continue_                   : CONTINUE_ ;

preposition                 : PREPOSITION_LEFT | PREPOSITION_RIGHT ;
declare_op                  : LOCAL_DECLARE_OP | GLOBAL_DECLARE_OP ;

POST_MOD_MATH_OP            : '所餘幾何' ;
AND                         : '中無陰乎';
OR                          : '中有陽乎';
NEQ                         : '不等於' ;
LTE                         : '不大於' ;
GTE                         : '不小於' ;
EQ                          : '等於' ;
GT                          : '大於' ;
LT                          : '小於' ;

CONTINUE_                    : '乃止是遍' ;
BREAK_                       : '乃止' ;
DATA_ID_LAST                : '其' ;

RETURN_NULL                 : '乃歸空無' ;
RETURN_LAST                 : '乃得矣' ;
RETURN                      : '乃得' ;

ASSIGN_RIGHT_NULL           : '不復存矣' ;
ASSIGN_RIGHT_END            : '是矣' ;
ASSIGN_RIGHT                : '今' ;


ELSE_                        : '若非' ;
IF_                          : '若' ;

FOR_WHILE_SART              : '恆為是' ;
FOR_ARR_BELONG              : '中之' ;
FOR_ENUM_START              : '為是' ;
FOR_ARR_START               : '凡' ;
FOR_ENUM_TIMES              : '遍' ;

FUNCTION_ARGS_START         : '欲行是術' ;
FUNCTION_ARGS_GET           : '必先得' ;
FUNCTION_BODY_START         : '是術曰' | '乃行是術曰' ;
FUNCTION_DEFINE_END         : '之術也' ;
FUNCTION_GET_ARGS           : '取' ;

LOCAL_DECLARE_OP            : '吾有' ;
GLOBAL_DECLARE_OP           : '今有' ;
DEFINE_CLOSURE              : '是謂' ;
FOR_IF_END                  : '云云' | '是也' | '也' ;
NAMING                      : '名之' ;
ASSIGN_LEFT                 : '昔之' ;
DECLARE_HAVE                : '有' ;
PREPOSITION_LEFT            : '於' ;
PREPOSITION_RIGHT           : '以' ;
CALLING_FUNCTION            : '施' ;


ZHE                         : '者' ;
FU                          : '夫' ;
YUE                         : '曰' ;
ZHI                         : '之' ;

NUM_TYPE                    : '數' ;
LIST_TYPE                   : '列' ;
STRING_TYPE                 : '言' ;
BOOL_TYPE                   : '爻' ;
FUNCTION_TYPE               : '術' ;

ADD                         : '加' ;
SUB                         : '減' ;
MUL                         : '乘' ;
DIV                         : '除' ;
UNARY_OP                    : '變' ;

ARRAY_COMBINE_OP            : '銜' ;
ARRAY_ADD_OP                : '充' ;
WRITE_KEY_FUNCTION          : '書' ;
FLUSH                       : '噫' ;

LONG                        : '長' ;

STRING_LITERAL              : '「「' ( ~('」') )* '」」' ;
IDENTIFIER                  : '「' ( ~('」') )+ '」' ;

FLOAT_NUM                   : INT_NUM FLOAT_NUM_DIVISION (INT_NUM FLOAT_NUM_KEYWORDS)+ ;
fragment
FLOAT_NUM_DIVISION          : '又';
fragment
FLOAT_NUM_KEYWORDS          : '分'|'釐'|'毫'|'絲'|'忽'|'微'|'纖'|'沙'|'塵'|'埃'|'渺'|'漠' ;

INT_NUM                     : INT_NUM_SIGN? INT_NUM_KEYWORDS+ ;
fragment
INT_NUM_SIGN                : '負' ;
fragment
INT_NUM_KEYWORDS            : '〇'|'零'|'一'|'二'|'三'|'四'|'五'|'六'|'七'|'八'|'九'
                            | '十'|'百'|'千'|'萬'|'億'|'兆'|'京'|'垓'|'秭'|'穣'|'溝'|'澗'|'正'|'載'|'極'
                            | FLOAT_NUM_DIVISION;

BOOL_VALUE                  : '陰'|'陽' ;

COMMENT                     : ('注曰'|'疏曰'|'批曰') WS? STRING_LITERAL -> skip ;
WS                          : ([ \t\r\n]|'。'|'、'|'，'|'　')+ -> skip ;
