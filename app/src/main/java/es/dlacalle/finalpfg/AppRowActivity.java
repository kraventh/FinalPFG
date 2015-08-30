package es.dlacalle.finalpfg;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import es.dlacalle.finalpfg.adapters.AppRowAdapter;
import es.dlacalle.finalpfg.objects.AppRow;

public class AppRowActivity extends AppCompatActivity {

    private final static String TAG = "AppRowActivity";

    private ListView lv_app_installed;

    private ArrayList<AppRow> listadoInstalled;

    private AppRowAdapter adapterInstalled;

    private String filtro = "";
    private SharedPreferences prefs;
    private boolean showList = true;
    private String selectedTitle = "";
    private String selectedPackage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_row);

        lv_app_installed = (ListView) findViewById(R.id.lv_app_installed);

        listadoInstalled = new ArrayList<>();

        adapterInstalled = new AppRowAdapter(this, listadoInstalled);
        lv_app_installed.setAdapter(adapterInstalled);

        lv_app_installed.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppRow clicked = (AppRow) parent.getItemAtPosition(position);
                if (!clicked.isChecked()) {
                    AppRow appTmp = (AppRow) parent.getItemAtPosition(position);
                    //Primero desmarco todos
                    for (int i = 0; i < parent.getCount(); i++)
                        ((AppRow) parent.getItemAtPosition(i)).setChecked(false);
                    //Luego marco el seleccionado
                    clicked.setChecked(true);
                    selectedTitle = appTmp.getName();
                    selectedPackage = appTmp.getApp_package();

                } else clicked.setChecked(false);
                adapterInstalled.notifyDataSetChanged();
            }

        });


        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String initialFilter = prefs.getString("app_filter", null);
        if (initialFilter == null) {
            initialFilter = "whatsapp\nmaps";
        }
        prefs.edit().putString("app_filter", initialFilter).apply();
        filtro = prefs.getString("app_filter", "Filtro vacío");
    }

    public void getInstalledApps(View v) {
        Button tb = (Button) findViewById(R.id.b_toggle_app_installed);
        tb.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_hide_list_apps, 0, 0, 0);
        if (showList) {
            PackageManager pm = getPackageManager();
            ArrayList<ApplicationInfo> paquetes = (ArrayList<ApplicationInfo>) pm.getInstalledApplications(PackageManager.GET_META_DATA);
            // El primer elemento es el elemento vacío, se añade SIEMPRE
            listadoInstalled.add(new AppRow("App no seleccionada", "No se monitoriza ninguna aplicación", getDrawable(R.mipmap.app)));
            String[] filtros = filtro.split("\n");

            //El resto se rellena siguiendo las reglas
            for (ApplicationInfo infoPaquete : paquetes) {
                AppRow app = new AppRow();
                app.setName(infoPaquete.loadLabel(getPackageManager()).toString());
                app.setApp_package(infoPaquete.packageName);
                app.setIcon(infoPaquete.loadIcon(getPackageManager()));

                if (filtro.isEmpty())
                    listadoInstalled.add(app); // Si no hay filtro, se añade y punto
                else for (String single : filtros) { // Si lo hay, verificamos uno a uno que cumpla
                    single = single.toLowerCase();
                    if (app.getName().toLowerCase().contains(single) || //Busca el nombre de la App
                            app.getApp_package().toLowerCase().contains(single)) { // O el nombre del paquete
                        listadoInstalled.add(app);
                    }
                }
            }
            adapterInstalled.notifyDataSetChanged();
            tb.setText(getString(R.string.text_app_installed) + " (" + adapterInstalled.getCount() + ")");

        } else {
            tb.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_show_list_apps, 0, 0, 0);
            tb.setText(getString(R.string.text_app_installed));
            listadoInstalled.clear();
            adapterInstalled.notifyDataSetChanged();
        }
        showList = !showList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app_row, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_save_app:
                prefs.edit().putString("app_monitorizada_titulo", selectedTitle).apply();
                prefs.edit().putString("app_monitorizada_paquete", selectedPackage).apply();
                Toast.makeText(this, "Aplicacion actualizada", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setFilters(View view) {
        filtro = prefs.getString("app_filter", "Filtro vacío");
        final EditText input = new EditText(this);
        input.setText(filtro);
        input.setSingleLine(false);
        new AlertDialog.Builder(this)
                .setTitle("Filtros ") //titulo
                .setMessage("Son válidos nombres y paquetes, completos o parciales, mayúsuculas o minúsculas, uno por línea" +
                        "\nSi se deja en blanco muestra todo")
                .setView(input) //añado el edittext
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() { //
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String tmp = input.getText().toString();
                        String msg;
                        try {
                            if (tmp.equalsIgnoreCase(filtro)) {
                                msg = "Guardado sin cambios";
                            } else {
                                msg = "Filtro actualizado";
                                prefs.edit().putString("app_filter", tmp).apply();
                                filtro = tmp;
                            }
                            Toast.makeText(getApplicationContext(), msg,
                                    Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Log.e(TAG, "Error actualizando filtro");
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                })
                .show();


    }

    /**
     * Muestra todas las preferencias guardadas en el archivo compartido, utilizada para depuración
     */
    public void debugPrefs() {
        Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getAll();
        for (String key : prefs.keySet()) {
            Object pref = prefs.get(key);
            String printVal = "";
            if (pref instanceof Boolean) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof Float) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof Integer) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof Long) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof String) {
                printVal = key + " : " + pref;
            }
            if (pref instanceof Set<?>) {
                printVal = key + " : " + pref;
            }
            Log.e(TAG, printVal);

            // create a TextView with printVal as text and add to layout
        }
    }
}
