package hr.fer.zari.midom.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;


import hr.fer.zari.midom.R;

public class NDKtest extends ActionBarActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("test-lib");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ndktest);


        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText(stringFromJNI());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
