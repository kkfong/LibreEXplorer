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

package com.theelix.libreexplorer;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.theelix.librefilemanager.R;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by theelix on 30/12/15.
 */
public class DetailsDialog extends DialogFragment {
    File file;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater factory = getActivity().getLayoutInflater();
        View view = factory.inflate(R.layout.details_dialog,null);
        ((TextView)view.findViewById(R.id.details_name)).setText(file.getName());
        ((TextView)view.findViewById(R.id.details_size)).setText(FileUtilties.getFileSize(file));
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        ((TextView) view.findViewById(R.id.details_lastmodified)).setText(dateFormat.format(file.lastModified()));
        builder.setView(view);
        return builder.create();


    }

    public void setTargetFile(File targetFile) {
       file = targetFile;
    }
}
