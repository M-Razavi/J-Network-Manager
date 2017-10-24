package net.mahdirazavi.java.toolkit.util;



public interface SimpleProperties {

  /**
   * Gets the property, a value associated with given key.
   * 
   * @param requestedKey the requested key
   * @return the property
   */
  public abstract String getProperty(String requestedKey);

  /**
   * Sets the property in default category.
   * 
   * @param key the key
   * @param value the value
   * @throws Exception throw an exception if Duplicate key found in saved properties files or set a
   *         property with same key in different category
   */
  public abstract void setProperty(String key, String value) throws Exception;

}
