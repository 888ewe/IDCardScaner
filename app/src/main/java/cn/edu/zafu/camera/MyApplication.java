package cn.edu.zafu.camera;

import android.app.Application;

/**
 * 作者：沉默
 * 日期：2017/4/10
 * QQ:823925783
 */

public class MyApplication extends Application {
//    public static int mScreenWidth = 0;
//    public static int mScreenHeight = 0;

    public static MyApplication CONTEXT;

//    private Bitmap mCameraBitmap;

    @Override
    public void onCreate() {
        super.onCreate();

//        // DisplayMetrics mDisplayMetrics = new DisplayMetrics();
//        DisplayMetrics mDisplayMetrics = getApplicationContext().getResources()
//                .getDisplayMetrics();
//        MyApplication.mScreenWidth = mDisplayMetrics.widthPixels;
//        MyApplication.mScreenHeight = mDisplayMetrics.heightPixels;

        CONTEXT = this;

    }

//    public Bitmap getCameraBitmap() {
//        return mCameraBitmap;
//    }
//
//    public void setCameraBitmap(Bitmap mCameraBitmap) {
//        if (mCameraBitmap != null) {
//            recycleCameraBitmap();
//        }
//        this.mCameraBitmap = mCameraBitmap;
//    }
//
//    public void recycleCameraBitmap() {
//        if (mCameraBitmap != null) {
//            if (!mCameraBitmap.isRecycled()) {
//                mCameraBitmap.recycle();
//            }
//            mCameraBitmap = null;
//        }
//    }
}
