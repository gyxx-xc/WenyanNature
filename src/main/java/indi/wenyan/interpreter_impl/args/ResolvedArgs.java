package indi.wenyan.interpreter_impl.args;

@SuppressWarnings("unchecked")
public record ResolvedArgs(Object[] values) {
    public <T> T get(int index) { return (T) values[index]; }
}
