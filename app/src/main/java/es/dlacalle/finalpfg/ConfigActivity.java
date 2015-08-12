package es.dlacalle.finalpfg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_cfg_container, new ConfigActivityFragment())
                    .commit();
        }
    }
}
