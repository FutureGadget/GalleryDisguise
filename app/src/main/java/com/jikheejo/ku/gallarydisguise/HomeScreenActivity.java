package com.jikheejo.ku.gallarydisguise;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

public class HomeScreenActivity extends Activity {
    /* A Data Structure for holding Items(target designation directory + keyword tag for deception*/
    private ArrayList<String> item = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        ListView lv = (ListView) findViewById(R.id.listview);
        generateListContent();
        lv.setAdapter(new MyListAdapter(this, R.layout.list_item, item));
    }


    private void generateListContent()
    {
        for(int i = 0; i < 3; i++)
            /* To Do: i = 0; i < number of items in the list; i++ */
            item.add("Row Number is " + i);
            /* To Fix: It keeps displaying Row Number is 1 on the first item and the rest of them are just 'Testing'. Dunno why */
    }

    /* Scratched chunks of code from YouTube, made some modifications */
    private class MyListAdapter extends ArrayAdapter<String>
    {
        private int layout;
        private MyListAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewholder = null;
            if(convertView == null)
            {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                /* folder_tag: For target directory and keyword tag */
                viewHolder.folder_tag = (TextView) convertView.findViewById(R.id.folder_tag);
                /* delete_button: Delete item, undiguise folder */
                viewHolder.delete_button = (ImageButton) convertView.findViewById(R.id.delete_button);
                viewHolder.delete_button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Toast.makeText(getContext(), "Delete Button!", Toast.LENGTH_SHORT).show();
                    }
                });
                /* sync_button: Apply encryption to the ones that haven't been disguised */
                viewHolder.sync_button = (ImageButton) convertView.findViewById(R.id.sync_button);
                viewHolder.sync_button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Toast.makeText(getContext(), "Sync Button!", Toast.LENGTH_SHORT).show();
                    }
                });
                convertView.setTag(viewHolder);
            }
            else
            {
                mainViewholder = (ViewHolder) convertView.getTag();
                mainViewholder.folder_tag.setText(getItem(position));
            }
            return convertView;
        }
    }

    public class ViewHolder
    {
        TextView folder_tag;
        ImageButton sync_button;
        ImageButton delete_button;
    }

}
