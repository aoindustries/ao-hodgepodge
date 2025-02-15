/*
 * @(#)EQSortAlgorithm.java 1.0 95/06/26 Jim Boritz
 *
 * Copyright (c) 1995 UBC Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * UBC MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. UBC SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package com.aoapps.hodgepodge.sort;

import java.util.Comparator;
import java.util.List;

/**
 * An enhanced quick sort demonstration algorithm
 * SortAlgorithm.java, Thu Oct 27 10:32:35 1994
 *
 * <pre>19 Feb 1996: Fixed to avoid infinite loop discoved by Paul Haberli.
 *              Misbehaviour expressed when the pivot element was not unique.
 *              -Jason Harrison
 *
 * 21 Jun 1996: Modified code based on comments from Paul Haeberli, and
 *              Peter Schweizer (Peter.Schweizer@mni.fh-giessen.de).
 *              Used Daeron Meyer's (daeron@geom.umn.edu) code for the
 *              new pivoting code. - Jason Harrison
 *
 * 09 Jan 1998: Another set of bug fixes by Thomas Everth (everth@wave.co.nz)
 *              and John Brzustowski (jbrzusto@gpu.srv.ualberta.ca).</pre>
 *
 * <p>Adapted from <a href="http://www.cs.ubc.ca/spider/harrison/Java/EQSortAlgorithm.java.html">Jim Boritz' EQSortAlgorithm</a>.</p>
 *
 * @author Jim Boritz
 * @version 1.0, 26 Jun 1995
 */
public final class EQSort extends BaseComparisonSortAlgorithm<Object> {

  private static final EQSort instance = new EQSort();

  public static EQSort getInstance() {
    return instance;
  }

  private EQSort() {
    // Do nothing
  }

  @Override
  public boolean isStable() {
    return false; // Not really sure since based on quicksort, safer to say not stable.
  }

  @Override
  public <T> void sort(List<T> list, Comparator<? super T> comparator, SortStatistics stats) {
    if (stats != null) {
      stats.sortStarting();
    }
    sort(list, 0, list.size() - 1, comparator, stats);
    if (stats != null) {
      stats.sortEnding();
    }
  }

  @Override
  public <T> void sort(T[] array, Comparator<? super T> comparator, SortStatistics stats) {
    if (stats != null) {
      stats.sortStarting();
    }
    sort(array, 0, array.length - 1, comparator, stats);
    if (stats != null) {
      stats.sortEnding();
    }
  }

  private static <T> void sort(List<T> list, int lo0, int hi0, Comparator<? super T> comparator, SortStatistics stats) {
    int lo = lo0;
    int hi = hi0;
    if ((hi - lo) <= 3) {
      if (stats != null) {
        stats.sortRecursing();
      }
      brute(list, lo, hi, comparator, stats);
      if (stats != null) {
        stats.sortUnrecursing();
      }
      return;
    }

    /*
     *  Pick a pivot and move it out of the way
     */
    T pivot = get(list, (lo + hi) / 2, stats);
    set(list, (lo + hi) / 2, get(list, hi, stats), stats);
    set(list, hi, pivot, stats);

    while (lo < hi) {
      /*
       *  Search forward from a[lo] until an element is found that
       *  is greater than the pivot or lo >= hi
       */
      while (
          compare(get(list, lo, stats), pivot, comparator, stats) <= 0
              && lo < hi
      ) {
        lo++;
      }

      /*
       *  Search backward from a[hi] until element is found that
       *  is less than the pivot, or hi <= lo
       */
      while (
          compare(pivot, get(list, hi, stats), comparator, stats) <= 0
              && lo < hi
      ) {
        hi--;
      }

      /*
       *  Swap elements a[lo] and a[hi]
       */
      if (lo < hi) {
        swap(list, lo, hi, stats);
      }
    }

    /*
     *  Put the median in the "center" of the list
     */
    set(list, hi0, get(list, hi, stats), stats);
    set(list, hi, pivot, stats);

    /*
     *  Recursive calls, elements a[lo0] to a[lo-1] are less than or
     *  equal to pivot, elements a[hi+1] to a[hi0] are greater than
     *  pivot.
     */
    if (stats != null) {
      stats.sortRecursing();
    }
    sort(list, lo0, lo - 1, comparator, stats);
    if (stats != null) {
      stats.sortUnrecursing();
    }

    if (stats != null) {
      stats.sortRecursing();
    }
    sort(list, hi + 1, hi0, comparator, stats);
    if (stats != null) {
      stats.sortUnrecursing();
    }
  }

