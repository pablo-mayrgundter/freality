package com.freality.ml.bayes;

/**
 * NOT FINISHED!
 *
 * Naive Bayes.
 *
 * @author <a href="mailto:pablo@freality.com">Pablo Mayrgundter</a>
 */
public class NaiveBayes {

  int [] mCategories;

  NaiveBayes() {
    mCategories = new int[0];
  }

  /**
   * OPTI: O(n) check against existing categories, could be O(1).
   * This method is called by any method in this class that accepts a
   * category argument.
   * @throws IllegalArgumentException if the category already exists.
   */
  boolean hasCategory(int category) {
    for (int i = 0; i < mCategories.length; i++) {
      if(mCategories[i] == category) {
        return true;
      }
    }
    return false;
  }

  void addCategory(final int category) {
    if (hasCategory(category)) {
      throw new IllegalArgumentException("Category already exists: " + category);
    }
    final int [] newCats = new int[mCategories.length + 1];
    System.arraycopy(mCategories, 0, newCats, 0, mCategories.length);
    newCats[newCats.length - 1] = category;
    mCategories = newCats;
  }

  /**
   * Associate these attributes with this category.
   */
  void train(int [] attributes, int category) {
    if(!hasCategory(category)) {
      throw new IllegalArgumentException("Category doesn't exist: " + category);
    }
  }

  /**
   * @return The category most likely given these attributes.
   */
  int predict(int [] attributes) {
    return -1;
  }

  public static void main(String [] args) {
    final NaiveBayes nb = new NaiveBayes();
    final int [] example0 = new int[]{0,1,2,3,4};
    final int [] example1 = new int[]{5,6,7,8,9};
    nb.train(example0, 0);
    nb.train(example1, 1);
    final int targetValue0 = nb.predict(example0);
    final int targetValue1 = nb.predict(example0);
    System.out.println("targetValue0: " + targetValue0);
    System.out.println("targetValue1: " + targetValue1);
  }
}
