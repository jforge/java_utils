package com.mplify.id.ranges;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mplify.checkers.Check;
import com.mplify.id.AbstractId;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2009, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Class to represent ranges of integer-based ids (T extends AbstractId;
 * we use a generic type RangeSequence<T extends AbstractId> to be 
 * a bit more precise and constraining than if we just used a 
 * RangeSequence based on AbstractId.
 *   
 * As the "ids" are evidently integer-based, ranges like 
 * 
 * [low,high]
 * 
 * with both limits inclusive make sense.
 * 
 * "RangeSequence" collects a number of these ranges into an ordered sequence
 * of non-intersecting, non-abutting ranges (i.e. it fuses ranges whenever
 * possible if ranges are added). 
 * 
 * "Single id ranges" are naturally represented by [id,id]
 * 
 * Note that we can't use the "T" constructor, but need a factory
 * class if construction is needed. 
 * 
 * TODO: This is a linear structure; a search tree would be better
 *       There should be a away to "thicken" sequences so that small
 *       holes are etched away to yield simpler range structure. 
 * 
 * 2009.02.XX - Created
 * 2009.12.18 - Slight review, moved imbricated factory interface to toplevel
 *              and also moved parser code to its own class.
 *              Added "addRangeSequence()"
 * 2009.12.29 - Reviewed because the original code is dogslow if there
 *              are millions of entries
 * 2010.09.27 - Moved from the specialized project "70_msgserver_cli" 
 *              to project "04_core_low" and package "com.mplify.id_ranges".
 * 2011.02.21 - Introduced Check
 * 2011.10.17 - Reformatting, comments added
 * 2012.05.22 - Added getFirst(), getLast()
 ******************************************************************************/

public class IdRangeSequence<T extends AbstractId> {

    private final static String CLASS = IdRangeSequence.class.getName();
    private final static Logger LOGGER_thicken = LoggerFactory.getLogger(CLASS + ".thicken");

    /**
     * The current sequence of ranges; no two ranges intersect, they are ordered so that range N comes strictly before
     * range N+1 and no two adjacent ranges are immediately adjacent. In order to allow for reasonably fast searches, a
     * search tree is used, with the ranges compared by their start values.
     */

    private static class RangeComparator<H extends AbstractId> implements Comparator<IdRange<H>> {

        @Override
        public int compare(IdRange<H> o1, IdRange<H> o2) {
            assert o1 != null;
            assert o2 != null;
            return o1.getLow().getValue() - o2.getLow().getValue();
        }
    }

    private final SortedSet<IdRange<T>> sequence = new TreeSet<IdRange<T>>(new RangeComparator<T>());

    /**
     * Buffer for toString() operation
     */

    private String toStringBuf = null;

    /**
     * Helper to compute the max of two ids
     */

    private T max(T a, T b) {
        if (a.getValue() > b.getValue()) {
            return a;
        } else {
            return b;
        }
    }

    /**
     * Helper to compute the min of two ids
     */

    @SuppressWarnings("unused")
    private T min(T a, T b) {
        if (a.getValue() > b.getValue()) {
            return b;
        } else {
            return a;
        }
    }

    /**
     * Add a complete range sequence. The passed range sequence is not modified!
     * To check the sequence invariant, set "checkSequence". This is best done in test
     * code because it is expensive.
     */

    public void addRangeSequence(IdRangeSequence<T> newRangeSequence, boolean checkSequence) {
        Check.notNull(newRangeSequence, "new range sequence");
        Check.isTrue(newRangeSequence != this, "The passed 'new range sequence' is the same as 'this'");
        for (IdRange<T> range : newRangeSequence.sequence) {
            this.addRange(range, checkSequence);
        }
    }

    /**
     * Add a single id! In order to not make this too costly, a separate implementation than addRange() exists.
     * "checkSequence" is expensive, use only if needed.
     */

    public void addId(T id, boolean checkSequence) {
        if (checkSequence) {
            checkSequence();
        }
        Check.notNull(id, "id");
        // the buffer holding the description is eliminated and will have to be rebuild on need
        toStringBuf = null;
        // find all those ranges that are strictly above "id" and that may abut
        // find all those ranges that are strictly below "id" and that may intersect
        IdRange<T> newRange = new IdRange<T>(id, id);
        SortedSet<IdRange<T>> tailSet = sequence.tailSet(newRange);
        SortedSet<IdRange<T>> headSet = sequence.headSet(newRange);
        // the "tail set" contains those ranges that start at "newRange" or come after "newRange"
        // possibilities: "newRange" comes strictly before the first range in tailSet --> DO NOTHING
        // "newRange" abuts the first range in tailSet --> FUSE to form a new "newRange", removing the fused range
        // ...then check how "newRange" intersect with the head set
        // "newRange" comes strictly after the last range in headSet ---> DO NOTHING
        // "newRange" abuts or is (partially/fully) covered by the last range in headSet ---> FUSE to form a new "newRange", remove the fused range
        // Finally, insert the "newRange"
        if (tailSet.isEmpty() || newRange.getHigh().getValue() + 1 < tailSet.first().getLow().getValue()) {
            // DO NOTHING
        } else {
            // fuse exactly once
            IdRange<T> abutting = tailSet.first();
            tailSet.remove(abutting);
            T lowest = newRange.getLow();
            T highest = abutting.getHigh();
            // TODO: Some mutability to avoid unnecessary "news" would be nice
            newRange = new IdRange<T>(lowest, highest);
        }
        if (headSet.isEmpty() || headSet.last().getHigh().getValue() + 1 < newRange.getLow().getValue()) {
            // DO NOTHING
        } else {
            // fuse exactly once
            IdRange<T> intersecting = headSet.last();
            headSet.remove(intersecting);
            T lowest = intersecting.getLow();
            T highest = max(intersecting.getHigh(), newRange.getHigh());
            // TODO: Some mutability to avoid unnecessary "news" would be nice
            newRange = new IdRange<T>(lowest, highest);
        }
        sequence.add(newRange);
        if (checkSequence) {
            checkSequence();
        }
    }

    /**
     * Add a single range of length 1 or N>1; this may cause fusion and destruction of existing ranges
     */

    public void addRange(IdRange<T> newRangeIn, boolean checkSequence) {
        Check.notNull(newRangeIn, "new range");
        IdRange<T> newRange = newRangeIn; // this is done to avoid setting newRangeId; keeps compiler happy
        if (checkSequence) {
            checkSequence();
        }
        // the buffer holding the description is eliminated and will have to be rebuild on need
        toStringBuf = null;
        // find all those ranges that are strictly above "newRange" and that may intersect
        // find all those ranges that are strictly below "newRange" and that may intersect
        SortedSet<IdRange<T>> tailSet = sequence.tailSet(newRange);
        SortedSet<IdRange<T>> headSet = sequence.headSet(newRange);
        // fuse into "tailset" (may take many fuse operations)
        while (!tailSet.isEmpty() && tailSet.first().getLow().getValue() <= newRange.getHigh().getValue() + 1) {
            IdRange<T> fuseThis = tailSet.first();
            tailSet.remove(fuseThis);
            T lowest = newRange.getLow();
            T highest = max(fuseThis.getHigh(), newRange.getHigh());
            newRange = new IdRange<T>(lowest, highest);
        }
        // fuse into "headset" (may take at most one fuse operation)
        if (headSet.isEmpty() || headSet.last().getHigh().getValue() + 1 < newRange.getLow().getValue()) {
            // DO NOTHING
        } else {
            // fuse exactly once
            IdRange<T> intersecting = headSet.last();
            headSet.remove(intersecting);
            T lowest = intersecting.getLow();
            T highest = max(intersecting.getHigh(), newRange.getHigh());
            newRange = new IdRange<T>(lowest, highest);
        }
        sequence.add(newRange);
        if (checkSequence) {
            checkSequence();
        }
    }

    /**
     * Check constraints. Calls to this should be avoided in prod code.
     */

    public void checkSequence() {
        IdRange<T> prev = null;
        for (IdRange<T> cur : sequence) {
            if (cur.getLow().getValue() > cur.getHigh().getValue()) {
                throw new IllegalStateException("Check fails: " + cur);
            }
            if (prev != null) {
                if (prev.getHigh().getValue() >= cur.getLow().getValue()) {
                    throw new IllegalStateException("Check fails: " + prev + "," + cur);
                }
            }
            prev = cur;
        }
    }
    
    /**
     * First entry (null if not exist)
     */

    public T getFirst() {
        if (sequence.isEmpty()) {
            return null; // separate case to avoid exception
        }
        else {
            return sequence.first().getLow();
        }
    }
    
    /**
     * Last entry (null if not exist)
     */

    public T getLast() {
        if (sequence.isEmpty()) {
            return null; // separate case to avoid exception
        }
        else {
            return sequence.last().getHigh();
        }        
    }

    /**
     * How many entries?
     */
    
    public int getCount() {
        int res = 0;
        for (IdRange<T> cur : sequence) {
            res += cur.size();
        }
        return res;
    }
    
    /**
     * Make a string describing the range sequence; it can be re-parsed through "addFromString()"
     */

    @Override
    public String toString() {
        if (toStringBuf == null) {
            StringBuilder b = new StringBuilder();
            boolean addComma = false;
            for (IdRange<T> range : sequence) {
                if (addComma) {
                    b.append(",");
                }
                b.append(range.toString());
                addComma = true;
            }
            toStringBuf = b.toString();
        }
        return toStringBuf;
    }

    /**
     * How many ranges?
     */

    public int size() {
        return sequence.size();
    }

    /**
     * No ranges?
     */

    public boolean isEmpty() {
        return sequence.isEmpty();
    }

    /**
     * Return a reference to the unmodifiable inner list (not multithreadable though)
     */

    public SortedSet<IdRange<T>> getSequence() {
        return Collections.unmodifiableSortedSet(this.sequence);
    }

    /**
     * Generate an SQL WHERE criterium on the given fieldName. It's called a "factor" because it will be AND-ed with
     * other factors. It is an error if this is called with an empty range sequence.
     */

    public String generateSqlFactor(String fieldName) {
        Check.isTrue(!sequence.isEmpty(), "The sequence is currently empty; cannot generate SQL factor");
        Check.notNull(fieldName, "field name");
        StringBuilder bufForRanges = new StringBuilder();
        StringBuilder bufForAtoms = new StringBuilder();
        boolean addCommaBetweenAtoms = false;
        int atomCounter = 0;
        // boolean addParenth = sequence.size() > 1;
        boolean addOr = false;
        for (IdRange<T> r : sequence) {
            if (r.size() == 1) {
                if (addCommaBetweenAtoms) {
                    bufForAtoms.append(",");
                    if (atomCounter % 30 == 0) {
                        bufForAtoms.append("\n");
                    }
                }
                bufForAtoms.append(r.getLow().getValue());
                addCommaBetweenAtoms = true;
                atomCounter++;
            } else {
                if (addOr) {
                    bufForRanges.append(" OR\n");
                }
                bufForRanges.append("(");
                bufForRanges.append(fieldName);
                bufForRanges.append(">=");
                bufForRanges.append(r.getLow().getValue());
                bufForRanges.append(" AND ");
                bufForRanges.append(fieldName);
                bufForRanges.append("<=");
                bufForRanges.append(r.getHigh().getValue());
                bufForRanges.append(")");
                addOr = true;
            }
        }
        if (bufForRanges.length() > 0 && bufForAtoms.length() > 0) {
            return bufForRanges + " OR\n" + fieldName + " IN (" + bufForAtoms + ")";
        } else if (bufForRanges.length() > 0) {
            return bufForRanges.toString();
        } else if (bufForAtoms.length() > 0) {
            return fieldName + " IN (" + bufForAtoms + ")";
        } else {
            return "";
        }
    }

    /**
     * "Thicken" the sequence by eliminating holes. If "gapSize" <= 0, nothing is done.
     * Note that a factory for T is not needed here!!
     */

    public void thicken(int gapSize, boolean checkSequence) {
        Logger logger = LOGGER_thicken;
        if (gapSize >= 1) {
            if (logger.isDebugEnabled()) {
                logger.debug("Thickening sequence of " + sequence.size() + " ranges by filling in gaps of size " + gapSize);
            }
            List<IdRange<T>> addThese = new LinkedList<IdRange<T>>();
            {
                IdRange<T> prev = null;
                for (IdRange<T> cur : sequence) {
                    if (prev != null) {
                        if (prev.getHigh().getValue() + gapSize + 1 >= cur.getLow().getValue()) {
                            T lowest = prev.getLow();
                            T highest = cur.getHigh();
                            addThese.add(new IdRange<T>(lowest, highest));
                        }
                    }
                    prev = cur;
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Adding " + addThese.size() + " ranges");
            }
            for (IdRange<T> r : addThese) {
                this.addRange(r, checkSequence);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Thickened sequence into " + sequence.size() + " ranges");
            }
        }
    }
}
