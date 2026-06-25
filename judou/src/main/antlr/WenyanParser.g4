parser grammar WenyanParser;

options {
    tokenVocab=WenyanLexer;
}

// for sym table: const, id, label; others already int
// to bytecode that has
// jmp:label (->), branch_false:label (value -> value)
// call:argc (arg2, arg1..., func_value -> ret), ret (value -> )
// call_attr:argc (arg..., self/ignore, func -> ret), handleWarper(h):argc (args... -> ret)
// push:const (-> value), pop (value ->)
// pushA (value ->), popA (-> value), peekA(-> value), peekA_N:cnt (-> cnt*val), empty
// casting:type (value -> value)
// load_attr:id[string] (self -> attr), load_attr_remain:id[string] (self -> self, attr),
// store_attr:id[string] (attr, self -> ), store_a_meth:id[string] (self, m -> self), s_a_prop:id[string] (self, p -> self)
// create_type (parent -> self)
// FOR_ITER:label_end (iter -> iter+1, i), FOR_NUM:label_end (i -> i - 1) [remove i f jump out]
// load:id[index] (-> value), store:id[index] (value -> ), set_val(value2, value1 -> ) [v1 -> v2]
// load_ref:id[index] (-> value), load_global:id[string] (-> value)
// create_function:const (-> func)

program                     : statements EOF;

statements                  : statement* ;

statement                   : candy_statement // make the candy first
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
                            | ASSIGN_LEFT data ZHE (ASSIGN_RIGHT)? ASSIGN_RIGHT_NULL    # assign_null_statement
                            | ASSIGNING data                                            # assign_simple_statement
                            ;

function_define_statement   : t=(LOCAL_DECLARE_OP|ASYNC_DECLARE_OP) INT_NUM FUNCTION_TYPE NAMING YUE IDENTIFIER
                              function_define_body DEFINE_CLOSURE IDENTIFIER FUNCTION_DEFINE_END  # named_function_define
                            | declare=(LOCAL_DECLARE_OP|ASYNC_DECLARE_OP) INT_NUM FUNCTION_TYPE
                              lambda_function_body FUNCTION_DEFINE_END                            # declared_lambda_function
                            | DECLARE_HAVE FUNCTION_TYPE lambda_function_body FUNCTION_DEFINE_END # simple_lambda_function
                            ;

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
                              function_define_body DEFINE_CLOSURE (IDENTIFIER | CREATE_OBJECT) FUNCTION_DEFINE_END ;
object_property_define      : OBJECT_STATIC_DECLARE IDENTIFIER ZHE type (YUE data)? ;

import_as_statement         : IMPORT_START name=IDENTIFIER IMPORT_PACKAGE define_statement ;
import_statement            : IMPORT_START name=IDENTIFIER IMPORT_PACKAGE (FROM_IMPORT prop+=IDENTIFIER+ FROM_IMPORT_END)? ;

function_define_body        : (FUNCTION_ARGS_START FUNCTION_ARGS_GET
                              (args+=INT_NUM t+=(NUM_TYPE|LIST_TYPE|STRING_TYPE|BOOL_TYPE|OBJECT_TYPE|FUNCTION_TYPE)
                              (YUE id+=IDENTIFIER)+)+)? FUNCTION_BODY_START statements ;

lambda_function_body        : (NEED (
                              (t+=(NUM_TYPE|LIST_TYPE|STRING_TYPE|BOOL_TYPE|OBJECT_TYPE|FUNCTION_TYPE)
                              YUE id+=IDENTIFIER)+ | // typed
                              (id+=IDENTIFIER)+ // or no typed at all
                              ))? statements ;

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
