public interface MaxPQ<T extends Comparable<T>> {
  T removeMax();
  T getMax();
  void insert(T key);
  boolean isEmpty();
  int size();
  String toString();
}
