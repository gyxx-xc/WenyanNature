package indi.wenyan.interpreter_impl.args;

import indi.wenyan.judou.api.exec.request.IArgsRequest;
import indi.wenyan.judou.api.values.exception.WenyanException;
import indi.wenyan.judou.api.values.primitive.WenyanBoolean;
import indi.wenyan.judou.api.values.primitive.WenyanDouble;
import indi.wenyan.judou.api.values.primitive.WenyanInteger;
import indi.wenyan.judou.api.values.primitive.WenyanString;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class ArgsSpecBuilder {
    List<ArgExtractor<?>> argExtractors;

    private ArgsSpecBuilder(List<ArgExtractor<?>> argExtractors) {
        this.argExtractors = new ArrayList<>(argExtractors);
    }

    ArgsSpecBuilder() {
        argExtractors = new ArrayList<>();
    }

    public <T> Step<T> first() {
        return new DummyStep<>();
    }


    public abstract class Step<T> {
        final ArgExtractor.Extractor<T> extractor = extractor();
        List<ArgExtractor.Processor<T>> processors = new ArrayList<>();

        protected abstract ArgExtractor.Extractor<T> extractor();

        protected void addToSpec() {
            argExtractors.add(new ArgExtractor<>(extractor, processors));
        }

        public ResolvedArgs resolve(IArgsRequest argsRequest) throws WenyanException {
            addToSpec();
            return new WenyanArgsResolver(argExtractors, argsRequest).resolve();
        }

        public Step<T> copy() {
            return new ArgsSpecBuilder(argExtractors).first();
        }

        public IntStep int_() {
            addToSpec();
            return new IntStep();
        }

        public StringStep string_() {
            addToSpec();
            return new StringStep();
        }

        public DoubleStep double_() {
            addToSpec();
            return new DoubleStep();
        }

        public BooleanStep boolean_() {
            addToSpec();
            return new BooleanStep();
        }

        public DummyStep<T> dummy() {
            addToSpec();
            return new DummyStep<>();
        }
    }

    public class DummyStep<T> extends Step<T> {
        @Override
        protected void addToSpec() { // do not add
        }

        @Override
        protected ArgExtractor.Extractor<T> extractor() {
            return null;
        }
    }

    public class IntStep extends Step<Integer> {
        @Override
        protected ArgExtractor.Extractor<Integer> extractor() {
            return value -> value.as(WenyanInteger.TYPE).value();
        }

        public IntStep range(int min, int max) {
            processors.add(value -> Math.clamp(value, min, max));
            return this;
        }

        public IntStep rangeThrow(int min, int max) {
            processors.add(value -> {
                if (value < min || value > max) {
                    throw new WenyanException("value out of range");
                }
                return value;
            });
            return this;
        }
    }

    public class StringStep extends Step<String> {
        @Override
        protected ArgExtractor.Extractor<String> extractor() {
            return value -> value.as(WenyanString.TYPE).value();
        }

        public StringStep nonEmpty() {
            processors.add(value -> {
                if (value.isEmpty()) {
                    throw new WenyanException("string must not be empty");
                }
                return value;
            });
            return this;
        }
    }

    public class DoubleStep extends Step<Double> {
        @Override
        protected ArgExtractor.Extractor<Double> extractor() {
            return value -> value.as(WenyanDouble.TYPE).value();
        }

        public DoubleStep range(double min, double max) {
            processors.add(value -> Math.clamp(value, min, max));
            return this;
        }

        public DoubleStep rangeThrow(double min, double max) {
            processors.add(value -> {
                if (value < min || value > max) {
                    throw new WenyanException("value out of range");
                }
                return value;
            });
            return this;
        }
    }

    public class BooleanStep extends Step<Boolean> {
        @Override
        protected ArgExtractor.Extractor<Boolean> extractor() {
            return value -> value.as(WenyanBoolean.TYPE).value();
        }
    }
}
