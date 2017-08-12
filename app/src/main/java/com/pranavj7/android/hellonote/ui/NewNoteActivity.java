package com.pranavj7.android.hellonote.ui;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pranavj7.android.hellonote.provider.NotesContract.*;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.renderscript.Sampler;
import android.support.annotation.ColorInt;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.pranavj7.android.hellonote.R;
import com.thebluealliance.spectrum.SpectrumPalette;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewNoteActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = NewNoteActivity.class.getSimpleName();
    public static final String EXTRA_NOTE_ID = "note_id";
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.note_title) TextInputEditText mNoteTitle;
    @BindView(R.id.note_description) TextInputEditText mNotesDescription;
    @BindView(R.id.note_color) SpectrumPalette mColorPalette;
    @BindView(R.id.note_date) TextInputEditText mDate;
    @BindView(R.id.adView) AdView mAdView;
    private int mNoteId = -1;
    private DatePickerDialog mDatePickerDialog;
    private Calendar mDateVal = Calendar.getInstance();
    private int mNoteColor = -1;
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        ButterKnife.bind(this);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mAdView.loadAd(adRequest);
            setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }
        mNoteId = this.getIntent().getIntExtra(EXTRA_NOTE_ID, -1);
        mDatePickerDialog = new DatePickerDialog(this, this,
                mDateVal.get(Calendar.YEAR),
                mDateVal.get(Calendar.MONTH),
                mDateVal.get(Calendar.DAY_OF_MONTH));
        updateStartDateDisplay();
        mNoteColor = ContextCompat.getColor(this, R.color.default_theme);
        mColorPalette.setSelectedColor(mNoteColor);
        mColorPalette.setOnColorSelectedListener(new SpectrumPalette.OnColorSelectedListener() {
            @Override
            public void onColorSelected(@ColorInt int color) {
                mNoteColor = color;
            }
        });

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatePickerDialog.show();
            }
        });

        if (mNoteId != -1) {
            getSupportLoaderManager().initLoader(NoteFetch.ID, null, this);
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_notes, menu);

        return true;
    }

        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveAndFinish();
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        mDateVal.set(year, month, day);
        updateStartDateDisplay();
    }

    void updateStartDateDisplay() {
        mDate.setText(SimpleDateFormat.getDateInstance().format(mDateVal.getTime()));
     }


    boolean isValid() {
        String Title = mNoteTitle.getText().toString();
        String Notes = mNotesDescription.getText().toString();

        //check if the title is empty
        if (Title.isEmpty()) {
            mNoteTitle.setError(getString(R.string.note_error_Title_required));
            return false;
        }
         // checks if notes description is empty
        if (Notes.isEmpty()) {
            mNotesDescription.setError(getString(R.string.note_error_notes_required));
            return false;
        }
        return true;
    }

    String n="";
String fp;
    byte[] mByteArray;

  /**  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PICK_FILE:
                if (requestCode == REQUEST_PICK_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    try {
                        // Code to create the file and storing the images within the file..................
                        // Log.d(TAG, String.valueOf(bitmap));
                        String folder_main = "Hello Note";
                        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                        if (!f.exists()) {
                            f.mkdirs();
                        }
                        Uri uri = data.getData();
                        File f1 = new File("" + uri);
                        String pj = f1.getName();
                        String s = f.getAbsolutePath();
                        File file = new File(s , pj+".PDF");
                        FileOutputStream out = new FileOutputStream(file);
                        out.flush();
                        out.close();
                    }
                         catch (Exception e) {
                            e.printStackTrace();
                        }
                            }
                break;
        }
    }
*/
  /*  @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            try {
              // Code to create the file and storing the images within the file..................

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                String folder_main = "Hello Note";
                File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                if (!f.exists()) {
                    f.mkdirs();
                }
                File f1 = new File("" + uri);
                 String pj = f1.getName();
                OutputStream fOut;
                String s = f.getAbsolutePath();
                File file = new File(s , pj+".JPEG");
                fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                fOut.close(); // d
            //imageView.setMaxHeight(200);
                n = file.getAbsolutePath();
                Bitmap bitmap1 = BitmapFactory.decodeFile(n);                ;
                assert imageView != null;
                imageView.setImageBitmap(bitmap1);
               //  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                //bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
               // mByteArray = outputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    byte[] mbit ;
    @TargetApi(Build.VERSION_CODES.KITKAT)*/
    void saveAndFinish() {
        if (!isValid()) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(NOTESC.COLUMN_COLOR, mNoteColor);
        values.put(NOTESC.COLUMN_DESCRIPTION, mNotesDescription.getText().toString());
        values.put(NOTESC.COLUMN_TITLE, mNoteTitle.getText().toString());
            values.put(NOTESC.COLUMN_DATE, mDateVal.getTimeInMillis());

     //   if(n!=null && !n.equals("")) {
       //     values.put(NOTESC.COLUMN_LINK, n);
        //}
        //else if(!Objects.equals(n, ""))
        //{
          //  values.put(NOTESC.COLUMN_LINK,n);

//        //}
  //      Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_add_white_dp1);
    //    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      //  bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        //mbit = outputStream.toByteArray();
        //values.put(NOTESC.COLUMN_NUM, mbit);
        //if(mByteArray!=null) {
          //  values.put(NOTESC.COLUMN_IMAGE, mByteArray);
        //}
            if (mNoteId != -1) {
                Uri contentUri = ContentUris.withAppendedId(NOTESC.CONTENT_URI, mNoteId);
            getContentResolver().update(contentUri, values, null, null);
        } else{
            getContentResolver().insert(NOTESC.CONTENT_URI, values);
        }
        finish();
    }
    private static final int REQUEST_PICK_FILE = 1;


   public void pj7(View view)
   {

// Show only images, no videos or anything else
       //  intent.setType("image/*");
       // intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
       //startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
       Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
       intent.setType("*/*");
       intent.addCategory(Intent.CATEGORY_OPENABLE);

       try {
           startActivityForResult(
                   Intent.createChooser(intent, "Select a File to Upload"),
                   REQUEST_PICK_FILE);
       } catch (android.content.ActivityNotFoundException ex) {
           // Potentially direct the user to the Market with a Dialog
           Toast.makeText(this, "Please install a File Manager.",
                   Toast.LENGTH_SHORT).show();
       }
   }


   // public void pj7(View view)
    //{
     //   Intent intent = new Intent();
