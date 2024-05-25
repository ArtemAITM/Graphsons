package com.example.opm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class Item {
    protected final Context context;

    protected Item(Context context) {
        this.context = context;
    }

    abstract View getView(LayoutInflater inflater, ViewGroup parent);
}
