package cc.squall.relapse.utils.datatypes.lists;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Random;

public class FastDefaultList<T> extends AbstractList<T> {

    private static final int MAX_HEIGHT = 32;

    class Node {
        T x;
        Node[] next;
        int[] length;

        @SuppressWarnings("unchecked")
        public Node(T ix, int h) {
            x = ix;
            next = (Node[]) Array.newInstance(Node.class, h + 1);
            length = new int[h + 1];
        }

        public int height() {
            return next.length - 1;
        }
    }

    protected Node sentinel;

    int h;

    int n;

    Random rand;

    public FastDefaultList() {
        sentinel = new Node(null, MAX_HEIGHT);
        h = 0;
        n = 0;
        rand = new Random();
    }

    public class CreatePair {
        Node u;
        int j;

        public CreatePair(Node a, int i) {
            u = a;
            j = i;
        }
    }

    protected CreatePair findPred(int i) {
        Node u = sentinel;
        int r = h;
        int j = -1;
        while (r >= 0) {
            while (u.next[r] != null && j + u.length[r] < i) {
                j += u.length[r];
                u = u.next[r];
            }
            r--;
        }
        return new CreatePair(u, j);
    }

    private boolean hasNodeAt(int i) {
        CreatePair p = findPred(i);
        return p.u.next[0] != null && p.j + p.u.length[0] == i;
    }

    @Override
    public T get(int i) {
        if (i < 0) throw new IndexOutOfBoundsException("index " + i);
        CreatePair p = findPred(i);
        if (p.u.next[0] != null && p.j + p.u.length[0] == i) {
            return p.u.next[0].x;
        }
        return null;
    }

    @Override
    public T set(int i, T x) {
        if (i < 0) throw new IndexOutOfBoundsException("index " + i);

        CreatePair p = findPred(i);

        if (p.u.next[0] != null && p.j + p.u.length[0] == i) {
            Node u = p.u.next[0];
            T old = u.x;
            u.x = x;
            return old;
        }

        if (x == null) return null;

        Node w = new Node(x, pickHeight());
        if (w.height() > h) h = w.height();
        add(i, w, 1);
        n++;
        return null;
    }

    protected Node add(int i, Node w, int s) {
        Node u = sentinel;
        int k = w.height();
        int r = h;
        int j = -1;
        while (r >= 0) {
            while (u.next[r] != null && j + u.length[r] < i) {
                j += u.length[r];
                u = u.next[r];
            }
            u.length[r] += s;
            if (r <= k) {
                w.next[r] = u.next[r];
                u.next[r] = w;
                w.length[r] = u.length[r] - (i - j);
                u.length[r] = i - j;
            }
            r--;
        }
        return u;
    }

    public Node add(int i, Node u) {
        return add(i, u, 1);
    }

    @Override
    public void add(int i, T x) {
        if (i < 0) throw new IndexOutOfBoundsException("index " + i);
        if (x == null) return;
        if (hasNodeAt(i)) return;

        Node w = new Node(x, pickHeight());
        if (w.height() > h) h = w.height();
        add(i, w, 1);
        n++;
    }

    @Override
    public T remove(int i) {
        if (i < 0) throw new IndexOutOfBoundsException("index " + i);

        T removed = null;
        Node u = sentinel;
        int r = h;
        int j = -1;

        while (r >= 0) {
            while (u.next[r] != null && j + u.length[r] < i) {
                j += u.length[r];
                u = u.next[r];
            }

            if (u.next[r] != null && j + u.length[r] == i) {
                if (r == 0) removed = u.next[r].x;
                u.length[r] += u.next[r].length[r] - 1;
                u.next[r] = u.next[r].next[r];

                if (u == sentinel && u.next[r] == null) h--;
            } else {
                u.length[r] -= 1;
            }
            r--;
        }

        if (removed != null) n--;
        return removed;
    }

    protected int pickHeight() {
        int z = rand.nextInt();
        int k = 0;
        int m = 1;
        while ((z & m) != 0) {
            k++;
            m <<= 1;
        }
        return Math.min(k, MAX_HEIGHT);
    }

    @Override
    public int size() {
        return n;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = -1;
        Node u = sentinel;
        while (u.next[0] != null) {
            i += u.length[0];
            u = u.next[0];
            sb.append(" ").append(i).append("=>").append(u.x);
        }
        return sb.toString();
    }
}