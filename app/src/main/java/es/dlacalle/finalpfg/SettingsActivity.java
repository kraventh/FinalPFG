package es.dlacalle.finalpfg;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
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

            if ("config".equals(setting)) {
                setTitle("Configuración");
                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_settings_container, new SettingsConfigFragment())
                        .commit();

            }
        }

    }

    public static class SettingsConfigFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //Cargar las preferencias desde el XML
            addPreferencesFromResource(R.xml.pref_config);

            Preference apps = findPreference("app_filter");
            apps.setSummary(((EditTextPreference) apps).getText());
            apps.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    EditTextPreference pref = (EditTextPreference) preference;
                    pref.setText(newValue.toString());
                    pref.setSummary(newValue.toString());
                    return true;
                }
            });
            Preference freq = findPreference("frecuencia_gps");
            freq.setTitle("Frecuencia de actualización: " + ((EditTextPreference)freq).getText()+"ms");
            freq.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    EditTextPreference pref = (EditTextPreference)preference;
                    pref.setText(newValue.toString());
                    pref.setTitle("Frecuencia de actualización: "+newValue.toString()+"ms");
                    return true;
                }
            });
            Preference dist = findPreference("distancia_gps");
            dist.setTitle("Distancia de actualización: " + ((EditTextPreference)dist).getText()+"m");
            dist.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    EditTextPreference pref = (EditTextPreference)preference;
                    pref.setText(newValue.toString());
                    pref.setTitle("Distancia de actualización: " + newValue.toString() + "m");
                    return true;
                }
            });

        }
    }
}