// Show only images, no videos or anything else
      //  intent.setType("image/*");
       // intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        //startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
   // }
    //public void pj8(View view)
    //{
      //  Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_add_white_dp1);
       //ImageView imageView = (ImageView) findViewById(R.id.imageView7);
        //imageView.setImageBitmap(bitmap);
        //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
       // mbit = outputStream.toByteArray();
        //n=null;
    //}

    void reloadNote(Cursor cursor) {
        mNoteTitle.setText(cursor.getString(NoteFetch.COLUMN_TITLE));
        mNotesDescription.setText(cursor.getString(NoteFetch.COLUMN_DESCRIPTION));
        mColorPalette.setSelectedColor(cursor.getInt(NoteFetch.COLUMN_COLOR));
        mDateVal.setTime(new Date(cursor.getLong(NoteFetch.COLUMN_DATE)));
        mDate.setText(cursor.getString(NoteFetch.COLUMN_DATE));
        //String s = cursor.getString(NoteFetch.COLUMN_LINK);
        //if (s!=null) {
          //  Bitmap bitmap1 = BitmapFactory.decodeFile(s);
          //  Glide.with(this).load(s).asBitmap().into(mImg7);
          //  mImg7.setImageBitmap(bitmap1);
        //}
        //else if(cursor.getBlob(NoteFetch.COLUMN_NUM)!=null)
       // {
        //    ByteArrayInputStream inputStream = new ByteArrayInputStream(cursor.getBlob(NoteFetch.COLUMN_NUM));
           // Glide.with(this).load(inputStream).asBitmap().into(mImg7);
            //Bitmap mBitmap = BitmapFactory.decodeStream(inputStream);
          //  mImg7.setImageBitmap(mBitmap);
       // }
        updateStartDateDisplay();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case NoteFetch.ID:
                Uri contentUri = ContentUris.withAppendedId(NOTESC.CONTENT_URI, mNoteId);
                return new CursorLoader(this, contentUri, NoteFetch.PROJECTION, null, null, null);
            default:
                throw new UnsupportedOperationException("Unknown loader ID");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || !cursor.moveToFirst()) {
            return;
        }

        switch (loader.getId()) {
            case NoteFetch.ID:
                reloadNote(cursor);
                break;

            default:
                throw new UnsupportedOperationException("Unknown loader ID");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    private static final class NoteFetch {
        private NoteFetch() {}

        public static final int ID = 101;

        private static final String[] PROJECTION = new String[] {
                NOTESC.COLUMN_COLOR,
                NOTESC.COLUMN_DESCRIPTION,
                NOTESC.COLUMN_TITLE,
                NOTESC.COLUMN_DATE,
                NOTESC.COLUMN_IMAGE,
                NOTESC.COLUMN_NUM,
                NOTESC.COLUMN_LINK
        };

        private static final int COLUMN_COLOR = 0;
        private static final int COLUMN_DESCRIPTION = 1;
        private static final int COLUMN_TITLE = 2;
        private static final int COLUMN_DATE = 3;
        //private static final int COLUMN_IMAGE= 4;
       // private static final int COLUMN_NUM= 5;
        //private static final int COLUMN_LINK=6;
    }
}
