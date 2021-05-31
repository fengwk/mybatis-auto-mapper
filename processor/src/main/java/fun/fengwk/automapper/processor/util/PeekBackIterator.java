package fun.fengwk.automapper.processor.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * 支持查看与退回操作的迭代器。
 *
 * @author fengwk
 */
public class PeekBackIterator<E> implements Iterator<E> {

    protected Iterator<E> iterator;
    protected LinkedList<E> bufferQueue = new LinkedList<>();
    protected LinkedList<E> putBackStack = new LinkedList<>();
    protected E end;
    protected int putBackCapacity;

    /**
     * 创建支持PeekBack的迭代器。
     *
     * @param iterator
     */
    public PeekBackIterator(Iterator<E> iterator) {
        this(iterator, null, Integer.MAX_VALUE);
    }

    /**
     * 创建支持PeekBack的迭代器。
     *
     * @param iterator
     * @param end 将在迭代器尾部添加的元素。
     */
    public PeekBackIterator(Iterator<E> iterator, E end) {
        this(iterator, end, Integer.MAX_VALUE);
    }

    /**
     * 创建支持PeekBack的迭代器。
     *
     * @param iterator
     * @param end 将在迭代器尾部添加的元素。
     * @param putBackCapacity 指定putBack容量，例如指定了10就意味着我们最多只支持putBack10次，若超出则会抛出异常，这一参数能显著优化程序的内存占用量。
     */
    public PeekBackIterator(Iterator<E> iterator, E end, int putBackCapacity) {
        if (putBackCapacity < 0) {
            throw new IllegalArgumentException("putBackCapacity cannot be less than zero");
        }

        this.iterator = iterator;
        this.end = end;
        this.putBackCapacity = putBackCapacity;
    }

    /**
     * 检查是否能够回退一个元素。
     *
     * @return
     */
    public boolean canPutBack() {
        return !putBackStack.isEmpty();
    }

    /**
     * 回退一个元素，若无元素可以回退将抛出异常。
     */
    public void putBack() {
        if (putBackStack.isEmpty()) {
            handleNoElementCanPutBack();
        }

        bufferQueue.offerFirst(putBackStack.pop());
    }

    /**
     * 查看下一元素，若没有下一元素则会抛出异常。
     *
     * @return
     */
    public E peek() {
        E e = next();
        putBack();
        return e;
    }

    /**
     * 与next功能类似，但drop的元素将再进行putBack。
     *
     * @return
     */
    public E drop() {
        if (!hasNext()) {
            handleNoElementCanNext();
        }

        E next = bufferQueue.pollFirst();
        return next;
    }

    @Override
    public boolean hasNext() {
        if (bufferQueue.isEmpty()) {
            fillBufferQueue();
        }

        return !bufferQueue.isEmpty();
    }

    @Override
    public E next() {
        if (!hasNext()) {
            handleNoElementCanNext();
        }

        E next = bufferQueue.pollFirst();
        pushPutBackStack(next);
        return next;
    }

    private void fillBufferQueue() {
        if (iterator.hasNext()) {
            bufferQueue.offerLast(iterator.next());
        } else if (end != null) {
            bufferQueue.offerLast(end);
            end = null;
        }
    }

    private void pushPutBackStack(E e) {
        while (putBackStack.size() >= putBackCapacity) {
            putBackStack.removeLast();
        }
        putBackStack.push(e);
    }

    /**
     * 处理没有元素进行putBack的情况，该方法必须抛出一个运行时异常，将其设置为protected是为了子类实现中能够方便地修改异常的信息和内容。
     *
     * @throws RuntimeException
     */
    protected void handleNoElementCanPutBack() throws RuntimeException {
        throw new NoSuchElementException("No element can be putBack");
    }

    /**
     * 处理没有元素进行next的情况，该方法必须抛出一个运行时异常，将其设置为protected是为了子类实现中能够方便地修改异常的信息和内容。
     *
     * @throws RuntimeException
     */
    protected void handleNoElementCanNext() throws RuntimeException {
        throw new NoSuchElementException("No next element");
    }

}
