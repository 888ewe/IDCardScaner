package cn.edu.zafu.camera.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import cn.edu.zafu.camera.R;
import cn.edu.zafu.camera.view.PreviewBorderView;

public class CameraActivity extends Activity implements SurfaceHolder.Callback {
    private LinearLayout mLinearLayout;
    private PreviewBorderView mPreviewBorderView;
    private SurfaceView mSurfaceView;
//    private boolean hasSurface;
    private Intent mIntent;
    private static final String DEFAULT_PATH = "/sdcard/";
    private static final String DEFAULT_NAME = "default.jpg";
    private static final String DEFAULT_TYPE = "default";
    private String filePath;
    private String fileName;
    private String type;
    private Button take, light;
    private boolean toggleLight;
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private Camera.Parameters parameters;


    private SoundPool sp;
    private int soundid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initIntent();
        initLayoutParams();
        //初始化声音
        initSound();
    }

    private void initSound() {
        // 最多几个资源 资源类型 srcQuality暂无意义 默认用0
        sp = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        // 第三个参数暂时无用
        //加载声音至声音池
        soundid=sp.load(this, R.raw.taobao, 1);
    }

    private void initIntent() {
        mIntent = getIntent();
        filePath = mIntent.getStringExtra("path");
        fileName = mIntent.getStringExtra("name");
        type = mIntent.getStringExtra("type");
        if (filePath == null) {
            filePath = DEFAULT_PATH;
        }
        if (fileName == null) {
            fileName = DEFAULT_NAME;
        }
        if (type == null) {
            type = DEFAULT_TYPE;
        }
        Log.e("TAG", filePath + "/" + fileName + "_" + type);
    }

    /**
     * 重置surface宽高比例为3:4，不重置的话图形会拉伸变形
     */
    private void initLayoutParams() {
//        takePicture();
//        take = (Button) findViewById(R.id.take);
        light = (Button) findViewById(R.id.light);
//        take.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                cameraManager.takePicture(null, null, myjpegCallback);
//                //拍照
//                takePicture();
//            }
//        });
        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!toggleLight) {
                    toggleLight = true;
                    openLight();
                } else {
                    toggleLight = false;
                    offLight();
                }
            }
        });

        //重置宽高，3:4
        int widthPixels = getScreenWidth(this);
        int heightPixels = getScreenHeight(this);
        mLinearLayout = (LinearLayout) findViewById(R.id.linearlaout);
        mPreviewBorderView = (PreviewBorderView) findViewById(R.id.borderview);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        RelativeLayout.LayoutParams surfaceviewParams = (RelativeLayout.LayoutParams) mSurfaceView.getLayoutParams();
        surfaceviewParams.width = heightPixels * 4 / 3;
        surfaceviewParams.height = heightPixels;
        mSurfaceView.setLayoutParams(surfaceviewParams);
        RelativeLayout.LayoutParams borderViewParams = (RelativeLayout.LayoutParams) mPreviewBorderView.getLayoutParams();
        borderViewParams.width = heightPixels * 4 / 3;
        borderViewParams.height = heightPixels;
        mPreviewBorderView.setLayoutParams(borderViewParams);

        RelativeLayout.LayoutParams linearLayoutParams = (RelativeLayout.LayoutParams) mLinearLayout.getLayoutParams();
        linearLayoutParams.width = widthPixels - heightPixels * 4 / 3;
        linearLayoutParams.height = heightPixels;
        mLinearLayout.setLayoutParams(linearLayoutParams);
        Log.e("TAG", "Screen width:" + heightPixels * 4 / 3);
        Log.e("TAG", "Screen height:" + heightPixels);

        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);


        SensorControler.getInstance().onStart();
        SensorControler.getInstance().setCameraFocusListener(new SensorControler.CameraFocusListener() {
            @Override
            public void onFocus() {
                    takePicture();

            }
        });

    }

    //拍照
    private void takePicture() {
        parameters = mCamera.getParameters();
        parameters.setPreviewSize(800, 600);
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setJpegQuality(100);
        parameters.setPictureSize(800, 600);
        //自动对焦
        parameters.setFocusMode(Camera.Parameters.FLASH_MODE_AUTO);
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    SensorControler.getInstance().onStop();
                    sp.play(soundid, 1.0f, 0.3f, 0, 0, 2.0f);
                    mCamera.takePicture(null, null, myjpegCallback);
                }
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setstartPreview(mCamera, mHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();
        setstartPreview(mCamera, mHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        hasSurface = false;
    }

    /**
     * 拍照回调
     */
    Camera.PictureCallback myjpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            // 根据拍照所得的数据创建位图
            final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                    data.length);
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            final Bitmap bitmap1 = Bitmap.createBitmap(bitmap, (width - height) / 2, height / 6, height, height * 2 / 3);
            Log.e("TAG", "width:" + width + " height:" + height);
            Log.e("TAG", "x:" + (width - height) / 2 + " y:" + height / 6 + " width:" + height + " height:" + height * 2 / 3);
            // 创建一个位于SD卡上的文件

            File path = new File(filePath);
            if (!path.exists()) {
                path.mkdirs();
            }
            File file = new File(path, type + "_" + fileName);

            FileOutputStream outStream = null;
            try {
                // 打开指定文件对应的输出流
                outStream = new FileOutputStream(file);
                // 把位图输出到指定文件中
                bitmap1.compress(Bitmap.CompressFormat.JPEG,
                        60, outStream);
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("path", file.getAbsolutePath());
            bundle.putString("type", type);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            CameraActivity.this.finish();

        }
    };

    /**
     * 开始预览相机内容
     */
    private void setstartPreview(Camera camera, SurfaceHolder holder) {
//        try {
//            camera.setPreviewDisplay(holder);
//            //将系统的横屏变成竖屏
//            camera.setDisplayOrientation(90);
//            camera.startPreview();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCamera == null) {
            mCamera = getCamera();

            if (mHolder != null) {
                setstartPreview(mCamera, mHolder);
            }
        }
    }

    /**
     * 获取camera
     *
     * @return
     */
    private Camera getCamera() {

        Camera camera;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            camera = null;
            e.printStackTrace();
        }
        return camera;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }


    /**
     * 获得屏幕宽度，单位px
     *
     * @param context 上下文
     * @return 屏幕宽度
     */
    public int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }


    /**
     * 获得屏幕高度
     *
     * @param context 上下文
     * @return 屏幕除去通知栏的高度
     */
    public int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels - getStatusBarHeight(context);
    }

    /**
     * 获取通知栏高度
     *
     * @param context 上下文
     * @return 通知栏高度
     */
    public int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object obj = clazz.newInstance();
            Field field = clazz.getField("status_bar_height");
            int temp = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 打开闪光灯
     */
    public synchronized void openLight() {
        Log.e("TAG", "openLight");
        if (mCamera != null) {
            parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(parameters);
        }
    }

    /**
     * 关闭闪光灯
     */
    public synchronized void offLight() {
        Log.e("TAG", "offLight");
        if (mCamera != null) {
            parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
        }
    }


}
