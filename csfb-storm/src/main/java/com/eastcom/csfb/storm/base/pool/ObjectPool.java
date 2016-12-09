package com.eastcom.csfb.storm.base.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;

public class ObjectPool<T> {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ObjectFactory<T> poolObjectFactory;

    private final ArrayBlockingQueue<T> available;

    /**
     * 构造方法，初始化参数
     *
     * @param poolObjectFactory
     * @param maxSize
     */
    public ObjectPool(ObjectFactory<T> poolObjectFactory, int maxSize) {
        this.poolObjectFactory = poolObjectFactory;
        this.available = new ArrayBlockingQueue<T>(maxSize);
    }

    /**
     * @return
     */
    public T create() {
        return poolObjectFactory.create();
    }

    /**
     * 抽取队列头元素，并把其从队列头移除，如果队列没有元素，则等待，直到有元素为止。
     *
     * @return
     * @throws InterruptedException
     */
    public T take() throws InterruptedException {
        return available.take();
    }

    /**
     * 抽取队列头元素，并把其从队列头移除，如果队列没有元素，则返回null
     *
     * @return
     * @throws InterruptedException
     */
    public T poll() throws InterruptedException {
        return available.poll();
    }

    public T restore(T object) {
        // 添加元素object到队尾，如果队列满了，则返回false，如果成功插入，则返回true。
        boolean succ = available.offer(object);
        if (succ) {
            return create();
        } else {
            return object;
        }
    }

}
