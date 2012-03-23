/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2012  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public.
 *
 * aocode-public is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.util.zip;

import com.aoindustries.io.IoUtils;
import com.aoindustries.lang.NotImplementedException;
import com.aoindustries.util.WrappedException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * ZIP file utilities.
 */
public class ZipUtils {

    /**
     * Gets the time for a ZipEntry, converting from GMT as stored in the ZIP
     * entry to make times correct between time zones.
     *
     * @return  the time assuming GMT zone or <code>-1</code> if not specified.
     *
     * @see #setZipEntryTime(ZipEntry,long)
     */
    public static long getZipEntryTime(ZipEntry entry) {
        long time = entry.getTime();
        return time==-1 ? -1 : time + TimeZone.getDefault().getOffset(time);
    }

    /**
     * Sets the time for a ZipEntry, converting to GMT while storing to the ZIP
     * entry to make times correct between time zones.  The actual time stored
     * may be rounded to the nearest two-second interval.
     *
     * @see #getZipEntryTime(ZipEntry)
     */
    public static void setZipEntryTime(ZipEntry entry, long time) {
        entry.setTime(time - TimeZone.getDefault().getOffset(time));
    }

    /**
     * Recursively packages a directory into a file.
     */
    public static void createZipFile(File sourceDirectory, File zipFile) throws IOException {
        OutputStream out = new FileOutputStream(zipFile);
        try {
            createZipFile(sourceDirectory, out);
        } finally {
            out.close();
        }
    }

    /**
     * Recursively packages a directory into an output stream.
     */
    public static void createZipFile(File sourceDirectory, OutputStream out) throws IOException {
        ZipOutputStream zipOut = new ZipOutputStream(out);
        try {
            createZipFile(sourceDirectory, zipOut);
        } finally {
            zipOut.finish();
        }
    }

    /**
     * Recursively packages a directory into a ZIP output stream.
     */
    public static void createZipFile(File sourceDirectory, ZipOutputStream zipOut) throws IOException {
        File[] list = sourceDirectory.listFiles();
        if(list!=null) {
            for(File file : list) createZipFile(file, zipOut, "");
        }
    }

    /**
     * Recursively packages a directory into a ZIP output stream.
     */
    public static void createZipFile(File file, ZipOutputStream zipOut, String path) throws IOException {
        final String filename = file.getName();
        final String newPath = path.isEmpty() ? filename : (path+'/'+filename);
        if(file.isDirectory()) {
            // Add directory
            ZipEntry zipEntry = new ZipEntry(newPath + '/');
            setZipEntryTime(zipEntry, file.lastModified());
            zipOut.putNextEntry(zipEntry);
            zipOut.closeEntry();
            // Add all children
            File[] list = file.listFiles();
            if(list!=null) {
                for(File child : list) createZipFile(child, zipOut, newPath);
            }
        } else {
            ZipEntry zipEntry = new ZipEntry(newPath);
            setZipEntryTime(zipEntry, file.lastModified());
            zipOut.putNextEntry(zipEntry);
            try {
                InputStream in = new FileInputStream(file);
                try {
                    IoUtils.copy(in, zipOut);
                } finally {
                    in.close();
                }
            } finally {
                zipOut.closeEntry();
            }
        }
    }

    /**
     * Unzips the provided file to the given destination directory.
     */
    public static void unzip(File sourceFile, File destination) throws IOException {
        unzip(sourceFile, "", destination, null);
    }

