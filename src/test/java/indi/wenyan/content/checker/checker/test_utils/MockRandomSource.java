package indi.wenyan.content.checker.checker.test_utils;

import indi.wenyan.judou.utils.function.Either;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A mock implementation of {@link RandomSource} for testing purposes.
 * This class allows predefined sequences of random values to be injected,
 * enabling deterministic testing of code that depends on random number generation.
 * <p>
 * use {@link InputBuilder} to create the sequence of inputs.
 */
@SuppressWarnings({"NullableProblems", "OptionalGetWithoutIsPresent"})
public class MockRandomSource implements RandomSource {
    private final List<Either<Long, Double>> outputSequence;
    private int outputCounter = 0;

    /**
     * Constructs a new MockRandomSource with the specified output sequence.
     *
     * @param outputSequence the predefined sequence of random values to return
     */
    private MockRandomSource(List<Either<Long, Double>> outputSequence) {
        this.outputSequence = outputSequence;
    }

    @Override
    public @Nullable RandomSource fork() {
        return null;
    }

    @Override
    public @Nullable PositionalRandomFactory forkPositional() {
        return null;
    }

    @Override
    public void setSeed(long l) {
    }

    @Override
    public int nextInt() {
        return Math.toIntExact(nextLong());
    }

    @Override
    public int nextInt(int i) {
        return nextInt();
    }

    @Override
    public int nextIntBetweenInclusive(int min, int maxInclusive) {
        return nextInt();
    }

    @Override
    public long nextLong() {
        return outputSequence.get(outputCounter++).left().get();
    }

    @Override
    public boolean nextBoolean() {
        return nextLong() > 0;
    }

    @Override
    public float nextFloat() {
        return (float) nextDouble();
    }

    @Override
    public double nextDouble() {
        return outputSequence.get(outputCounter++).right().get();
    }

    @Override
    public double nextGaussian() {
        return nextDouble();
    }

    @Override
    public double triangle(double mean, double spread) {
        return nextDouble();
    }

    @Override
    public float triangle(float mean, float spread) {
        return nextFloat();
    }

    @Override
    public int nextInt(int origin, int bound) {
        return nextInt();
    }

    /**
     * Builder for creating MockRandomSource instances with predefined output sequences.
     * Provides a fluent API for constructing test data.
     */
    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public static class InputBuilder {
        private final List<Either<Long, Double>> outputSequence;

        /**
         * Constructs a new InputBuilder with an empty output sequence.
         *
         * @param outputSequence the list to store predefined random values
         */
        private InputBuilder(List<Either<Long, Double>> outputSequence) {
            this.outputSequence = outputSequence;
        }

        /**
         * Creates a new InputBuilder instance.
         *
         * @return a new InputBuilder
         */
        public static InputBuilder create() {
            return new InputBuilder(new ArrayList<>());
        }

        /**
         * Adds a long value to the output sequence.
         *
         * @param l the long value to add
         * @return this builder for method chaining
         */
        public InputBuilder addLong(long l) {
            outputSequence.add(Either.left(l));
            return this;
        }

        /**
         * Adds a boolean value to the output sequence.
         * Boolean values are stored as longs (true=1, false=0).
         *
         * @param b the boolean value to add
         * @return this builder for method chaining
         */
        public InputBuilder addBoolean(boolean b) {
            outputSequence.add(Either.left(b ? 1L : 0L));
            return this;
        }

        /**
         * Adds a double value to the output sequence.
         *
         * @param d the double value to add
         * @return this builder for method chaining
         */
        public InputBuilder addDouble(double d) {
            outputSequence.add(Either.right(d));
            return this;
        }

        /**
         * Adds a sequence of values to the output sequence.
         * Supports Long, Integer, Double, Float, and Boolean types.
         *
         * @param seq the sequence of values to add
         * @return this builder for method chaining
         * @throws IllegalStateException if an unsupported type is encountered
         */
        public InputBuilder addSeq(Object... seq) {
            for (var v : seq) {
                switch (v) {
                    case Long l -> addLong(l);
                    case Integer i -> addLong(i.longValue());
                    case Double d -> addDouble(d);
                    case Float f -> addDouble(f.doubleValue());
                    case Boolean b -> addBoolean(b);
                    default -> throw new IllegalStateException("Unexpected value: " + v);
                }
            }
            return this;
        }

        public MockRandomSource build() {
            return new MockRandomSource(outputSequence);
        }
    }
}
