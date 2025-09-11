package com.vecoo.extralib.storage;

import java.util.Collection;

public interface ICustomJsonProvider<T> {
    Collection<T> getStorage();

    void write();

    void init();
}
