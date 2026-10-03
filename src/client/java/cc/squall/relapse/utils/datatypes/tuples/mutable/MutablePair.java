package cc.squall.relapse.utils.datatypes.tuples.mutable;


import cc.squall.client.utils.datatypes.tuples.Pair;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

/**
 * @author cedo
 * @since 05/24/2022
 */
public class MutablePair<A, B> extends Pair<A, B> {
    private A a;
    private B b;

    private MutablePair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public static <A, B> MutablePair<A, B> of(A a, B b) {
        return new MutablePair<>(a, b);
    }

    public static <A> MutablePair<A, A> of(A a) {
        return new MutablePair<>(a, a);
    }

    // extraction
    public MutableUnit<A> unitOfFirst() {
        return MutableUnit.of(a);
    }

    public MutableUnit<B> unitOfSecond() {
        return MutableUnit.of(b);
    }

    // replication
    public MutablePair<A, A> pairOfFirst() {
        return of(a);
    }

    public MutablePair<B, B> pairOfSecond() {
        return of(b);
    }

    @Override
    public A getFirst() {
        return a;
    }

    public void setFirst(A a) {
        this.a = a;
    }

    @Override
    public B getSecond() {
        return b;
    }

    public void setSecond(B b) {
        this.b = b;
    }

    @Override
    public <R> R apply(BiFunction<? super A, ? super B, ? extends R> func) {
        return func.apply(a, b);
    }

    @Override
    public void use(BiConsumer<? super A, ? super B> func) {
        func.accept(a, b);
    }

    public void computeFirst(UnaryOperator<A> operator) {
        this.a = operator.apply(a);
    }

    public void computeSecond(UnaryOperator<B> operator) {
        this.b = operator.apply(b);
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "MutablePair[" + a + ", " + b + "]";
    }
}