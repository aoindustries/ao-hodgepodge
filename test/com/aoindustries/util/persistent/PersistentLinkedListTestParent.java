/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2008, 2009  AO Industries, Inc.
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
package com.aoindustries.util.persistent;

import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.WrappedException;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Tests the <code>LinkedFileList</code> against the standard <code>LinkedList</code>
 * by performing equal, random actions on each and ensuring equal results.
 *
 * @author  AO Industries, Inc.
 */
abstract public class PersistentLinkedListTestParent extends TestCase {

    public PersistentLinkedListTestParent(String testName) {
        super(testName);
    }

    private static String getRandomString(Random random) {
        int len = random.nextInt(130)-1;
        if(len==-1) return null;
        StringBuilder SB = new StringBuilder(len);
        for(int d=0;d<len;d++) SB.append((char)random.nextInt(Character.MAX_VALUE+1));
        return SB.toString();
    }

    private Random secureRandom = new SecureRandom();
    private Random random = new Random(secureRandom.nextLong());

    protected abstract PersistentBuffer getPersistentBuffer(File tempFile, ProtectionLevel protectionLevel) throws Exception;

    private void doTestCorrectnessString(int numElements) throws Exception {
        File tempFile = File.createTempFile("PersistentLinkedListTest", null);
        tempFile.deleteOnExit();
        PersistentLinkedList<String> linkedFileList = new PersistentLinkedList<String>(getPersistentBuffer(tempFile, ProtectionLevel.NONE), String.class);
        LinkedList<String> linkedList = new LinkedList<String>();
        try {
            // Populate the list
            for(int c=0;c<numElements;c++) {
                String s = getRandomString(random);
                assertEquals(linkedFileList.add(s), linkedList.add(s));
            }
            // Check size match
            assertEquals(linkedFileList.size(), linkedList.size());
            if(numElements>0) {
                // Check first
                assertEquals(linkedFileList.getFirst(), linkedList.getFirst());
                // Check last
                assertEquals(linkedFileList.getLast(), linkedList.getLast());
                // Update random locations to random values
                for(int c=0;c<numElements;c++) {
                    int index = random.nextInt(numElements);
                    String newVal = getRandomString(random);
                    assertEquals(linkedFileList.set(index, newVal), linkedList.set(index, newVal));
                }
            }
            // Check equality
            assertEquals(linkedFileList, linkedList);
            // Remove random indexes
            if(numElements>0) {
                int numRemove = random.nextInt(numElements);
                for(int c=0;c<numRemove;c++) {
                    assertEquals(linkedFileList.size(), linkedList.size());
                    int index = random.nextInt(linkedFileList.size());
                    assertEquals(
                        linkedFileList.remove(index),
                        linkedList.remove(index)
                    );
                }
            }
            // Add random values to end
            if(numElements>0) {
                int numAdd = random.nextInt(numElements);
                for(int c=0;c<numAdd;c++) {
                    assertEquals(linkedFileList.size(), linkedList.size());
                    String newVal = getRandomString(random);
                    assertEquals(linkedFileList.add(newVal), linkedList.add(newVal));
                }
            }
            // Check equality
            assertEquals(linkedFileList, linkedList);
            // Add random values in middle
            if(numElements>0) {
                int numAdd = random.nextInt(numElements);
                for(int c=0;c<numAdd;c++) {
                    assertEquals(linkedFileList.size(), linkedList.size());
                    int index = random.nextInt(linkedFileList.size());
                    String newVal = getRandomString(random);
                    linkedFileList.add(index, newVal);
                    linkedList.add(index, newVal);
                    assertEquals(
                        linkedFileList.remove(index),
                        linkedList.remove(index)
                    );
                }
            }
            // Check equality
            assertEquals(linkedFileList, linkedList);
            // Save and restore, checking matches
            linkedFileList.close();
            PersistentLinkedList<String> newFileList = new PersistentLinkedList<String>(getPersistentBuffer(tempFile, ProtectionLevel.READ_ONLY), String.class);
            try {
                assertEquals(newFileList, linkedList);
            } finally {
                newFileList.close();
            }
        } finally {
            linkedFileList.close();
            linkedFileList = null;
            tempFile.delete();
            new File(tempFile.getPath()+".new").delete();
            new File(tempFile.getPath()+".old").delete();
            linkedList = null;
        }
    }

