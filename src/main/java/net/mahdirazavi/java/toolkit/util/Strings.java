/**
 * 
 */
package net.mahdirazavi.java.toolkit.util;

/**
 * @author Mahdi
 *
 */
public class Strings {

  /**
   * Convert given string to camel case style.
   * Change first letter to upper case and all other letter to lower case.  
   *
   * @param s the s
   * @return the string
   */
  public static String toPascalCase(String s)
  {
    return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
  }
}
