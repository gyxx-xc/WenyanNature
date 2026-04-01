---
name: wenyan-lang
description: Complete language specification for the Wenyan programming language (文言编程), including syntax, grammar rules, allowed operators, control structures, and examples. Use when writing or analyzing Wenyan code.
---

# 语言宪法

## 白名单：唯一允许的语法全集

### 数据类型
- `數` - 数字（整数、小数、负数）
- `言` - 字符串（双引号包裹）
- `爻` - 布尔值（`陰` 假，`陽` 真）
- `列` - 数组（可变列表）
- `物` - 对象（类实例）
- `術` - 函数（一等公民）

### 变量声明
- `吾有一數 曰三。名之曰「甲」。` - 声明数字变量
- `吾有一言 曰「「文本」」。名之曰「乙」。` - 声明字符串变量
- `吾有一爻 曰陽。名之曰「丙」。` - 声明布尔变量
- `吾有一列。名之曰「丁」。` - 声明数组变量
- `吾有一物。名之曰「戊」。` - 声明对象变量
- `吾有一術。名之曰「己」。` - 声明函数变量
- `有數十三。名之曰「甲」。` - 简化声明
- `夫十三。名之曰「甲」。` - 使用夫声明
- 多变量声明：`吾有二數 曰十三 曰十五。名之曰「甲」曰「乙」。`

### 赋值
- `夫三。予之以「甲」。` - 重新赋值
- `夫三。予之以「甲」。予之以「乙」。` - 链式赋值
- `昔之「甲」者 不复存矣。` - 赋空值

### 运算符
#### 算术运算符
- `加` - 加法
- `減` - 减法
- `乘` - 乘法
- `除` - 除法
- `所餘幾何` - 模运算

#### 逻辑运算符
- `且` - 逻辑与
- `或` - 逻辑或
- `變` - 逻辑非
- `中無陰乎` - 布尔与（无阴乎）
- `中有陽乎` - 布尔或（有阳乎）

#### 比较运算符
- `等於` - 等于
- `不等於` - 不等于
- `大於` - 大于
- `小於` - 小于
- `不大於` - 不大于（小于等于）
- `不小於` - 不小于（大于等于）

#### 数组运算符
- `充` - 向数组添加元素
- `銜` - 合并数组
- `長` - 数组长度

#### 一元运算符
- `變陰` - 取反布尔值

### 控制流
#### 条件判断
- `若「甲」大於五十者。...云云。` - 简单if
- `若...者...若非...也。` - if-else
- `若...者...或若...者...若非...云云。` - if-elif-else

#### 循环
- `恆為是。...云云。` - 无限循环
- `為是百遍。...云云。` - 计数循环
- `凡「列」中之「元」。...云云。` - 数组遍历
- `乃止` - 跳出循环
- `乃止是遍` - 跳过本轮

### 函数
#### 函数定义
- `吾有一術。名之曰「法」。是術曰。...是謂「法」之術也。` - 无参数函数
- `吾有一術 名之曰「三法」。欲行是術 必先得 二數曰「甲」曰「乙」。一言曰「丙」。是術曰。...是謂「三法」之術也。` - 带参数函数
- `同有一術。名之曰「異步法」。...` - 异步函数

#### 函数调用
- `施「法」於一於四。` - 直接调用
- `夫一。夫四。取三以施「法」。` - 堆栈调用
- `加一以二` - 关键字函数调用
- `待「甲」` - 异步等待

#### 返回值
- `乃得一` - 返回值
- `乃得矣` - 返回最后计算结果
- `乃歸` - 无返回值退出

### 对象系统
#### 类定义
- `吾有一物。名之曰「物」。其物如是。...是謂「物」之物也。` - 类定义
- `吾有一物繼「物」。名之曰「子物」。...` - 继承

#### 成员定义
- `物之「甲」者數 曰二。` - 静态属性
- `物之造者術 是術曰。...` - 构造函数
- `物之「甲」者術 ...` - 方法