    /**
     * Tests for correctness comparing to standard LinkedList implementation.
     */
    public void teTODOstCorrectnessString() throws Exception {
        doTestCorrectnessString(0);
        doTestCorrectnessString(1);
        for(int c=0; c<10; c++) doTestCorrectnessString(100 + random.nextInt(101));
    }

    private void doTestCorrectnessInteger(int numElements) throws Exception {
        File tempFile = File.createTempFile("PersistentLinkedListTest", null);
        tempFile.deleteOnExit();
        PersistentLinkedList<Integer> linkedFileList = new PersistentLinkedList<Integer>(getPersistentBuffer(tempFile, ProtectionLevel.NONE), Integer.class);
        LinkedList<Integer> linkedList = new LinkedList<Integer>();
        try {
            // Populate the list
            for(int c=0;c<numElements;c++) {
                Integer I = random.nextInt();
                assertEquals(linkedFileList.add(I), linkedList.add(I));
            }
            // Check size match
            assertEquals(linkedFileList.size(), linkedList.size());
            if(numElements>0) {
                // Check first
                assertEquals(linkedFileList.getFirst(), linkedList.getFirst());
                // Check last
                assertEquals(linkedFileList.getLast(), linkedList.getLast());
                // Update random locations to random values
                for(int c=0;c<numElements;c++) {
                    int index = random.nextInt(numElements);
                    Integer newVal = random.nextInt();
                    assertEquals(linkedFileList.set(index, newVal), linkedList.set(index, newVal));
                }
            }
            // Check equality
            assertEquals(linkedFileList, linkedList);
            // Remove random indexes
            if(numElements>0) {
                int numRemove = random.nextInt(numElements);
                for(int c=0;c<numRemove;c++) {
                    assertEquals(linkedFileList.size(), linkedList.size());
                    int index = random.nextInt(linkedFileList.size());
                    assertEquals(
                        linkedFileList.remove(index),
                        linkedList.remove(index)
                    );
                }
            }
            // Add random values to end
            if(numElements>0) {
                int numAdd = random.nextInt(numElements);
                for(int c=0;c<numAdd;c++) {
                    assertEquals(linkedFileList.size(), linkedList.size());
                    Integer newVal = random.nextInt();
                    assertEquals(linkedFileList.add(newVal), linkedList.add(newVal));
                }
            }
            // Check equality
            assertEquals(linkedFileList, linkedList);
            // Add random values in middle
            if(numElements>0) {
                int numAdd = random.nextInt(numElements);
                for(int c=0;c<numAdd;c++) {
                    assertEquals(linkedFileList.size(), linkedList.size());
                    int index = random.nextInt(linkedFileList.size());
                    Integer newVal = random.nextInt();
                    linkedFileList.add(index, newVal);
                    linkedList.add(index, newVal);
                    assertEquals(
                        linkedFileList.remove(index),
                        linkedList.remove(index)
                    );
                }
            }
            // Check equality
            assertEquals(linkedFileList, linkedList);
            // Save and restore, checking matches
            linkedFileList.close();
            PersistentLinkedList<Integer> newFileList = new PersistentLinkedList<Integer>(getPersistentBuffer(tempFile, ProtectionLevel.READ_ONLY), Integer.class);
            try {
                assertEquals(newFileList, linkedList);
            } finally {
                newFileList.close();
            }
        } finally {
            linkedFileList.close();
            linkedFileList = null;
            tempFile.delete();
            new File(tempFile.getPath()+".new").delete();
            new File(tempFile.getPath()+".old").delete();
            linkedList = null;
        }
    }

