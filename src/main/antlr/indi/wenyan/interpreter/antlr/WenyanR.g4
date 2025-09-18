grammar WenyanR;

@header{package indi.wenyan.interpreter.antlr;}

// for sym table: const, id, label; others already int
// to bytecode that has
// jmp:label (->), branch_false:label (value -> value)
// call:argc (arg2, arg1..., func_value -> ret), ret (value -> )
// call_attr:argc (arg..., self/ignore, func -> ret), handleWarper(h):argc (args... -> ret)
// push:const (-> value), pop (value ->)
// pushA (value ->), popA (-> value), peekA(-> value), peekA_N:cnt (-> cnt*val), empty
// load:id (-> value), store:id (value -> ), set_val(value2, value1 -> ) [v1 -> v2]
// casting:type (value -> value)
// load_attr:id (self -> attr), load_attr_remain:id (self -> self, attr),
// store_attr:id (attr, self -> ), store_a_meth:id (self, m -> self), s_a_prop:id (self, p -> self)
// create_type:id (parent -> self), create_object:argc (arg..., obj_type -> obj_ins)
// FOR_ITER:label_end (iter -> iter+1, i), FOR_NUM:label_end (i -> i - 1) [remove i f jump out]

program                     : statements EOF;

statements                  : statement* ;

statement                   : candy_statement // make the candy first
                            | expr_statement
                            | control_statement
                            | import_statement
                            ;

candy_statement             : declare_write_candy_statement
                            | boolean_algebra_statement
                            | mod_math_statement
                            ;

expr_statement              : declare_statement
                            | init_declare_statement
                            | reference_statement
                            | define_statement
                            | assign_statement

                            | function_define_statement
                            | function_call_statement

                            | object_statement
                            ;

control_statement           : if_statement
                            | for_statement
                            | flush_statement
                            | return_statement
                            | break_
                            | continue_
                            ;

// Here has a typo of primitive but I don't want to fix it
data                        : data_type=(STRING_LITERAL|BOOL_VALUE|FLOAT_NUM|INT_NUM)   # data_primary
                            | DATA_ID_LAST                                              # id_last
                            | ZHI                                                       # id_last_remain
                            | IDENTIFIER                                                # id
                            | SELF                                                      # self
                            | PARENT                                                    # parent
                            | data ZHI p=(INT_NUM|DATA_ID_LAST)                         # array_index
                            | data ZHI p=(IDENTIFIER|LONG|CREATE_OBJECT)                # data_child
                            | data if_logic_op data                                     # logic_data
                            ;

reference_statement         : FU data ;
declare_statement           : declare_op INT_NUM type (YUE d+=data)* ;
init_declare_statement      : DECLARE_HAVE type data ;
define_statement            : NAMING (YUE definable_value)+ ;
definable_value             : IDENTIFIER | (SELF ZHI IDENTIFIER) ;

declare_write_candy_statement : declare_statement WRITE_KEY_FUNCTION ZHI
                              ;

mod_math_statement          : DIV data pp=(PREPOSITION_LEFT|PREPOSITION_RIGHT) data POST_MOD_MATH_OP ;
boolean_algebra_statement   : FU data data op=(AND_STMT | OR_STMT) ;
assign_statement            : ASSIGN_LEFT data ZHE ASSIGN_RIGHT data ASSIGN_RIGHT_END   # assign_data_statement
                            | ASSIGN_LEFT data ZHE (ASSIGN_RIGHT)? ASSIGN_RIGHT_NULL       # assign_null_statement;

function_define_statement   : LOCAL_DECLARE_OP INT_NUM FUNCTION_TYPE NAMING YUE IDENTIFIER
                              function_define_body IDENTIFIER FUNCTION_DEFINE_END ;

function_call_statement     : ((call= (CALLING_FUNCTION|CREATE_OBJECT) data) | key_function)
                              (preposition (args+=data))?
                              (preposition args+=data)*                         # function_pre_call
                            | key_function (data)
                              (pp+=(PREPOSITION_LEFT|PREPOSITION_RIGHT) data)*  # key_function_call
                            | FUNCTION_GET_ARGS INT_NUM PREPOSITION_RIGHT
                              ((call= (CALLING_FUNCTION|CREATE_OBJECT) data) | key_function) # function_post_call
                            ;

flush_statement             : FLUSH ;

if_statement                : IF_ data ZHE if_=statements (ELIF el_data+=data ZHE elif+=statements)*
                              (ELSE_ else_=statements)? FOR_IF_END ;

for_statement               : FOR_ARR_START data FOR_ARR_BELONG IDENTIFIER statements FOR_IF_END  # for_arr_statement
                            | FOR_ENUM_START data FOR_ENUM_TIMES statements FOR_IF_END            # for_enum_statement
                            | FOR_WHILE_SART statements FOR_IF_END                                # for_while_statement
                            ;

return_statement            : RETURN data                     # return_data_statement
                            | RETURN_LAST                     # return_last_statement
                            | RETURN_NULL                     # return_void_statement
                            ;

object_statement            : LOCAL_DECLARE_OP INT_NUM OBJECT_TYPE (EXTENDS data)? NAMING YUE IDENTIFIER
                              OBJECT_BODY_START (object_property_define | object_method_define)*
                              DEFINE_CLOSURE IDENTIFIER OBJECT_DEFINE_END ;
object_method_define        : OBJECT_STATIC_DECLARE (IDENTIFIER | CREATE_OBJECT) ZHE FUNCTION_TYPE
                              function_define_body (IDENTIFIER | CREATE_OBJECT) FUNCTION_DEFINE_END ;
