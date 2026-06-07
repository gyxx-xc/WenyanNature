package indi.wenyan.interpreter_impl.args;

import indi.wenyan.judou.api.WenyanException;
import indi.wenyan.judou.api.values.IWenyanValue;

import java.util.List;

public class ArgExtractor<T> {
    private final Extractor<T> extractor;
    private final List<Processor<T>> processors;

    public ArgExtractor(Extractor<T> extractor, List<Processor<T>> processors) {
        this.extractor = extractor;
        this.processors = processors;
    }

    public T extract(IWenyanValue value) throws WenyanException {
        T result = extractor.extract(value);
        for (Processor<T> processor : processors) {
            result = processor.process(result);
        }
        return result;
    }

    @FunctionalInterface
    public interface Extractor<T> {
        T extract(IWenyanValue value) throws WenyanException;
    }

    @FunctionalInterface
    public interface Processor<T> {
        T process(T value) throws WenyanException;
    }
}
