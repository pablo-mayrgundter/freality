package nl.loc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.List;
import java.util.Properties;

class Train {

  public static void main(String [] args) throws IOException {
    Train t = new Train();
    FileReader in = new FileReader(args[0]);
    t.load(in);
    in.close();
    t.shuffle();
    t.quiz(!Boolean.getBoolean("reverse"));
  }

  List<String[]> transList;

  Train() {
    transList = new ArrayList<String[]>();
  }

  void load(Reader in) throws IOException {
    BufferedReader r = new BufferedReader(in);
    String line;
    while ((line = r.readLine()) != null) {
      if (line.equals("")) {
        continue;
      }
      transList.add(line.split("="));
    }
  }

  void shuffle() {
    Collections.shuffle(transList);
  }
 
  void quiz(boolean reverse) throws IOException {
    int count = 0, guesses = 0, correct = 0;
    BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
    List<String> practiceList = new ArrayList<String>();
    try {
      for (String [] transPair : transList) {
        String phrase = transPair[reverse ? 1 : 0];
        String trans = transPair[reverse ? 0 : 1];
        count++;
        for (int i = 0; i < 3; i++) {
          System.out.print(phrase + ": ");
          String guess = r.readLine();
          if (guess == null || guess.equals("q")) {
            return;
          }
          guess = guess.trim();
          guesses++;
          if (trans.equalsIgnoreCase(guess)) {
            correct++;
            break;
          }
          if (i < 2) {
            System.out.println("Try again!");
          } else {
            System.out.println("Correct translation: " + trans);
            practiceList.add(phrase + " -> " + trans);
          }
        }
      }
      if (practiceList.isEmpty()) {
        System.out.println("Perfect!");
      } else {
        System.out.println("Practice the ones you missed. Type each of these in 3 times:");
        for (String transPair : practiceList) {
          System.out.println(transPair + ":");
          for (int i = 0; i < 3; i++) {
            r.readLine();
          }
        }
      }
      practiceList.clear();
    } finally {
      System.out.printf("count: %d, guesses: %d, correct: %d\n", count, guesses, correct);
    }
  }
}