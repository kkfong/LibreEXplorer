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
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.MenuItem;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * LibreFM Low Level Class. It Manages the basic IO and File Manipulation Actions
 */
public class FileManager {
    public static final int REQUEST_GET_DOCUMENT = 0;
    private static File currentDirectory;
    private static File homeDirectory = Environment.getExternalStorageDirectory();
    private static Context mContext;
    private static Boolean isMoving;
    private static Object[] selectedFiles;
    private static File targetFile;

    /**This method chooses the appropriate Apps and Open the file
     * @param file The target file*/
    public static void openFile(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, FileUtilties.getMimeType(uri));
        mContext.startActivity(intent);
    }

    /**This method calls FileManagerActivity refresh() Method*/
    public static void refresh() {
        FileManagerActivity fma = (FileManagerActivity) mContext;
        fma.refresh();
    }

    /**
     * This method prepares the copy of the selected files
     *
     * @param files The Selected Files
     */
    public static void prepareCopy(Object[] files) {
        selectedFiles = files;
        isMoving = false;
        setPasteVisible(true);
    }

    /**
     * This method prepares the copy of the selected files
     *
     * @param files The Selected Files
     */
    public static void prepareCut(Object[] files) {
        selectedFiles = files;
        isMoving = true;
        setPasteVisible(true);
    }


    private static void paste(File sourceFile) throws IOException {
        File destFile = new File(FileManager.getCurrentDirectory().toString() + File.separator + sourceFile.getName());
        File destFileOrig = destFile;
        int lastIndex = 1;
        while (destFile.exists()) {
            destFile = new File(FileManager.getCurrentDirectory() + File.separator + FileUtilties.getFileNameWithoutExtension(destFileOrig) + "(" + lastIndex + ")" + FileUtilties.getFileExtension(destFile));
            lastIndex++;
        }
        try {
            if (sourceFile.isDirectory()) {

                FileUtils.copyDirectory(sourceFile, destFile);
            } else {
                FileUtils.copyFile(sourceFile, destFile);
            }
            if (isMoving) {
                sourceFile.delete();
            }
        } catch (FileNotFoundException e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.setType(FileUtilties.getMimeType(sourceFile));
                intent.putExtra(Intent.EXTRA_TITLE, sourceFile.getName());
                ((FileManagerActivity) mContext).startActivityForResult(intent, REQUEST_GET_DOCUMENT);
            }
        }
    }

    /**
     * This Method use the file set by prepareCopy or PrepareCut and then paste the files
     */
    public static void preparePaste() throws IOException {
        Object[] selectedFiles = getSelectedFiles();
        for (int i = 0; i < selectedFiles.length; i++) {
            setTargetFile((File) selectedFiles[i]);
            paste((File) getTargetFile());
        }

    }

    /**
     * Uses the new KK+ Apis to paste a File to the SD
     *
     * @param uri Uri of the target File
     */
    public static void pasteToSdCard(Uri uri) {

        try {
            InputStream input = new FileInputStream(getTargetFile());
            OutputStream output = mContext.getContentResolver().openOutputStream(uri);
            IOUtils.copy(input, output);
            if (isMoving) {
                getTargetFile().delete();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the given file
     *
     * @param selectedFiles The Selected Files
     * @throws IOException
     */
    public static void delete(Object[] selectedFiles) throws IOException {
        for (int i = 0; i < selectedFiles.length; i++) {
            File file = (File) selectedFiles[i];
            if (file.isDirectory()) {
                FileUtils.deleteDirectory(file);
            } else {
                file.delete();
                if (file.equals(targetFile)){
                    targetFile = null;
                    setPasteVisible(false);
                }
            }
        }
    }

    /**
     * Rename the given file
     *
     * @param sourceFile  The Target File
     * @param newFileName The New File Name
     * @throws IOException
     */
    public static void rename(File sourceFile, String newFileName) throws IOException {
        File destFile = new File(sourceFile.getParent() + File.separator + newFileName);
        if (sourceFile.isDirectory()) {
            FileUtils.copyDirectory(sourceFile, destFile, false);
            sourceFile.delete();
        }
        FileUtils.copyFile(sourceFile, destFile);
        sourceFile.delete();
        if (sourceFile.equals(targetFile)){
            targetFile = destFile;
        }


    }


    //Getter and Setters


    public static File getCurrentDirectory() {
        return currentDirectory;
    }

    public static void setCurrentDirectory(File currentDirectory) {
        FileManager.currentDirectory = currentDirectory;
        refresh();
    }

    public static void setContext(Context mContext) {
        FileManager.mContext = mContext;
    }

    public static File getTargetFile() {
        return targetFile;
    }

    public static void setTargetFile(File targetFile) {
        FileManager.targetFile = targetFile;
    }


    public static void setPasteVisible(Boolean visible) {
        MenuItem item = ((FileManagerActivity) mContext).mMenu.findItem(R.id.toolbar_menu_paste);
        item.setVisible(visible);

    }

    public static File getHomeDirectory() {
        return homeDirectory;
    }

    public static void setHomeDirectory(File homeDirectory) {
        FileManager.homeDirectory = homeDirectory;
    }

    public static Object[] getSelectedFiles() {
        return selectedFiles;
    }

}
