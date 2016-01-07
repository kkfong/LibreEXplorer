/*
 * Copyright 2015 Elia Zammuto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * FibreFM Main Activity's Class, manages the High-Level App functions (Layout,Input and so on...)
 */
package com.theelix.librefilemanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


public class FileManagerActivity extends AppCompatActivity {


    public Menu mMenu;
    ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private AbsListView itemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FileUtilties.setContext(this);
        FileManager.setContext(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_layout);
        FileManager.setCurrentDirectory(FileManager.getHomeDirectory());
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        //Checks if we're running M or later, then shows the permission dialog
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }

    }

    /** This function reads the current Folder, and populates the ListView with the content of that folder */
    public void refresh() {
        ((ViewGroup) findViewById(R.id.list_layout)).removeView(itemView);
        if (FileManager.getCurrentDirectory().getName().equals("")) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        } else {
            getSupportActionBar().setTitle(getString(R.string.app_name) + ": " + FileManager.getCurrentDirectory().getName());

        }
        boolean useGrid = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_layout_grid", false);
        int itemLayout;
        if (useGrid) {
            itemView = new GridView(this);
            itemLayout = R.layout.file_grid_element;
            ((GridView) itemView).setNumColumns(4);
            ((GridView) itemView).setHorizontalSpacing(0);

        } else {
            itemView = new ListView(this);
            itemLayout = R.layout.file_list_element;
        }
        boolean hideHiddenFiles = !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_show_hidden_files", false);
        try {
            ArrayList<File> fileList = new ArrayList<>(Arrays.asList(FileManager.getCurrentDirectory().listFiles()));
            for (int i = 0; i < fileList.size(); i++) {
                if (((File) fileList.get(i)).isHidden() && hideHiddenFiles) {
                    fileList.remove(fileList.get(i));
                }
            }
            ArrayAdapter filelistAdapter = new FileListArrayAdapter(this, itemLayout, fileList);
            filelistAdapter.sort(new FileComparator());
            itemView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            ActionModeCallback callback = new ActionModeCallback();
            callback.setListView(itemView);
            callback.setContext(this);
            itemView.setMultiChoiceModeListener(callback);
            mDrawerLayout.closeDrawers();
            itemView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                    File file = (File) adapter.getItemAtPosition(position);
                    if (file.isDirectory()) {
                        FileManager.setCurrentDirectory(file);
                    } else {
                        FileManager.openFile(file);

                    }
                }
            });

            itemView.setAdapter(filelistAdapter);
            itemView.setTextFilterEnabled(true);
            ((ViewGroup) findViewById(R.id.list_layout)).addView(itemView, new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
        } catch (NullPointerException e) {
            Toast.makeText(this, "Unable to Access File", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.toolbar_menu_new_folder:
                DialogFragment newFolderDialog = new NewFolderDialog();
                newFolderDialog.show(getSupportFragmentManager(), "new_folder_dialog");
                return true;
            case R.id.toolbar_menu_paste:
                PasteTask task = new PasteTask(this);
                task.execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        goBack();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == FileManager.REQUEST_GET_DOCUMENT) {
                FileManager.pasteToSdCard(data.getData());
            }

        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        FileManager.refresh();
    }


    /**
     * This Function  make the App go to the Parent Folder, if any
     */
    public void goBack() {
        if (FileManager.getCurrentDirectory().getParentFile() != null) {
            FileManager.setCurrentDirectory(FileManager.getCurrentDirectory().getParentFile());
        } else {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            finish();
        }

    }

    /**
     * This Function is Called when "Home" button on Sidebar is clicked
     *
     * @param v the button's view
     */
    public void onHomeButtonClicked(View v) {
        FileManager.setCurrentDirectory(FileManager.getHomeDirectory());
    }

    /**
     * This Function is Called when "Settings" button on Sidebar is clicked
     *
     * @param v the button's view
     */
    public void onSettingsButtonClicked(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }

    /**
     * This Function is Called when "Exit" button on Sidebar is clicked
     *
     * @param v the button's view
     */
    public void onExitButtonClicked(View v) {
        finish();
    }
}
