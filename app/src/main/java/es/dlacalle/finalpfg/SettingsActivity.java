package es.dlacalle.finalpfg;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            Intent i = getIntent();

            String setting = i.getStringExtra("setting");
            if ("app".equals(setting)) {
//                getSupportActionBar().setDisplayShowHomeEnabled(true);
//                getSupportActionBar().setIcon(R.drawable.bt_generic_icon);
                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_settings_container, new SettingsAppFragment())
                        .commit();
            } else if("config".equals(setting)){
                setTitle("Configuración");
                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_settings_container, new SettingsConfigFragment())
                        .commit();
            }
        }
    }

    public static class SettingsAppFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //Cargar las preferencias desde el XML
            addPreferencesFromResource(R.xml.pref_app);

        }
    }

    public static class SettingsConfigFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //Cargar las preferencias desde el XML
            addPreferencesFromResource(R.xml.pref_config);

        }
    }
}

