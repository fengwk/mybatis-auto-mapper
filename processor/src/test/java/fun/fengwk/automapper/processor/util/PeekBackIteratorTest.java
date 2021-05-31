package fun.fengwk.automapper.processor.util;

import org.junit.Test;

import java.util.Iterator;

/**
 * @author fengwk
 */
public class PeekBackIteratorTest {

    @Test
    public void test1() {
        String str = "0123456789";
        Iterator<Character> charIter = str.chars().mapToObj(c -> (char) c).iterator();
        PeekBackIterator<Character> iter = new PeekBackIterator<>(charIter, 'A');

        assert iter.next() == '0';
        assert iter.next() == '1';
        assert iter.next() == '2';
        assert iter.next() == '3';
        assert iter.next() == '4';
        assert iter.next() == '5';
        assert iter.next() == '6';
        assert iter.next() == '7';
        assert iter.next() == '8';
        assert iter.next() == '9';
        assert iter.next() == 'A';

        iter.putBack();
        iter.putBack();
        iter.putBack();
        iter.putBack();
        iter.putBack();
        iter.putBack();
        iter.putBack();
        iter.putBack();
        iter.putBack();
        iter.putBack();
        iter.putBack();

        assert iter.next() == '0';
        assert iter.next() == '1';
        assert iter.next() == '2';
        assert iter.next() == '3';
        assert iter.next() == '4';
        assert iter.next() == '5';
        assert iter.next() == '6';
        assert iter.next() == '7';
        assert iter.next() == '8';
        assert iter.next() == '9';
        assert iter.next() == 'A';
    }

    @Test
    public void test2() {
        String str = "0123456789";
        Iterator<Character> charIter = str.chars().mapToObj(c -> (char) c).iterator();
        PeekBackIterator<Character> iter = new PeekBackIterator<>(charIter, 'A', 5);

        assert iter.next() == '0';
        assert iter.next() == '1';
        assert iter.next() == '2';
        assert iter.next() == '3';
        assert iter.next() == '4';
        assert iter.next() == '5';
        assert iter.next() == '6';
        assert iter.next() == '7';
        assert iter.next() == '8';
        assert iter.next() == '9';
        assert iter.next() == 'A';

        iter.putBack();
        iter.putBack();
        iter.putBack();
        iter.putBack();
        iter.putBack();

        assert iter.next() == '6';
        assert iter.next() == '7';
        assert iter.next() == '8';
        assert iter.next() == '9';
        assert iter.next() == 'A';
    }

    @Test
    public void test3() {
        String str = "0123456789";
        Iterator<Character> charIter = str.chars().mapToObj(c -> (char) c).iterator();
        PeekBackIterator<Character> iter = new PeekBackIterator<>(charIter, 'A');

        assert iter.next() == '0';
        assert iter.next() == '1';
        assert iter.next() == '2';
        assert iter.next() == '3';
        assert iter.next() == '4';
        assert iter.drop() == '5';
        assert iter.drop() == '6';
        assert iter.drop() == '7';
        assert iter.drop() == '8';
        assert iter.drop() == '9';
        assert iter.drop() == 'A';

        iter.putBack();
        iter.putBack();
        iter.putBack();
        iter.putBack();
        iter.putBack();

        assert iter.next() == '0';
        assert iter.next() == '1';
        assert iter.next() == '2';
        assert iter.next() == '3';
        assert iter.next() == '4';
    }

}
