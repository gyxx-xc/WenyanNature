# exec function

a poc method for rpc

make it able to run the function at other's fu

## design

1. reference other fu, better to use current impled import, import(fu) → obj fu
2. give a function exec(fu, function) → future to run
3. able to wait future

## impl

1. import behavior of import fu will not return package now. return a fu's object
   fu's object will overwrite getAttr to make turn to package lazy, transparent to user
   fu's content will contain a blockPos?
2. to exec, we need the other's fu's block entity