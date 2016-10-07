package com.wanghaisheng.xiaoyaviewpagerindicator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Author: sheng on 2016/10/4 20:20
 * Email: 1392100700@qq.com
 */

public class NavFragment extends Fragment {

    public static final String ARG_TITLE = "arg_title";

    private String title;

    public static NavFragment newInstance(String title) {

        Bundle args = new Bundle();
        args.putString(ARG_TITLE,title);

        NavFragment fragment = new NavFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.title = getArguments().getString(ARG_TITLE);

        TextView content = new TextView(getActivity());
        content.setGravity(Gravity.CENTER);
        content.setTextSize(30);
        content.setTextColor(0xffff0000);
        content.setText(title);

        return content;
    }
}
