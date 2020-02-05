import java.util.*;
public class LinkedListQueue<T> implements Queue<T> {
  private LinkedList<T> queue = new LinkedList<>();


  @Override
  public void enqueue(T element){
    queue.add(element);
  }

  @Override
  public T dequeue() throws NoSuchElementException {
    if(queue.isEmpty()) throw new NoSuchElementException();
    return queue.removeFirst();
  }

  @Override
  public T top() throws NoSuchElementException {
    if(queue.isEmpty()) throw new NoSuchElementException();
    return queue.getFirst();
  }

  @Override
  public boolean isEmpty(){
    return queue.isEmpty();
  }

  @Override
  public void clear(){
    queue.clear();
  }

  @Override
  public int size(){
    return queue.size();
  }

}
