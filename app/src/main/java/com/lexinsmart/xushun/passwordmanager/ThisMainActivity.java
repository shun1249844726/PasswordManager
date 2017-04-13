package com.lexinsmart.xushun.passwordmanager;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lexinsmart.xushun.passwordmanager.RSAUtil.Base64Utils;
import com.lexinsmart.xushun.passwordmanager.RSAUtil.RSAUtils;
import com.lexinsmart.xushun.passwordmanager.modle.PasswordBean;
import com.orhanobut.logger.Logger;


import java.io.InputStream;
import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by xushun on 2017/4/7.
 */

public class ThisMainActivity extends AppCompatActivity {

    int x, y, width, height, hypotenuse;
    float pixelDensity;
    Animation alphaAppear, alphaDisappear;
    boolean gridClickable = true;


    @BindView(R.id.grid_psd)
    GridView gridPsd;
    static List<PasswordBean> data_list = new ArrayList<PasswordBean>();
    @BindView(R.id.fab_add_psd)
    FloatingActionButton fabAddPsd;
    @BindView(R.id.closeButton)
    ImageButton closeButton;
    @BindView(R.id.layoutButtons)
    LinearLayout layoutButtons;
    @BindView(R.id.linearView)
    RelativeLayout linearView;
    @BindView(R.id.edt_psd_username)
    EditText edtPsdUsername;
    @BindView(R.id.edt_psd_name)
    EditText edtPsdName;
    @BindView(R.id.edt_psd_content)
    EditText edtPsdContent;
    @BindView(R.id.btn_add_sure)
    AppCompatButton btnAddSure;
    private PsdGridAdapter psd_adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);
        ButterKnife.bind(this);

        pixelDensity = getResources().getDisplayMetrics().density;
        Logger.v("pixelDesity:" + pixelDensity);

        psd_adapter = new PsdGridAdapter(this);
        gridPsd.setAdapter(psd_adapter);

        gridPsd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!gridClickable) {
                    return;
                }
                Intent intent = new Intent(ThisMainActivity.this, DetailsActivity.class);
                intent.putExtra("datalist", (Serializable) data_list);
                intent.putExtra("datalistindex", position);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ThisMainActivity.this).toBundle());
                } else {
                    startActivity(intent);
                }
            }
        });
        alphaAppear = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
        alphaDisappear = AnimationUtils.loadAnimation(this, R.anim.alpha_disappear);


        findPsds();
    }

    @OnClick(R.id.fab_add_psd)
    public void addPsd() {
        launchAddView();

    }

    @OnClick(R.id.closeButton)
    public void closeAdd() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            closeAddView();
        }
    }

    @OnClick(R.id.btn_add_sure)
    public void addSure() {
        String psdName, psdUserName, psdContent;
        psdName = edtPsdName.getText().toString();
        psdUserName = edtPsdUsername.getText().toString();
        psdContent = edtPsdContent.getText().toString();

        if (psdName.equals("") || psdUserName.equals("") || psdContent.equals("")) {
            ToastUtils.showLongToast(getApplicationContext(), "请填写完整信息！");
        } else {

            BmobUser myuser = BmobUser.getCurrentUser();
            if (myuser == null) {
                ToastUtils.showShortToast(this, "请先登陆");
                return;
            }

            String afterencrypt = "";
            try {
                // 从字符串中得到公钥
                // PublicKey publicKey = RSAUtils.loadPublicKey(PUCLIC_KEY);
                // 从文件中得到公钥
                InputStream inPublic = getResources().getAssets().open("rsa_public_key.pem");
                PublicKey publicKey = RSAUtils.loadPublicKey(inPublic);
                // 加密
                byte[] encryptByte = RSAUtils.encryptData(psdContent.getBytes(), publicKey);
                // 为了方便观察吧加密后的数据用base64加密转一下，要不然看起来是乱码,所以解密是也是要用Base64先转换
                afterencrypt = Base64Utils.encode(encryptByte);
            } catch (Exception e) {
                e.printStackTrace();
            }

            PasswordBean psdbean = new PasswordBean();
            psdbean.setPassword(afterencrypt);
            psdbean.setPsdname(psdName);
            psdbean.setUsername(psdUserName);
            psdbean.setUser(myuser);
            psdbean.setImgsource("");
            psdbean.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {

                        ToastUtils.showShortToast(getApplicationContext(), "ok");
                        findPsds();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            closeAddView();
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closeAddView() {

        Animator anim = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(
                    linearView,
                    (int) ((float) (300 * pixelDensity) / 2),
                    (int) ((float) (300 * pixelDensity) / 2),
                    hypotenuse,
                    28 * pixelDensity

            );
        }
        anim.setDuration(350);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                gridPsd.setAlpha(1f);
                gridClickable = true;


                linearView.setVisibility(View.GONE);
                fabAddPsd.setVisibility(View.VISIBLE);
                fabAddPsd.animate()
                        .translationX(0f)
                        .translationY(0f)
                        .setDuration(200)
                        .setListener(null);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        edtPsdContent.setVisibility(View.GONE);
        edtPsdName.setVisibility(View.GONE);
        edtPsdUsername.setVisibility(View.GONE);
        btnAddSure.setVisibility(View.GONE);


        layoutButtons.startAnimation(alphaDisappear);
        layoutButtons.setVisibility(View.GONE);
        anim.start();

    }

    public void launchAddView() {
        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;

        x = width / 2;
        y = height / 2;

        hypotenuse = (int) Math.hypot(x, y);


        x = (int) (x - ((36 * pixelDensity) + (28 * pixelDensity)));
        y = (int) (y - ((20 * pixelDensity) + (28 * pixelDensity)) - 125);
        fabAddPsd.animate()
                .translationY(-y)
                .translationX(-x)
                .setDuration(200)
                .setListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {

                        Animator anim = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            anim = ViewAnimationUtils.createCircularReveal(
                                    linearView,
                                    (int) ((float) (300 * pixelDensity) / 2),
                                    (int) ((float) (300 * pixelDensity) / 2),
                                    28 * pixelDensity,
                                    hypotenuse
                            );
                            Logger.d("x:  " + gridPsd.getWidth() / 2 + "  y:  " + (int) ((float) 300 * pixelDensity / 2) + "  " + 28 * pixelDensity + "  " + hypotenuse);
                        }
                        anim.setDuration(350);
                        anim.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                edtPsdContent.setVisibility(View.VISIBLE);
                                edtPsdName.setVisibility(View.VISIBLE);
                                edtPsdUsername.setVisibility(View.VISIBLE);
                                btnAddSure.setVisibility(View.VISIBLE);

                                layoutButtons.setVisibility(View.VISIBLE);
                                layoutButtons.startAnimation(alphaAppear);

                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });
                        gridPsd.setAlpha(0.5f);

                        gridClickable = false;
                        gridPsd.setClickable(false);
                        gridPsd.setFocusable(false);

                        fabAddPsd.setVisibility(View.GONE);
                        linearView.setVisibility(View.VISIBLE);

                        anim.start();

                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });


    }

    private class PsdGridAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        private Context mContext;

        public PsdGridAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        class ViewHolder {
            ImageView img_type;
            TextView tv_name;

        }

        @Override
        public int getCount() {
            return data_list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.grid_psd_item, null);

                holder = new ViewHolder();
                holder.img_type = (ImageView) convertView.findViewById(R.id.grid_item_image);
                holder.tv_name = (TextView) convertView.findViewById(R.id.grid_item_text);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final PasswordBean psdbean = (PasswordBean) data_list.get(position);
            BmobUser user = psdbean.getUser();
            String psdName = psdbean.getPsdname();
            holder.tv_name.setText(psdName);


            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.grid_img_content, null);
            ImageView gridImgImg = (ImageView) view.findViewById(R.id.img_grid_img);
            TextView gridImgTv = (TextView) view.findViewById(R.id.tv_grid_tv);
            gridImgTv.setText(psdName.substring(0, 1));
            view.destroyDrawingCache();
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.UNSPECIFIED);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = view.getDrawingCache();

            String uri = psdbean.getImgsource();
            if (!uri.equals("")) {
                Glide.with(mContext).load(uri).into(holder.img_type);
            } else {
                holder.img_type.setImageBitmap(bitmap);
            }

            return convertView;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.v("restart");
        findPsds();
        psd_adapter.notifyDataSetChanged();
    }

    private void findPsds() {
        BmobUser myuser = BmobUser.getCurrentUser();
        BmobQuery<PasswordBean> query = new BmobQuery<PasswordBean>();
        query.addWhereEqualTo("user", myuser);
        query.order("-updatedAt");
        query.include("psdname");
        query.findObjects(new FindListener<PasswordBean>() {
            @Override
            public void done(List<PasswordBean> list, BmobException e) {
                if (e == null) {
                    data_list = list;
                    psd_adapter.notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }
        });


    }
}
