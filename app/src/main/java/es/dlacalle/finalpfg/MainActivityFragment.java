package es.dlacalle.finalpfg;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;


/**
 * Fragment para preferencias y configuración
 */
public class MainActivityFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Cargar las preferencias desde el XML
        addPreferencesFromResource(R.xml.preferences);

        // Monitoriza cambios en EditTextPreference
        // para guardar los cambios en un fichero.log si se pulsa Guardar
        Preference pref = findPreference("save_log");
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Toast.makeText(getActivity().getBaseContext(), "Guardado '" + newValue + "'",
                            Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }


}