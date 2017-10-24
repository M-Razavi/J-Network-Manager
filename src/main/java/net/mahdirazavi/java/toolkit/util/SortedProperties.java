/**
 * Copyright (c) 2015 net.mahdirazavi.java.Co . All rights reserved.
 * 
 * 
 * 
 * LastEdit Jun 23, 2015-9:47:24 AM Using JRE 1.7.0_55
 * 
 * 
 * @author Mahdi Razavi Razavi.Dev@Gmail.com
 * @version 1.0
 * @see
 */
package net.mahdirazavi.java.toolkit.util;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * To be compatible with version control systems, we need to sort properties before storing them to
 * disk, Otherwise each change may lead to problems by getting difference(SVN Diff command) against
 * previous version.Class {@link Properties} uses {@link HashMap} to storing key/value pairs, so
 * Property entries are randomly distributed.
 */
public final class SortedProperties extends Properties {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Overridden to be able to write properties sorted by keys to the disk.
   * 
   * @return the enumeration
   * @see java.util.Hashtable#keys()
   */
  @SuppressWarnings("unchecked")
  @Override
  public synchronized Enumeration<Object> keys() {
    Set<?> set = keySet();
    return (Enumeration<Object>) sortKeys((Set<String>) set);
  }

  /**
   * Sort given key set.
   * 
   * @param keySet non null set instance to sort
   * @return non null list which contains all given keys, sorted lexicographically. The list may be
   *         empty if given set was empty
   */
  static public Enumeration<?> sortKeys(Set<String> keySet) {
    List<String> sortedList = new ArrayList<String>();
    sortedList.addAll(keySet);
    Collections.sort(sortedList);
    return Collections.enumeration(sortedList);
  }

  /**
   * Overridden to be able to write XML properties sorted by keys to the disk.
   * 
   * @return the sets the
   * @see java.util.Properties#stringPropertyNames()
   */
  @Override
  public Set<String> stringPropertyNames() {
    Set<String> sortedSet = new TreeSet<String>();
    for (Object key : keySet()) {
      sortedSet.add(key.toString());
    }
    //TODO: use sortedSet.addAll(keySet());
    return sortedSet;
  }
}
