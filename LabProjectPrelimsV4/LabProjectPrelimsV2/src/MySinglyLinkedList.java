/**
 * AUTHOR: John Carlo Palipa
 * Activity: Individual Exercises Prelims
 * SUBJECT: DATA STRUCTURE IT212 9458
 */

public class MySinglyLinkedList<T> {
    private Node<T> head;
    private int size;

    public MySinglyLinkedList() {
        this.head = null;
        this.size = 0;
    }

    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(newNode);
        }
        size++;
    }

    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);
        newNode.setNext(head);
        head = newNode;
        size++;
    }
    public T getFirst() {
        if (head == null) {
            return null;
        }
        return head.getData();
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }
        return current.getData();
    }

    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (index == 0) {
            head = head.getNext();
        } else {
            Node<T> current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.getNext();
            }
            current.setNext(current.getNext().getNext());
        }
        size--;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node<T> current = head;
        while (current != null) {
            sb.append(current.getData());
            if (current.getNext() != null) {
                sb.append(" ");
            }
            current = current.getNext();
        }
        return sb.toString();
    }


}