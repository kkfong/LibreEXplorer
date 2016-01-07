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

import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.theelix.librefilemanager.R;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class FileUtilties {
    private static Context mContext;


    public static void setIcon(File file, ImageView iv) throws ExecutionException, InterruptedException, IOException {
        if (file.isDirectory()) {
            iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_folder));
        }
        String mime = getMimeType(file);
        if (mime != null) {
            if (mime.contains("audio")) {
                iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_file_audio));
            } else if (mime.contains("image")) {
                int iconSize = mContext.getResources().getDimensionPixelSize(R.dimen.iconSize);
                Glide.with(mContext).load(file).placeholder(R.drawable.ic_file_image).centerCrop().into(iv);

            } else if (mime.contains("video")) {
                iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_file_video));


            } else {
                iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_file_generic));

            }

        }
    }

    public static String getMimeType(File file) {
        try {

            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(FileUtilties.getFileExtension(file)));
        } catch (StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static String getMimeType(Uri uri) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
    }

    public static String getFileSize(File file) {
        long size = file.length();
        if (file.isDirectory()) {
            size = getFolderLenght(file);
        }
        //Array that contains Order of Magnitude of Bytes
        String[] ofm = new String[]{"B", "KB", "MB", "GB"};
        int aofm = 0;
        while (size > 1024) {
            size = size / 1024;
            aofm++;
        }
        return size + ofm[aofm];
    }

    static long getFolderLenght(File folder) {
        long size = folder.length();
        for (int i = 0; i < folder.listFiles().length; i++) {
            if (folder.listFiles()[i].isDirectory()) {
                size = size + getFolderLenght(folder.listFiles()[i]);
            } else {
                size = size + folder.listFiles()[i].length();
            }
        }
        return size;

    }


    public static void setContext(Context context) {
        mContext = context;
    }

    public static String getFileNameWithoutExtension(File file) {
        return file.getName().substring(0, file.getName().lastIndexOf("."));
    }

    public static String getFileExtension(File file) {
        return file.getName().substring(file.getName().lastIndexOf("."));
    }

}