    /**
     * Tests for correctness comparing to standard LinkedList implementation.
     */
    public void teTODOstCorrectnessInteger() throws Exception {
        doTestCorrectnessInteger(0);
        doTestCorrectnessInteger(1);
        for(int c=0; c<10; c++) doTestCorrectnessInteger(100 + random.nextInt(101));
    }

    /**
     * Tests the time complexity by adding many elements and making sure the time stays near linear
     */
    public void teTODOstAddRandomStrings() throws Exception {
        File tempFile = File.createTempFile("PersistentLinkedListTest", null);
        tempFile.deleteOnExit();
        PersistentLinkedList<String> linkedFileList = new PersistentLinkedList<String>(getPersistentBuffer(tempFile, ProtectionLevel.NONE), String.class);
        try {
            // Add in groups of 1000, timing the add
            String[] toAdd = new String[1000];
            for(int d=0;d<1000;d++) toAdd[d] = getRandomString(random);
            for(int c=0;c<100;c++) {
                long startNanos = System.nanoTime();
                for(int d=0;d<1000;d++) linkedFileList.add(toAdd[d]);
                long endNanos = System.nanoTime();
                System.out.println((c+1)+" of 100: Added 1000 random strings in "+SQLUtility.getMilliDecimal((endNanos-startNanos)/1000)+" ms");
            }
            // TODO: Calculate the mean and standard deviation, compare for linear
        } finally {
            linkedFileList.close();
            linkedFileList = null;
            tempFile.delete();
            new File(tempFile.getPath()+".new").delete();
            new File(tempFile.getPath()+".old").delete();
        }
    }

    /**
     * Tests the time complexity by adding many elements and making sure the time stays near linear
     */
    public void teTODOstAddRandomIntegers() throws Exception {
        File tempFile = File.createTempFile("PersistentLinkedListTest", null);
        tempFile.deleteOnExit();
        PersistentLinkedList<Integer> linkedFileList = new PersistentLinkedList<Integer>(getPersistentBuffer(tempFile, ProtectionLevel.NONE), Integer.class);
        try {
            // Add in groups of 1000, timing the add
            Integer[] toAdd = new Integer[1000];
            for(int d=0;d<1000;d++) toAdd[d] = random.nextInt();
            for(int c=0;c<100;c++) {
                long startNanos = System.nanoTime();
                for(int d=0;d<1000;d++) linkedFileList.add(toAdd[d]);
                long endNanos = System.nanoTime();
                System.out.println((c+1)+" of 100: Added 1000 random integers in "+SQLUtility.getMilliDecimal((endNanos-startNanos)/1000)+" ms");
            }
            // TODO: Calculate the mean and standard deviation, compare for linear
        } finally {
            linkedFileList.close();
            linkedFileList = null;
            tempFile.delete();
            new File(tempFile.getPath()+".new").delete();
            new File(tempFile.getPath()+".old").delete();
        }
    }

    /**
     * Tests the time complexity for integers (all null to avoid serialization)
     */
    public void teTODOstAddNull() throws Exception {
        File tempFile = File.createTempFile("PersistentLinkedListTest", null);
        tempFile.deleteOnExit();
        PersistentLinkedList<Integer> linkedFileList = new PersistentLinkedList<Integer>(getPersistentBuffer(tempFile, ProtectionLevel.NONE), Integer.class);
        try {
            // Add in groups of 1000, timing the add
            for(int c=0;c<100;c++) {
                long startNanos = System.nanoTime();
                for(int d=0;d<1000;d++) linkedFileList.add(null);
                long endNanos = System.nanoTime();
                System.out.println((c+1)+" of 100: Added 1000 null Integer in "+SQLUtility.getMilliDecimal((endNanos-startNanos)/1000)+" ms");
            }
            // TODO: Calculate the mean and standard deviation, compare for linear
        } finally {
            linkedFileList.close();
            linkedFileList = null;
            tempFile.delete();
            new File(tempFile.getPath()+".new").delete();
            new File(tempFile.getPath()+".old").delete();
        }
    }

