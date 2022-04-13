package it.bz.idm.bdp.augeg4.util;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.LinkedBlockingQueue;

public class FixedQueue<T> extends LinkedBlockingQueue<T> {
    private static final Logger LOG = LoggerFactory.getLogger(FixedQueue.class.getName());

    int max;

    public FixedQueue(int size) {
        super(size);
        this.max = size;
    }

    @Override
    public boolean add(T o) {
        if (this.size()>=max) {
            LOG.error("FIXED QUEUE FULL: discards oldest content");
            remove();
        }
        return super.add(o);
    }
}
