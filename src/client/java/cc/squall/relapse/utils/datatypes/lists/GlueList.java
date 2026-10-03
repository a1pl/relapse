package cc.squall.relapse.utils.datatypes.lists;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * GlueList is a brand new List implementation which is way faster than ArrayList and LinkedList.
 * This implementation inspired from ArrayList and LinkedList working mechanism.
 * <br>
 * Nodes holding data in arrays, in the beginning the world just like ArrayList ,inserts data into array one by one when there is no space for insertion to array
 * new Node will be created and linked with the last Node.
 * <br>
 * The array which belongs to newly created node has half of the size of list , just like ArrayList.
 * In ArrayList when there is no space for it it creates new array with double of old size and inserts old data into new one.
 * Unlike ArrayList GlueList does it dynamically way with creating new node so old data does NOT have to be moved to another array.
 * You can think that GlueList is dynamic version of ArrayList.
 * <br>
 * Adding and removing operations much faster than ArrayList and LinkedList.
 * Searching operations nearly same with ArrayList and way better than LinkedList.
 * <p/>
 * Best Case<br>
 * Add O(1)<br>
 * Remove O(1)<br>
 * Search O(1)<br>
 * Access O(1)
 * <br><br>
 * "m" number of created nodes.<br>
 * "n" size of node array.<br>
 * If you insert 10_000_000 record into List there will be just 36 nodes.<br><br>
 * Worst Case<br>
 * Add O(n*m)<br>
 * Remove O(n*m)<br>
 * Search O(m)<br>
 * Access O(m)
 * <p/>
 * version v1.0
 * <p/>
 * Date: 03.11.2015
 * <p/>
 *
 * @author Ertuğrul Çetin ~ ertu.ctn@gmail.com
 * @see Collection
 * @see List
 * @see LinkedList
 * @see ArrayList
 * @param <T> the type of elements held in this collection
 */
