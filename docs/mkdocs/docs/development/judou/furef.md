add a new import statement

for importing the fu as a variable, for better communication for distributed
computing. this import should be async, since the user might need to import
a lot fu once.

### user story

1. they want to run a program through another device(fu), however, current
   has no good way for them to get a fu that is remote. they can only use
   string to get one. but the fu is more behavior like a object (like other
   module), so it's much better to use var to import.
2. do we need to remain the way of import using string also?
3. user want to exec remotely more than locally, cause that's much more
   efficient. however, exec locally is also a very important function to
   reduce code redundancy, we might need to keep both of the two method to
   import.

import:

1. device: same
2. runner0 (not impl yet): current code import(String)
3. runner n: import as device(WenyanCodeWithExecutor)

---

## class

- CREATE `IWenyanPackageable.java`
- CREATE `WenyanCodeWithExecutor.java`
  Key structure:

```java
public record WenyanCodeWithExecutor(IWenyanPackageable packageable)
        implements IWenyanObject {
    public static final WenyanType<WenyanCodeWithExecutor> TYPE = ...;

    @Override
    public WenyanType<?> type() {
        return TYPE;
    }

    @Override
    public IWenyanValue getAttribute(String name) throws WenyanException {
        // NOT IMPLEMENT IN THIS DESIGN
    }
}
```

- MODIFY `BlockPackageGetter.java`