  private static <T> void sort(T[] array, int lo0, int hi0, Comparator<? super T> comparator, SortStatistics stats) {
    int lo = lo0;
    int hi = hi0;
    if ((hi - lo) <= 3) {
      if (stats != null) {
        stats.sortRecursing();
      }
      brute(array, lo, hi, comparator, stats);
      if (stats != null) {
        stats.sortUnrecursing();
      }
      return;
    }

    /*
     *  Pick a pivot and move it out of the way
     */
    T pivot = get(array, (lo + hi) / 2, stats);
    set(array, (lo + hi) / 2, get(array, hi, stats), stats);
    set(array, hi, pivot, stats);

    while (lo < hi) {
      /*
       *  Search forward from a[lo] until an element is found that
       *  is greater than the pivot or lo >= hi
       */
      while (
          compare(get(array, lo, stats), pivot, comparator, stats) <= 0
              && lo < hi
      ) {
        lo++;
      }

      /*
       *  Search backward from a[hi] until element is found that
       *  is less than the pivot, or hi <= lo
       */
      while (
          compare(pivot, get(array, hi, stats), comparator, stats) <= 0
              && lo < hi
      ) {
        hi--;
      }

      /*
       *  Swap elements a[lo] and a[hi]
       */
      if (lo < hi) {
        swap(array, lo, hi, stats);
      }
    }

    /*
     *  Put the median in the "center" of the list
     */
    set(array, hi0, get(array, hi, stats), stats);
    set(array, hi, pivot, stats);

    /*
     *  Recursive calls, elements a[lo0] to a[lo-1] are less than or
     *  equal to pivot, elements a[hi+1] to a[hi0] are greater than
     *  pivot.
     */
    if (stats != null) {
      stats.sortRecursing();
    }
    sort(array, lo0, lo - 1, comparator, stats);
    if (stats != null) {
      stats.sortUnrecursing();
    }

    if (stats != null) {
      stats.sortRecursing();
    }
    sort(array, hi + 1, hi0, comparator, stats);
    if (stats != null) {
      stats.sortUnrecursing();
    }
  }

  private static <T> void brute(List<T> list, int lo, int hi, Comparator<? super T> comparator, SortStatistics stats) {
    if ((hi - lo) == 1) {
      T ohi = get(list, hi, stats);
      T olo = get(list, lo, stats);
      if (compare(ohi, olo, comparator, stats) < 0) {
        set(list, lo, ohi, stats);
        set(list, hi, olo, stats);
      }
    }
    if ((hi - lo) == 2) {
      T olo = get(list, lo, stats);
      int pmin = compare(olo, get(list, lo + 1, stats), comparator, stats) < 0 ? lo : lo + 1;
      pmin = compare(get(list, pmin, stats), get(list, lo + 2, stats), comparator, stats) < 0 ? pmin : lo + 2;
      if (pmin != lo) {
        set(list, lo, get(list, pmin, stats), stats);
        set(list, pmin, olo, stats);
      }
      if (stats != null) {
        stats.sortRecursing();
      }
      brute(list, lo + 1, hi, comparator, stats);
      if (stats != null) {
        stats.sortUnrecursing();
      }
    }
    if ((hi - lo) == 3) {
      int pmin = compare(get(list, lo, stats), get(list, lo + 1, stats), comparator, stats) < 0 ? lo : lo + 1;
      pmin = compare(get(list, pmin, stats), get(list, lo + 2, stats), comparator, stats) < 0 ? pmin : lo + 2;
      pmin = compare(get(list, pmin, stats), get(list, lo + 3, stats), comparator, stats) < 0 ? pmin : lo + 3;
      if (pmin != lo) {
        swap(list, lo, pmin, stats);
      }

      int pmax = compare(get(list, hi, stats), get(list, hi - 1, stats), comparator, stats) > 0 ? hi : hi - 1;
      pmax = compare(get(list, pmax, stats), get(list, hi - 2, stats), comparator, stats) > 0 ? pmax : hi - 2;
      if (pmax != hi) {
        swap(list, hi, pmax, stats);
      }

      if (stats != null) {
        stats.sortRecursing();
      }
      brute(list, lo + 1, hi - 1, comparator, stats);
      if (stats != null) {
        stats.sortUnrecursing();
      }
    }
  }

  private static <T> void brute(T[] array, int lo, int hi, Comparator<T> comparator, SortStatistics stats) {
    if ((hi - lo) == 1) {
      T ohi = get(array, hi, stats);
      T olo = get(array, lo, stats);
      if (compare(ohi, olo, comparator, stats) < 0) {
        set(array, lo, ohi, stats);
        set(array, hi, olo, stats);
      }
    }
    if ((hi - lo) == 2) {
      T olo = get(array, lo, stats);
      int pmin = compare(olo, get(array, lo + 1, stats), comparator, stats) < 0 ? lo : lo + 1;
      pmin = compare(get(array, pmin, stats), get(array, lo + 2, stats), comparator, stats) < 0 ? pmin : lo + 2;
      if (pmin != lo) {
        set(array, lo, get(array, pmin, stats), stats);
        set(array, pmin, olo, stats);
      }
      if (stats != null) {
        stats.sortRecursing();
      }
      brute(array, lo + 1, hi, comparator, stats);
      if (stats != null) {
        stats.sortUnrecursing();
      }
    }
    if ((hi - lo) == 3) {
      int pmin = compare(get(array, lo, stats), get(array, lo + 1, stats), comparator, stats) < 0 ? lo : lo + 1;
      pmin = compare(get(array, pmin, stats), get(array, lo + 2, stats), comparator, stats) < 0 ? pmin : lo + 2;
      pmin = compare(get(array, pmin, stats), get(array, lo + 3, stats), comparator, stats) < 0 ? pmin : lo + 3;
      if (pmin != lo) {
        swap(array, lo, pmin, stats);
      }

      int pmax = compare(get(array, hi, stats), get(array, hi - 1, stats), comparator, stats) > 0 ? hi : hi - 1;
      pmax = compare(get(array, pmax, stats), get(array, hi - 2, stats), comparator, stats) > 0 ? pmax : hi - 2;
      if (pmax != hi) {
        swap(array, hi, pmax, stats);
      }

      if (stats != null) {
        stats.sortRecursing();
      }
      brute(array, lo + 1, hi - 1, comparator, stats);
      if (stats != null) {
        stats.sortUnrecursing();
      }
    }
  }
}