#### 实例化
- `造「物」。名之曰「甲」。` - 创建实例
- `施「物」。名之曰「乙」。` - 调用构造函数

#### 特殊变量
- `己` - 当前实例
- `父` - 父类类型

### 模块导入
- `吾嘗觀「甲」之書。` - 导入整个模块
- `吾嘗觀「甲」之書。方悟「乙」「丙」之義。` - 选择性导入
- `吾嘗觀「甲」之書。名之曰「乙」。` - 导入并命名

### 注释
- `注曰「「注释」」`
- `疏曰「「注释」」`
- `批曰「「注释」」`

### 特殊语句
- `噫` - 刷新输出缓冲区
- `書` - 输出函数
- `其` - 引用前一个结果（弹出堆栈）
- `之` - 引用前一个结果（不弹出堆栈）
- `取 N 以` - 从堆栈取N个参数

### 作用域规则
- 使用 `吾有` 或 `有` 声明的变量在当前作用域有效
- 函数内部可以访问外部变量（闭包）
- 函数内部声明的变量在函数返回时销毁

## 黑名单：绝对禁止的语法全集

以下Python/JavaScript语法特性在本方言中**完全禁止**：

### 控制结构
- **禁止** `for` 循环（使用 `為是...遍` 或 `凡...中之` 代替）
- **禁止** `while` 循环（使用 `恆為是` 代替）
- **禁止** `switch` 语句（使用 `或若` 链代替）
- **禁止** `do...while` 循环

### 变量与赋值
- **禁止** 变量重新赋值（除了使用 `昔之...者 今...是矣` 语法）
- **禁止** 可变变量（所有变量默认不可变，只能通过显式赋值语句修改）
- **禁止** 变量声明不使用 `吾有`、`有` 或 `夫` 前缀
- **禁止** 变量名不使用中文引号 `「」` 包裹

### 类型系统
- **禁止** 类定义使用 `class` 关键字（使用 `吾有一物`）
- **禁止** 继承使用 `extends` 关键字（使用 `繼`）
- **禁止** `new` 操作符（使用 `造` 或 `施`）
- **禁止** `typeof`、`instanceof` 操作符

### 函数
- **禁止** `function` 关键字（使用 `吾有一術`）
- **禁止** `=>` 箭头函数
- **禁止** `arguments` 对象
- **禁止** 默认参数值（需在函数体内处理）
- **禁止** 解构赋值

### 操作符
- **禁止** `++`、`--` 自增自减操作符
- **禁止** `%` 取模操作符（使用 `所餘幾何`）
- **禁止** `&&`、`||`、`!` 逻辑操作符（使用 `且`、`或`、`變`）
- **禁止** `===`、`!==` 严格相等操作符
- **禁止** `**` 幂操作符
- **禁止** 位操作符 (`&`, `|`, `^`, `~`, `<<`, `>>`, `>>>`)

### 语法特性
- **禁止** 三元操作符 `? :`
- **禁止** 模板字符串（使用字符串拼接）
- **禁止** 解构赋值
- **禁止** 展开操作符 `...`
- **禁止** 可选链 `?.`
- **禁止** 空值合并 `??`
- **禁止** `yield` 和生成器
- **禁止** `async/await` 关键字（使用 `同有` 和 `待`）

### 对象与数组
- **禁止** 对象字面量 `{key: value}`（使用对象定义和构造函数）
- **禁止** 数组字面量 `[1,2,3]`（使用 `充` 构建）
- **禁止** 点号属性访问 `obj.key`（使用 `之`：`夫「obj」之「key」`）
- **禁止** 方括号属性访问 `obj["key"]`（同上）

### 其他
- **禁止** 分号 `;` 作为语句结束符
- **禁止** 大括号 `{}` 作为代码块（使用缩进和 `云云`）
- **禁止** `try...catch...finally` 异常处理（暂未实现）
- **禁止** `import/export` ES6模块语法（使用 `吾嘗觀`）
- **禁止** `require` CommonJS语法
- **禁止** `eval`、`setTimeout`、`setInterval` 等宿主环境API

