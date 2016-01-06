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

package com.theelix.librefilemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ActionModeCallback implements ListView.MultiChoiceModeListener {
    private AbsListView listView;
    private Context mContext;

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        Menu menu = mode.getMenu();
        boolean onlyOneItem = listView.getCheckedItemCount() == 1;
        menu.findItem(R.id.toolbar_menu_rename).setVisible(onlyOneItem);
        menu.findItem(R.id.toolbar_menu_details).setVisible(onlyOneItem);



    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.cab_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        SparseBooleanArray checkedArray = listView.getCheckedItemPositions();
        ListAdapter adapter = listView.getAdapter();
        final List<File> selectedFiles = new ArrayList<>();
        final ArrayList<Uri> selectedUris = new ArrayList<>();
        for (int i = 0; i < listView.getCount(); i++) {
            if (checkedArray.get(i)) {
                selectedFiles.add((File) listView.getItemAtPosition(i));
                selectedUris.add(android.net.Uri.fromFile((File) listView.getItemAtPosition(i)));
            }
        }


        switch (item.getItemId()) {
            case R.id.toolbar_menu_copy:
                FileManager.prepareCopy(selectedFiles.toArray());
                break;
            case R.id.toolbar_menu_cut:
                FileManager.prepareCut(selectedFiles.toArray());
                break;
            case R.id.toolbar_menu_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                if (selectedFiles.size() > 1) {
                    builder.setMessage(mContext.getString(R.string.delete_dialog_multiple)+ " " + selectedFiles.size() + " " + mContext.getString(R.string.delete_dialog_items) + "?");
                } else
                builder.setMessage(mContext.getString(R.string.delete_dialog_single));

                builder.setPositiveButton(mContext.getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            FileManager.delete(selectedFiles.toArray());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        FileManager.refresh();
                    }
                });
                builder.setNegativeButton(mContext.getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                break;
            case R.id.toolbar_menu_rename:
                DialogFragment renameDialog = new RenameDialog();
                ((RenameDialog)renameDialog).setSelectedFiles(selectedFiles.toArray());
                renameDialog.show(((FileManagerActivity) mContext).getSupportFragmentManager(), "rename_dialog");
                break;
            case R.id.toolbar_menu_details:
                DialogFragment detailsDialog = new DetailsDialog();
                ((DetailsDialog)detailsDialog).setTargetFile(selectedFiles.get(0));
                detailsDialog.show(((FileManagerActivity) mContext).getSupportFragmentManager(), "details_dialog");
                break;
            case R.id.toolbar_menu_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,selectedUris);
                shareIntent.setType("*/*");
                mContext.startActivity(Intent.createChooser(shareIntent, "Share to..."));
                break;

        }
        mode.finish();
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    public void setListView(AbsListView listView) {
        this.listView = listView;
    }
    public void setContext(Context mContext){this.mContext = mContext;}
}
