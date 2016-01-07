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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.theelix.librefilemanager.R;

import java.io.IOException;

/**
 * Created by theelix on 22/12/15.
 */
public class PasteTask extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private ProgressDialog dialog;
    public PasteTask(Context context){
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(mContext, mContext.getString(R.string.paste_dialog_title),mContext.getString(R.string.paste_dialog_message, false));
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            FileManager.preparePaste();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
        FileManager.refresh();
    }
}
