package es.dlacalle.finalpfg;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

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

            final EditTextPreference filtro = (EditTextPreference) findPreference("app_filter");
            filtro.setSummary(filtro.getText());
            filtro.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    filtro.setSummary(newValue.toString());
                    return false;
                }
            });
        }
    }

    public static class SettingsConfigFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //Cargar las preferencias desde el XML
            addPreferencesFromResource(R.xml.pref_config);

            Preference pref = findPreference("limpiar_log");
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Toast.makeText(getActivity().getBaseContext(), "Log Caché vaciado",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

        }
    }
}

