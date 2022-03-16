public class Encryptor
{
  /** A two-dimensional array of single-character strings, instantiated in the constructor */
  private String[][] letterBlock;

  /** The number of rows of letterBlock, set by the constructor */
  private int numRows;

  /** The number of columns of letterBlock, set by the constructor */
  private int numCols;

  /** Constructor*/
  public Encryptor(int r, int c)
  {
    letterBlock = new String[r][c];
    numRows = r;
    numCols = c;
  }
  
  public String[][] getLetterBlock()
  {
    return letterBlock;
  }
  
  /** Places a string into letterBlock in row-major order.
   *
   *   @param str  the string to be processed
   *
   *   Postcondition:
   *     if str.length() < numRows * numCols, "A" in each unfilled cell
   *     if str.length() > numRows * numCols, trailing characters are ignored
   */
  public void fillBlock(String str)
  {
    int counter = 0;
    for (int r = 0; r < letterBlock.length; r++) {
      for (int c = 0; c < letterBlock[0].length; c++) {
        if (counter < str.length()) letterBlock[r][c] = str.substring(counter, counter + 1);
        else letterBlock[r][c] = "A";
        counter++;
      }
    }
  }

  /** Extracts encrypted string from letterBlock in column-major order.
   *
   *   Precondition: letterBlock has been filled
   *
   *   @return the encrypted string from letterBlock
   */
  public String encryptBlock()
  {
    String encString = "";
    for (int c = 0; c < letterBlock[0].length; c++) {
      for (int r = 0; r < letterBlock.length; r++) {
        encString += letterBlock[r][c];
      }
    }
    return encString;
  }

  /** Encrypts a message.
   *
   *  @param message the string to be encrypted
   *
   *  @return the encrypted message; if message is the empty string, returns the empty string
   */
  public String encryptMessage(String message)
  {
    String encString = "";
    int blockLen = numCols * numRows;
    int currentIndex = blockLen;
    String currentMessage = message.substring(0, blockLen);
    while (currentIndex < message.length() + blockLen) {
      fillBlock(currentMessage);
      encString += encryptBlock();
      if (currentIndex + blockLen < message.length()) currentMessage= message.substring(currentIndex, currentIndex + blockLen);
      else if (currentIndex < message.length()) currentMessage = message.substring(currentIndex);
      currentIndex += blockLen;
    }
    return encString;
  }
  
  /**  Decrypts an encrypted message. All filler 'A's that may have been
   *   added during encryption will be removed, so this assumes that the
   *   original message (BEFORE it was encrypted) did NOT end in a capital A!
   *
   *   NOTE! When you are decrypting an encrypted message,
   *         be sure that you have initialized your Encryptor object
   *         with the same row/column used to encrypted the message! (i.e. 
   *         the �encryption key� that is necessary for successful decryption)
   *         This is outlined in the precondition below.
   *
   *   Precondition: the Encryptor object being used for decryption has been
   *                 initialized with the same number of rows and columns
   *                 as was used for the Encryptor object used for encryption. 
   *  
   *   @param encryptedMessage  the encrypted message to decrypt
   *
   *   @return  the decrypted, original message (which had been encrypted)
   *
   *   TIP: You are encouraged to create other helper methods as you see fit
   *        (e.g. a method to decrypt each section of the decrypted message,
   *         similar to how encryptBlock was used)
   */
  public String decryptMessage(String encryptedMessage)
  {
    String decryptedString = "";
    int blockLen = numCols * numRows;
    int currentIndex = blockLen;
    String currentBlock = encryptedMessage.substring(0, blockLen);
    for (int i = 0; i < encryptedMessage.length() / blockLen; i++) {
      int i2 = 0;
      int c = 0;
      while (c < blockLen) {
        decryptedString += currentBlock.substring(i2, i2 + 1);
        c++;
        i2 += numRows;
        if (i2 >= blockLen) i2 -= blockLen - 1;
      }
      if (i < encryptedMessage.length() / blockLen - 1) currentBlock = encryptedMessage.substring(currentIndex, blockLen + currentIndex);
      else currentBlock = encryptedMessage.substring(currentIndex);
      currentIndex += blockLen;
    }
    int lastLetterIndex = decryptedString.length() - 1;
    while (lastLetterIndex > 0 && decryptedString.substring(lastLetterIndex, lastLetterIndex + 1).equals("A")) lastLetterIndex--;
    decryptedString = decryptedString.substring(0, lastLetterIndex + 1);
    return decryptedString;
  }

  public String encrypt2d(String message, int horShift, int verShift) {
    int[] dimensions = getDimensions(message.length());
    numRows = dimensions[0];
    numCols = dimensions[1];
    letterBlock = new String[numRows][numCols];
    fillBlock(message);
    for (int r = 0; r < letterBlock.length; r++) {
      for (int c = 0; c < letterBlock[r].length; c++) {
        int charNum = (int) letterBlock[r][c].charAt(0);
        int max = 90;
        int min = 65;
        if (charNum > 90) {
          max = 122;
          min = 97;
        }
        if ((charNum >= 65 && charNum <= 90) || (charNum >= 97 && charNum <= 122)) {
          charNum = countWrapAround(charNum, max, min, r + c, true);
          letterBlock[r][c] = String.valueOf((char) charNum);
        }
      }
    }
    String[][] shiftedLetterBlock = new String[letterBlock.length][letterBlock[0].length];
    for (int r = 0; r < letterBlock.length; r++) {
      for (int c = 0; c < letterBlock[r].length; c++) {
        int newC = countWrapAround(c, letterBlock[r].length - 1, 0, horShift, true);
        int newR = countWrapAround(r, letterBlock.length - 1, 0, verShift, true);
        shiftedLetterBlock[newR][newC] = letterBlock[r][c];
      }
    }
    letterBlock = shiftedLetterBlock; 
    String encryptedString = "";
    for (String[] arr: letterBlock) {
      for (String s: arr) {
        encryptedString += s;
      }
    }
    return encryptedString;
  }

  public String decrypt2D(String message, int horShift, int verShift) {
    letterBlock = new String[numRows][numCols];
    fillBlock(message);
    String[][] shiftedLetterBlock = new String[letterBlock.length][letterBlock[0].length];
    for (int r = 0; r < letterBlock.length; r++) {
      for (int c = 0; c < letterBlock[r].length; c++) {
        int newC = subtractWrapAround(c, letterBlock[r].length - 1, 0, horShift, true);
        int newR = subtractWrapAround(r, letterBlock.length - 1, 0, verShift, true);
        shiftedLetterBlock[newR][newC] = letterBlock[r][c];
      }
    }
    letterBlock = shiftedLetterBlock;
    for (int r = 0; r < letterBlock.length; r++) {
      for (int c = 0; c < letterBlock[r].length; c++) {
        int charNum = (int) letterBlock[r][c].charAt(0);
        int max = 90;
        int min = 65;
        if (charNum > 90) {
          max = 122;
          min = 97;
        }
        if ((charNum >= 65 && charNum <= 90) || (charNum >= 97 && charNum <= 122)) {
          charNum = subtractWrapAround(charNum, max, min, r + c, true);
          letterBlock[r][c] = String.valueOf((char) charNum);
        }
      }
    }
    letterBlock = shiftedLetterBlock; 
    String decryptedString = "";
    for (String[] arr: letterBlock) {
      for (String s: arr) {
        decryptedString += s;
      }
    }
    return decryptedString;
  }

  private int countWrapAround(int num, int max, int min, int count, boolean subtract) {
    int nNum = num;
    nNum += count;
    while (nNum > max) { 
      nNum -= max - min; 
      if (subtract) nNum--;
    }
    return nNum;
  }

  private int subtractWrapAround(int num, int max, int min, int count, boolean add) {
    int nNum = num;
    nNum -= count;
    while (nNum < min) { 
      nNum += max - min; 
      if (add) nNum++;
    }
    return nNum;
  }

  private int[] getDimensions(int len) {
    int lowestSpaces = Integer.MAX_VALUE;
    int[] currentDimensions = new int[2];
    for (int i = 2; i < len; i++) {
      for (int i2 = 2; i2 < len; i2++) {
        if (i * i2 < lowestSpaces && i * i2 >= len) {
          lowestSpaces = i * i2;
          currentDimensions[0] = i;
          currentDimensions[1] = i2;
          if (lowestSpaces == len) return currentDimensions;
        }
      }
    }
    return currentDimensions;
  }
}