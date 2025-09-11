package com.vecoo.extralib.storage;

public interface IServerJsonProvider<T> {
    T getStorage();

    void updateStorage(T storage);

    void write();

    void init();
}
