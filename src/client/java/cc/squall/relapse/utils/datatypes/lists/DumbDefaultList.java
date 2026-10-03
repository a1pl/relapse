package cc.squall.relapse.utils.datatypes.lists;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.Map;

public class DumbDefaultList<T> extends AbstractList<T> {

    private final Map<Integer, T> map;

    public DumbDefaultList() {
        map = new HashMap<>();
    }

    @Override
    public int size() {
        return Integer.MAX_VALUE;
    }

    @Override
    public T get(int i) {
        return map.get(i);
    }

    @Override
    public T set(int i, T x) {
        if (x == null) {
            return map.remove(i);
        }
        return map.put(i, x);
    }

    @Override
    public void add(int i, T x) {
        if (x == null) return;

        shiftFrom(i, +1);
        map.put(i, x);
    }

    @Override
    public T remove(int i) {
        T retval = map.remove(i);
        shiftFrom(i + 1, -1);
        return retval;
    }

    private void shiftFrom(int from, int delta) {
        Map<Integer, T> moved = new HashMap<>();
        for (Map.Entry<Integer, T> e : map.entrySet()) {
            if (e.getKey() >= from) {
                moved.put(e.getKey(), e.getValue());
            }
        }
        for (Integer k : moved.keySet()) {
            map.remove(k);
        }
        for (Map.Entry<Integer, T> e : moved.entrySet()) {
            map.put(e.getKey() + delta, e.getValue());
        }
    }
}