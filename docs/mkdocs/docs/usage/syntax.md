# Syntax

The syntax of this project is largely the same as the original [Wenyan](https://github.com/wenyan-lang/wenyan) project [syntax](https://github.com/wenyan-lang/wenyan/wiki/Syntax-Cheatsheet), with some minor changes.

The syntax is designed to be fully compatible with the original syntax, meaning code that runs on the original version can be used directly in this project. However, most feature caused by compiling to JavaScript, have not been extended.

## Functions
- All operators (e.g., `加`, `減`) have been replaced with functions, allowing them to be used as function calls. For example: `取三以施加, 施加於一以一`.
- `書` is now an operator and can be used as a normal function, e.g., `書一`.

## Pronouns
- The pronoun `其` now only pops one value. However, the order of popping in a single call is undefined. For example, `除其以其` may produce unexpected results. ~~A tribute to the GCC compiler (?~~
- A new pronoun `之` has been added. It behaves like `其` but does not pop the element from the answer stack. As with `其`, avoid using `之` and `其` in the same function call. This is already used in the original syntax, e.g., `書之`, `名之曰`.

## Data
- Attribute referencing can now be used anywhere a normal data value is expected. For example: `加「甲」之「乙」於一`.
- `夫` can now be paired with any data, e.g., `夫其`.
- `名之曰` is now an independent statement that defines a variable equal to the top value of the stack.

## Type
- The language is currently weakly typed. It only attempts to cast values during assignment (e.g., `昔之...者 今...是矣`) and function calls.
- When adding two different types of values, the language attempts to cast them to a compatible type.

## Object
- Object definitions now define a type, and objects can be instantiated using `造「物」` or `施「物」` to call their constructor. The original method of defining objects is retained as static variables of the class.
- Functions can be declared as `物之「「甲」」者術....`
- To declare non-static variables, define attributes in the constructor, e.g., `名之曰己之「「甲」」`, similar to Python. ~~Great, it’s Python.~~
- Objects can now be extended using `繼「物」` after `吾有一物`. This only extends functions.
- Two new variables are introduced: `己`, which refers to the instance of the class, and `父`, which refers to the superclass type.
- Functions can be static by omitting `己`.

## Unimplemented
- Macros
- Error handling

