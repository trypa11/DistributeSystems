import java.util.*;

public class SynchronizedQueue<T> {
    private Queue<T> queue = new LinkedList<>();


    public synchronized void add(T item) throws InterruptedException {
        queue.add(item);
        notifyAll();
    }

    public synchronized T poll() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        T item = queue.remove();
        notifyAll();
        return item;
    }
}