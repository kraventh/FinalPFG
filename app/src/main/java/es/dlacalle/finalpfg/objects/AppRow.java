package es.dlacalle.finalpfg.objects;

import android.graphics.drawable.Drawable;

public class AppRow {
    private String name;
    private String app_package;
    private Drawable icon;
    private boolean checked;

    public AppRow() {
        this.checked = false;
    }
    public AppRow(String name, String app_package, Drawable icon) {
        this.name = name;
        this.app_package = app_package;
        this.icon = icon;
        this.checked = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApp_package() {
        return app_package;
    }

    public void setApp_package(String app_package) {
        this.app_package = app_package;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icono) {
        this.icon = icono;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }


}
