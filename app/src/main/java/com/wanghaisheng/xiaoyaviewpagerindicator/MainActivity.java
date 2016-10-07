package com.wanghaisheng.xiaoyaviewpagerindicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.wanghaisheng.view.viewpagerindicator.XiaoyaViewPagerIndicator;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private XiaoyaViewPagerIndicator mIndicator;
    private FragmentPagerAdapter mPagerAdapter;

    List<String> contentTitles = Arrays.asList("吴亚玲1","李亚1","白一冰1","吴亚玲2","李亚2","白一冰2","吴亚玲3","李亚3","白一冰3");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        
        initView();
        
        initData();
        
    }

    private void initData() {

        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return NavFragment.newInstance(contentTitles.get(position));
            }

            @Override
            public int getCount() {
                return contentTitles.size();
            }
        };

        mViewPager.setAdapter(mPagerAdapter);
        mIndicator.setVisibleItemCount(4);
        mIndicator.setTabTitles(contentTitles);
        mIndicator.setViewPager(mViewPager,0);

    }

    private void initView() {
        this.mViewPager = (ViewPager) findViewById(R.id.viewpager);
        this.mIndicator = (XiaoyaViewPagerIndicator) findViewById(R.id.indicator);
    }



}
