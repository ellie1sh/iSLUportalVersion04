public class DoublyLinkedNode<T> {

    private T data;
    private DoublyLinkedNode<T> next;
    private DoublyLinkedNode<T> prev;

    public DoublyLinkedNode (T data){
        this.data = data;
        this.next = null;
        this.prev = null;
    }

    public T getData(){
        return this.data;
    }

    public void setNext(DoublyLinkedNode<T> next){
        this.next = next;
    }

    public DoublyLinkedNode<T> getNext(){
        return this.next;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setPrev(DoublyLinkedNode<T> prev) {
        this.prev = prev;
    }

    public DoublyLinkedNode<T> getPrev() {
        return prev;
    }
}
