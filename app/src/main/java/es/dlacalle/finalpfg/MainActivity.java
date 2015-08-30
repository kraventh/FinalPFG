package es.dlacalle.finalpfg;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity implements
        LogFragment.OnFragmentInteractionListener, MainFragment.OnFragmentInteractionListener {

    public static final int BT_DEVICE_TO_CONNECT = 1234;
    private static final String TAG = "MainActivity";
    private static final int MENU_MAIN = 0;
    private static final int MENU_LOG = 1;
    SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private Menu menu;
    //Otras variables
    private String filename = ""; //Para el nombre del LOG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_viewpager);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        setTitle("FinalPFG");
                        cambiarMenu(MENU_MAIN);

                        break;
                    case 1:
                        setTitle("FinalPFG Log");
                        cambiarMenu(MENU_LOG);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_empty_log:
                onLogInteraction("", LogFragment.REPLACE);
                Toast.makeText(this, "Se ha vaciado el Log", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_save_log:
                saveLog();
                return true;
            case R.id.action_settings_prefs:
                Intent applist = new Intent(this, AppRowActivity.class);
                startActivity(applist);
                return true;
            default:
                break;
        }
        return false;
    }

    public void cambiarMenu(int tipoMenu) {
        switch (tipoMenu) {
            case MENU_MAIN:
                menu.findItem(R.id.action_empty_log).setVisible(false);
                menu.findItem(R.id.action_save_log).setVisible(false);
                menu.findItem(R.id.action_settings_prefs).setVisible(true);
                break;
            case MENU_LOG:
                menu.findItem(R.id.action_empty_log).setVisible(true);
                menu.findItem(R.id.action_save_log).setVisible(true);
                menu.findItem(R.id.action_settings_prefs).setVisible(false);
                break;
        }
    }

    public boolean saveLog() {
        final boolean[] success = {false};
        final TextView tv_log = (TextView) findViewById(R.id.tv_log);

        if (isExternalStorageWritable()) {

            //Nombre por defecto del fichero
            filename = "pfg-" + DateFormat.format("yyyyMMddHHmmss", new java.util.Date()) + ".log";

            //Creo el dialogo para solicitar el nombre del fichero
            //Campo de entrada: campo de texto (podriamos poner un datepicker o cualquier otra cosa)
            final EditText input = new EditText(this);
            input.setText(filename);

            new AlertDialog.Builder(this)
                    .setTitle("Nombre del archivo: ") //titulo
                    .setView(input) //añado el edittext
                    .setPositiveButton("Guardar", new DialogInterface.OnClickListener() { //
                        public void onClick(DialogInterface dialog, int whichButton) {
                            filename = input.getText().toString();
                            try {
                                // Ruta a la tarjetaSD
                                File sdcard = Environment.getExternalStorageDirectory();

                                // Añadimos la ruta a nuestro directorio
                                File dir = new File(sdcard.getAbsolutePath() + "/PFG/");

                                // Creamos nuestro directorio
                                if (!dir.mkdir()) Log.d(TAG, "Directorio no creado");

                                // Creamos el archivo en el que grabaremos los datos
                                File file = new File(dir, filename);

                                //Creamos el stream de salida
                                FileOutputStream os = new FileOutputStream(file, true);

                                //Recuperamos el texto a guardar
                                String data = tv_log.getText().toString();

                                //Lo escribimos y cerramos
                                os.write(data.getBytes());
                                os.close();

                                Toast.makeText(getApplicationContext(), "Guardado LOG en " +
                                                file.getAbsolutePath(),
                                        Toast.LENGTH_SHORT).show();
                                success[0] = true;
                            } catch (Exception e) {
                                Log.d(TAG, "Error guardando log");
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
        return success[0];
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public void onLogInteraction(String string, int action) {
        TextView log = (TextView) findViewById(R.id.tv_log);
        switch (action) {
            case LogFragment.APPEND:
                log.append(string + "\n");
                break;
            case LogFragment.REPLACE:
                log.setText(string);
                break;
        }

    }

    /* Checks if external storage is available for read and write */

    public void onMainFragmentInteraction(String string) {
        onLogInteraction(string, LogFragment.APPEND);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    rootView = inflater.inflate(R.layout.loader_main_fragment, container, false);
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.loader_log_fragment, container, false);
                    break;

            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }

}
