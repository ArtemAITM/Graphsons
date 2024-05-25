package com.example.opm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ButtonAndImageButtonItem extends Item {
    private final String buttonText;
    private final int imageResourceId;
    private final View.OnClickListener buttonClickListener;
    private final View.OnClickListener imageButtonClickListener;

    public ButtonAndImageButtonItem(
            Context context,
            String buttonText, int imageResourceId,
            View.OnClickListener buttonClickListener,
            View.OnClickListener imageButtonClickListener
    ) {
        super(context);
        this.buttonText = buttonText;
        this.imageResourceId = imageResourceId;
        this.buttonClickListener = buttonClickListener;
        this.imageButtonClickListener = imageButtonClickListener;
    }

    @Override
    View getView(LayoutInflater inflater, ViewGroup parent) {
        ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.item_button_and_image_button, parent, false);

        Button button = layout.findViewById(R.id.button);
        button.setText(buttonText);
        button.setOnClickListener(buttonClickListener);

        ImageButton imageButton = layout.findViewById(R.id.image_button);
        imageButton.setImageResource(imageResourceId);
        imageButton.setBackgroundResource(0);
        imageButton.setScaleType(ImageButton.ScaleType.FIT_CENTER);
        imageButton.setOnClickListener(imageButtonClickListener);

        return layout;
    }
}

