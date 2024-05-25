package com.example.opm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import java.util.Arrays;
import java.util.List;

public class DialogManager {
    Context context;
    DialogManager(Context context){
        this.context = context;
    }
    public void showDialogInfo(int id){
        List<Integer> layouts = Arrays.asList(R.layout.alert_info1, R.layout.alert_info2,
                R.layout.alert_info3, R.layout.alert_info4, R.layout.alert_info5);
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        View DialogView = LayoutInflater.from(context).inflate(layouts.get(id), null);
        builder.setView(DialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
