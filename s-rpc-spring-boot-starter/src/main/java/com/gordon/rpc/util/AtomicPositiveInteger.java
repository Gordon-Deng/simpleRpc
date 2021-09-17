package com.gordon.rpc.util;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;


public class AtomicPositiveInteger extends Number {

    private static final long serialVersionUID = -3038533876489105940L;
    private static final AtomicIntegerFieldUpdater<AtomicPositiveInteger> indexUpdater = AtomicIntegerFieldUpdater.newUpdater(AtomicPositiveInteger.class, "index");
    private volatile int index = 0;

    public AtomicPositiveInteger() {
    }

    public AtomicPositiveInteger(int initialValue) {
        indexUpdater.set(this, initialValue);
    }

    public final int getAndIncrement() {
        return indexUpdater.getAndIncrement(this) & 2147483647;
    }

    public final int getAndDecrement() {
        return indexUpdater.getAndDecrement(this) & 2147483647;
    }

    public final int incrementAndGet() {
        return indexUpdater.incrementAndGet(this) & 2147483647;
    }

    public final int decrementAndGet() {
        return indexUpdater.decrementAndGet(this) & 2147483647;
    }

    public final int get() {
        return indexUpdater.get(this) & 2147483647;
    }

    public final void set(int newValue) {
        if (newValue < 0) {
            throw new IllegalArgumentException("new value " + newValue + " < 0");
        } else {
            indexUpdater.set(this, newValue);
        }
    }

    public final int getAndSet(int newValue) {
        if (newValue < 0) {
            throw new IllegalArgumentException("new value " + newValue + " < 0");
        } else {
            return indexUpdater.getAndSet(this, newValue) & 2147483647;
        }
    }

    public final int getAndAdd(int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("delta " + delta + " < 0");
        } else {
            return indexUpdater.getAndAdd(this, delta) & 2147483647;
        }
    }

    public final int addAndGet(int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("delta " + delta + " < 0");
        } else {
            return indexUpdater.addAndGet(this, delta) & 2147483647;
        }
    }

    public final boolean compareAndSet(int expect, int update) {
        if (update < 0) {
            throw new IllegalArgumentException("update value " + update + " < 0");
        } else {
            return indexUpdater.compareAndSet(this, expect, update);
        }
    }

    public final boolean weakCompareAndSet(int expect, int update) {
        if (update < 0) {
            throw new IllegalArgumentException("update value " + update + " < 0");
        } else {
            return indexUpdater.weakCompareAndSet(this, expect, update);
        }
    }

    @Override
    public byte byteValue() {
        return (byte)this.get();
    }

    @Override
    public short shortValue() {
        return (short)this.get();
    }

    @Override
    public int intValue() {
        return this.get();
    }

    @Override
    public long longValue() {
        return (long)this.get();
    }

    @Override
    public float floatValue() {
        return (float)this.get();
    }

    @Override
    public double doubleValue() {
        return (double)this.get();
    }

    @Override
    public String toString() {
        return Integer.toString(this.get());
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + this.get();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof AtomicPositiveInteger)) {
            return false;
        } else {
            AtomicPositiveInteger other = (AtomicPositiveInteger)obj;
            return this.intValue() == other.intValue();
        }
    }

}
