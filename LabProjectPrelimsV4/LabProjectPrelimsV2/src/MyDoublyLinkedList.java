import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyDoublyLinkedList<T> implements Iterable<T> {
    private DoublyLinkedNode<T> head;
    private DoublyLinkedNode<T> tail;
    private int size;


    public MyDoublyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void add(T data) {
        addLast(data);
    }

    public void addLast(T data) {
        DoublyLinkedNode<T> newNode = new DoublyLinkedNode<>(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
        }
        size++;
    }

    public void addFirst(T data) {
        DoublyLinkedNode<T> newNode = new DoublyLinkedNode<>(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            head.setPrev(newNode);
            newNode.setNext(head);
            head = newNode;
        }
        size++;
    }

    public void removeFirst() {
        if (head == null) {
            return;
        }
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            head = head.getNext();
            head.setPrev(null);
        }
        size--;
    }

    public void removeLast() {
        if (tail == null) {
            return;
        }
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            tail = tail.getPrev();
            tail.setNext(null);
        }
        size--;
    }

    public int getSize() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public T getFirst() {
        if (head == null) {
            return null;
        }
        return head.getData();
    }
    
    public T getLast() {
        if (tail == null) {
            return null;
        }
        return tail.getData();
    }
    
    public boolean contains(T data) {
        DoublyLinkedNode<T> current = head;
        while (current != null) {
            if (current.getData().equals(data)) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }
    
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
    
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        DoublyLinkedNode<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }
        return current.getData();
    }
    
    public void set(int index, T data) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        DoublyLinkedNode<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }
        current.setData(data);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        DoublyLinkedNode<T> current = head;
        while (current != null) {
            sb.append(current.getData());
            if (current.getNext() != null) {
                sb.append(" <-> ");
            }
            current = current.getNext();
        }
        return sb.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new MyDoublyLinkedListIterator();
    }
    private class MyDoublyLinkedListIterator implements Iterator<T> {
        private DoublyLinkedNode<T> current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T data = current.getData();
            current = current.getNext();
            return data;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove operation not supported");
        }
    }
}