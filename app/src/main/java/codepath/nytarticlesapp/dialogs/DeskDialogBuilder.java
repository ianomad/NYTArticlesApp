/*
 * Iliiazbek Akhmedov
 * Copyright (c) 2016.
 */

package codepath.nytarticlesapp.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.util.HashSet;

import codepath.nytarticlesapp.R;

public class DeskDialogBuilder {

    private final Context context;
    private final DesksChosen desksChosenCallback;
    private final HashSet<String> selectedItems;
    private final String[] items;

    DeskDialogBuilder(Context context, DesksChosen desksChosenCallback) {
        this.desksChosenCallback = desksChosenCallback;
        this.context = context;
        this.selectedItems = new HashSet<>();
        this.items = context.getResources().getStringArray(R.array.desk_values);
    }

    void showDialog() {
        AlertDialog dialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.choose_category));

        boolean[] checkedItems = new boolean[items.length];
        for (int i = 0; i < items.length; i++) {
            checkedItems[i] = selectedItems.contains(items[i]);
        }

        builder.setMultiChoiceItems(items, checkedItems,
                (dialog1, indexSelected, isChecked) -> {
                    if (isChecked) {
                        selectedItems.add(items[indexSelected]);
                    } else if (selectedItems.contains(items[indexSelected])) {
                        selectedItems.remove(items[indexSelected]);
                    }
                })
                // Set the action buttons
                .setPositiveButton("OK", (dialog12, id) -> desksChosenCallback.desksChosen(selectedItems))
                .setNegativeButton("Cancel", (dialog13, id) -> {

                });

        dialog = builder.create();
        dialog.show();
    }

    public interface DesksChosen {
        void desksChosen(HashSet<String> chosenDesks);
    }
}
