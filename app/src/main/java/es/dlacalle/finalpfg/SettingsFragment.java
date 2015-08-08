package es.dlacalle.finalpfg;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * Fragment para preferencias y configuración
 */
public class SettingsFragment extends PreferenceFragment {

    private final int REQUEST_ENABLE_BT = 0;
    BluetoothAdapter mBluetoothAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Cargar las preferencias desde el XML
        addPreferencesFromResource(R.xml.preferences);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getActivity().getBaseContext(), "Bluetooth no encontrado", Toast.LENGTH_SHORT).show();
        }

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