## BNF 语法规范

以下为文言编程语言的完整BNF语法规范，基于ANTLR4语法文件 `WenyanR.g4`：

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

// 词法规则（部分关键令牌）
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

## 平行示例库：10组基础示例

### 示例1：Hello World
**自然语言需求**：输出"问天地好在"三次。

**文言实现**：
```wenyan
吾有一數 曰三。名之曰「甲」。
為是「甲」遍。
    吾有一言 曰「「問天地好在。」」。書之。
云云。
```

### 示例2：变量声明与算术运算
**自然语言需求**：计算两个数字的和、差、积、商。

**文言实现**：
```wenyan
吾有二數 曰十五 曰三。名之曰「甲」曰「乙」。
加「甲」以「乙」。書之。
減「甲」以「乙」。書之。
乘「甲」以「乙」。書之。
除「甲」以「乙」。書之。
```

### 示例3：条件判断
**自然语言需求**：判断一个数是否大于50，大于则加1，否则减1。

**文言实现**：
```wenyan
吾有一數 曰六十。名之曰「貯」。
若「貯」大於五十者。
    加「貯」以一。昔之「貯」者 今其是矣。
若非。
    減「貯」以一。昔之「貯」者 今其是矣。
也。
夫「貯」。書之。
```

### 示例4：循环遍历数组
**自然语言需求**：遍历数组并输出每个元素。

**文言实现**：
```wenyan
吾有一列。充其以二以九以四。名之曰「列」。
凡「列」中之「元」。
    夫「元」。書之。
云云。
```

### 示例5：函数定义与调用
**自然语言需求**：定义一个函数计算平方，并调用它计算5的平方。

**文言实现**：
```wenyan
吾有一術。名之曰「平方」。
欲行是術 必先得一數曰「甲」。是術曰。
    乘「甲」以「甲」。乃得矣。
是謂「平方」之術也。

施「平方」於五。書之。
```

### 示例6：对象与类
**自然语言需求**：定义一个"人"类，有姓名属性，创建实例并输出姓名。

**文言实现**：
```wenyan
吾有一物。名之曰「人」。其物如是。
    物之造者術 是術曰。
        欲行是術 必先得一言曰「名」。是術曰。
            夫「名」。名之曰己之「名」。
        是謂造之術也。
    是謂造之術也。
    物之「說名」者術 是術曰。
        夫己之「名」。書之。
    是謂「說名」之術也。
是謂「人」之物也。

造「人」於「「張三」」。名之曰「某人」。
施「某人」之「說名」。
```

### 示例7：异步编程
**自然语言需求**：定义一个异步函数，等待指定时间后输出消息。

**文言实现**：
```wenyan
同有一術。名之曰「等待輸出」。
欲行是術 必先得一數曰「秒」。是術曰。
    待「秒」。批曰「「等待了」」
    書「「完成」」。
是謂「等待輸出」之術也。

施「等待輸出」於三。
```

### 示例8：数组操作
**自然语言需求**：创建两个数组，合并它们，然后输出合并后的数组长度和第三个元素。

**文言实现**：
```wenyan
吾有一列。充其以一以二以三。名之曰「甲」。
吾有一列。充其以四以五以六。名之曰「乙」。
銜「甲」以「乙」。名之曰「丙」。
夫「丙」之長。書之。
夫「丙」之三。書之。
```

### 示例9：逻辑运算
**自然语言需求**：进行布尔代数运算，演示与、或、非操作。

**文言实现**：
```wenyan
吾有二爻 曰陽 曰陰。名之曰「甲」曰「乙」。
且「甲」以「乙」。書之。
或「甲」以「乙」。書之。
變「甲」。書之。
變「乙」。書之。
```

### 示例10：模块导入
**自然语言需求**：导入一个名为"数学"的模块，使用其中的"圆周率"常量和"开方"函数。

**文言实现**：
```wenyan
吾嘗觀「數學」之書。方悟「圓周率」「開方」之義。

夫「圓周率」。書之。
施「開方」於十六。書之。
```
