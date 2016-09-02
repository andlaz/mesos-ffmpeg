package com.github.andlaz.mesos.ffmpeg.shared;

import com.github.andlaz.mesos.ffmpeg.shared.impl.SynchronizedRequestObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SharedMaps {

    public Map<String, SynchronizedRequestObject> getRequestObjectMap() {
        return requestObjectMap;
    }

    public void setRequestObjectMap(Map<String, SynchronizedRequestObject> requestObjectMap) {
        this.requestObjectMap = requestObjectMap;
    }

    public Map<String, StreamInitialized> getStringStreamInitializedMap() {
        return stringStreamInitializedMap;
    }

    public void setStringStreamInitializedMap(Map<String, StreamInitialized> stringStreamInitializedMap) {
        this.stringStreamInitializedMap = stringStreamInitializedMap;
    }

    private Map<String, SynchronizedRequestObject> requestObjectMap = new ConcurrentHashMap<>();

    private Map<String , StreamInitialized> stringStreamInitializedMap = new ConcurrentHashMap<>();







}
