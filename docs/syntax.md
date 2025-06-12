# Syntax

The syntax of this project is mostly as same as the origin
[Wenyan](https://github.com/wenyan-lang/wenyan) project, with some
tiny changes.

The syntax is designed to fully compatible with the
origin syntax, which means the code that can run on the original
version can be directly used on this project. However, some feature
like calling a JS function is not extended.

1. All operator (eg. add, mul) are replaced with functions, which
   means these operator can be used as a function call. e.g. 取三以施
   加, 施加於一以一
2. print is now a operator, which can be used as normal e.g. 書一
3. the pronoun `qi` is now only pop one value. However, order of pop
   in one call is undefined. e.g. 除其以其 will cause unexcepted
   result. ~~致敬传奇编译器gcc（？~~
4. added a new pronoun `zhi`, same as `qi` but not pop the element in
   answer stack. Same as `qi`, don't use `zhi` and `qi` in one call of
   parameter.
