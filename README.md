## Problem Set 7: Maximum priority queue

### Due Friday, March 20 @ 11:59pm

---

## Background
As we have seen, *complete binary trees* can be efficiently represented in a sequential array by allocating the elements of the tree in level-order in the array. Binary trees can also be represented with a linked data structures, as shown below, in which each node contains some value and has a pointer to the left child and a pointer to the right child.


```java
class Node {
                                           +-----------------+
  T info;                                  |       info      |
  Node left;                               +--------+--------+
  Node right;                              |  left  |  right |
  ...                                      +--------+--------+
}
```

In this problem set, you will implement a **maximum priority queue** using a **heap-ordered binary tree**, which you will implement with a **triply-linked Node** structure instead of an array. You will need **three** links per node: two to traverse down the tree (one for the left child and one for the right child) and one to traverse up the tree to the node's parent, like this:


```java
class Node {
                                           +-----------------+
  T info;                                  |      parent     |
  Node left;                               +-----------------+
  Node right;                              |       info      |
  Node parent;                             +--------+--------+
  ...                                      |  left  |  right |
                                           +--------+--------+
}
```

Please use the following interface for your ADT (included in the `src` directory as `MaxPQ.java`):

```java
public interface MaxPQ<T extends Comparable<T>> {
  T removeMax();
  T getMax();
  void insert(T key);
  boolean isEmpty();
  int size();
  String toString();
}
```

Note that the specification of the generic type variable `T` in:

```java
T extends Comparable<T>
```

means that the type variable `T` can be replaced by any type that includes an `int compareTo(T other)` function. You don't need to implement anything special! Just remember that when you declare an instance of `LinkedMaxPQ`, you should make sure that it is storing something that extends `Comparable` (e.g., a String, an Integer). Recall that `compareTo` returns a positive number when the object calling it is larger than the argument, 0 if they're equal, and a negative number if the object calling is smaller than the argument.

## Implementation
I have included some code to get you started in the `LinkedMaxPQ.java` file in the `src` directory. You do not have to use this code, but you should name your implementation `LinkedMaxPQ.java`, and as always, the file should go in the `src` directory.

### `insert()` and `removeMax()` methods
As with the sequential implementation, the `insert` operation must find the tree node to which a new entry is to be attached, i.e., the next available place to attach a new node. It should then "swim" the info in that new node up to a node where it is bigger than its children but smaller than its parent.

Similarly, the `removeMax` operation, after it removes the top (maximum, largest) node, must replace the info in the top node with the info of the last node in the complete binary tree, and then "sink" it down to a location where it is larger than its children but smaller than its parent. 

### `toString()` method
The toString() method should return the values in the nodes in *level-order* (a.k.a. "breadth first"). That is, it should print the value of each node on each level from left to right, starting at the level of the root node and proceeding down  level by level. I have provided some code for this in the `example_code` repo on binary trees, which you can repurpose for this problem set.

### `sink()` and `swim()` helper methods
You should write a `sink()` method and a `swim()` method to help you with your insert and delete methods. Both will use the `compareTo` method to determine whether to swap the info in the current node with its parent (when swimming) or the larger of its two children (when sinking). 

`swim()` takes a pointer to the `Node` you inserted at the bottom of the tree. It compares that Node's `info` with the `info` of its parent. If its `info` is greater than that of its parent (use `compareTo()`), swap their infos, and then make the pointer point at the parent. Do this until you find a parent whose info is greater or when you have no more parents.

`sink()` takes a pointer to `top`, whose info should be the info of the node previously at the bottom of the tree. It compares that Node's info with the info of the larger of its children. If its info is less than that of its larger child, swap their infos and make the pointer point at that child. Do this until your info is larger than that of both children.

Remember that **you are not actually sinking or swimming a `Node`**. Instead, you just swap the **data** in the `info` field of the current `Node` with the data in the `info` field in the parent `Node` (if swimming) or child `Node` (if sinking). These will be functions with return type `void`.

