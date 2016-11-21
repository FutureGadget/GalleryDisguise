package com.jikheejo.ku.gallarydisguise;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jikheejo.ku.gallarydisguise.picpath.PhotoPath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DirectoryListActivity extends AppCompatActivity {
    private RecyclerView mDirRecyclerView;
    private DirListAdapter mAdapter;
    private Set<String> mSelectedPaths;
    private Button mDirEncryptButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_list);

        //list setting
        mDirRecyclerView = (RecyclerView)findViewById(R.id.dirListRecyclerView);
        mDirRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateUI();

        // Button setting
        mDirEncryptButton = (Button)findViewById(R.id.dirEncryptButton);
        mDirEncryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DirectoryListActivity.this);
                builder.setTitle("Encryption")
                        .setItems(mSelectedPaths.toArray(new CharSequence[mSelectedPaths.size()]), new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Ok button behavior
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel button Behavior
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // init select array
        mSelectedPaths = new HashSet<>();
    }

    private void updateUI() {
        // For test purposes only.
        List<String> dirPaths = PhotoPath.getLeafPhotoDirs();
        for (int i = 0; i < 100; ++i) {
            dirPaths.add("TEST"+i);
        }
        mAdapter = new DirListAdapter(dirPaths);
        mDirRecyclerView.setAdapter(mAdapter);
    }

    private class DirListAdapter extends RecyclerView.Adapter<DirListHolder> {
        private List<String> mDirPaths;
        public DirListAdapter(List<String> dirPaths) {
            mDirPaths = dirPaths;
        }

        @Override
        public DirListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(DirectoryListActivity.this);
            View view = layoutInflater.inflate(R.layout.list_item_dir, parent, false);
            return new DirListHolder(view);
        }

        @Override
        public void onBindViewHolder(final DirListHolder holder, final int position) {
            final String dirPath = mDirPaths.get(position);
            // Since recycler view reuses same objects, this code is needed to avoid wrongly checked objects.
            holder.mPathTextView.setText(dirPath);
            if (mSelectedPaths.contains(dirPath)) {
                holder.mSelectCheckBox.setChecked(true);
            } else {
                holder.mSelectCheckBox.setChecked(false);
            }
            holder.mSelectCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!holder.mSelectCheckBox.isChecked()) {
                        mSelectedPaths.remove(dirPath);
                    } else {
                        mSelectedPaths.add(dirPath);
                        Log.i("TEST", dirPath);
                    }
                    // Log.i("TEST", holder.getAdapterPosition()+"");
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDirPaths.size();
        }
    }

    private static class DirListHolder extends RecyclerView.ViewHolder
    {
        public TextView mPathTextView;
        public CheckBox mSelectCheckBox;

        public DirListHolder(View itemView) {
            super(itemView);
            mPathTextView = (TextView)itemView.findViewById(R.id.list_item_dir_text_view2);
            mSelectCheckBox = (CheckBox)itemView.findViewById(R.id.list_item_dir_selected_checkbox);
        }
    }
}
