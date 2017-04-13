package com.lexinsmart.xushun.passwordmanager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lexinsmart.xushun.passwordmanager.RSAUtil.Base64Utils;
import com.lexinsmart.xushun.passwordmanager.RSAUtil.RSAUtils;
import com.lexinsmart.xushun.passwordmanager.fileutil.PictureUtil;
import com.lexinsmart.xushun.passwordmanager.modle.PasswordBean;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;


/**
 * Created by xushun on 2017/4/12.
 */

public class DetailsActivity extends AppCompatActivity {
    Context mContext;
    String imgPath = "";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;
    private static final int REQUEST_CODE_PICK_IMAGE = 3;
    static List<PasswordBean> data_list = new ArrayList<PasswordBean>();
    String objectiId = "";
    static int dataListIndex = 0;
    @BindView(R.id.et_details_name)
    EditText etDetailsName;
    @BindView(R.id.et_details_username)
    EditText etDetailsUsername;
    @BindView(R.id.et_details_password)
    EditText etDetailsPassword;
    @BindView(R.id.btn_details_save)
    Button btnDetailsSave;
    @BindView(R.id.btn_details_delete)
    Button btnDetailsDelete;
    String detailsName, detailsUserName, detailPassword;
    @BindView(R.id.img_details_img)
    ImageView imgDetailsImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        mContext = this;

        Intent intent = getIntent();
        data_list = (List<PasswordBean>) intent.getSerializableExtra("datalist");
        dataListIndex = intent.getIntExtra("datalistindex", 0);

        String decryptStr = "";
        try {
            // 从字符串中得到私钥
            // PrivateKey privateKey = RSAUtils.loadPrivateKey(PRIVATE_KEY);
            // 从文件中得到私钥
            InputStream inPrivate = getResources().getAssets().open("pkcs8_rsa_private_key.pem");
            PrivateKey privateKey = RSAUtils.loadPrivateKey(inPrivate);
            // 因为RSA加密后的内容经Base64再加密转换了一下，所以先Base64解密回来再给RSA解密
            byte[] decryptByte = RSAUtils.decryptData(Base64Utils.decode(data_list.get(dataListIndex).getPassword()), privateKey);
            decryptStr = new String(decryptByte);
        } catch (Exception e) {
            e.printStackTrace();
        }

        etDetailsName.setText(data_list.get(dataListIndex).getPsdname());
        etDetailsUsername.setText(data_list.get(dataListIndex).getUsername());
        etDetailsPassword.setText(decryptStr);
        String imgUri = data_list.get(dataListIndex).getImgsource();
        if (!imgUri.equals("")) {
            Glide.with(mContext).load(imgUri).into(imgDetailsImg);
        } else {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.grid_img_content, null);
            ImageView gridImgImg = (ImageView) view.findViewById(R.id.img_grid_img);
            TextView gridImgTv = (TextView) view.findViewById(R.id.tv_grid_tv);
            gridImgTv.setText(data_list.get(dataListIndex).getPsdname().substring(0, 1));
            view.destroyDrawingCache();
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.UNSPECIFIED);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = view.getDrawingCache();
            imgDetailsImg.setImageBitmap(bitmap);
        }

        objectiId = data_list.get(dataListIndex).getObjectId();


    }

    @OnClick(R.id.btn_details_save)
    public void saveEdit() {
        detailsName = etDetailsName.getText().toString();
        detailsUserName = etDetailsUsername.getText().toString();
        detailPassword = etDetailsPassword.getText().toString();
        if (detailsName.equals("") || detailsUserName.equals("") || detailPassword.equals("")) {
            return;
        }
        final PasswordBean psdbean = new PasswordBean();
        psdbean.setPsdname(detailsName);
        psdbean.setUsername(detailsUserName);
        if (!imgPath.equals("")) {
            final BmobFile bmobFile = new BmobFile(new File(imgPath));
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        //bmobFile.getFileUrl()--返回的上传文件的完整地址
                        ToastUtils.showLongToast(getApplicationContext(), "上传文件成功:" + bmobFile.getFileUrl());
                        String imgSourceUri = bmobFile.getFileUrl();
                        psdbean.setImgsource(imgSourceUri);
                        psdbean.update(objectiId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    ToastUtils.showLongToast(getApplicationContext(), "更新图片成功！");
                                } else {
                                    Logger.d("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                                }
                            }
                        });


                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }


        String afterencrypt = "";
        try {
            // 从字符串中得到公钥
            // PublicKey publicKey = RSAUtils.loadPublicKey(PUCLIC_KEY);
            // 从文件中得到公钥
            InputStream inPublic = getResources().getAssets().open("rsa_public_key.pem");
            PublicKey publicKey = RSAUtils.loadPublicKey(inPublic);
            // 加密
            byte[] encryptByte = RSAUtils.encryptData(detailPassword.getBytes(), publicKey);
            // 为了方便观察吧加密后的数据用base64加密转一下，要不然看起来是乱码,所以解密是也是要用Base64先转换
            afterencrypt = Base64Utils.encode(encryptByte);
        } catch (Exception e) {
            e.printStackTrace();
        }

        psdbean.setPassword(afterencrypt);


        psdbean.update(objectiId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    ToastUtils.showLongToast(getApplicationContext(), "更新成功！");
                } else {
                    Logger.d("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    @OnClick(R.id.btn_details_delete)
    public void deleteEdit() {
        PasswordBean passwordBean = new PasswordBean();
        passwordBean.setObjectId(objectiId);
        passwordBean.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    ToastUtils.showLongToast(getApplicationContext(), "删除成功");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.img_details_img)
    public void selectPic() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE2);

        } else {
            choosePhoto();
        }
    }

    /**
     * 从相册选取图片
     */
    void choosePhoto() {
        /**
         * 打开选择图片的界面
         */
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhoto();
            } else {
                // Permission Denied
                Toast.makeText(DetailsActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {


            /**
             * 从相册中选取图片的请求标志
             */

            case REQUEST_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    try {
                        /**
                         * 该uri是上一个Activity返回的
                         */

                        Uri outputFileUri = data.getData();
                        String realPath = getRealPathFromURI(outputFileUri);
                        Log.i("qqliLog", "outputFileUri:" + outputFileUri);
                        Log.i("qqliLog", "realUri:" + realPath);
                        imgDetailsImg.setImageBitmap(PictureUtil
                                .getSmallBitmap(getRealPathFromURI(outputFileUri), 480, 800));
                        imgPath = realPath;
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("tag", e.getMessage());
                        Toast.makeText(this, "程序崩溃", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("liang", "失败");
                }

                break;

            default:
                break;
        }

    }

    /**
     * android4.4以后返回的URI只有图片编号
     * 获取图片真实路径
     *
     * @param contentURI
     * @return
     */
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