public class GlueList<T> extends AbstractList<T> implements List<T>, Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_CAPACITY = 10;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    transient Node<T> first;
    transient Node<T> last;
    int size;
    int initialCapacity;

    public GlueList() {
        Node<T> initNode = new Node<>(null, null, 0, DEFAULT_CAPACITY);
        first = initNode;
        last = initNode;
    }

    public GlueList(int initialCapacity) {
        this.initialCapacity = (initialCapacity > MAX_ARRAY_SIZE) ? MAX_ARRAY_SIZE : initialCapacity;
        Node<T> initNode = new Node<>(null, null, 0, this.initialCapacity);
        first = initNode;
        last = initNode;
    }

    public GlueList(Collection<? extends T> c) {
        Objects.requireNonNull(c);
        Object[] arr = c.toArray();
        int len = arr.length;

        if (len != 0) {
            Node<T> initNode = new Node<>(null, null, 0, len);
            first = initNode;
            last = initNode;
            System.arraycopy(arr, 0, last.elementData, 0, len);
            last.elementDataPointer += len;
            last.endingIndex = len - 1;
        } else {
            Node<T> initNode = new Node<>(null, null, 0, DEFAULT_CAPACITY);
            first = initNode;
            last = initNode;
        }

        modCount++;
        size += len;
    }

    @Override
    public boolean add(T element) {
        Node<T> l = last;
        if (l.isAddable()) {
            l.add(element);
            // keep endingIndex in sync
            if (l.elementDataPointer - 1 > l.endingIndex) {
                l.endingIndex = l.elementDataPointer - 1;
            }
        } else {
            Node<T> newNode = new Node<>(l, null, size);
            newNode.add(element);
            last = newNode;
            l.next = last;
        }
        modCount++;
        size++;
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void add(int index, T element) {
        rangeCheckForAdd(index);

        Node<T> node = getNodeForAdd(index);

        if (node == null) {
            Node<T> l = last;
            Node<T> newNode = new Node<>(l, null, size);
            last = newNode;
            l.next = last;
            node = newNode;
        }

        if (node == last && node.elementData.length - node.elementDataPointer > 0) {
            int nodeArrIndex = index - node.startingIndex;

            System.arraycopy(
                    node.elementData,
                    nodeArrIndex,
                    node.elementData,
                    nodeArrIndex + 1,
                    node.elementDataPointer - nodeArrIndex
            );

            node.elementData[nodeArrIndex] = element;
            node.elementDataPointer++;

            if (node.elementDataPointer - 1 > node.endingIndex) {
                node.endingIndex = node.elementDataPointer - 1;
            }
        } else {
            int newLen = node.elementData.length + 1;
            T[] newElementData = (T[]) new Object[newLen];

            int nodeArrIndex = index - node.startingIndex;

            System.arraycopy(
                    node.elementData,
                    nodeArrIndex,
                    newElementData,
                    nodeArrIndex + 1,
                    node.elementDataPointer - nodeArrIndex
            );

            newElementData[nodeArrIndex] = element;

            if (nodeArrIndex > 0) {
                System.arraycopy(node.elementData, 0, newElementData, 0, nodeArrIndex);
            }

            node.elementData = newElementData;
            node.endingIndex++;
            node.elementDataPointer++;
        }

        updateNodesAfterAdd(node);

        modCount++;
        size++;
    }

    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
    }

    private void updateNodesAfterAdd(Node<T> nodeFrom) {
        for (Node<T> node = nodeFrom.next; node != null; node = node.next) {
            node.startingIndex++;
            node.endingIndex++;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean addAll(Collection<? extends T> c) {
        Objects.requireNonNull(c);
        Object[] collection = c.toArray();
        int len = collection.length;

        if (len == 0) return false;

        if (size == 0) {
            if (initialCapacity >= len) {
                System.arraycopy(collection, 0, last.elementData, 0, len);
            } else {
                last.elementData = Arrays.copyOf((T[]) collection, len);
            }
            last.elementDataPointer += len;
            last.endingIndex = len - 1;
            modCount++;
            size += len;
            return true;
        }

        int elementDataLen = last.elementData.length;
        int elementSize = last.elementDataPointer;
        int remainedStorage = elementDataLen - elementSize;

        if (remainedStorage == 0) {
            Node<T> l = last;
            int newLen = (size >>> 1);
            int initialLen = (len > newLen) ? len : newLen;

            Node<T> newNode = new Node<>(l, null, size, initialLen);
            System.arraycopy(collection, 0, newNode.elementData, 0, len);
            newNode.elementDataPointer += len;
            newNode.endingIndex = len - 1;
            last = newNode;
            l.next = last;

            modCount++;
            size += len;
            return true;
        }

        if (len <= remainedStorage) {
            System.arraycopy(collection, 0, last.elementData, elementSize, len);
            last.elementDataPointer += len;
            if (last.elementDataPointer - 1 > last.endingIndex) {
                last.endingIndex = last.elementDataPointer - 1;
            }
            modCount++;
            size += len;
            return true;
        }

        // len > remainedStorage
        System.arraycopy(collection, 0, last.elementData, elementSize, remainedStorage);
        last.elementDataPointer += remainedStorage;
        if (last.elementDataPointer - 1 > last.endingIndex) {
            last.endingIndex = last.elementDataPointer - 1;
        }
        size += remainedStorage;

        int newLen = (size >>> 1);
        int remainedDataLen = len - remainedStorage;
        int initialLen = (newLen > remainedDataLen) ? newLen : remainedDataLen;

        Node<T> l = last;
        Node<T> newNode = new Node<>(l, null, size, initialLen);
        System.arraycopy(collection, remainedStorage, newNode.elementData, 0, remainedDataLen);
        newNode.elementDataPointer += remainedDataLen;
        newNode.endingIndex = remainedDataLen - 1;
        last = newNode;
        l.next = last;

        modCount++;
        size += remainedDataLen;
        return true;
    }

    @Override
    public T set(int index, T element) {
        rangeCheck(index);
        Node<T> node = getNode(index);
        int nodeArrIndex = index - node.startingIndex;
        T oldValue = node.elementData[nodeArrIndex];
        node.elementData[nodeArrIndex] = element;
        return oldValue;
    }

    @Override
    public T get(int index) {
        rangeCheck(index);
        Node<T> node = getNode(index);
        return node.elementData[index - node.startingIndex];
    }

    @Override
    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            for (Node<T> node = first; node != null; node = node.next) {
                for (int i = 0; i < node.elementDataPointer; i++) {
                    if (node.elementData[i] == null) return index;
                    index++;
                }
            }
        } else {
            for (Node<T> node = first; node != null; node = node.next) {
                for (int i = 0; i < node.elementDataPointer; i++) {
                    if (o.equals(node.elementData[i])) return index;
                    index++;
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int index = size - 1;
        if (o == null) {
            for (Node<T> node = last; node != null; node = node.pre) {
                for (int i = node.elementDataPointer - 1; i >= 0; i--) {
                    if (node.elementData[i] == null) return index;
                    index--;
                }
            }
        } else {
            for (Node<T> node = last; node != null; node = node.pre) {
                for (int i = node.elementDataPointer - 1; i >= 0; i--) {
                    if (o.equals(node.elementData[i])) return index;
                    index--;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T remove(int index) {
        rangeCheck(index);

        Node<T> node;

        if (size == 2 && first != last) {
            // collapse into a single node holding both remaining elements
            Node<T> newNode = new Node<>(null, null, 0, 2);
            // Walk to find the two elements; they may be spread across nodes
            Object[] all = toArray();
            for (Object o : all) newNode.add((T) o);

            first = last = newNode;
            node = newNode;
        } else {
            node = getNode(index);
        }

        T[] elementData = node.elementData;
        int elementSize = node.elementDataPointer;
        int nodeArrIndex = index - node.startingIndex;

        T oldValue = elementData[nodeArrIndex];

        int numMoved = elementSize - nodeArrIndex - 1;
        if (numMoved > 0) {
            System.arraycopy(
                    node.elementData,
                    nodeArrIndex + 1,
                    node.elementData,
                    nodeArrIndex,
                    numMoved
            );
        }

        if (first == last || node == last) {
            node.elementData[elementSize - 1] = null;
        } else {
            node.elementData = Arrays.copyOf(node.elementData, elementSize - 1);
            node.endingIndex = (--node.endingIndex < 0) ? 0 : node.endingIndex;
        }

        node.elementDataPointer--;

        // keep endingIndex consistent for the last node
        if (node == last) {
            node.endingIndex = node.elementDataPointer == 0
                    ? node.startingIndex
                    : node.startingIndex + node.elementDataPointer - 1;
        }

        updateNodesAfterRemove(node);

        if (node.elementDataPointer == 0 && first != last) {
            Node<T> next = node.next;
            Node<T> prev = node.pre;
            if (prev == null) {
                first = next;
            } else {
                prev.next = next;
                node.pre = null;
            }
            if (next == null) {
                last = prev;
            } else {
                next.pre = prev;
                node.next = null;
            }
            node.elementData = null;
        }

        size--;
        modCount++;
        return oldValue;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        Object[] arr = c.toArray();
        if (arr.length == 0) return false;
        boolean isModified = false;
        for (Object o : arr) {
            isModified |= remove(o);
        }
        return isModified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        if (c.isEmpty()) {
            boolean modified = !isEmpty();
            clear();
            return modified;
        }
        boolean isModified = false;
        Object[] elements = toArray();
        for (Object element : elements) {
            if (!c.contains(element)) {
                isModified |= remove(element);
            }
        }
        return isModified;
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index != -1) {
            remove(index);
            return true;
        }
        return false;
    }

    private void updateNodesAfterRemove(Node<T> fromNode) {
        for (Node<T> node = fromNode.next; node != null; node = node.next) {
            node.startingIndex = (--node.startingIndex < 0) ? 0 : node.startingIndex;
            node.endingIndex = (--node.endingIndex < 0) ? 0 : node.endingIndex;
        }
    }

    private Node<T> getNode(int index) {
        if (first == last) return first;
        int firstStartingIndex = first.startingIndex;
        int firstEndingIndex = first.endingIndex;
        int firstMinDistance = min(abs(index - firstStartingIndex), abs(index - firstEndingIndex));

        int lastStartingIndex = last.startingIndex;
        int lastEndingIndex = last.endingIndex;
        int lastMinDistance = min(abs(index - lastStartingIndex), abs(index - lastEndingIndex));

        if (firstMinDistance <= lastMinDistance) {
            Node<T> node = first;
            while (node != null) {
                if (node.startingIndex <= index && index <= node.endingIndex) {
                    return node;
                }
                node = node.next;
            }
        } else {
            Node<T> node = last;
            while (node != null) {
                if (node.startingIndex <= index && index <= node.endingIndex) {
                    return node;
                }
                node = node.pre;
            }
        }

        throw new IndexOutOfBoundsException("index " + index + " not found");
    }

    private Node<T> getNodeForAdd(int index) {
        if (index == size && !(last.startingIndex <= index && index <= last.endingIndex)) {
            return null;
        }
        return getNode(index);
    }

    private void rangeCheck(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
    }

    @Override
    public void clear() {
        for (Node<T> node = first; node != null; ) {
            Node<T> next = node.next;
            node.next = null;
            node.pre = null;
            node.elementData = null;
            node = next;
        }

        first = last = null;

        int capacity = min(MAX_ARRAY_SIZE, max(size, max(initialCapacity, DEFAULT_CAPACITY)));
        Node<T> initNode = new Node<>(null, null, 0, capacity);
        initialCapacity = capacity;
        first = initNode;
        last = initNode;

        modCount++;
        size = 0;
    }

    public void trimToSize() {
        int pointer = last.elementDataPointer;
        int arrLen = last.elementData.length;
        if (pointer < arrLen && arrLen > 2) {
            if (pointer < 2) {
                last.elementData = Arrays.copyOf(last.elementData, 2);
                last.endingIndex = last.startingIndex + 1;
            } else {
                last.elementData = Arrays.copyOf(last.elementData, pointer);
                last.endingIndex = last.startingIndex + pointer - 1;
            }
        }
    }

    @Override
    public Object[] toArray() {
        Object[] objects = new Object[size];
        int i = 0;
        for (Node<T> node = first; node != null; node = node.next) {
            int len = node.elementDataPointer;
            if (len > 0) {
                System.arraycopy(node.elementData, 0, objects, i, len);
            }
            i += len;
        }
        return objects;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E[] toArray(E[] a) {
        if (a.length < size) {
            return (E[]) Arrays.copyOf(toArray(), size, a.getClass());
        }
        System.arraycopy(toArray(), 0, a, 0, size);
        if (a.length > size) a[size] = null;
        return a;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Itr();
    }

    private class Itr implements Iterator<T> {

        Node<T> node = first;
        int i = 0;
        int j = 0;
        int lastReturn = -1;
        int expectedModCount = modCount;
        int elementDataPointer = node.elementDataPointer;

        @Override
        public boolean hasNext() {
            return j < size;
        }

        @Override
        public T next() {
            checkForComodification();
            if (j >= size) {
                throw new NoSuchElementException();
            }
            if (node == null) {
                throw new NoSuchElementException();
            }
            if (j == 0) {
                node = first;
                elementDataPointer = node.elementDataPointer;
                i = 0;
            }
            //empty
            while (node != null && i >= elementDataPointer) {
                node = node.next;
                i = 0;
                elementDataPointer = (node != null) ? node.elementDataPointer : 0;
            }
            if (node == null) {
                throw new NoSuchElementException();
            }
            T val = node.elementData[i++];
            lastReturn = j++;
            return val;
        }

        @Override
        public void remove() {
            if (lastReturn < 0) {
                throw new IllegalStateException();
            }
            checkForComodification();
            try {
                GlueList.this.remove(lastReturn);
                j = lastReturn;
                lastReturn = -1;
                // reset position from scratch on the node level
                node = first;
                i = 0;
                elementDataPointer = (node != null) ? node.elementDataPointer : 0;
                int remaining = j;
                while (node != null && remaining > 0) {
                    int nodeSize = node.elementDataPointer;
                    if (remaining >= nodeSize) {
                        remaining -= nodeSize;
                        node = node.next;
                        i = 0;
                        elementDataPointer = (node != null) ? node.elementDataPointer : 0;
                    } else {
                        i = remaining;
                        remaining = 0;
                    }
                }
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        checkPositionIndex(index);
        return new ListItr(index);
    }

    private void checkPositionIndex(int index) {
        if (!(index >= 0 && index <= size)) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
    }

    @Override
    public ListIterator<T> listIterator() {
        return new ListItr(0);
    }

    private class ListItr extends Itr implements ListIterator<T> {

        ListItr(int index) {
            node = (index == size) ? last : getNode(index);
            j = index;
            i = (node != null) ? index - node.startingIndex : 0;
            elementDataPointer = (node != null) ? node.elementDataPointer : 0;
        }

        @Override
        public boolean hasPrevious() {
            return j != 0;
        }

        @Override
        public T previous() {
            checkForComodification();
            int temp = j - 1;
            if (temp < 0) {
                throw new NoSuchElementException();
            }
            if (j == size) {
                node = last;
                elementDataPointer = (node != null) ? node.elementDataPointer : 0;
                i = elementDataPointer;
            }
            int index = j - node.startingIndex;
            if (index == 0) {
                node = node.pre;
                if (node == null) throw new NoSuchElementException();
                elementDataPointer = node.elementDataPointer;
                i = elementDataPointer;
            }
            T val = node.elementData[--i];
            j = temp;
            lastReturn = j;
            return val;
        }

        @Override
        public int nextIndex() {
            return j;
        }

        @Override
        public int previousIndex() {
            return j - 1;
        }

        @Override
        public void set(T t) {
            if (lastReturn < 0) {
                throw new IllegalStateException();
            }
            checkForComodification();
            try {
                GlueList.this.set(lastReturn, t);
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void add(T t) {
            checkForComodification();
            try {
                int temp = j;
                GlueList.this.add(temp, t);
                j = temp + 1;
                lastReturn = -1;
                // reset
                node = getNode(Math.min(j, size - 1));
                i = j - node.startingIndex;
                elementDataPointer = node.elementDataPointer;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
    }

    @Override
    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object clone() {
        try {
            GlueList<T> clone = (GlueList<T>) super.clone();

            clone.first = clone.last = null;

            int capacity = min(MAX_ARRAY_SIZE, max(clone.size, max(clone.initialCapacity, DEFAULT_CAPACITY)));
            Node<T> initNode = new Node<>(null, null, 0, capacity);
            clone.initialCapacity = capacity;
            clone.first = clone.last = initNode;
            clone.modCount = 0;
            clone.size = 0;

            for (Node<T> node = first; node != null; node = node.next) {
                for (int i = 0; i < node.elementDataPointer; i++) {
                    clone.add(node.elementData[i]);
                }
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        int expectedModCount = modCount;
        s.defaultWriteObject();
        s.writeInt(size);
        for (Node<T> node = first; node != null; node = node.next) {
            for (int i = 0; i < node.elementDataPointer; i++) {
                s.writeObject(node.elementData[i]);
            }
        }
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();


        Node<T> initNode = new Node<>(null, null, 0, DEFAULT_CAPACITY);
        first = initNode;
        last = initNode;
        size = 0;
        modCount = 0;

        int count = s.readInt();
        for (int i = 0; i < count; i++) {
            add((T) s.readObject());
        }
    }

    static class Node<T> {

        Node<T> pre;
        Node<T> next;
        int listSize;
        int startingIndex;
        int endingIndex;
        T[] elementData;
        int elementDataPointer;

        @SuppressWarnings("unchecked")
        Node(Node<T> pre, Node<T> next, int listSize) {
            this.pre = pre;
            this.next = next;
            this.listSize = listSize;
            int cap = (listSize >>> 1);
            if (cap < DEFAULT_CAPACITY) cap = DEFAULT_CAPACITY;
            this.elementData = (T[]) new Object[cap];
            this.startingIndex = listSize;
            this.endingIndex = listSize + elementData.length - 1;
        }

        Node(Node<T> pre, Node<T> next, int listSize, int initialCapacity) {
            this.pre = pre;
            this.next = next;
            this.listSize = listSize;
            this.elementData = createElementData(initialCapacity);
            this.startingIndex = listSize;
            this.endingIndex = listSize + elementData.length - 1;
        }

        @SuppressWarnings("unchecked")
        T[] createElementData(int capacity) {
            if (capacity == 0 || capacity == 1) {
                return (T[]) new Object[DEFAULT_CAPACITY];
            } else if (capacity > 1) {
                return (T[]) new Object[capacity];
            } else {
                throw new IllegalArgumentException("Illegal Capacity: " + capacity);
            }
        }

        boolean isAddable() {
            return elementDataPointer < elementData.length;
        }

        void add(T element) {
            elementData[elementDataPointer++] = element;
        }

        @Override
        public String toString() {
            return String.format("[sIndex: %d - eIndex: %d | elementDataPointer: %d | elementDataLength: %d]", startingIndex, endingIndex, elementDataPointer, elementData.length);
        }
    }
}