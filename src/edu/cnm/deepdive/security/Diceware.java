package edu.cnm.deepdive.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The <code>Diceware</code> class generates a passphrase using a word list provided in the constructor.
 * Will use a defualt RNG if not provided an external one.
 * 
 * @author jake_
 *
 */
public class Diceware {

  private static final String LINE_PATTERN = "^\\s*(\\d+)\\s+(\\S+)\\s*$";

  private List<String> words;
  private Random rng = null;

  /**
   * Intializes an instance of <code>Diceware</code> using a reference to
   *  a {@link java.io.File} object. If the <code>File</code> does not exist,
   *  of cannot be read, an exception will be thrown.
   *  
   * @param file                    file to read for word list.
   * @throws FileNotFoundException  if file does not exist.
   * @throws IOException            if file can't be read.
   */
  public Diceware(File file) throws FileNotFoundException, IOException {
    words = new ArrayList<>();
    try (FileInputStream input = new FileInputStream(file);
        InputStreamReader reader = new InputStreamReader(input);
        BufferedReader buffer = new BufferedReader(reader);) {
      Pattern p = Pattern.compile(LINE_PATTERN);
      for (String line = buffer.readLine(); line != null; line = buffer.readLine()) {
        Matcher m = p.matcher(line);
        if (m.matches()) {
          words.add(m.group(2));
        }
      }
    }
  }

  /**
   * Intializes an instance of <code>Diceware</code> using a {@link 
   * Collection} as the source of words for the word list.
   * 
   * @param source  word list source.
   */
  public Diceware(Collection<String> source) {
    words = new ArrayList<>(source);
  }
  
  /**
   * Intializes an instance of <code>Diceware</code> using a reference to
   *  a {@link ResourceBundle} as the source for the word list. (Keys/names are igonored).
   * 
   * @param bundle      resource bundle to provide words for word list.
   */
  public Diceware(ResourceBundle bundle) {
    words = new ArrayList<>();
    Enumeration<String> en = bundle.getKeys();
    while (en.hasMoreElements()) {
      words.add(bundle.getString(en.nextElement()));
    }
  }
  
  /**
   * Returns a RNG to select words from wordlist if no RNG is provided.
   * 
   * @return                            Returns a RNG.
   * @throws NoSuchAlgorithmException   If lazy initialization is used can throw this exception.
   */
  public Random getRng() throws NoSuchAlgorithmException {
    if (rng == null) {
      rng = SecureRandom.getInstanceStrong();
    }
    return rng;
  }

  /**
   * Sets a reference to an RNG to be used to select words from the word list.
   * 
   * @param rng     RNG to get words from word list.
   */
  public void setRng(Random rng) {
    this.rng = rng;
  }

  /**
   * Generates a passphrase of the specified length, the use of duplicates is controlled by <code>duplicatesAllowed</code>
   * parameter. If the provided word list has less words than the specified passphrase length and duplicates aren't allowed,
   * an infinite loop will occur.
   * 
   * @param length                      desired length of passphrase.
   * @param duplicatesAllowed           If passphrase can have duplicate words.
   * @return                            returns a <code>String[]</code> that shows words selected for passphrase.
   * @throws NoSuchAlgorithmException   If lazy initialization is used can throw this exception.
   */
  public String[] generate(int length, boolean duplicatesAllowed) throws NoSuchAlgorithmException {
    List<String> passphrase = new LinkedList<>();
    while (passphrase.size() < length) {
      String word = generate();
      if (duplicatesAllowed || !passphrase.contains(word)) {
        passphrase.add(word);
      }
    }
//    return passphrase.toArray(new String[0]);
    return passphrase.toArray(new String[passphrase.size()]);
  }

  /**
   * Allows for consumer to just specify <code>length</code> of passphrase parameter and assign defualt
   * value of <code>true</code> to <code>duplicatesAllowed</code> parameter.
   * 
   * @param length                      Desired length of passphrase.
   * @return                            returns a <code>String[]</code> that shows words selected for passphrase.
   * @throws NoSuchAlgorithmException   If lazy initialization is used can throw this exception.
   */
  public String[] generate(int length) throws NoSuchAlgorithmException {
    return generate(length, true);
  }
  
  private String generate() throws NoSuchAlgorithmException {
    int index = getRng().nextInt(words.size());
    return words.get(index);
  }

}
