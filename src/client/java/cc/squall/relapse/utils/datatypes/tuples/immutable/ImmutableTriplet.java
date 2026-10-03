package cc.squall.relapse.utils.datatypes.tuples.immutable;


import cc.squall.client.utils.datatypes.tuples.Triplet;
import cc.squall.client.utils.datatypes.tuples.Unit;

/**
 * @author cedo
 * @since 05/24/2022
 */
public final class ImmutableTriplet<A, B, C> extends Triplet<A, B, C> {
    private final A a;
    private final B b;
    private final C c;

    private ImmutableTriplet(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public static <A, B, C> ImmutableTriplet<A, B, C> of(A a, B b, C c) {
        return new ImmutableTriplet<>(a, b, c);
    }

    // extraction
    public Unit<A> unitOfFirst() {
        return Unit.of(a);
    }

    public Unit<B> unitOfSecond() {
        return Unit.of(b);
    }

    public Unit<C> unitOfThird() {
        return Unit.of(c);
    }

    // replication
    public Triplet<A, A, A> pairOfFirst() {
        return Triplet.of(a);
    }

    public Triplet<B, B, B> pairOfSecond() {
        return Triplet.of(b);
    }

    public Triplet<C, C, C> pairOfThird() {
        return Triplet.of(c);
    }

    @Override
    public A getFirst() {
        return a;
    }

    @Override
    public B getSecond() {
        return b;
    }

    @Override
    public C getThird() {
        return c;
    }

    @Override
    public <R> R apply(TriFunction<? super A, ? super B, ? super C, ? extends R> func) {
        return func.apply(a, b, c);
    }

    @Override
    public void use(TriConsumer<? super A, ? super B, ? super C> func) {
        func.accept(a, b, c);
    }

    @Override
    public String toString() {
        return "ImmutableTriplet[" + a + ", " + b + ", " + c + "]";
    }
}