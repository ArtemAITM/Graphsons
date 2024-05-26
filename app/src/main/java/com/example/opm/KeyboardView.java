package com.example.opm;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KeyboardView extends LinearLayout {

    private List<Button> buttons;
    private OnKeyboardButtonClickListener listener;
    private String[] buttonTexts;
    private EditText editText;

    public KeyboardView(Context context) {
        this(context, null);
    }

    public KeyboardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        setGravity(Gravity.END);
        setPadding(0, 0, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
        buttons = new ArrayList<>();
        buttonTexts = new String[]{
                "x", "a", "b", "c", "7", "8", "9", "+",  "home",
                "d", "e", "f", "g", "4", "5", "6", "-",  "del",
                "^2", "^", "(", ")", "1", "2", "3", "*",  "sett",
                "log", "exp", "sin", "cos", "tan", ".", "0", "/", "null"
        };
        createKeyboard();
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    private void createKeyboard() {
        int numRows = 4;
        int numColumns = 9;
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;

        for (int i = 0; i < numRows; i++) {
            LinearLayout rowLayout = new LinearLayout(getContext());
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            for (int j = 0; j < numColumns; j++) {
                int buttonId = i * numColumns + j;
                Button button = new Button(getContext());
                button.setText(buttonTexts[buttonId]);
                button.setId(buttonId);
                int buttonWidth = screenWidth / numColumns;
                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                        buttonWidth,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                button.setLayoutParams(buttonParams);

                GradientDrawable background = new GradientDrawable();
                background.setColor(Color.parseColor("#D0E6EB"));
                background.setStroke(2, Color.parseColor("#4d5373"));
                background.setCornerRadius(10);
                button.setBackground(background);

                button.setOnClickListener(v -> handleButtonClick(button));
                rowLayout.addView(button);
                buttons.add(button);
            }

            addView(rowLayout);
        }
    }

    private void handleButtonClick(Button button) {
        int buttonId = button.getId();
        String buttonText = button.getText().toString();

        if (buttonText.equals("home")) {
            if (listener != null) {
                listener.onHomeButtonClick();
            }
        } else if (buttonText.equals("del")) {
            if (listener != null) {
                listener.onDeleteButtonClick();
            }
        } else {
            if (editText != null) {
                editText.append(buttonText);
            }
            if (listener != null) {
                listener.onNormalButtonClick(buttonId, buttonText);
            }
        }
    }


    public void setOnKeyboardButtonClickListener(OnKeyboardButtonClickListener listener) {
        this.listener = listener;
    }

    public interface OnKeyboardButtonClickListener {
        void onHomeButtonClick();
        void onDeleteButtonClick();

        void onNormalButtonClick(int buttonId, String buttonText);
    }
}
