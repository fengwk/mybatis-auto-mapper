package fun.fengwk.automapper.processor.util;

/**
 * @author fengwk
 */
public class StringCharSequenceView implements CharSequence {

    private final String str;
    private final int start;
    private final int end;

    public StringCharSequenceView(String str, int start) {
        this(str, start, str.length());
    }

    public StringCharSequenceView(String str, int start, int end) {
        this.str = str;
        this.start = start;
        this.end = end;
    }

    @Override
    public int length() {
        return str.length() - start;
    }

    @Override
    public char charAt(int index) {
        return str.charAt(start + index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        if (start < 0 || end > length()) {
            throw new IndexOutOfBoundsException();
        }
        return new StringCharSequenceView(str, this.start + start, this.start + end);
    }

    @Override
    public String toString() {
        return str.substring(start, end);
    }

}
