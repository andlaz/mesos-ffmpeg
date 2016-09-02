package com.github.andlaz.mesos.ffmpeg.service;

public  class ConfigService {

    public static String getMesosMaster() {
        return mesosMaster;
    }

    private static String mesosMaster = "locahost:5050";

    public static String getImageName() {
        return imageName;
    }

    private static  String imageName  = "blabl";


}
