package cc.squall.relapse.utils.datatypes.lists;

public class DefaultList<T> {

    private final FastDefaultList<T> inner = new FastDefaultList<>();

    public T get(int i) {
        if (i < 0) throw new IndexOutOfBoundsException("index " + i);
        return inner.get(i);
    }

    public T set(int i, T x) {
        if (i < 0) throw new IndexOutOfBoundsException("index " + i);
        if (x == null) return inner.get(i);
        return inner.set(i, x);
    }

    public void add(int i, T x) {
        if (i < 0) throw new IndexOutOfBoundsException("index " + i);
        if (x != null) inner.add(i, x);
    }

    public T remove(int i) {
        if (i < 0) throw new IndexOutOfBoundsException("index " + i);
        return inner.remove(i);
    }

    public int count() {
        int c = 0;
        FastDefaultList<T>.Node u = inner.sentinel;
        while (u.next[0] != null) {
            u = u.next[0];
            c++;
        }
        return c;
    }

    @Override
    public String toString() {
        return inner.toString();
    }
}