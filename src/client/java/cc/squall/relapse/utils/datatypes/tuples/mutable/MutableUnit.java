package cc.squall.relapse.utils.datatypes.tuples.mutable;


import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author cedo
 * @since 05/24/2022
 */
public class MutableUnit<A> implements Serializable {

    private A a;

    private MutableUnit(A a) {
        this.a = a;
    }

    public static <A> MutableUnit<A> of(A a) {
        return new MutableUnit<>(a);
    }

    public A get() {
        return a;
    }

    public void set(A a) {
        this.a = a;
    }

    public <R> R apply(Function<? super A, ? extends R> func) {
        return func.apply(a);
    }

    public void use(Consumer<? super A> func) {
        func.accept(a);
    }

    public void compute(UnaryOperator<A> mapper) {
        this.a = mapper.apply(a);
    }

    @Override
    public String toString() {
        return "MutableUnit[" + a + "]";
    }
}