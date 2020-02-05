import java.util.*;
public interface Queue<T>{

  public abstract void enqueue(T element);
  public abstract T dequeue() throws NoSuchElementException;
  public abstract T top() throws NoSuchElementException;
  public abstract boolean isEmpty();
  public abstract int size();
  public abstract void clear();

}