object_property_define      : OBJECT_STATIC_DECLARE IDENTIFIER ZHE type (YUE data)? ;

import_statement            : '吾嘗觀' IDENTIFIER '之書' ('方悟' IDENTIFIER+ '之義')? ;

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
                            ) ;

type                        : NUM_TYPE|LIST_TYPE|STRING_TYPE|BOOL_TYPE ;

break_                      : BREAK_ ;
continue_                   : CONTINUE_ ;

preposition                 : PREPOSITION_LEFT | PREPOSITION_RIGHT ;
declare_op                  : LOCAL_DECLARE_OP | GLOBAL_DECLARE_OP ;

POST_MOD_MATH_OP            : '所餘幾何' | '所余几何';
AND_STMT                    : '中無陰乎' | '中无阴乎';
OR_STMT                     : '中有陽乎' | '中有阳乎';
AND                         : '且' ;
OR                          : '或' ;
NEQ                         : '不等於' | '不等于';
LTE                         : '不大於' | '不大于';
GTE                         : '不小於' | '不小于';
EQ                          : '等於' | '等于';
GT                          : '大於' | '大于';
LT                          : '小於' | '小于';


CONTINUE_                    : '乃止是遍' ;
BREAK_                       : '乃止' ;
DATA_ID_LAST                 : '其' ;

RETURN_NULL                  : '乃归空无' | '乃归' | '乃歸空無' | '乃歸';
RETURN_LAST                  : '乃得矣' ;
RETURN                       : '乃得' ;

ASSIGN_RIGHT_NULL            : '不复存矣' | '不復存矣';
ASSIGN_RIGHT_END             : '是矣' ;
ASSIGN_RIGHT                 : '今' ;

IF_                          : '若' ;
ELIF                         : '或若' ;
ELSE_                        : '若非' ;

FOR_WHILE_SART               : '恒为是' | '恆為是';
FOR_ARR_BELONG               : '中之' ;
FOR_ENUM_START               : '为是' | '為是';
FOR_ARR_START                : '凡' ;
FOR_ENUM_TIMES               : '遍' ;

FUNCTION_ARGS_START          : '欲行是术' | '欲行是術';
FUNCTION_ARGS_GET            : '必先得' ;
FUNCTION_BODY_START          : '是术曰' | '乃行是术曰' | '是術曰' | '乃行是術曰';
FUNCTION_DEFINE_END          : '之术也' | '之術也';
FUNCTION_GET_ARGS            : '取' ;

OBJECT_BODY_START           : '其物如是' ;
OBJECT_DEFINE_END           : '之物也' ;
OBJECT_STATIC_DECLARE       : '物之' ;

LOCAL_DECLARE_OP            : '吾有' ;
GLOBAL_DECLARE_OP           : '今有' ;
DEFINE_CLOSURE              : '是谓' | '是謂';

FOR_IF_END                  : '云云' | '是也' | '也' | '雲雲';

NAMING                      : '名之' ;
ASSIGN_LEFT                 : '昔之' ;
DECLARE_HAVE                : '有' ;

PREPOSITION_LEFT            : '于' | '於';
PREPOSITION_RIGHT           : '以' ;

CALLING_FUNCTION            : '施' ;
CREATE_OBJECT               : '造' ;
EXTENDS                     : '继' | '繼';

ZHE                         : '者' ;
FU                          : '夫' ;
YUE                         : '曰' ;
ZHI                         : '之' ;

NUM_TYPE                    : '数' | '數';
LIST_TYPE                   : '列' ;
STRING_TYPE                 : '言' ;
BOOL_TYPE                   : '爻' ;
FUNCTION_TYPE               : '术' | '術';
OBJECT_TYPE                 : '物' ;

ADD                         : '加' ;
SUB                         : '减' | '減';
MUL                         : '乘' ;
DIV                         : '除' ;
UNARY_OP                    : '变' | '變';

ARRAY_COMBINE_OP            : '衔' | '銜';
ARRAY_ADD_OP                : '充' ;
WRITE_KEY_FUNCTION          : '书' | '書';
FLUSH                       : '噫' ;

SELF                        : '己' ;
PARENT                      : '父' ;
LONG                        : '长' | '長';


STRING_LITERAL              : '「「' ( ~('」') )* '」」' ;
IDENTIFIER                  : '「' ( ~('」') )* '」' ;

FLOAT_NUM                   : INT_NUM FLOAT_NUM_DIVISION (INT_NUM FLOAT_NUM_KEYWORDS)+ ;
fragment
FLOAT_NUM_DIVISION          : '又';
fragment
FLOAT_NUM_KEYWORDS          : '分'|'釐'|'毫'|'絲'|'忽'|'微'|'纖'|'沙'|'塵'|'埃'|'渺'|'漠' ;

INT_NUM                     : INT_NUM_SIGN? INT_NUM_KEYWORDS+ ;
fragment
INT_NUM_SIGN                : '負' | '负' ;
fragment
INT_NUM_KEYWORDS            : '〇'|'零'|'一'|'二'|'三'|'四'|'五'|'六'|'七'|'八'|'九'
                            | '十'|'百'|'千'|'万'|'萬'|'亿'|'億'|'兆'|'京'|'垓'|'秭'|'穰'|'穣'|'沟'|'溝'|'涧'|'澗'|'正'|'载'|'載'|'极'|'極';


BOOL_VALUE                  : '陰'|'陽'|'阴'|'阳';

COMMENT                     : ('注曰'|'疏曰'|'批曰') WS? STRING_LITERAL -> channel(HIDDEN) ;
WS                          : ([ \t\r\n]|'。'|'、'|'，'|'　')+ -> channel(HIDDEN) ;
