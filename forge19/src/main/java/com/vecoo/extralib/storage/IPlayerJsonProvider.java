package com.vecoo.extralib.storage;

import java.util.Map;
import java.util.UUID;

public interface IPlayerJsonProvider<T> {
    Map<UUID, T> getMap();

    T getStorage(UUID playerUUID);

    void updateStorage(T storage);

    void write();

    void init();
}
