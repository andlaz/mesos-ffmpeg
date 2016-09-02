package com.github.andlaz.mesos.ffmpeg.shared.impl;

import com.github.andlaz.mesos.ffmpeg.shared.RequestObject;
import com.github.andlaz.mesos.ffmpeg.utils.ParamObject;

public class SynchronizedRequestObject implements RequestObject {

    private ParamObject paramObject;
    private String taskId;

    public ParamObject getParamObject() {
        return paramObject;
    }

    public void setParamObject(ParamObject paramObject) {
        this.paramObject = paramObject;
    }


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public  SynchronizedRequestObject(ParamObject paramObject, String taskId) {
        this.paramObject = paramObject;
        this.taskId = taskId;
    }




}
