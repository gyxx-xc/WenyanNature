# 阵眼

## feature

a block used for controlling large amount of fu.
should has difference/easier to the originally calling function (i.e. import)

### apperance

modeling, but still need some rendering effect in game, showing this block is powerful.
showing the range of captured fu.

### function

function provided by a set of wenyan function.

1. void start(string... fu_name): start a set of fu, this reach range should be a litte bit futher
   then the fu (r = 10)
2. status status(string fu_name): return the status of a fu which started by this module, return status should be one of:
   not-running, running, error
3. void join(): waiting all fu started by this is done

the fu search and call should be cached.

## impl

maintain a cache of map<name, pos>, for every time use string, check the cache first.
otherwise, search the map.
