package ru.spbhse.treeset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class TreeSetTest {

    private TreeSet<Integer> testWithoutComparator;

    @BeforeEach
    private void init() {
        testWithoutComparator = new TreeSet<>();
    }

    @Test
    void iteratorTotalTest() {
        var elements = new int[] {23, 42, 51, 90};
        for (int element : elements) {
            testWithoutComparator.add(element);
        }
        var iter = testWithoutComparator.iterator();
        for (int element : elements) {
            assertTrue(iter.hasNext());
            assertEquals(element, iter.next());
        }
        assertFalse(iter.hasNext());
    }

    @Test
    void iteratorShouldThrowNoSuchElement() {
        var iter = testWithoutComparator.iterator();
        assertThrows(NoSuchElementException.class, iter::next);
        testWithoutComparator.add(23);
        iter = testWithoutComparator.iterator();
        iter.next();
        assertThrows(NoSuchElementException.class, iter::next);
    }

    @Test
    void iteratorShouldThrowAfterModification() {
        var iter = testWithoutComparator.iterator();
        testWithoutComparator.add(42);
        assertThrows(ConcurrentModificationException.class, iter::hasNext);
        iter = testWithoutComparator.iterator();
        testWithoutComparator.add(23);
        assertThrows(ConcurrentModificationException.class, iter::next);
    }

    @Test
    void sizeTotalTest() {
        assertEquals(0, testWithoutComparator.size());
        testWithoutComparator.add(42);
        assertEquals(1, testWithoutComparator.size());
        testWithoutComparator.add(23);
        assertEquals(2, testWithoutComparator.size());
        testWithoutComparator.add(42);
        assertEquals(2, testWithoutComparator.size());
        testWithoutComparator.remove(42);
        assertEquals(1, testWithoutComparator.size());
        testWithoutComparator.remove(42);
        assertEquals(1, testWithoutComparator.size());
        testWithoutComparator.remove(23);
        assertEquals(0, testWithoutComparator.size());
    }

    @Test
    void descendingIteratorTotalTest() {
        var elements = new int[] {90, 51, 42, 23};
        for (int element : elements) {
            testWithoutComparator.add(element);
        }
        var iter = testWithoutComparator.descendingIterator();
        for (int element : elements) {
            assertTrue(iter.hasNext());
            assertEquals(element, iter.next());
        }
        assertFalse(iter.hasNext());
    }

    @Test
    void descendingSetTotalTest() {
        var elements = new int[] {90, 51, 42, 23};
        for (int element : elements) {
            testWithoutComparator.add(element);
        }
        TreeSet<Integer> descending = testWithoutComparator.descendingSet();
        var iter = descending.iterator();
        for (int element : elements) {
            assertTrue(iter.hasNext());
            assertEquals(element, iter.next());
        }
        testWithoutComparator.add(5);
        assertEquals(5, descending.size());
    }

    @Test
    void containsTotalTest() {
        assertFalse(testWithoutComparator.contains(42));
        testWithoutComparator.add(42);
        assertTrue(testWithoutComparator.contains(42));
        testWithoutComparator.add(23);
        assertTrue(testWithoutComparator.contains(23));
        testWithoutComparator.remove(42);
        assertFalse(testWithoutComparator.contains(42));
    }

    @Test
    void containsShouldNotGetNull() {
        assertThrows(IllegalArgumentException.class, () -> testWithoutComparator.contains(null));
    }

    @Test
    void removeTotalTest() {
        assertFalse(testWithoutComparator.contains(42));
        testWithoutComparator.add(42);
        testWithoutComparator.add(55);
        testWithoutComparator.add(878);
        assertTrue(testWithoutComparator.remove(55));
        assertTrue(testWithoutComparator.remove(878));
        assertTrue(testWithoutComparator.remove(42));
    }

    @Test
    void removeShouldNotGetNull() {
        assertThrows(IllegalArgumentException.class, () -> testWithoutComparator.remove(null));
    }

    @Test
    void manyAdds() {
        for (int i = 0; i < 20; i++) {
            assertTrue(testWithoutComparator.add(i));
        }
        for (int i = -1; i >= -20; i--) {
            assertTrue(testWithoutComparator.add(i));
        }
        for (int i = 20; i < 40; i++) {
            assertTrue(testWithoutComparator.add(i));
        }
        for (int i = -20; i < 40; i++) {
            assertTrue(testWithoutComparator.contains(i));
        }
    }

    @Test
    void addTotalTest() {
        assertTrue(testWithoutComparator.add(42));
        assertTrue(testWithoutComparator.contains(42));
        assertTrue(testWithoutComparator.add(21));
        assertTrue(testWithoutComparator.contains(21));
        assertTrue(testWithoutComparator.add(239));
        assertTrue(testWithoutComparator.contains(239));
        assertFalse(testWithoutComparator.add(42));
        assertFalse(testWithoutComparator.add(42));
        assertFalse(testWithoutComparator.add(239));
    }

    @Test
    void addShouldNotGetNull() {
        assertThrows(IllegalArgumentException.class, () -> testWithoutComparator.add(null));

    }

    @Test
    void firstTotalTest() {
        assertNull(testWithoutComparator.first());
        testWithoutComparator.add(1337);
        testWithoutComparator.add(42);
        testWithoutComparator.add(239);
        assertEquals(42, testWithoutComparator.first());
    }

    @Test
    void lastTotalTest() {
        assertNull(testWithoutComparator.last());
        testWithoutComparator.add(1337);
        testWithoutComparator.add(42);
        testWithoutComparator.add(239);
        assertEquals(1337, testWithoutComparator.last());
    }

    @Test
    void lowerTotalTest() {
        assertThrows(IllegalArgumentException.class, () -> testWithoutComparator.lower(null));
        assertNull(testWithoutComparator.lower(42));
        for (int i = 0; i < 20; ++i) {
            testWithoutComparator.add(i);
        }
        assertEquals(1, testWithoutComparator.lower(2));
        assertEquals(0, testWithoutComparator.lower(1));
        assertEquals(19, testWithoutComparator.lower(42));
        assertNull(testWithoutComparator.lower(0));
        assertNull(testWithoutComparator.lower(-42));
        for (int i = 30; i < 40; ++i) {
            testWithoutComparator.add(i);
        }
        assertEquals(19, testWithoutComparator.lower(25));
    }

    @Test
    void floorTotalTest() {
        assertThrows(IllegalArgumentException.class, () -> testWithoutComparator.floor(null));
        assertNull(testWithoutComparator.floor(42));
        for (int i = 0; i < 20; ++i) {
            testWithoutComparator.add(i);
        }
        assertEquals(0, testWithoutComparator.floor(0));
        assertEquals(1, testWithoutComparator.floor(1));
        assertEquals(19, testWithoutComparator.floor(42));
        assertNull(testWithoutComparator.floor(-1));
        assertNull(testWithoutComparator.floor(-42));
        for (int i = 30; i < 40; ++i) {
            testWithoutComparator.add(i);
        }
        assertEquals(19, testWithoutComparator.floor(25));
    }

    @Test
    void ceilingTotalTest() {
        assertThrows(IllegalArgumentException.class, () -> testWithoutComparator.ceiling(null));
        assertNull(testWithoutComparator.ceiling(42));
        for (int i = 0; i < 20; ++i) {
            testWithoutComparator.add(i);
        }
        assertEquals(0, testWithoutComparator.ceiling(0));
        assertEquals(19, testWithoutComparator.ceiling(19));
        assertEquals(0, testWithoutComparator.ceiling(-42));
        assertNull(testWithoutComparator.ceiling(42));
        assertNull(testWithoutComparator.ceiling(20));
        for (int i = 30; i < 40; ++i) {
            testWithoutComparator.add(i);
        }
        assertEquals(30, testWithoutComparator.ceiling(25));
    }

    @Test
    void higherTotalTest() {
        assertThrows(IllegalArgumentException.class, () -> testWithoutComparator.higher(null));
        assertNull(testWithoutComparator.higher(42));
        for (int i = 0; i < 20; ++i) {
            testWithoutComparator.add(i);
        }
        assertEquals(1, testWithoutComparator.higher(0));
        assertNull(testWithoutComparator.higher(19));
        assertEquals(0, testWithoutComparator.higher(-42));
        assertNull(testWithoutComparator.higher(42));
        assertNull(testWithoutComparator.higher(20));
        for (int i = 30; i < 40; ++i) {
            testWithoutComparator.add(i);
        }
        assertEquals(30, testWithoutComparator.higher(25));
    }

    @Test
    void unsupportedClassShouldThrow() {
        var test = new TreeSet<ClassWithoutComparator>();
        test.add(new ClassWithoutComparator(42));
        assertThrows(ClassCastException.class, () -> test.add(new ClassWithoutComparator(23)));
        assertThrows(ClassCastException.class, () -> test.contains(new ClassWithoutComparator(42)));
    }

    @Test
    void withSelfWrittenComparator() {
        var comparator = new Comparator<ClassWithoutComparator>() {
            @Override
            public int compare(ClassWithoutComparator o1, ClassWithoutComparator o2) {
                return o2.x - o1.x;
            }
        };
        var test = new TreeSet<>(comparator);
        test.add(new ClassWithoutComparator(42));
        test.add(new ClassWithoutComparator(51));
        test.add(new ClassWithoutComparator(-42));

        var order = new int[] {51, 42, -42};
        var iter = test.iterator();
        for (int element : order) {
            assertEquals(element, iter.next().x);
        }
    }

    @Test
    void nullComparatorIsForbidden() {
        assertThrows(IllegalArgumentException.class, () -> new TreeSet<Integer>(null));
    }

    private static class ClassWithoutComparator {
        private int x;
        private ClassWithoutComparator(int x) {
            this.x = x;
        }
    }
}