package com.example.gobang;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;

public class ItemDialogFragment extends DialogFragment {
    private String title;
    private String[] items;
    private DialogInterface.OnClickListener onClickListener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setItems(items, onClickListener);
        AlertDialog dialog=builder.create();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.alpha = 0.5f;
        dialog.getWindow().setAttributes(lp);
        return dialog;
    }
    public void show(String title, String[] items, DialogInterface.OnClickListener onClickListener,
                     FragmentManager fragmentManager) {
        this.title = title;
        this.items = items;
        this.onClickListener = onClickListener;
        show(fragmentManager, "ItemsDialogFragment");
    }
}
