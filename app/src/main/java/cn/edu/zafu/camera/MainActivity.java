package cn.edu.zafu.camera;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import cn.edu.zafu.camera.activity.CameraActivity;


/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-09-05
 * Time: 13:49
 */
public class MainActivity extends Activity {
    private static final int CAMERA_JAVA_REQUEST_CODE = 100;
    private Button mCatch;
    //    private EditText mPath, mName, mType;
    private ImageView mPhoto;
    private TextView mTextView;
    private String mPhotoPath;
    private File file2;
    private ProgressDialog progressdialog;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView(savedInstanceState);
        initClickListener();
    }

    private void initClickListener() {
        //申明一个权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_JAVA_REQUEST_CODE);
        }
        mCatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 100);
            }
        });
    }

    private void uploadAndRecognize() {
        if (!TextUtils.isEmpty(mPhotoPath)) {
            Log.e("main", "path" + mPhotoPath);
            Log.e("main", "file" + file2);
            OkHttpUtils
                    .post()
                    .url("https://api-cn.faceplusplus.com/cardpp/v1/ocridcard")
                    .addParams("api_key", "FAoDVUhRsJp6D4pD5K5WSw1WVzYxaWbN")
                    .addParams("api_secret", "pzYb_gonjOXNnm9yld6_tALxHzVkYAoG")
                    .addFile("image_file", "image_file", file2)
                    .build()
                    .writeTimeOut(3000)
                    .readTimeOut(3000)
                    .connTimeOut(3000)
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Request request, Exception e) {
                            Log.e("main", "error:" + e.getMessage());
                            progressdialog.dismiss();
                        }

                        @Override
                        public void onResponse(String response) {
                            Log.e("main", "success:" + response.toString());
                            progressdialog.dismiss();
                            Bean bean = new Gson().fromJson(response, Bean.class);
                            List<Bean.CardsBean> beans = bean.getCards();
                            if (beans.size() == 0) {
                                Toast.makeText(MainActivity.this, "重拍", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Bean.CardsBean cardsBean = beans.get(0);
                            String side = cardsBean.getSide();
                            if ("front".equals(side)) {
                                //正面
                                mTextView.setText(" 姓名:" + cardsBean.getName()
                                        + "\n 性别：" + cardsBean.getGender()
                                        + "\n 民族：" + cardsBean.getRace()
                                        + "\n 生日：" + cardsBean.getBirthday()
                                        + "\n 地址：" + cardsBean.getAddress()
                                        + "\n 身份证号：" + cardsBean.getId_card_number()
                                );

                            } else {
                                mTextView.setText("\n 签发单位：" + cardsBean.getIssued_by()
                                        + "\n 有效期：" + cardsBean.getValid_date()
                                );
                            }
                        }
                    });
        }
    }

    private void initView(Bundle savedInstanceState) {
        mCatch = (Button) findViewById(R.id.btn);
        mTextView = (TextView) findViewById(R.id.tv);
        mPhoto = (ImageView) findViewById(R.id.photo);
        //判断是否为空
        if (savedInstanceState != null) {
            bitmap = savedInstanceState.getParcelable("bitmap");
            mPhoto.setImageBitmap(bitmap);
            String content = savedInstanceState.getString("mTextView");
            mTextView.setText(content);
        }
        File externalFile = getExternalFilesDir("/idcard/");
        Log.e("TAG", externalFile.getAbsolutePath() + "\n" + externalFile.getAbsolutePath());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("TAG", "onActivityResult");
        if (requestCode == CAMERA_JAVA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                String path = extras.getString("path");
                String type = extras.getString("type");
//                Toast.makeText(getApplicationContext(), "path:" + path + " type:" + type, Toast.LENGTH_LONG).show();
                mPhotoPath = path;
                file2 = new File(path);
                FileInputStream inStream = null;
                try {
                    inStream = new FileInputStream(file2);
                    bitmap = BitmapFactory.decodeStream(inStream);
                    mPhoto.setImageBitmap(bitmap);
                    inStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressdialog = new ProgressDialog(MainActivity.this);
                progressdialog.setMessage("请稍候...");
                progressdialog.show();
                uploadAndRecognize();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("bitmap", bitmap);
        outState.putString("mTextView", mTextView.getText().toString());
    }

}
