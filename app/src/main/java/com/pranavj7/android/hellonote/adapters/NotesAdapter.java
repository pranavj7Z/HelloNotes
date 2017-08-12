package com.pranavj7.android.hellonote.adapters;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;


import com.pranavj7.android.hellonote.R;
import com.pranavj7.android.hellonote.provider.NotesContract;
import com.pranavj7.android.hellonote.Utility.Date;
import com.pranavj7.android.hellonote.ui.NewNoteActivity;
import com.pranavj7.android.hellonote.ui.NotesFragment;

import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.util.Calendar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotesAdapter extends CursorAdapter<NotesAdapter.ViewHolder> {


    private Context mContext; //instance variable
    private Listeners mListeners;
    public static final String[] PROJECTION = new String[]{
            NotesContract.NOTESC._ID,
            NotesContract.NOTESC.COLUMN_TITLE,
            NotesContract.NOTESC.COLUMN_DESCRIPTION,
            NotesContract.NOTESC.COLUMN_COLOR,
            NotesContract.NOTESC.COLUMN_DATE,
            NotesContract.NOTESC.COLUMN_FAV,
            NotesContract.NOTESC.COLUMN_NUM,
            NotesContract.NOTESC.COLUMN_LINK,
            NotesContract.NOTESC.COLUMN_INDICATOR
    };
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_TITLE = 1;
    private static final int COLUMN_DESCRIPTION = 2;
    private static final int COLUMN_COLOR = 3;
    private static final int COLUMN_DATE = 4;
    private static final int COLUMN_FAV = 5;
    private static final int COLUMN_NUM = 6;
    private static final int COLUMN_LINK = 7;
    private static final int COLUMN_INDICATOR = 8;


    public NotesAdapter(Listeners listeners) {
        mListeners = listeners;
    }
    public NotesAdapter(Context context) {
        mContext = context;
    }
  /*  static Bitmap ShrinkBitmap(String file, int width, int height){

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);
        int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);

        if (heightRatio > 1 || widthRatio > 1)
        {
            if (heightRatio > widthRatio)
            {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }
    */
    public interface Listeners {

        void NoteClicked(int id);

        void NoteLongClicked(int id);

        void NoteEditClicked(int id);

        void NoteArchiveClicked(int id);

        void NoteDeleteClicked(int id);

        void NoteUnArchiveClicked(int id);

        void NoteFavClicked(int id);

        void NoteUnfavClicked(int id);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())

                .inflate(R.layout.notes_list_item, parent, false);
        return new ViewHolder(view, mListeners ,mContext);
    }



    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {

        //   if (cursor.getString(COLUMN_LINK) != null) {
        //     String n = cursor.getString(COLUMN_LINK);
        //   Bitmap bitmap1 = BitmapFactory.decodeFile(n);
        // if (bitmap1 != null)
        //{
        //      glide.load(n).into(mImg7);
        //}
        // ByteArrayInputStream inputStream = new ByteArrayInputStream(cursor.getBlob(COLUMN_IMAGE));
        //Bitmap mBitmap = BitmapFactory.decodeStream(inputStream);
        //mImg7.setImageBitmap(mBitmap);
    //   else if(cursor.getBlob(COLUMN_NUM)!=null)
    // {
    //   ByteArrayInputStream inputStream = new ByteArrayInputStream(cursor.getBlob(COLUMN_NUM));
    // glide.load(inputStream).asBitmap().into(mImg7);
    // Bitmap mBitmap = BitmapFactory.decodeStream(inputStream);
    //mImg7.setImageBitmap(mBitmap);
    //}
viewHolder.bind(cursor);
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            PopupMenu.OnMenuItemClickListener {
        @BindView(R.id.note_title) TextView mTitle;
        @BindView(R.id.jb) LinearLayoutCompat mColor;
        @BindView(R.id.note_date) TextView mdate;
        @BindView(R.id.note_menu) ImageButton mMenu;
        @BindView(R.id.notes_desc) TextView mNotesdesc;
        @BindView(R.id.indicator) ImageButton mIndicator;
        @BindView(R.id.fav) ImageButton mFav;
        @BindView(R.id.id)CardView cd;
        @BindView(R.id.fav123) ImageButton mFav123;

//        @BindView(R.id.thumbnail) ImageView Img7;
private static final Typeface NORMAL_TYPEFACE =
        Typeface.create(Typeface.SERIF, Typeface.NORMAL);
        private int mNoteId = -1;
        private java.util.Date mDate;
        private final PopupMenu mMenuPopup;
        private final Listeners mListeners;
        ViewHolder(final View itemView, Listeners listeners , final Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mListeners = listeners;
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -6);
            itemView.setOnClickListener(this);
            mMenu.setOnClickListener(this);
            mMenuPopup = new PopupMenu(mMenu.getContext(), mMenu, Gravity.END);
            mMenuPopup.setOnMenuItemClickListener(this);
            mMenuPopup.getMenuInflater()
                    .inflate(R.menu.menu_notes_list, mMenuPopup.getMenu());
            mFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListeners.NoteUnfavClicked(mNoteId);
                }
            });
            mFav123.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListeners.NoteFavClicked(mNoteId);
                }
            });

            cd.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mListeners.NoteLongClicked(mNoteId);
                    return true;
                }
            });
        }

        public void bind(Cursor cursor) {
            mDate = Date.clearTime(new java.util.Date(cursor.getLong(COLUMN_DATE)));
            mNoteId = cursor.getInt(COLUMN_ID);
            mNotesdesc.setText(cursor.getString(COLUMN_DESCRIPTION));
            mdate.setText(DateFormat.getDateInstance().format(cursor.getLong(COLUMN_DATE)));
            mTitle.setText(cursor.getString(COLUMN_TITLE));
            mTitle.setTypeface(NORMAL_TYPEFACE);


         //   if (cursor.getString(COLUMN_LINK) != null) {
           //                     String n = cursor.getString(COLUMN_LINK);
             //   Bitmap bitmap1 = BitmapFactory.decodeFile(n);


            if(cursor.getInt(COLUMN_FAV)==0)
            {
                mFav.setVisibility(View.INVISIBLE);
                mFav123.setVisibility(View.VISIBLE);
            }
            else
            {
                mFav.setVisibility(View.VISIBLE);
                mFav123.setVisibility(View.INVISIBLE);
            }



        if(cursor.getInt(COLUMN_LINK)==0)
            {
                mMenuPopup.getMenu().getItem(2).setVisible(false);
                mMenuPopup.getMenu().getItem(1).setVisible(true);
            }
            else
            {
                mMenuPopup.getMenu().getItem(2).setVisible(true);
                mMenuPopup.getMenu().getItem(1).setVisible(false);
            }


            // mImg7.setImageBitmap(bitmap1);
               // ByteArrayInputStream inputStream = new ByteArrayInputStream(cursor.getBlob(COLUMN_IMAGE));
               //Bitmap mBitmap = BitmapFactory.decodeStream(inputStream);
                //mImg7.setImageBitmap(mBitmap);
            //}
          //  String imagefile ="/sdcard/.JPG";
          //  if(cursor.getBlob(COLUMN_IMAGE)!=null)
           //{
              //  ByteArrayInputStream inputStream = new ByteArrayInputStream(cursor.getBlob(COLUMN_IMAGE));
                //Bitmap mBitmap = BitmapFactory.decodeStream(inputStream);
              // ..Bitmap bm = ShrinkBitmap(imagefile, 300, 300);
              // Img7.setImageBitmap(bm);
           //}
            if(cursor.getInt(COLUMN_INDICATOR)==0)
            {
               mIndicator.setVisibility(View.GONE);
            }
             else
            {
               mIndicator.setVisibility(View.VISIBLE);
            }
                mColor.setBackgroundColor(cursor.getInt(COLUMN_COLOR));
        }
        @Override
        public void onClick(View view) {
            if (view == mMenu) {
                mMenuPopup.show();
            }
            else {
                mListeners.NoteClicked(mNoteId);
            }
        }

boolean userRegistered=false;
        boolean u = false;
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    mListeners.NoteEditClicked(mNoteId);
                    return true;
                case R.id.action_archive:
                    mListeners.NoteArchiveClicked(mNoteId);
                    userRegistered=true;
                    return true;
                case R.id.action_remove:
                    mListeners.NoteDeleteClicked(mNoteId);
                    return true;
                case R.id.action_unarchive:
                    userRegistered= false;
                    mListeners.NoteUnArchiveClicked(mNoteId);
                    return true;
              /**  case R.id.action_fav:
                    u=true;
                    mListeners.NoteFavClicked(mNoteId);
                    return true;
                case R.id.action_unfav:
                    u=false;
                    mListeners.NoteUnfavClicked(mNoteId);
                    return true; **/

            }
            return false;
        }

    }


}