    /**
     * Test iteration performance.
     */
    public void teTODOstStringIterationPerformance() throws Exception {
        File tempFile = File.createTempFile("PersistentLinkedListTest", null);
        tempFile.deleteOnExit();
        PersistentLinkedList<String> linkedFileList = new PersistentLinkedList<String>(getPersistentBuffer(tempFile, ProtectionLevel.NONE), String.class);
        try {
            for(int c=0;c<=100;c++) {
                if(c>0) for(int d=0;d<100;d++) linkedFileList.add(getRandomString(random));
                long startNanos = System.nanoTime();
                for(String value : linkedFileList) {
                    // Do nothing
                }
                long endNanos = System.nanoTime();
                System.out.println("Iterated "+linkedFileList.size()+" random strings in "+SQLUtility.getMilliDecimal((endNanos-startNanos)/1000)+" ms");
            }
        } finally {
            linkedFileList.close();
            linkedFileList = null;
            tempFile.delete();
            new File(tempFile.getPath()+".new").delete();
            new File(tempFile.getPath()+".old").delete();
        }
    }

    /**
     * Test iteration performance.
     */
    public void teTODOstIntegerIterationPerformance() throws Exception {
        File tempFile = File.createTempFile("PersistentLinkedListTest", null);
        tempFile.deleteOnExit();
        PersistentLinkedList<Integer> linkedFileList = new PersistentLinkedList<Integer>(getPersistentBuffer(tempFile, ProtectionLevel.NONE), Integer.class);
        try {
            for(int c=0;c<=100;c++) {
                if(c>0) for(int d=0;d<100;d++) linkedFileList.add(random.nextInt());
                long startNanos = System.nanoTime();
                for(Integer value : linkedFileList) {
                    // Do nothing
                }
                long endNanos = System.nanoTime();
                System.out.println("Iterated "+linkedFileList.size()+" random integers in "+SQLUtility.getMilliDecimal((endNanos-startNanos)/1000)+" ms");
            }
        } finally {
            linkedFileList.close();
            linkedFileList = null;
            tempFile.delete();
            new File(tempFile.getPath()+".new").delete();
            new File(tempFile.getPath()+".old").delete();
        }
    }

    /**
     * Test circular list performance.
     */
    public void teTODOstStringCircularListPerformance() throws Exception {
        File tempFile = File.createTempFile("PersistentLinkedListTest", null);
        tempFile.deleteOnExit();
        PersistentLinkedList<String> linkedFileList = new PersistentLinkedList<String>(getPersistentBuffer(tempFile, ProtectionLevel.NONE), String.class);
        try {
            for(int c=0;c<100000;c++) {
                String newValue = getRandomString(random);
                String oldValue = null;
                if(linkedFileList.size()>=1000) oldValue = linkedFileList.removeLast();
                linkedFileList.addFirst(newValue);
            }
        } finally {
            linkedFileList.close();
            linkedFileList = null;
            tempFile.delete();
            new File(tempFile.getPath()+".new").delete();
            new File(tempFile.getPath()+".old").delete();
        }
    }

    /**
     * Test circular list performance.
     */
    public void teTODOstIntegerCircularListPerformance() throws Exception {
        File tempFile = File.createTempFile("PersistentLinkedListTest", null);
        tempFile.deleteOnExit();
        PersistentLinkedList<Integer> linkedFileList = new PersistentLinkedList<Integer>(getPersistentBuffer(tempFile, ProtectionLevel.NONE), Integer.class);
        try {
            for(int c=0;c<100000;c++) {
                Integer newValue = random.nextInt();
                Integer oldValue = null;
                if(linkedFileList.size()>=1000) oldValue = linkedFileList.removeLast();
                linkedFileList.addFirst(newValue);
            }
        } finally {
            linkedFileList.close();
            linkedFileList = null;
            tempFile.delete();
            new File(tempFile.getPath()+".new").delete();
            new File(tempFile.getPath()+".old").delete();
        }
    }