### `findNode()` helper method
You should write a method for finding a particular node based on its number. You need to be able to identify the bottom node so that you can move it to the top after removing the top in `removeMax()`. And you need to be able to know where to insert a new node when calling `insert()`. 

In the sequential implementation, the these locations were easy to find using the size of the tree to compute the appropriate array index. With this linked implementation, a little more work is required. You still use the size of the data structure, but you will use it to compute the path from the root to the desired node using integer division by 2 and the modulus operator.

Here's an intuition about how this will work. Look at the tree below, where the nodes are numbered from right to left on each level.
```
                                                     1 
                                                   /    \
                                                 /       \
                                                /         \
                                               2           3
                                             /   \       /   \
                                            4     5     6     7
                                           / \   / \   / \   / \
                                          8  9  10 11 12 13 14  15

```

**Observation** What do all left children have in common? They are all even numbers (i.e., for each left child `l`, `l%2==0`). What do all right children have in common? They are all odd numbers (i.e., for each right child `r`, `r%2==1`).

To navigate in a tree to a node according to its number, you can write a recursive method that works like this. (You are free to use an iterative method, if you like!)

1. The method will have two arguments: `Node traverse` and `int z`. The first time you call the method, you'll pass in `top` for the `traverse` parameter and the number of the node you want to reach for the `z` parameter.

2. Base case: if `z` is equal to 1, return `traverse`.

3. Otherwise, Get the remainder of `z` when dividing by 2 using the modulus operator. This is where your recursive call comes in. If `z%2==0`, return the left child of a recursive call replacing `z` with `z/2`. If `z%2==1`, return the right child of a recursive call replacing `z` with `z/2`. The recursive calls will look something like this:

```java
return getNode(z/2, traverse).right;
```

When you **remove the max** from the root, you want to find the bottom node, so you can move its info to the root node and then sink it down to where it belongs. (You'll also want to disconnect it from its parent node.)

When you **insert an element**, you want to find where that new bottom node is **going to go**. This means that you are actually trying to find the *parent node* of the new node you'll be adding.

You will have to use a different value for `z` above, depending on whether you want it to return a pointer to the `Node` you have to delete (`z` should be the size of the PQ) or to the `Node` whose right or left child you want to create (`z` should be the future size of the PQ divided by 2).

### Don't forget the special cases
When inserting to an empty priority queue, you don't need to call `swim()` or `getNode()`. Just create a new `Node` and have `top` point at it. When deleting the min from a priority queue of size 1, you don't need to call `sink()` or `getNode()`. Just return the `info` and set `top` to `null`. And if you try to `removeMax()` from an empty priority queue, you can just return `null`.

### Testing your code `main()`

Write code in the main() method to test your implementation. Be sure to do lots of inserting and removing and inserting again, and print out the priority queue after each change to make sure it looks right.

---

## Pushing and verifying your submission

Once your code works to your satisfaction, push `LinkedMaxPQ.java`to your personal master repo on the GitHub Classroom site, as you have done for your previous problem sets. Use the commit message "READY FOR GRADING" so we know you are done. 

---

## Important notes on grading

1. The file **must be in the `src` directory**. You will lose a point if it's in the wrong directory.

2. Your code must compile. If it does not compile, you will get a 0. If your code does not compile when it's time to submit, comment out the part of the code that is preventing compilation, and include an explanation of what you were trying to do and why it's not working.

3. The TAs will review and run your code. Note that in addition to running your `main()` method, they will try out one of their own. It's a good idea to do some error checking to avoid any surprises during grading.

4. One point in your grade will be for "style and formatting". This includes (1) comments; (2) indenting in a way that allows for easy reading; (3) naming variables in helpful or memorable ways; (4) remembering that methods and variables should start with lowercase letters, while class and interface names should begin with a capital letter; (5) making consistent formamtting choices.

5. One point in your grade for this problem set will be for "efficiency". Do not write more code than you need. Do not create objects that do not need to be created. Do not adjust pointers in `swim()` and `sink()` if it's possible to just swap what is stored in the data storage variable.

