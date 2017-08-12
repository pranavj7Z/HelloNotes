package com.pranavj7.android.hellonote.adapters;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.CheckBox;
import android.widget.TextView;
import com.pranavj7.android.hellonote.R;
import com.pranavj7.android.hellonote.provider.NotesContract.*;
import java.text.DateFormat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TodoAdapter extends CursorAdapter<TodoAdapter.ViewHolder> {

    public interface Listeners {
        void TaskEditClicked(int id);
        void TaskDeleteClicked(int id);
        void TaskUnchecked(int id);
        void TaskChecked(int id);
    }

        public static final String[] PROJECTION = new String[] {
            TODOC._ID,
            TODOC.COLUMN_TASKS,
            TODOC.COLUMN_TIME,
            TODOC.COLUMN_IS_CHECKED,
            TODOC.COLUMN_DATE,
                TODOC.COLUMN_NOTIFY_CB,
    };
    
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_TODOS = 1;
    private static final int COLUMN_TIME = 2;
    private static final int COLUMN_IS_CHECKED = 3;
    private static final int COLUMN_DATE = 4;
    private static final int COLUMN_NOTIFY_CB = 5;



    private Listeners mListeners;

    public TodoAdapter(Listeners listeners) {
        mListeners = listeners;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_list_item, parent, false);

        return new ViewHolder(view, mListeners);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.bind(cursor);
    }

    static class ViewHolder extends RecyclerView.ViewHolder
            implements PopupMenu.OnMenuItemClickListener {
        @BindView(R.id.Tasks)
        TextView mT;
        @BindView(R.id.todo_menu)
        ImageButton mMenu;
        @BindView(R.id.checkbox)
        CheckBox mIsChecked;
        @BindView(R.id.goal_start_date) TextView mStartDate;
        @BindView(R.id.reminder_time) TextView mTime;
        @BindView(R.id.checkbox1) ImageButton cb;
        private final Context mContext;
        private int mTodoId;
        private int mTodo;
        private boolean mIsBinding;
        private final Listeners mListeners;
        private final PopupMenu mMenuPopup;
        ViewHolder(final View itemView, final Listeners listeners ) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mListeners = listeners;
            mContext = itemView.getContext();
            mMenuPopup = new PopupMenu(mMenu.getContext(), mMenu, Gravity.END);
            mMenuPopup.setOnMenuItemClickListener(this);
            mMenuPopup.getMenuInflater()
                    .inflate(R.menu.menu_list_item, mMenuPopup.getMenu());
            mIsChecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mIsChecked.isChecked()) {
                        n=1;
                        mListeners.TaskChecked(mTodoId);
                    }
                    else
                    {
                        n=0;
                        mListeners.TaskUnchecked(mTodoId);
                    }
                }
            });
        }
        public void bind(Cursor cursor) {
            mIsBinding = true;
            mTodoId = cursor.getInt(COLUMN_ID);
            mIsChecked.setChecked(cursor.getInt(COLUMN_IS_CHECKED) != 0);
            int hour = cursor.getInt(COLUMN_TIME) / 60;
            int minute = cursor.getInt(COLUMN_TIME) % 60;
            mStartDate.setText(DateFormat.getDateInstance().format(cursor.getLong(COLUMN_DATE)));
            mT.setText(cursor.getString(COLUMN_TODOS));
            mTime.setText(mContext.getString(R.string.time_format, hour, minute));

            if(cursor.getInt(COLUMN_NOTIFY_CB) == 0)
            {
                mStartDate.setVisibility(View.INVISIBLE);
                mTime.setVisibility(View.INVISIBLE);
                cb.setVisibility(View.INVISIBLE);
            }
            else
            {
                mStartDate.setVisibility(View.VISIBLE);
                mTime.setVisibility(View.VISIBLE);
                cb.setVisibility(View.VISIBLE);
            }
                mIsBinding = false;
        }

      //  void del()
        //{
        //    mListeners.TaskDeleteClicked1(mTodoId);
       // }



        @OnClick(R.id.todo_menu)
        void showMen() {
            mMenuPopup.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    mListeners.TaskEditClicked(mTodoId);
                    return true;
                case R.id.action_remove:
                    mListeners.TaskDeleteClicked(mTodoId);
                    return true;
            }
            return false;
        }

int n=0;
      /**  @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (mIsBinding) {
                return;
            }
            if (isChecked) {
                n=1;
                mListeners.TaskChecked(mTodoId);
            } else {
                n=0;
                mListeners.TaskUnchecked(mTodoId);
            }
        }
      **/
    }
}