package me.sunmisc.transflow;

public interface Fallback<R, E extends Exception> {

    void success(R result);

    void exception(E exception);

}
