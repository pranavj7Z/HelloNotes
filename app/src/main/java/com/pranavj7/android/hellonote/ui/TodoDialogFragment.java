package com.pranavj7.android.hellonote.ui;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;

import android.widget.TimePicker;

import com.pranavj7.android.hellonote.notifications.RescheduleIntentService;
import com.pranavj7.android.hellonote.provider.NotesContract.*;
import com.pranavj7.android.hellonote.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;


public class TodoDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener,LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.reminder_time_container) View mTimeContainer;
    @BindView(R.id.date_time_container) View mDateContainer;
    @BindView(R.id.reminder_time) TextInputEditText mReminderTime;
    @BindView(R.id.start_date) TextInputEditText mStartDateInput;
    @BindView(R.id.Tasks)
    TextInputEditText mTasks;
    @BindView(R.id.cb)
    CheckBox mcb;

    private int mReminderTimeMinute = TIME_MIN;
    private int mTodoId = -1;
    private int mNoteId = -1;
    private boolean mIsChecked = false;
    private boolean mNotifyChecked = false;
    private static final int TIME_MIN = 0;

    private static final String LOG_TAG = TodoDialogFragment.class.getSimpleName();
    private static final String EXTRA_NOTES_ID = "notes_id";
    private static final String EXTRA_TODO_ID = "todo_id";
    private static final int TODO_LOADER_ID = 801;
    private static final String[] PROJECTION = {
            TODOC.COLUMN_IS_CHECKED,
            TODOC.COLUMN_NOTIFY_CB,
            TODOC.COLUMN_NOTES_ID,
            TODOC.COLUMN_TIME,
            TODOC.COLUMN_DATE,
            TODOC.COLUMN_TASKS,
    };
    private static final int COLUMN_IS_CHECKED = 0;
    private static final int COLUMN_NOTIFY_CB=1;
    private static final int COLUMN_NOTES_ID = 2;
    private static final int COLUMN_TIME = 3;
    private static final int COLUMN_DATE = 4;
    private static final int COLUMN_TASK = 5;

    public static TodoDialogFragment newInstanceNote(int noteId) {
        return newInstance(noteId, -1);
    }

    public static TodoDialogFragment newInstanceTodo(int TodoId) {
        return newInstance(-1, TodoId);
    }

    public static TodoDialogFragment newInstance(int noteId, int todoId) {
        Bundle args = new Bundle();
        args.putInt(TodoDialogFragment.EXTRA_NOTES_ID, noteId);
        args.putInt(TodoDialogFragment.EXTRA_TODO_ID, todoId);

        TodoDialogFragment dialog = new TodoDialogFragment();
        dialog.setArguments(args);

        return dialog;
    }

    private Calendar mStartDate = Calendar.getInstance();
    private DatePickerDialog mDatePickerDialog;



    @Override
    public void onResume() {
        super.onResume();

        Bundle args = getArguments();

        if (args != null) {
            mNoteId = args.getInt(EXTRA_NOTES_ID, -1);
            mTodoId = args.getInt(EXTRA_TODO_ID, -1);
        }

        if (mNoteId == -1 && mTodoId == -1) {
            throw new UnsupportedOperationException("An ID is required");
        }

        if (mTodoId != -1) {
            getLoaderManager().initLoader(TODO_LOADER_ID, null, this);
        }
    }


    void updateStartDateDisplay() {
        mStartDateInput.setText(SimpleDateFormat.getDateInstance().format(mStartDate.getTime()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    int n=0;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        super.onCreateDialog(savedInstanceState);

        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_todo_dialog, null, false);

        ButterKnife.bind(this, view);

        mDateContainer.setVisibility(View.INVISIBLE);
        mTimeContainer.setVisibility(View.INVISIBLE);
        mDatePickerDialog = new DatePickerDialog(getContext(), this,
                mStartDate.get(Calendar.YEAR),
                mStartDate.get(Calendar.MONTH),
                mStartDate.get(Calendar.DAY_OF_MONTH));


        mStartDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatePickerDialog.show();
            }
        });
        updateStartDateDisplay();
        mcb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((CheckBox) v).isChecked()) {
                    //Case 1
                   n=1;
                    mDateContainer.setVisibility(View.VISIBLE);
                    mTimeContainer.setVisibility(View.VISIBLE);
                } else {
                    n=0;
                    mDateContainer.setVisibility(View.INVISIBLE);
                    mTimeContainer.setVisibility(View.INVISIBLE);
                    //case 2
                }
            }
        });

        Calendar cal = Calendar.getInstance();
        int nowInMinutes = (cal.get(Calendar.HOUR_OF_DAY) * 60) + cal.get(Calendar.MINUTE);

        setReminderTime(nowInMinutes);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.Todo_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.Todo_dialog_positive, null)
                .setNegativeButton(R.string.Todo_dialog_negative, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (validateAndSave()) {
                                    dialog.dismiss();
                                }
                            }
                        });
            }
        });

    return dialog;
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }


    boolean isValid() {
        String Reminder = mTasks.getText().toString();
        if (Reminder.isEmpty()) {
            mTasks.setError(getString(R.string.note_error_Title_required));
            return false;
        }
        return true;
    }


    boolean validateAndSave() {
        if (!isValid()) {
            return false;
        }

        ContentValues values = new ContentValues();
        // insert values got form the dialog
        values.put(TODOC.COLUMN_NOTIFY_CB,n);
        values.put(TODOC.COLUMN_NOTES_ID, mNoteId);
        values.put(TODOC.COLUMN_TASKS, mTasks.getText().toString());
        values.put(TODOC.COLUMN_DATE, mStartDate.getTimeInMillis());
        values.put(TODOC.COLUMN_TIME, mReminderTimeMinute);


        if (mTodoId != -1) {
            Uri reminderUri = ContentUris.withAppendedId(TODOC.CONTENT_URI, mTodoId);
            getContext().getContentResolver().update(reminderUri, values, null, null);
        } else {
            Uri uri = getContext().getContentResolver().insert(TODOC.CONTENT_URI, values);
            mTodoId = (int) ContentUris.parseId(uri);
        }
        if(n==1) {
            Intent intent = new Intent(getContext(), RescheduleIntentService.class);
            intent.putExtra(RescheduleIntentService.EXTRA_TODO_ID, mTodoId);
            intent.putExtra(RescheduleIntentService.EXTRA_CLEAR_ONLY, false);
            getContext().startService(intent);
        }
        else
        {
            Intent intent = new Intent(getContext(), RescheduleIntentService.class);
            intent.putExtra(RescheduleIntentService.EXTRA_TODO_ID, mTodoId);
            intent.putExtra(RescheduleIntentService.EXTRA_CLEAR_ONLY, true);
            getContext().startService(intent);
        }

            return true;
    }

  /**  private void setNotifyChecked(int id, int n) {
        final Uri reminder = ContentUris.withAppendedId(TODOC.CONTENT_URI, id);
        final ContentValues values = new ContentValues();
        values.put(TODOC.COLUMN_NOTIFY_CB, n);
        getContext().getContentResolver().update(reminder, values, null, null);
    }
   **/

    // text in edit text
    void setReminderTime(int minutes) {
        int hour = minutes / 60;
        int minute = minutes % 60;

        mReminderTimeMinute = minutes;
        mReminderTime.setText(getString(R.string.time_format, hour, minute));
    }

    @OnClick({R.id.reminder_time})
    void onTimeClicked(final View view) {
        int time = mReminderTimeMinute;
        int hour = time / 60;
        int minute = time % 60;
        TimePickerDialog picker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                int minutes = (hour * 60) + minute;
                setReminderTime(minutes);
            }
        }, hour, minute, true);
        picker.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        mStartDate.set(year, month, day);
        updateStartDateDisplay();
    }

    void reloadNote(Cursor cursor) {
        mTasks.setText(cursor.getString(COLUMN_TASK));

    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri reminderUri = ContentUris.withAppendedId(TODOC.CONTENT_URI, mTodoId);

        return new CursorLoader(getContext(), reminderUri, PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || !cursor.moveToFirst()) {

            Log.i(LOG_TAG, "Couldn't load reminder. Loader result was either null or empty.");
            return;
        }
        reloadNote(cursor);
        //setting the checkbox unchecked and giving the note id
        mNoteId = cursor.getInt(COLUMN_NOTES_ID);
        mStartDate.setTime(new Date(cursor.getLong(COLUMN_DATE)));
        setReminderTime(cursor.getInt(COLUMN_TIME));
        if (cursor.getInt(COLUMN_NOTIFY_CB) == 0) {
            mcb.setChecked(false);
            mTimeContainer.setVisibility(View.INVISIBLE);
            mDateContainer.setVisibility(View.INVISIBLE);


        }
        else
        {
            mcb.setChecked(true);
            mTimeContainer.setVisibility(View.VISIBLE);
            mDateContainer.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}