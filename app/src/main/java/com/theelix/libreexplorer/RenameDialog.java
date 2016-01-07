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
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.theelix.librefilemanager.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by theelix on 19/12/15.
 */
public class RenameDialog extends AppCompatDialogFragment {
    private Object[] selectedFiles;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final File file = (File) selectedFiles[0];
        final EditText input = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setText(file.getName());
        builder.setView(input);
        //Add OK Button
        builder.setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = input.getText().toString();
                try {
                    FileManager.rename(file, fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast toast = new Toast(getContext());
                    toast.setText("IOException D:");
                    toast.show();

                }
            }
        });

        builder.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();

            }
        });
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        FileManager.refresh();
        super.onDismiss(dialog);
    }

    public void setSelectedFiles(Object[] selectedFiles) {
        this.selectedFiles = selectedFiles;
    }
}