    private static Comparator<File> reverseFileComparator = new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
            try {
                String path1 = o1.getCanonicalPath();
                String path2 = o2.getCanonicalPath();
                return path2.compareTo(path1);
            } catch(IOException e) {
                throw new WrappedException(e);
            }
        }
    };

    /**
     * Unzips the provided file to the given destination directory.
     */
    public static void unzip(File sourceFile, String sourcePrefix, File destination, ZipEntryFilter filter) throws IOException {
        // Destination directory must exist
        if(!destination.isDirectory()) throw new IOException("Not a directory: "+destination.getPath());
        // Add trailing / to sourcePrefix if missing
        if(!sourcePrefix.isEmpty() && !sourcePrefix.endsWith("/")) sourcePrefix = sourcePrefix+"/";
        ZipFile zipFile = new ZipFile(sourceFile);
        try {
            SortedMap<File,Long> directoryModifyTimes = new TreeMap<File,Long>(reverseFileComparator);

            // Pass one: create directories and files
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while(entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if(sourcePrefix.isEmpty() || name.startsWith(sourcePrefix)) {
                    name = name.substring(sourcePrefix.length());
                    if(!name.isEmpty()) {
                        if(filter==null || filter.accept(entry)) {
                            long entryTime = getZipEntryTime(entry);
                            if(entry.isDirectory()) {
                                name = name.substring(0, name.length()-1);
                                //System.out.println("Directory: "+name);
                                File directory = new File(destination, name);
                                if(!directory.exists()) directory.mkdirs();
                                if(entryTime!=-1) directoryModifyTimes.put(directory, entryTime);
                            } else {
                                //System.out.println("File: "+name);
                                File file = new File(destination, name);
                                File directory = file.getParentFile();
                                if(!directory.exists()) directory.mkdirs();
                                if(file.exists()) throw new IOException("File exists: "+file.getPath());
                                InputStream in = zipFile.getInputStream(entry);
                                try {
                                    OutputStream out = new FileOutputStream(file);
                                    try {
                                        long copyBytes = IoUtils.copy(in, out);
                                        long size = entry.getSize();
                                        if(size!=-1 && copyBytes!=size) throw new IOException("copyBytes!=size: "+copyBytes+"!="+size);
                                    } finally {
                                        out.close();
                                    }
                                    if(entryTime!=-1) file.setLastModified(entryTime);
                                } finally {
                                    in.close();
                                }
                            }
                        }
                    }
                }
            }

            // Pass two: go backwards through directories, setting the modification times
            for(Map.Entry<File,Long> entry : directoryModifyTimes.entrySet()) {
                //System.out.println("File: "+entry.getKey()+", mtime="+entry.getValue());
                entry.getKey().setLastModified(entry.getValue());
            }
        } finally {
            zipFile.close();
        }
    }

    /**
     * Combine contents of all ZIP files while unzipping, only allowing duplicates
     * where the file contents are equal.  When duplicates are found, uses the most
     * recent modification time.
     */
    public static void mergeUnzip(File destination, File ... zipFiles) throws IOException {
        mergeUnzip((ZipEntryFilter)null, destination, zipFiles);
    }

    /**
     * Combine contents of all ZIP files while unzipping, only allowing duplicates
     * where the file contents are equal.  When duplicates are found, uses the most
     * recent modification time.
     */
    public static void mergeUnzip(ZipEntryFilter filter, File destination, File ... zipFiles) throws IOException {
        if(zipFiles.length>0) {
            if(zipFiles.length==1) {
                unzip(zipFiles[0], "", destination, filter);
            } else {
                throw new NotImplementedException("Implement merge feature when first needed");
            }
        }
    }

    /**
     * Copies all non-directory entries.
     */
    public static void copyEntries(File file, ZipOutputStream zipOut) throws IOException {
        copyEntries(file, zipOut, null);
    }

    /**
     * Copies all non-directory entries.
     */
    public static void copyEntries(File file, ZipOutputStream zipOut, ZipEntryFilter filter) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        try {
            copyEntries(zipFile, zipOut, filter);
        } finally {
            zipFile.close();
        }
    }

    /**
     * Copies all non-directory entries.
     */
    public static void copyEntries(ZipFile zipFile, ZipOutputStream zipOut) throws IOException {
        copyEntries(zipFile, zipOut, null);
    }

    /**
     * Copies all entries.
     */
    public static void copyEntries(ZipFile zipFile, ZipOutputStream zipOut, ZipEntryFilter filter) throws IOException {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while(entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if(filter==null || filter.accept(entry)) {
                ZipEntry newEntry = new ZipEntry(entry.getName());
                long time = entry.getTime();
                if(time!=-1) newEntry.setTime(time);
                zipOut.putNextEntry(newEntry);
                try {
                    InputStream in = zipFile.getInputStream(entry);
                    try {
                        IoUtils.copy(in, zipOut);
                    } finally {
                        in.close();
                    }
                } finally {
                    zipOut.closeEntry();
                }
            }
        }
    }

    /**
     * Make no instances.
     */
    private ZipUtils() {
    }
}
