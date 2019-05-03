package it.bz.idm.bdp.augeg4.util;

import java.util.concurrent.LinkedBlockingQueue;

public class FixedQueue<T> extends LinkedBlockingQueue<T> {
    int max;

    public FixedQueue(int size) {
        super(size);
        this.max = size;
    }

    @Override
    public boolean add(T o) {
        if (this.size()>=max) {
            remove();
        }
        return super.add(o);
    }
}
