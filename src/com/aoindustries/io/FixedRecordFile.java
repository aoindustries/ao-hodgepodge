package com.aoindustries.io;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;

/**
 * A way to more easily manipulate files with fixed-record-size rows.
 *
 * @author  AO Industries, Inc.
 */
public class FixedRecordFile extends RandomAccessFile {

    final private int recordLength;

    final private byte[] buff1;
    final private byte[] buff2;

    public FixedRecordFile(
        String name,
        String mode,
        int recordLength
    ) throws FileNotFoundException {
        super(name, mode);
        Profiler.startProfile(Profiler.FAST, FixedRecordFile.class, "<init>(String,String,int)", null);
        try {
            this.recordLength=recordLength;
            buff1=new byte[recordLength];
            buff2=new byte[recordLength];
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public FixedRecordFile(
        File file,
        String mode,
        int recordLength
    ) throws FileNotFoundException {
        super(file, mode);
        Profiler.startProfile(Profiler.FAST, FixedRecordFile.class, "<init>(File,String,int)", null);
        try {
            this.recordLength=recordLength;
            buff1=new byte[recordLength];
            buff2=new byte[recordLength];
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    public void seekToExistingRecord(int index) throws IndexOutOfBoundsException, IOException {
        Profiler.startProfile(Profiler.IO, FixedRecordFile.class, "seekToExistingRecord(int)", null);
        try {
            if(index<0) throw new IndexOutOfBoundsException(index+"<0");
            long startPos=(long)index*recordLength;
            if(startPos>=length()) throw new IndexOutOfBoundsException(index+">="+getRecordCount());
            seek(startPos);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void addRecord(int index) throws IndexOutOfBoundsException, IOException {
        Profiler.startProfile(Profiler.FAST, FixedRecordFile.class, "addRecord(int)", null);
        try {
            addRecords(index, 1);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void addRecords(int index, int numRecords) throws IndexOutOfBoundsException, IOException {
        Profiler.startProfile(Profiler.IO, FixedRecordFile.class, "addRecord(int,numRecords)", null);
        try {
            if(numRecords<0) throw new IllegalArgumentException("numRecords<0: "+numRecords);

            if(numRecords>0) {
                if(index<0) throw new IndexOutOfBoundsException("index<0: "+index);
                long recordsStart=(long)index*recordLength;
                long recordsBytes=(long)numRecords*recordLength;
                long recordsEnd=recordsStart+recordsBytes;

                if(recordsStart>=length()) {
                    // Add the record to the end
                    setLength(recordsEnd);
                } else {
                    // Insert the record if not at the end
                    long moveLength=length()-recordsStart;
                    setLength(length()+recordsBytes);
                    copyBytes(this, recordsStart, this, recordsStart+recordsBytes, moveLength);
                }
                seek(recordsStart);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public int getRecordCount() throws IOException {
        Profiler.startProfile(Profiler.IO, FixedRecordFile.class, "getRecordCount()", null);
        try {
            long size=length()/recordLength;
            return size>Integer.MAX_VALUE?Integer.MAX_VALUE:(int)size;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public static void copyBytes(RandomAccessFile from, long fromIndex, RandomAccessFile to, long toIndex, long numBytes) throws IOException {
        Profiler.startProfile(Profiler.IO, FixedRecordFile.class, "copyBytes(RandomAccessFile,long,RandomAccessFile,long,long)", null);
        try {
            if(numBytes<0) throw new IllegalArgumentException("numBytes<0: "+numBytes);

            if(numBytes>0) {
                byte[] buff=BufferManager.getBytes();
                try {
                    if(fromIndex<toIndex) {
                        // Perform the copy backward
                        long readLocation=fromIndex+numBytes;
                        while(readLocation>fromIndex) {
                            long readEnd=readLocation;
                            readLocation-=BufferManager.BUFFER_SIZE;
                            if(readLocation<fromIndex) readLocation=fromIndex;
                            from.seek(readLocation);
                            int pos=0;
                            int bytesLeft;
                            while((bytesLeft=(int)(readEnd-readLocation-pos))>0) {
                                int ret=from.read(buff, pos, bytesLeft);
                                if(ret==-1) throw new EOFException();
                                pos+=ret;
                            }
                            to.seek(toIndex+(readLocation-fromIndex));
                            to.write(buff, 0, pos);
                        }
                    } else {
                        // Perform the copy forward
                        long numCopied=0;
                        while(numCopied<numBytes) {
                            long blockSizeLong=numBytes-numCopied;
                            int blockSize=blockSizeLong>BufferManager.BUFFER_SIZE?BufferManager.BUFFER_SIZE:(int)blockSizeLong;
                            from.seek(fromIndex+numCopied);
                            int ret=from.read(buff, 0, blockSize);
                            if(ret==-1) throw new EOFException();
                            to.seek(toIndex+numCopied);
                            to.write(buff, 0, ret);
                            numCopied+=ret;
                        }
                    }
                } finally {
                    BufferManager.release(buff);
                }
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public static void copyRecords(FixedRecordFile from, long fromIndex, FixedRecordFile to, long toIndex, long numRecords) throws IOException {
        Profiler.startProfile(Profiler.FAST, FixedRecordFile.class, "copyRecords(FixedRecordFile,long,FixedRecordFile,long,long)", null);
        try {
            if(numRecords<0) throw new IllegalArgumentException("numRecords<0: "+numRecords);

            int recordLength=from.recordLength;
            if(recordLength!=to.recordLength) throw new IllegalArgumentException("Files do not have the same record length: from.recordLength="+recordLength+", to.recordLength="+to.recordLength);

            copyBytes(from, (long)fromIndex*recordLength, to, (long)toIndex*recordLength, (long)numRecords*recordLength);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void removeAllRecords() throws IOException {
        Profiler.startProfile(Profiler.IO, FixedRecordFile.class, "removeAllRecords()", null);
        try {
            setLength(0);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void removeRecord(int index) throws IOException {
        Profiler.startProfile(Profiler.IO, FixedRecordFile.class, "removeRecord(int)", null);
        try {
            if(index<0) throw new IndexOutOfBoundsException(index+"<0");
            long startPos=(long)index*recordLength;
            if(startPos>=length()) throw new IndexOutOfBoundsException(index+">="+getRecordCount());

            // Shift objects if not at new end of list
            long newEnd=length()-recordLength;
            if(newEnd>startPos) copyBytes(this, startPos+recordLength, this, startPos, newEnd-startPos);

            // Truncate the file
            setLength(newEnd);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }

    }
    
    public int getRecordLength() throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, FixedRecordFile.class, "getRecordLength()", null);
        try {
            return recordLength;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public void swap(int index1, int index2) throws IOException {
        Profiler.startProfile(Profiler.IO, FixedRecordFile.class, "swap(int,int)", null);
        try {
            if(index1!=index2) {
                if(index1<0) throw new IndexOutOfBoundsException("index1<0: "+index1);
                long startPos1=(long)index1*recordLength;
                if(startPos1>=length()) throw new IndexOutOfBoundsException("index1>="+getRecordCount()+": "+index1);

                if(index2<0) throw new IndexOutOfBoundsException("index2<0: "+index2);
                long startPos2=(long)index2*recordLength;
                if(startPos2>=length()) throw new IndexOutOfBoundsException("index2>="+getRecordCount()+": "+index2);

                // Do the swap
                seek(startPos1);
                readFully(buff1);
                seek(startPos2);
                readFully(buff2);
                seek(startPos2);
                write(buff1);
                seek(startPos1);
                write(buff2);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}
