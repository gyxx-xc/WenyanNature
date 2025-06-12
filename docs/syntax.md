# Syntax

The syntax of this project is largely the same as the original [Wenyan](https://github.com/wenyan-lang/wenyan) project, with some minor changes.

The syntax is designed to be fully compatible with the original syntax, meaning code that runs on the original version can be used directly in this project. However, some features, such as calling JavaScript functions, have not been extended.

## Functions
- All operators (e.g., add, mul) have been replaced with functions, allowing them to be used as function calls. For example: `取三以施加, 施加於一以一`.
- `print` is now an operator and can be used as a standard function, e.g., `書一`.

## Pronouns
- The pronoun `qi` now only pops one value. However, the order of popping in a single call is undefined. For example, `除其以其` may produce unexpected results. ~~A tribute to the GCC compiler (?~~
- A new pronoun `zhi` has been added. It behaves like `qi` but does not pop the element from the answer stack. As with `qi`, avoid using `zhi` and `qi` in the same parameter call. This is already used in the original syntax, e.g., `書之`, `名之曰`.

## Data
- Attribute referencing can now be used anywhere a normal data value is expected. For example: `加「甲」之「乙」於一`.
- `fu` can now be paired with any data, e.g., `夫其`.
- `名之曰` is now an independent statement that defines a variable equal to the top value of the stack.

## Type
- The language is currently weakly typed. It attempts to cast values during assignment (e.g., `昔之...者 今...是矣`) and function calls.
- When adding two different types of values, the language attempts to cast them to a compatible type.

## Object
- Object definitions now define a type, and objects can be instantiated using `造「物」` or `施「物」` to call their constructor. The original method of defining objects is retained as static variables of the class.
- Functions can be declared as `物之「「甲」」者術....`
- To declare non-static variables, define attributes in the constructor, e.g., `名之曰己之「「甲」」`, similar to Python. ~~Great, it’s Python.~~
- Objects can now be extended using 繼「物」 after 吾有一物. This only extends functions.
- Two new variables are introduced: `self` (`己`), which refers to the instance of the class, and `super` (`父`), which refers to the superclass type.
- Functions can be static by omitting `self`.

## Unimplemented
- Macros
- Error handling

