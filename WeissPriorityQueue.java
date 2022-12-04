import java.util.Iterator;
import java.util.Comparator;
import java.util.NoSuchElementException;


import java.util.HashMap;


/**
 * PriorityQueue class implemented via the binary heap.
 * From your textbook (Weiss)
 *
 * @param <T> generic
 */
public class WeissPriorityQueue<T> extends WeissAbstractCollection<T>
{
    /**
     * Max number of elements the queue can hold.
     */
    private static final int DEFAULT_CAPACITY = 100;
    /**
     * number of elements in heap.
     */
    private int currentSize; // Number of elements in heap
    /**
     * array where the priority queue is stored.
     */
    private T [] array; // The heap array
    /**
     * Uses the comparator that compares
     * the objects in the queue by the given comparison rules.
     */
    private Comparator<? super T> cmp;
    /**
     * HashMap that stores the priority of each element in the queue.
     */
    private HashMap<T, Integer> indexMap;
    /**
     * method that makes a student object to test the
     * priority queue.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        /**
         * student object for testing that has has a G-number
         * and name that uses the hashcode of the G-Number to
         * get unique objects as every student should have
         * unique G-numbers in GMU.
         */
        class Student {
            String gnum;
            String name;
            Student(String gnum, String name) { this.gnum = gnum; this.name = name; }

            /**
             * Returns true or false based on if
             * the G-numbers are the same between the
             * two objects.
             *
             * @param o object being checked
             * @return true or false if gnum is the same
             */
            public boolean equals(Object o) {
                if(o instanceof Student) return this.gnum.equals(((Student)o).gnum);
                return false;
            }
            public String toString() { return name + "(" + gnum + ")"; }
            public int hashCode() { return gnum.hashCode(); }
        }

        Comparator<Student> comp = new Comparator<>() {
            public int compare(Student s1, Student s2) {
                return s1.name.compareTo(s2.name);
            }
        };

        WeissPriorityQueue<Student> q = new WeissPriorityQueue<>(comp);
        q.add(new Student("G00000000", "Robert"));
        System.out.print(q.getIndex(new Student("G00000001", "Cindi")) + " "); //-1, no index
        System.out.print(q.getIndex(new Student("G00000000", "Robert")) + " "); //1, at root
        System.out.println();

        q.add(new Student("G00000001", "Cindi"));
        System.out.print(q.getIndex(new Student("G00000001", "Cindi")) + " "); //1, at root
        System.out.print(q.getIndex(new Student("G00000000", "Robert")) + " "); //2, lower down
        System.out.println();

        q.remove(); //remove Cindi
        System.out.print(q.getIndex(new Student("G00000001", "Cindi")) + " "); //-1, no index
        System.out.print(q.getIndex(new Student("G00000000", "Robert")) + " "); //1, at root
        System.out.println();
        System.out.println();

        q = new WeissPriorityQueue<>(comp);
        q.add(new Student("G00000000", "Robert"));
        q.add(new Student("G00000001", "Cindi"));

        for(Student s : q) System.out.print(q.getIndex(s) + " "); //1 2
        System.out.println();
        for(Student s : q) System.out.print(s.name + " "); //Cindi Robert
        System.out.println();

        Student bobby = new Student("G00000000", "Bobby");
        q.update(bobby);

        for(Student s : q) System.out.print(q.getIndex(s) + " "); //1 2
        System.out.println();
        for(Student s : q) System.out.print(s.name + " ");  //Bobby Cindi
        System.out.println();

        bobby.name = "Robert";
        q.update(bobby);

