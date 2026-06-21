# 文言 BNF 语法规范（ANTLR4）

基于 `WenyanR.g4`。

## 解析器规则（Parser Rules）

```
program                     : statements EOF;

statements                  : statement* ;

statement                   : candy_statement
                            | expr_statement
                            | control_statement
                            ;

candy_statement             : declare_write_candy_statement
                            | boolean_algebra_statement
                            | mod_math_statement
                            | import_as_statement
                            ;

expr_statement              : declare_statement
                            | init_declare_statement
                            | reference_statement
                            | define_statement
                            | assign_statement
                            | function_define_statement
                            | function_call_statement
                            | object_statement
                            | import_statement
                            ;

control_statement           : if_statement
                            | for_statement
                            | flush_statement
                            | return_statement
                            | break_
                            | continue_
                            ;

data                        : data_type=(STRING_LITERAL|BOOL_VALUE|FLOAT_NUM|INT_NUM)
                            | DATA_ID_LAST
                            | ZHI
                            | IDENTIFIER
                            | SELF
                            | PARENT
                            | data ZHI p=(INT_NUM|DATA_ID_LAST)
                            | data ZHI p=(IDENTIFIER|LONG|CREATE_OBJECT)
                            | data if_logic_op data
                            ;

reference_statement         : FU data ;
declare_statement           : declare_op INT_NUM type (YUE d+=data)* ;
init_declare_statement      : DECLARE_HAVE type data ;
define_statement            : NAMING (YUE definable_value)+ ;
definable_value             : IDENTIFIER | (SELF ZHI IDENTIFIER) ;

declare_write_candy_statement : declare_statement WRITE_KEY_FUNCTION ZHI ;

mod_math_statement          : DIV data pp=(PREPOSITION_LEFT|PREPOSITION_RIGHT) data POST_MOD_MATH_OP ;
boolean_algebra_statement   : FU data data op=(AND_STMT | OR_STMT) ;
assign_statement            : ASSIGN_LEFT data ZHE ASSIGN_RIGHT data ASSIGN_RIGHT_END
                            | ASSIGN_LEFT data ZHE (ASSIGN_RIGHT)? ASSIGN_RIGHT_NULL
                            | ASSIGNING data
                            ;

function_define_statement   : t=(LOCAL_DECLARE_OP|ASYNC_DECLARE_OP) INT_NUM FUNCTION_TYPE NAMING YUE IDENTIFIER
                              function_define_body IDENTIFIER FUNCTION_DEFINE_END ;

function_call_statement     : ((call= (CALLING_FUNCTION|CREATE_OBJECT) data) | key_function)
                              (preposition (args+=data))?
                              (preposition args+=data)*
                            | key_function (data)
                              (pp+=(PREPOSITION_LEFT|PREPOSITION_RIGHT) data)*
                            | FUNCTION_GET_ARGS INT_NUM PREPOSITION_RIGHT
                              ((call= (CALLING_FUNCTION|CREATE_OBJECT) data) | key_function)
                            ;

flush_statement             : FLUSH ;

if_statement                : IF_ data ZHE if_=statements (ELIF el_data+=data ZHE elif+=statements)*
                              (ELSE_ else_=statements)? FOR_IF_END ;

for_statement               : FOR_ARR_START data FOR_ARR_BELONG IDENTIFIER statements FOR_IF_END
                            | FOR_ENUM_START data FOR_ENUM_TIMES statements FOR_IF_END
                            | FOR_WHILE_SART statements FOR_IF_END
                            ;

return_statement            : RETURN data
                            | RETURN_LAST
                            | RETURN_NULL
                            ;

object_statement            : LOCAL_DECLARE_OP INT_NUM OBJECT_TYPE (EXTENDS data)? NAMING YUE IDENTIFIER
                              OBJECT_BODY_START (object_property_define | object_method_define)*
                              DEFINE_CLOSURE IDENTIFIER OBJECT_DEFINE_END ;
object_method_define        : OBJECT_STATIC_DECLARE (IDENTIFIER | CREATE_OBJECT) ZHE FUNCTION_TYPE
                              function_define_body (IDENTIFIER | CREATE_OBJECT) FUNCTION_DEFINE_END ;
object_property_define      : OBJECT_STATIC_DECLARE IDENTIFIER ZHE type (YUE data)? ;

import_as_statement         : IMPORT_START name=IDENTIFIER IMPORT_PACKAGE define_statement ;
import_statement            : IMPORT_START name=IDENTIFIER IMPORT_PACKAGE (FROM_IMPORT prop+=IDENTIFIER+ FROM_IMPORT_END)? ;

function_define_body        : (FUNCTION_ARGS_START FUNCTION_ARGS_GET
                              (args+=INT_NUM t+=(NUM_TYPE|LIST_TYPE|STRING_TYPE|BOOL_TYPE|OBJECT_TYPE|FUNCTION_TYPE)
                              (YUE id+=IDENTIFIER)+)+)? FUNCTION_BODY_START statements DEFINE_CLOSURE ;

if_logic_op                 : op=(EQ|NEQ|LTE|GTE|GT|LT) ;

key_function                : op=(
                              ADD | SUB | MUL | DIV
                            | AND | OR | UNARY_OP
                            | ARRAY_COMBINE_OP
                            | ARRAY_ADD_OP
                            | WRITE_KEY_FUNCTION
                            | AWAIT_KEY_FUNCTION
                            ) ;

type                        : NUM_TYPE|LIST_TYPE|STRING_TYPE|BOOL_TYPE ;
break_                      : BREAK_ ;
continue_                   : CONTINUE_ ;
preposition                 : PREPOSITION_LEFT | PREPOSITION_RIGHT ;
declare_op                  : LOCAL_DECLARE_OP | GLOBAL_DECLARE_OP ;
```

## 词法规则（Lexer Rules）

```
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

STRING_LITERAL           : '「「' ( ~('」') )* '」」' ;
IDENTIFIER               : '「' ( ~('」') )* '」' ;

FLOAT_NUM                : INT_NUM FLOAT_NUM_DIVISION (INT_NUM FLOAT_NUM_KEYWORDS)+ ;
INT_NUM                  : INT_NUM_SIGN? INT_NUM_KEYWORDS+ ;
BOOL_VALUE               : '陰'|'陽';

COMMENT                  : ('注曰'|'疏曰'|'批曰') WS? STRING_LITERAL -> channel(HIDDEN) ;
WS                       : ([ \t\r\n]|'。'|'、'|'，'|'　')+ -> channel(HIDDEN) ;
```
