/**
 * Copyright (c) 2015 net.mahdirazavi.java.Co . All rights reserved.
 * 
 * 
 * 
 * LastEdit Feb 22, 2015-3:03:54 PM Using JRE 1.7.0_55
 * 
 * 
 * @author Mahdi Razavi Razavi.Dev@Gmail.com
 * @version 1.0
 * @see
 */
package net.mahdirazavi.java.toolkit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The Class PropertiesCache. This class contain properties as name value pair in a file/files and
 * store each properties file in a instance of {@link PropertiesFile}. Only one instance of
 * {@link PropertiesCache} class is exist in an application. If you worked with a
 * {@link #defaultCategory} default category, only one properties file created. In other case each
 * category has a properties file.
 * 
 * @author Mahdi Razavi
 * @version 1.0
 * @created 06-Jun-2015 3:05:54 PM
 */
public class PropertiesCache implements SimpleProperties {

  /** The default category. */
  final String defaultCategory = "app";

  /** The default path of properties directory in current path. */
  final String defaultPath = "properties";

  /** The property file extension. */
  final String fileExtension = ".prop";

  /** The absolute path of properties directory. */
  private String absolutePath;

  /**
   * The configuration properties. This map contains category name as a key and PropertiesFile
   * instance as value.
   */
  private final ConcurrentMap<String, PropertiesFile> propertiesInstances;

  /**
   * configurations Keys. This map contains all keys of whole configuration files as key and
   * category name as value. This map used for prevent duplicate in keys.
   */
  private final ConcurrentMap<String, String> propertyKeys;

  /**
   * Instantiates a new properties cache. It's private due to Singleton implementation. For create a
   * instance you must call {@link #getInstance()} method.
   * 
   * @throws Exception throw an exception if Duplicate key found in saved properties files
   */
  private PropertiesCache() throws Exception {
    propertiesInstances = new ConcurrentHashMap<String, PropertiesFile>();
    propertyKeys = new ConcurrentHashMap<String, String>();
    searchSavedPropertyFiles(defaultPath);
  }

  /**
   * Gets the single instance of PropertiesCache.
   * 
   * @return single instance of PropertiesCache
   */
  public static PropertiesCache getInstance() {
    return SingletonLazyHolder.INSTANCE;
  }

  /**
   * Check for key exists in properties.
   * 
   * @param key the key
   * @return true, if successful
   */
  public boolean containKey(String key) {
    for (PropertiesFile properties : propertiesInstances.values()) {
      if (properties.containKey(key)) {
        return true;
      }
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.util.Property#getProperty(java.lang.String)
   */
  @Override
  public String getProperty(String requestedKey) {
    if (propertyKeys.containsKey(requestedKey)) {
      String category = propertyKeys.get(requestedKey);
      return propertiesInstances.get(category).getProperty(requestedKey);
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.mahdirazavi.java.toolkit.util.Property#setProperty(java.lang.String, java.lang.String)
   */
  @Override
  public void setProperty(String key, String value) throws Exception {
    if (key == null || key.isEmpty()) {
      throw new Exception("Key can't be empty or null.");
    }

    if (value == null || value.isEmpty()) {
      throw new Exception("value can't be empty or null.");
    }

    setProperty(key, value, defaultCategory);
  }

  /**
   * Sets the property in given category.
   * 
   * @param key the key
   * @param value the value
   * @param category the category
   * @throws Exception throw an exception if Duplicate key found in saved properties files or set a
   *         property with same key in different category
   */
  public void setProperty(String key, String value, String category) throws Exception {
    if (category == null || category.isEmpty()) {
      throw new Exception("Category can't be empty or null.");
    }
    category = category.toLowerCase();

    if (propertyKeys.containsKey(key)) {
      // check for same key in different category
      if (!propertyKeys.get(key).equals(category))
        throw new Exception("Set duplicate key in properties. key=" + key + "; category="
            + category);
    }
    if (propertiesInstances.containsKey(category)) {
      propertiesInstances.get(category).setProperty(key, value);
    } else {
      makeNewProperties(category);
      setProperty(key, value, category);
      propertyKeys.put(key, category);
    }
  }

  /**
   * Make new properties for given category and add it to {@link #propertiesInstances}
   * 
   * @param categoryName the category name
   * @throws Exception throw an exception if Duplicate key found in saved properties files
   */
  private synchronized void makeNewProperties(String categoryName) throws Exception {
    PropertiesFile propertiesFile =
        new PropertiesFile(absolutePath + File.separator + categoryName);

    if (!Collections.disjoint(propertyKeys.keySet(), propertiesFile.getAllPropertyNames())) {
      throw new Exception("Duplicate key in properties. category=" + categoryName);
    }

    for (String key : propertiesFile.getAllPropertyNames()) {
      propertyKeys.put(key, categoryName);
    }

    propertiesInstances.put(categoryName, propertiesFile);
  }

  /**
   * Search in default path for saved properties files. if find any file, make a new category
   * instance by call {@link #makeNewProperties(String)}.
   * 
   * @param path the path
   * @throws Exception throw an exception if Duplicate key found in saved properties files
   */
  private void searchSavedPropertyFiles(String path) throws Exception {
    File currentDirectory = new File("");
    File propertyFilesPath = new File(currentDirectory.getAbsolutePath() + File.separator + path);
    absolutePath = currentDirectory.getAbsolutePath() + File.separator + path;
    String[] files = null;
    if (propertyFilesPath.exists()) {

      files = propertyFilesPath.list(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          if (name.endsWith(fileExtension)) {
            return true;
          }
          return false;
        }
      });
      for (String propertyFile : files) {
        makeNewProperties(propertyFile.substring(0, propertyFile.lastIndexOf(".")));
      }
    } else {
      propertyFilesPath.mkdirs();
    }

  }

  /**
   * The Class LazyHolder. Create and hold a singleton instance of {@link PropertiesCache} class;
   * 
   * @author Mahdi Razavi
   * @version 1.0
   * @created 06-Jun-2015 3:05:54 PM
   */
  private static class SingletonLazyHolder {
    private static final PropertiesCache INSTANCE;

    /**
     * The Constant INSTANCE.
     */
    static {
      try {
        INSTANCE = new PropertiesCache();
      } catch (Exception e) {
        throw new ExceptionInInitializerError(e);
      }
    }
  }

  /**
   * The Class PropertiesFile. This class stores properties as name value pair in a file. Only one
   * instance of this class should be exist in application, except a property container (like
   * PropertiesContainer) manages instances.
   */
  private class PropertiesFile {

    /** The configuration properties. */
    private final Properties configProp = new SortedProperties();

    /** The input stream to read from properties file */
    InputStream in;

    /** The out to write on properties file */
    OutputStream out;

    /** The properties file name. */
    String propFileName = null;

    /**
     * 
     * Determine properties file type. set true for XML file otherwise set false for TXT file.
     */
    boolean useXmlFile = true;

    /**
     * Instantiates a new properties cache.
     * 
     * @param fileName the file name
     */
    private PropertiesFile(String fileName) {
      propFileName = fileName + fileExtension;
      try {
        File propFile = new File(propFileName);
        if (!propFile.exists()) {
          saveConfig();
        }

        in = new FileInputStream(propFileName);
        if (in == null) {
          in.close();
          throw new FileNotFoundException("Property file '" + propFileName + "' was not found in classpath.");
        } else {
          if (useXmlFile) {
            configProp.loadFromXML(in);
          } else {
            configProp.load(in);
          }
          in.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    /**
     * Save properties in file.
     */
    private synchronized void saveConfig() {
      if (useXmlFile) {
        storeConfigToXML();
      } else {
        storeConfigToTXT();
      }
    }

    /**
     * Store properties in xml file.
     */
    private void storeConfigToXML() {
      try {
        out = new FileOutputStream(propFileName);
        configProp.storeToXML(out,
            new SimpleDateFormat("YYYY/MM/dd-HH:mm:ss").format(Calendar.getInstance().getTime()),
            "UTF8");
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /**
     * Store properties in txt file.
     */
    private void storeConfigToTXT() {
      try {
        out = new FileOutputStream(propFileName);
        configProp.store(out,
            new SimpleDateFormat("YYYY/MM/dd-HH:mm:ss").format(Calendar.getInstance().getTime()));
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /**
     * Gets the property by given key.
     * 
     * @param key the key
     * @return the property
     */

    private synchronized String getProperty(String key) {
      return configProp.getProperty(key);
    }

    /**
     * Gets the all property names.
     * 
     * @return the all property names
     */
    private Set<String> getAllPropertyNames() {
      return configProp.stringPropertyNames();
    }

    /**
     * Check properties contains given key.
     * 
     * @param key the key
     * @return true, if successful
     */
    private synchronized boolean containKey(String key) {
      return configProp.containsKey(key);
    }

    /**
     * Sets the property.
     * 
     * @param key the key
     * @param value the value
     */
    private synchronized void setProperty(String key, String value) {
      configProp.setProperty(key, value);
      saveConfig();
    }
  }

}