        for(Student s : q) System.out.print(q.getIndex(s) + " "); //1 2
        System.out.println();
        for(Student s : q) System.out.print(s.name + " "); //Cindi Robert
        System.out.println();


    }


    /**
     * Returns the index (priority) of an object that's in the queue.
     *
     * @param x object being passed
     * @return the index
     */
    public int getIndex(T x) {

        if(indexMap.containsKey(x)) {
            return indexMap.get(x);
        }

        return -1;
    }

    /**
     * Updates current object if the hashcodes are equal
     * and then updates the priorities in the queue if the priority
     * of the current object has changed.
     *
     *
     * @param x object being updated
     * @return true or false based on if
     *     the method was successful or not
     */
    public boolean update(T x) {

        if(x == null) {
            return false;
        }
        int index = getIndex(x);

        if(index == -1) {
            return false;
        }

        if(!x.equals(array[index])) {
            return false;
        }

        array[index] = x;

        buildHeap();

        return true;
    }

    /**
     * Construct an empty PriorityQueue.
     */
    @SuppressWarnings("unchecked")
    public WeissPriorityQueue() {
        currentSize = 0;
        cmp = null;
        array = (T[]) new Object[DEFAULT_CAPACITY + 1];
        indexMap = new HashMap<>();
    }

    /**
     * Construct an empty PriorityQueue with a specified comparator.
     *
     * @param c comparator being used
     */
    @SuppressWarnings("unchecked")
    public WeissPriorityQueue( Comparator<? super T> c) {
        currentSize = 0;
        cmp = c;
        array = (T[]) new Object[DEFAULT_CAPACITY + 1];
        indexMap = new HashMap<>();
    }

    /**
     * Construct a PriorityQueue from another Collection.
     *
     * @param coll collection being used
     */
    @SuppressWarnings("unchecked")
    public WeissPriorityQueue(WeissCollection<? extends T> coll) {
        cmp = null;
        currentSize = coll.size();
        array = (T[]) new Object[(currentSize + 2) * 11 / 10];
        indexMap = new HashMap<>();

        int i = 1;
        for (T item : coll) {
            array[i++] = item;
            buildHeap();
        }
        for (int j = 1; j <= currentSize; j++) {
            indexMap.put(array[j], j);
        }
    }
    /**
     * Compares lhs and rhs using comparator if
     * provided by cmp, or the default comparator.
     *
     * @param lhs first object being compared
     * @param rhs second object being compared
     * @return the value of the comparison between the first to the second
     */
    @SuppressWarnings("unchecked")
    private int compare(T lhs, T rhs) {
        if( cmp == null )
            return ((Comparable)lhs).compareTo( rhs );
        else
            return cmp.compare( lhs, rhs );
    }

    /**
     * Adds an item to this PriorityQueue.
     * @param x any object.
     * @return true.
     */
    public boolean add(T x) {
        if(currentSize + 1 == array.length)
            doubleArray();

        int hole = ++currentSize;
        array[ 0 ] = x;

        for( ; compare( x, array[ hole / 2 ] ) < 0; hole /= 2 ) {
            array[ hole ] = array[ hole / 2 ];
        }

        array[hole] = x;
        indexMap.clear();
        for(int i = 1; i <= currentSize; i++) {
            indexMap.put(array[i], i);
        }
        return true;
    }

    /**
     * Returns the number of items in this PriorityQueue.
     * @return the number of items in this PriorityQueue.
     */
    public int size() {
        return currentSize;
    }

    /**
     * Make this PriorityQueue empty.
     */
    public void clear() {
        currentSize = 0;
        indexMap.clear();
    }

    /**
     * Returns an iterator over the elements in this PriorityQueue.
     * The iterator does not view the elements in any particular order.
     *
     * @return an Iterator of the generic
     */
    public Iterator<T> iterator() {
        return new Iterator<T>()
        {
            int current = 0;

            public boolean hasNext()
            {
                return current != size();
            }

            @SuppressWarnings("unchecked")
            public T next() {
                if(hasNext())
                    return array[ ++current ];
                else
                    throw new NoSuchElementException( );
            }

            public void remove()
            {
                throw new UnsupportedOperationException( );
            }
        };
    }

    /**
     * Returns the smallest item in the priority queue.
     * @return the smallest item.
     * @throws NoSuchElementException if empty.
     */
    public T element() {
        if(isEmpty())
            throw new NoSuchElementException();
        return array[1];
    }

    /**
     * Removes the smallest item in the priority queue.
     * @return the smallest item.
     * @throws NoSuchElementException if empty.
     */
    public T remove() {
        T minItem = element();
        array[1] = array[currentSize--];
        percolateDown( 1 );
        indexMap.clear();
        for(int i = 1; i <= currentSize; i++) {
            indexMap.put(array[i], i);
        }
        return minItem;
    }


    /**
     * Establish heap order property from an arbitrary
     * arrangement of items. Runs in linear time.
     */
    private void buildHeap() {
        indexMap.clear();
        for (int i = currentSize / 2; i > 0; i--) {
            percolateDown(i);
        }
        for (int j = 1; j <= currentSize; j++) {
            indexMap.put(array[j], j);
        }
    }
    /**
     * Internal method to percolate down in the heap.
     * @param hole the index at which the percolate begins.
     */
    private void percolateDown(int hole) {
        int child;
        T tmp = array[hole];

        for(;hole * 2 <= currentSize; hole = child)
        {
            child = hole * 2;
            if( child != currentSize &&
                    compare( array[ child + 1 ], array[ child ] ) < 0 )
                child++;
            if( compare( array[ child ], tmp ) < 0 ) {
                array[ hole ] = array[ child ];
            }
            else
                break;
        }
        array[hole] = tmp;
    }

    /**
     * Internal method to extend array.
     */
    @SuppressWarnings("unchecked")
    private void doubleArray() {
        T [] newArray;

        newArray = (T []) new Object[ array.length * 2 ];
        for( int i = 0; i < array.length; i++ )
            newArray[ i ] = array[ i ];
        array = newArray;
    }
}