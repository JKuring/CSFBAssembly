package com.eastcom.csfb.storm.base.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Exchanger;
import java.util.concurrent.atomic.AtomicBoolean;

@Deprecated
public class ObjectExchanger<T> {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ObjectFactory<T> poolObjectFactory;

    private Exchanger<T> exchanger = new Exchanger<T>();

    private AtomicBoolean readyToTake = new AtomicBoolean(false);

    public ObjectExchanger(ObjectFactory<T> poolObjectFactory) {
        this.poolObjectFactory = poolObjectFactory;
    }

    /**
     * @return
     */
    public T create() {
        return poolObjectFactory.create();
    }

    public T take() throws InterruptedException {
        readyToTake.set(true);
        return exchanger.exchange(create());
    }

    public T take(T obj) throws InterruptedException {
        readyToTake.set(true);
        return exchanger.exchange(obj);
    }

    /**
     * 线程交换数据
     *
     * @param object
     * @return
     * @throws InterruptedException
     */
    public T restore(T object) throws InterruptedException {
        // readyToTake与readyToTake.compareAndSet(期望值, 新值)；
        // readyToTake与“期望值”相等，则返回true，并且readyToTake赋值为“新值”
        boolean e = readyToTake.compareAndSet(true, false);
        if (e) {
            return exchanger.exchange(object);// 两个线程交换object数据
        } else {
            return object;
        }

    }
}
