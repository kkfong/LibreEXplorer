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

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FileListArrayAdapter extends ArrayAdapter<File> {

    Context mContext;
    int mLayout;
    List<File> files;


    public FileListArrayAdapter(Context context, int resource, List<File> objects) {
        super(context, resource, objects);
        mContext = context;
        mLayout = resource;
        files = objects;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        FileHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayout, parent, false);

            holder = new FileHolder();
            holder.textLabel = (TextView) row.findViewById(R.id.file_list_name);
            holder.icon = (ImageView) row.findViewById(R.id.file_list_icon);

            row.setTag(holder);
        } else {
            holder = (FileHolder) row.getTag();
        }
        File file = files.get(position);
        holder.textLabel.setText(file.getName());
        try {
            FileUtilties.setIcon(file, holder.icon);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return row;
    }


    static class FileHolder {
        TextView textLabel;
        ImageView icon;
    }
}
