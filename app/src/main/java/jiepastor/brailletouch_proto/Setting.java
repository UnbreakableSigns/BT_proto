package jiepastor.brailletouch_proto;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * default activity
 */


public class Setting extends AppCompatActivity {

    public static boolean rotationLocked = false;
    Switch rotationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();


        setContentView(R.layout.activity_setting);

        rotationSwitch = (Switch) findViewById(R.id.switch1);
        rotationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setSystemSettingsPermission();
                }
                else
                    rotationLocked = isChecked;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.System.canWrite(getApplicationContext())) {
                            Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, rotationLocked ? 0 : 1);
                        }
                    }

            }
        });
    }

    boolean setSystemSettingsPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            return true;
        }

        return false;
    }

    public void onButtonClick(View view){
        InputMethodManager mgr =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mgr != null) {
            mgr.showInputMethodPicker();
        }
    }


}