    /**
     * Ensures that the persistent list is not corrupt after a simulated failure.
     * <ol>
     *   <li>The persistent list must be the same size as linkedList or one smaller (for incomplete add).</li>
     *   <li>The persistent list may not have any entry that is not in linkedList.</li>
     *   <li>
     *     The persistent list may only be missing one item that is in linkedList,
     *     and this is only acceptable when partial is not null and the missing item
     *     equals partial.  When this is found, linkedList is updated.  At this point
     *     the two lists should precisely match.
     *   </li>
     *   <li>The lists must be the same size.</li>
     *   <li>The lists must iterate both forwards and backwards with identical elements.</li>
     * </ol>
     */
    private static void checkAddRecoveryConsistency(LinkedList<String> linkedList, PersistentLinkedList<String> linkedFileList, String partial) {
        assertTrue("Size mismatch: linkedList.size()="+linkedList.size()+", linkedFileList.size()="+linkedFileList.size(), linkedList.size()==linkedFileList.size() || (linkedList.size()-1)==linkedFileList.size());
        // TODO: Implement rest of the checks
    }

    private void doTestFailureRecovery(ProtectionLevel protectionLevel) throws Exception {
        File tempFile = File.createTempFile("PersistentLinkedListTest", null);
        tempFile.deleteOnExit();
        try {
            LinkedList<String> linkedList = new LinkedList<String>();
            final int iterations = 100;
            for(int c=0;c<iterations;c++) {
                String partial = null;
                // addFirst
                long startNanos = System.nanoTime();
                try {
                    try {
                        PersistentLinkedList<String> linkedFileList = new PersistentLinkedList<String>(new RandomFailBuffer(getPersistentBuffer(tempFile, protectionLevel), true), String.class);
                        try {
                            int batchSize = random.nextInt(100)+1;
                            for(int d=0;d<batchSize;d++) {
                                partial = getRandomString(random);
                                linkedList.add(partial);
                                linkedFileList.add(partial);
                                partial = null;
                            }
                        } finally {
                            linkedFileList.close();
                            linkedFileList = null;
                        }
                    } catch(WrappedException err) {
                        Throwable cause = err.getCause();
                        if(cause!=null && (cause instanceof IOException)) throw (IOException)cause;
                        throw err;
                    }
                } catch(IOException err) {
                    System.out.println(protectionLevel+": "+(c+1)+" of "+iterations+": addFirst: Caught failure: "+err.toString());
                }
                // Check consistency
                PersistentLinkedList<String> linkedFileList = new PersistentLinkedList<String>(getPersistentBuffer(tempFile, protectionLevel), String.class);
                try {
                    checkAddRecoveryConsistency(linkedList, linkedFileList, partial);
                } finally {
                    linkedFileList.close();
                    linkedFileList = null;
                }
                // TODO: removeLast
                // TODO: Check consistency
                // TODO: add random
                // TODO: Check consistency
                // TODO: remove random
                // TODO: Check consistency
                long endNanos = System.nanoTime();
                if((c%10)==9) System.out.println(protectionLevel+": "+(c+1)+" of "+iterations+": Tested block buffer failure recovery in "+SQLUtility.getMilliDecimal((endNanos-startNanos)/1000)+" ms");
            }
        } finally {
            tempFile.delete();
            new File(tempFile.getPath()+".new").delete();
            new File(tempFile.getPath()+".old").delete();
        }
    }

    /**
     * Tests the durability of a persistent linked list when in barrier mode.
     */
    public void testFailureRecoveryBarrier() throws Exception {
        doTestFailureRecovery(ProtectionLevel.BARRIER);
    }

    /**
     * Tests the durability of a persistent linked list when in force mode.
     */
    public void testFailureRecoveryForce() throws Exception {
        doTestFailureRecovery(ProtectionLevel.FORCE);
    }
}
