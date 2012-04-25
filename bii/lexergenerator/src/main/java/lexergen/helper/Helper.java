/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lexergen.helper;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author benjamin
 */
public class Helper
{

  /**
   * Should be work under Windows and Unix based systems
   *
   * @return path to the directory, where the pom.xml is located
   */
  public static String getApplicationPath()
  {
    String path = null;
    String pattern = "target(/|\\\\)classes(/|\\\\)lexergen";

    try
    {
      path = new java.io.File(".").getCanonicalPath().replaceFirst(pattern, "");
      System.out.println(path);
    }
    catch (IOException ex)
    {
      Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
    }
    return path;
  }

  /**
   * Gets the default token definition for testing purposes.
   * 
   * @return path to the token definition file
   */
  public static String getDefaultTokenDef()
  {
    String path = getApplicationPath();
    return path + "/src/main/resources/def/tokendefinition";
  }
}
