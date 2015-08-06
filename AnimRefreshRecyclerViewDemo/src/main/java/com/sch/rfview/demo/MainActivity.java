package com.sch.rfview.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by shichaohui on 2015/7/31 0031.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private LinearFragment listFragment;
    private GridFragment gridFragment;
    private StaggeredGridFragment staggeredGridFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listFragment = new LinearFragment();
        gridFragment = new GridFragment();
        staggeredGridFragment = new StaggeredGridFragment();

        getFragmentManager().beginTransaction().replace(
                R.id.fragment_container, listFragment).commit();

        findViewById(R.id.list).setOnClickListener(this);
        findViewById(R.id.grid).setOnClickListener(this);
        findViewById(R.id.staggered_grid).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.list:
                getFragmentManager().beginTransaction().replace(
                        R.id.fragment_container, listFragment).commit();
                break;
            case R.id.grid:
                getFragmentManager().beginTransaction().replace(
                        R.id.fragment_container, gridFragment).commit();
                break;
            case R.id.staggered_grid:
                getFragmentManager().beginTransaction().replace(
                        R.id.fragment_container, staggeredGridFragment).commit();
                break;
        }
    }
}
