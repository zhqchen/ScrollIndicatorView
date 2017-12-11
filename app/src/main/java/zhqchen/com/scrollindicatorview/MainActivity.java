package zhqchen.com.scrollindicatorview;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ViewPager pager;
    ScrollIndicatorView sivIndicator;

    TextView tvLeft;
    TextView tvRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pager = findViewById(R.id.vp_test);
        sivIndicator = findViewById(R.id.siv_indicator);
        tvLeft = findViewById(R.id.tv_left);
        tvRight = findViewById(R.id.tv_right);
        initViews();
    }

    private void initViews() {
        pager.setAdapter(new MyPagerApdater(getSupportFragmentManager()));
        sivIndicator.bindIndicateView(tvLeft);
        sivIndicator.setupWithViewPager(pager);
    }

    private class MyPagerApdater extends FragmentPagerAdapter {

        public MyPagerApdater(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.e("getItem", "new Item");
            return new TestFragment();
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
