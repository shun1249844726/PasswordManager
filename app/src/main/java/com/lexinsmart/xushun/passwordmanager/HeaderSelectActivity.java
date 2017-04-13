package com.lexinsmart.xushun.passwordmanager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xushun on 2017/4/13.
 */

public class HeaderSelectActivity extends AppCompatActivity {
    Context context ;
    @BindView(R.id.btn_load_img)
    Button btnLoadImg;
    @BindView(R.id.img_header)
    ImageView imgHeader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header_select);
        ButterKnife.bind(this);
        context = this;


    }

    @OnClick(R.id.btn_load_img)
    public void loadImg(){

    }
}
