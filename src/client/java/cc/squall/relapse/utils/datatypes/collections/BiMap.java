package cc.squall.relapse.utils.datatypes.collections;

import java.util.*;

public class BiMap<K, V> {
    private enum Null { INSTANCE }
    private static final Object NULL = Null.INSTANCE;

    private final Map<Object, Object> forwardMap;
    private final Map<Object, Object> backwardMap;

    public BiMap() {
        this.forwardMap = new HashMap<>();
        this.backwardMap = new HashMap<>();
    }

    public BiMap(int size) {
        this.forwardMap = new HashMap<>(size);
        this.backwardMap = new HashMap<>(size);
    }

    private static <T> Object mask(T value) {
        return value == null ? NULL : value;
    }

    @SuppressWarnings("unchecked")
    private static <T> T unmask(Object value) {
        return value == NULL ? null : (T) value;
    }

    public V get(K key) {
        Object mk = mask(key);
        if (!forwardMap.containsKey(mk)) {
            return null;
        }
        return unmask(forwardMap.get(mk));
    }

    public K inverse(V value) {
        Object mv = mask(value);
        if (!backwardMap.containsKey(mv)) {
            return null;
        }
        return unmask(backwardMap.get(mv));
    }

    public void put(K key, V value) {
        Object mk = mask(key);
        Object mv = mask(value);

        // Remove any existing pairing for this key.
        Object oldValue = forwardMap.put(mk, mv);
        if (oldValue != null) {
            backwardMap.remove(oldValue);
        }
        Object oldKey = backwardMap.put(mv, mk);
        if (oldKey != null) {
            forwardMap.remove(oldKey);
        }
    }

    public void remove(K key) {
        Object mk = mask(key);
        if (forwardMap.containsKey(mk)) {
            Object mv = forwardMap.remove(mk);
            backwardMap.remove(mv);
        }
    }

    public void removeInverse(V value) {
        Object mv = mask(value);
        if (backwardMap.containsKey(mv)) {
            Object mk = backwardMap.remove(mv);
            forwardMap.remove(mk);
        }
    }

    public Set<K> getKeys() {
        Set<K> keys = new HashSet<>(forwardMap.size());
        for (Object mk : forwardMap.keySet()) {
            keys.add(unmask(mk));
        }
        return Collections.unmodifiableSet(keys);
    }

    public Set<V> getValues() {
        Set<V> values = new HashSet<>(backwardMap.size());
        for (Object mv : backwardMap.keySet()) {
            values.add(unmask(mv));
        }
        return Collections.unmodifiableSet(values);
    }
}