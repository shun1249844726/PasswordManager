package com.lexinsmart.xushun.passwordmanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xushun on 2017/4/7.
 */

public class MainActivity extends AppCompatActivity {

    private boolean isAnimationRun;
    private boolean isAnimationRun2;

    AnimatorSet set1;
    AnimatorSet set2;
    @BindView(R.id.tv_login)
    TextView tvLogin;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.bt_go)
    Button btGo;
    @BindView(R.id.tv_forgetpsd)
    TextView tvForgetpsd;
    @BindView(R.id.cv)
    CardView cv;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.rl_login)
    RelativeLayout rlLogin;
    @BindView(R.id.et_regist_username)
    EditText etRegistUsername;
    @BindView(R.id.et_regist_password)
    EditText etRegistPassword;
    @BindView(R.id.et_repeatpassword)
    EditText etRepeatpassword;
    @BindView(R.id.bt_next)
    Button btNext;
    @BindView(R.id.cv_add)
    CardView cvAdd;
    @BindView(R.id.fab_exit)
    FloatingActionButton fabExit;
    @BindView(R.id.rl_regist)
    RelativeLayout rlRegist;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.fab)
    public void registerFab() {
        startAnimator();
    }

    @OnClick(R.id.bt_go)
    public void BtnGo() {
        attemptLogin();
    }

    @OnClick(R.id.fab_exit)
    public void fabExit() {

        startAnimator2();

    }

    private void attemptLogin() {
        Intent intent = new Intent(MainActivity.this, ThisMainActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
        } else {
            startActivity(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(rlLogin, "alpha", 1f, 0f);
        ObjectAnimator animator22 = ObjectAnimator.ofFloat(rlLogin, "scaleX", 1f,0.5f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(rlLogin, "scaleY", 1f,0.5f);

        ObjectAnimator animator3 = ObjectAnimator.ofFloat(rlRegist, "scaleX", 0.5f,1f);
        ObjectAnimator animator32 = ObjectAnimator.ofFloat(rlRegist, "scaleY", 0.5f,1f);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(rlRegist, "alpha", 0f, 1f);

        set1 = new AnimatorSet();
        set1.setDuration(1000);
        set1.setInterpolator(new DecelerateInterpolator());
        set1.playTogether(animator1, animator2, animator3, animator4,animator22,animator32);
        set1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                isAnimationRun = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimationRun = false;
                rlLogin.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimationRun = true;
                rlRegist.setVisibility(View.VISIBLE);
                rlRegist.setAlpha(0);

            }
        });
        //   startAnimator();

        ObjectAnimator animator1_1 = ObjectAnimator.ofFloat(rlRegist, "alpha", 1f, 0f);
        ObjectAnimator animator22_1 = ObjectAnimator.ofFloat(rlRegist, "scaleX", 1f,0.5f);
        ObjectAnimator animator2_1 = ObjectAnimator.ofFloat(rlRegist, "scaleY", 1f,0.5f);

        ObjectAnimator animator3_1 = ObjectAnimator.ofFloat(rlLogin, "scaleX", 0.5f,1f);
        ObjectAnimator animator32_1 = ObjectAnimator.ofFloat(rlLogin, "scaleY", 0.5f,1f);
        ObjectAnimator animator4_1 = ObjectAnimator.ofFloat(rlLogin, "alpha", 0f, 1f);


        set2 = new AnimatorSet();
        set2.setDuration(1000);
        set2.setInterpolator(new DecelerateInterpolator());
        set2.playTogether(animator1_1, animator22_1, animator2_1, animator3_1,animator32_1,animator4_1);
        set2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                isAnimationRun2 = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimationRun2 = false;

                rlRegist.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimationRun2 = true;
                rlLogin.setVisibility(View.VISIBLE);
                rlLogin.setAlpha(0);

            }
        });
        //   startAnimator2();
    }

    private void startAnimator2() {

        if (isAnimationRun2) {
            set2.cancel();
        }
        set2.setStartDelay(500);
        set2.start();
    }

    private void startAnimator() {
        if (isAnimationRun) {
            set1.cancel();
        }
        set1.setStartDelay(500);
        set1.start();
    }
}
