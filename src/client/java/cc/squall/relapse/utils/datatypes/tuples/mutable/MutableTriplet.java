package cc.squall.relapse.utils.datatypes.tuples.mutable;


import cc.squall.relapse.utils.datatypes.tuples.Triplet;

import java.util.function.UnaryOperator;

/**
 * @author cedo
 * @since 05/24/2022
 */
public class MutableTriplet<A, B, C> extends Triplet<A, B, C> {
    private A a;
    private B b;
    private C c;

    private MutableTriplet(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public static <A, B, C> MutableTriplet<A, B, C> of(A a, B b, C c) {
        return new MutableTriplet<>(a, b, c);
    }

    public static <A> MutableTriplet<A, A, A> of(A a) {
        return new MutableTriplet<>(a, a, a);
    }

    // extraction
    public MutableUnit<A> unitOfFirst() {
        return MutableUnit.of(a);
    }

    public MutableUnit<B> unitOfSecond() {
        return MutableUnit.of(b);
    }

    public MutableUnit<C> unitOfThird() {
        return MutableUnit.of(c);
    }

    // replication
    public MutableTriplet<A, A, A> pairOfFirst() {
        return of(a);
    }

    public MutableTriplet<B, B, B> pairOfSecond() {
        return of(b);
    }

    public MutableTriplet<C, C, C> pairOfThird() {
        return of(c);
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
    public C getThird() {
        return c;
    }

    public void setThird(C c) {
        this.c = c;
    }

    @Override
    public <R> R apply(TriFunction<? super A, ? super B, ? super C, ? extends R> func) {
        return func.apply(a, b, c);
    }

    @Override
    public void use(TriConsumer<? super A, ? super B, ? super C> func) {
        func.accept(a, b, c);
    }

    public void computeFirst(UnaryOperator<A> operator) {
        this.a = operator.apply(a);
    }

    public void computeSecond(UnaryOperator<B> operator) {
        this.b = operator.apply(b);
    }

    public void computeThird(UnaryOperator<C> operator) {
        this.c = operator.apply(c);
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
        return "MutableTriplet[" + a + ", " + b + ", " + c + "]";
    }
}