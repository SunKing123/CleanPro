package com.xiaoniu.cleanking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.ApplicationDelegate;
import com.xiaoniu.cleanking.ui.main.bean.GameSelectEntity;
import com.xiaoniu.common.base.BaseActivity;
import com.xiaoniu.common.utils.ToastUtils;

import java.util.ArrayList;

public class TestActivity extends BaseActivity {
    Button btnSerch;
    Button btn_insert;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_test;
    }

    @Override
    protected void initVariable(Intent intent) {
        setLeftTitle("超强省电");

        addRightButton(R.drawable.cc_icon_none_file, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort("haha");
            }
        });

        addRightButton(R.drawable.cc_icon_none_file, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort("haha");
            }
        });
    }


    @Override
    protected void initViews(Bundle savedInstanceState) {
//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
//                showErrorView();
//            }
//        });
        btnSerch = findViewById(R.id.btn_seach);
        btn_insert = findViewById(R.id.btn_insert);
        btnSerch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int size = ApplicationDelegate.getAppDatabase().gameSelectDao().getAll().size();
//                LogUtils.i("---size---"+size);
//                ToastUtils.showShort("--size--"+size);
            }
        });
        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<GameSelectEntity> selectSaveList = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    selectSaveList.add(new GameSelectEntity(i, "----zzzz----"+i));
                }
                ApplicationDelegate.getAppDatabase().gameSelectDao().insertAll(selectSaveList);
            }
        });



    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void loadData() {

    }
}
