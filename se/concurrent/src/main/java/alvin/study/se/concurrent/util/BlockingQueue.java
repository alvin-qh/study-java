package alvin.study.se.concurrent.util;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class BlockingQueue<T> {
    private final LinkedList<T> objectList = new LinkedList<>();

    private final Semaphore semaphore;

    public BlockingQueue(int capacity) {
        this.semaphore = new Semaphore(capacity);
    }

    public synchronized void put(T object) throws InterruptedException {
        semaphore.acquire();
        objectList.add(object);
    }
}
