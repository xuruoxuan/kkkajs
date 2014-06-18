
package com.philjay.circledisplay;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.philjay.circledisplay.CircleDisplay.SelectionListener;

public class MainActivity extends Activity implements SelectionListener {
    
    private CircleDisplay mCircleDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mCircleDisplay = (CircleDisplay) findViewById(R.id.circleDisplay);

        mCircleDisplay.setAnimDuration(4000);
        mCircleDisplay.setValueWidthPercent(55f);
        mCircleDisplay.setFormatDigits(1);
        mCircleDisplay.setDimAlpha(80);
        mCircleDisplay.setSelectionListener(this);
        mCircleDisplay.setTouchEnabled(true);
        mCircleDisplay.setUnit("%");
        mCircleDisplay.setStepSize(0.5f);
        mCircleDisplay.showValue(75f, 100f, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.refresh) mCircleDisplay.showValue((float) (Math.random() * 1000f), 1000f, true);
        if(item.getItemId() == R.id.github) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/PhilJay/CircleDisplay"));
            startActivity(i);
        }
        return true;
    }

    @Override
    public void onSelectionUpdate(float val, float maxval) {
        Log.i("Main", "Selection update: " + val + ", max: " + maxval);
    }

    @Override
    public void onValueSelected(float val, float maxval) {
        Log.i("Main", "Selection complete: " + val + ", max: " + maxval);
    }
}
