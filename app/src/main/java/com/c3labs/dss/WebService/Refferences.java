package com.c3labs.dss.WebService;

/**
 * Created by c3 on 2/6/2018.
 */

public class Refferences {
//    public static String url = "http://203.143.20.94/nsban/";
//    public static String url = "http://10.1.40.212/c3dss/";
//    public static String url = "http://nsb2.c3dss.com/";
    public static String url = "http://192.168.2.2/nsban/";
    public static String nameSpace = "service2.svc/";

    public static class Login {
        public static String methodName = url + nameSpace + "Login/";
    }

    public static class GetTime {
        public static String methodName = url + nameSpace + "GetTime/";
    }

    public static class GetDownloadMedia {
        public static final String methodName = url + nameSpace + "GetDownloadMedia/";
    }

    public static class GetNews {
        public static final String methodName = url + nameSpace + "GetNews/";
    }

    public static class GetTemplateSequence {
        public static final String methodName = url + nameSpace + "GetTemplateSequence/";
    }

    public static class GetSchedules {
        public static final String methodName = url + nameSpace + "GetSchedules/";
    }

    public static class UpdateStatus {
        public static final String methodName = url + nameSpace + "UpdateStatus/";
    }

    public static class UpdateNodeStatus {
        public static final String methodName = url + nameSpace + "UpdateNodeStatus/";
    }

    public static class SchedulePlayStatus {
        public static final String methodName = url + nameSpace + "SchedulePlayStatus/";
    }

    public static class UpdateFileDownloadStatus {
        public static final String methodName = url + nameSpace + "UpdateFileDownloadStatus";
    }


    //    -------------------------------NSB----------------------------
    public static class GetCurrency {
        public static final String method = url + "currencyAndroid.aspx?nodeId=";
    }

    public static class GetProduct {
        public static final String method = url + "ProductAndroid2.aspx";
    }

    public static class getAPKUpdate {
        public static final String method = url + "apk";
    }
    public static class getMediaPath {
        public static final String method = url + "upload_assets/upload_media/";
    }
}
