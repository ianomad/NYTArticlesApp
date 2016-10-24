/*
 * Iliiazbek Akhmedov
 * Copyright (c) 2016.
 */

package codepath.nytarticlesapp.dialogs;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import codepath.nytarticlesapp.R;
import codepath.nytarticlesapp.network.SearchRequest;
import codepath.nytarticlesapp.utils.Constants;

public class SearchDialog extends DialogFragment implements DeskDialogBuilder.DesksChosen {

    @BindView(R.id.begDateButton)
    Button begDateButton;
    @BindView(R.id.desksButton)
    Button desksButton;
    @BindView(R.id.sortSpinner)
    Spinner sortSpinner;
    @BindView(R.id.searchKeyEditText)
    EditText searchEditText;

    private Calendar calendar;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    private Date chosenDate;

    private DeskDialogBuilder deskDialogBuilder;
    private HashSet<String> chosenDesks = new HashSet<>();
    private DatePickerDialog datePicker;

    public SearchDialog() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        resetCalendar();
        if (null != chosenDate) {
            calendar.setTime(chosenDate);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        @SuppressLint("InflateParams")
        View advancedSearchView = LayoutInflater.from(getContext()).inflate(R.layout.advanced_search, null);

        ButterKnife.bind(this, advancedSearchView);

        builder.setView(advancedSearchView).setMessage(R.string.advanced_search)
                .setPositiveButton(R.string.search, (dialog, id) -> startSearch())
                .setNegativeButton(R.string.cancel, null)
                .setNeutralButton(R.string.reset, (dialog, id) -> {
                    resetData();
                    startSearch();
                });

        begDateButton.setOnClickListener(v -> showDatePicker());

        desksButton.setOnClickListener(v -> {
            if (null == deskDialogBuilder) {
                deskDialogBuilder = new DeskDialogBuilder(getContext(), SearchDialog.this);
            }

            deskDialogBuilder.showDialog();
        });

        updateDesksLabel();
        updateChosenDate();

        return builder.create();
    }

    private void startSearch() {
        SearchRequest request = new SearchRequest();
        request.setPage(1);
        request.setQ(searchEditText.getText().toString());
        request.setCategories(chosenDesks.toArray(new String[0]));
        request.setSort(String.valueOf(sortSpinner.getSelectedItem()));

        if(null != chosenDate) {
            request.setDate(chosenDate);
        }

        Intent searchRequestIntent = new Intent(Constants.SEARCH_REQUEST_CREATED);
        searchRequestIntent.putExtra("data", Parcels.wrap(request));

        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(searchRequestIntent);
    }

    private void resetData() {
        datePicker = null;
        deskDialogBuilder = null;

        chosenDate = null;
        chosenDesks = new HashSet<>();

        resetCalendar();
    }

    private void resetCalendar() {
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        if (null != chosenDate) {
            calendar.setTime(chosenDate);
        }
    }

    void showDatePicker() {
        if (null == datePicker) {
            DatePickerDialog.OnDateSetListener onDateSetListener = (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                chosenDate = calendar.getTime();
                updateChosenDate();
            };

            datePicker = new DatePickerDialog(getContext(), onDateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));


            //reset
            datePicker.setButton(DialogInterface.BUTTON_NEUTRAL, getContext().getString(R.string.reset), (dialog, which) -> {
                chosenDate = null;
                updateChosenDate();
            });
        }

        datePicker.show();
    }

    private void updateChosenDate() {
        if (null == chosenDate) {
            begDateButton.setText(getResources().getString(R.string.choose));
        } else {
            begDateButton.setText(sdf.format(calendar.getTime()));
        }
    }

    @Override
    public void desksChosen(HashSet<String> chosenDesks) {
        this.chosenDesks = chosenDesks;
        updateDesksLabel();
    }

    private void updateDesksLabel() {
        if (chosenDesks.isEmpty()) {
            desksButton.setText(getContext().getResources().getString(R.string.choose));
        } else {
            String chosenLabel = chosenDesks.size() + " categories";
            desksButton.setText(chosenLabel);
        }
    }
}
