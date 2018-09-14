public class DoublyLinkedList<T> {

    protected class ListNode<T> {

        private T data;
        private ListNode<T> next;
        private ListNode<T> previous;

        public ListNode(T data, ListNode<T> next, ListNode<T> previous) {
            this.data = data;
            this.next = next;
            this.previous = previous;
        }

        public ListNode(T data) {
            this(data, null, null);
        }

    }

    public ListNode<T> head;
    public ListNode<T> tail;
    private int size;

    public DoublyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public DoublyLinkedList(T[] data) {
        if(data.length == 0) {
            this.head = null;
            this.tail = null;
            this.size = 0;
        } else {
            for(int i = 0; i < data.length; i++) {
                this.enqueue(data[i]);
            }

            this.size = data.length;
        }
    }

    public T get(int index) {
        if((this.head == null) || (index >= this.size)) {
            return null;
        }

        ListNode<T> reference = this.head;

        for(int i = 0; i < index; i++) {
            reference = reference.next;
        }

        return reference.data;

    }

    public Boolean remove(int index) {
        if((this.head == null) || (index >= this.size)) {
            return false;
        }

        ListNode<T> reference = this.head;

        for(int i = 0; i < index; i++) {
            reference = reference.next;
        }

        removeNode(reference);

        return true;
    }

    public Boolean remove(T data) {
        if(this.head == null) {
            return false;
        }

        ListNode<T> reference = this.head;

        for(int i = 1; i < this.size; i++) {
            if(reference.data == data) {
                removeNode(reference);
                return true;
            } else {
                reference = reference.next;
            }
        }

        return false;
    }

    public void enqueue(T element) throws IllegalStateException {

        if(this.size == 0) {
            this.head = new ListNode<>(element);
            this.tail = this.head;
        } else {
            addAfter(element, this.tail);
        }
    }

    public T dequeue() throws IndexOutOfBoundsException {
        if(this.size == 0) {
            throw new IndexOutOfBoundsException();
        }

        ListNode<T> oldHead = this.head;

        this.head = this.head.next;
        this.head.previous = null;

        this.size--;

        return oldHead.data;
    }

    public int size() {
        return this.size;
    }

    private void removeNode(ListNode<T> node) {
        if(node.previous == null) {

            //Removing only element from list
            if(node.next == null) {
                this.head = null;
                this.tail = null;
                this.size--;
                return;

            //Removing head
            } else {
                node.next.previous = null;
            }

        //Removing tail
        } else if(node.next == null) {
            node.previous.next = null;

        //Removing middle node
        } else {
            node.previous.next = node.next;
            node.next.previous = node.previous;
        }
    }

    private void addAfter(T data, ListNode<T> addAfter) {
        ListNode<T> newNode = new ListNode(data, addAfter, addAfter.next);
        addAfter.next.previous = newNode;
        addAfter.next = newNode;
        this.size++;
    }
}