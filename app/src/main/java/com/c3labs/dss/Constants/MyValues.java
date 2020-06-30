package com.c3labs.dss.Constants;

/**
 * Created by c3 on 3/13/2018.
 */

public class MyValues {
//    private static String NODE_ID = "";
//    private static String BRANCH_NAME = "";
    private static boolean RESET_APP = false;
    private static boolean UPDATE_BEAT = false;
//    private static boolean BOOT_COMPLETED = false;
    private static final String NEW_APK_NAME = "/newC3DSS.apk";

    public static boolean isResetApp() {
        return RESET_APP;
    }

    public static void setResetApp(boolean resetApp) {
        RESET_APP = resetApp;
    }

//    public static String getBranchName() {
//        return BRANCH_NAME;
//    }
//
//    public static void setBranchName(String branchName) {
//        BRANCH_NAME = branchName;
//    }
//
//    public static String getNodeId() {
//        return NODE_ID;
//    }
//
//    public static void setNodeId(String nodeId) {
//        NODE_ID = nodeId;
//    }

    public static String getNewApkName() {
        return NEW_APK_NAME;
    }

    public static boolean isUpdateBeat() {
        return UPDATE_BEAT;
    }

    public static void setUpdateBeat(boolean updateBeat) {
        UPDATE_BEAT = updateBeat;
    }

//    public static boolean isBootCompleted() {
//        return BOOT_COMPLETED;
//    }
//
//    public static void setBootCompleted(boolean bootCompleted) {
//        BOOT_COMPLETED = bootCompleted;
//    }
}
