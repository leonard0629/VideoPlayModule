package com.jiuguo.app.utils;

public class ResolutionUtils {
    public static String changeResolution(int resolution){
        String resolutionS = "����";
        switch(resolution){
            case 0:
                resolutionS = "����";
                break;
            case 1:
                resolutionS = "����";
                break;
            case 2:
                resolutionS = "����";
                break;
            case 3:
                resolutionS = "1080P";
        }
        return resolutionS;
    }

    public static String formatResolution(int resolution){
        String resolutionS = "super";
        switch(resolution){
            case 0:
                resolutionS = "";
                break;
            case 1:
                resolutionS = "high";
                break;
            case 2:
                resolutionS = "super";
                break;
        }
        return resolutionS;
    }
}
