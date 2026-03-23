## feature

### apperance

paper

### function

1. put(blocked)
2. take(blocked)
3. offer?(non-blocked, return bool)
4. poll?(non-blocked, return null if empty)
5. peek?(return null if empty)
6. clear
7. size

## impl

array deque inner, also two queue store producer and consumer.
differ from java, since hard to run javacall after unblock.
if blocking, the rest of the request is done by the unblocker (producer do put, consumer do take).
note that all request is in single thread.

#### put

1. if full, block
2. else:
3. put inside the queue
4. get one consumer, currently consumer is blocked from take
5. do consumer's take
   but no need do unblock producer, cause currently producer should be empty
6. unblock

for producer should be empty assume. any operation that decrease
the size of array deque will filled from the producer queue first, so that, since array deque not
full,
indicated that all producer queue is already filled in and producer queue is empty.

#### take

1. if empty, block
2. else:
3. take from the queue
4. get one producer, currently producer is blocked from put
5. do producer's put, same we don't need to do unblock consumer
6. unblock

#### offer/pool

1. if full/empty, return false
2. else: same as put/take

#### peek

if empty: return null
else: just peek

#### size

just return queue size

#### clear
1. if empty: do nothing
2. clear queue
3. while: get the producer until queue full (i.e. get min(capacity, producer size))
4. do producer's put, don't need to do unblock, since consumer is empty (base on fact that queue is not empty)