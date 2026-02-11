package indi.wenyan.judou.utils;

import java.util.Objects;
import java.util.Optional;

public abstract class Either<L, R> {
    private static final class Left<L, R> extends Either<L, R> {
        private final L value;

        public Left(final L value) {
            this.value = value;
        }

        @Override
        public Optional<L> left() {
            return Optional.of(value);
        }

        @Override
        public Optional<R> right() {
            return Optional.empty();
        }

        @Override
        public String toString() {
            return "Left[" + value + "]";
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Left<?, ?> left = (Left<?, ?>) o;
            return Objects.equals(value, left.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    private static final class Right<L, R> extends Either<L, R> {
        private final R value;

        public Right(final R value) {
            this.value = value;
        }

        @Override
        public Optional<L> left() {
            return Optional.empty();
        }

        @Override
        public Optional<R> right() {
            return Optional.of(value);
        }

        @Override
        public String toString() {
            return "Right[" + value + "]";
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Right<?, ?> right = (Right<?, ?>) o;
            return Objects.equals(value, right.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    private Either() {
    }

    public abstract Optional<L> left();

    public abstract Optional<R> right();

    public static <L, R> Either<L, R> left(final L value) {
        return new Left<>(value);
    }

    public static <L, R> Either<L, R> right(final R value) {
        return new Right<>(value);
    }
}
