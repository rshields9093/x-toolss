/*
 * FileUtil.java, helpers for disk I/O.
 * Copyright (C) 2001 - 2010 Achim Westermann.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * If you modify or optimize the code in a useful way please let me know.
 * Achim.Westermann@gmx.de
 */
package info.monitorenter.util;

import info.monitorenter.util.collections.Entry;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * Utility class for file operations.
 * <p>
 * For methods that are not static get the singleton instance via
 * <code>{@link #getInstance()}</code>.
 * <p>
 * 
 * @author Achim Westermann
 * 
 * @version 1.1
 */
public final class FileUtil
    extends Object {
  /** The singleton instance of this class. */
  private static FileUtil instance;

  /**
   * Cuts all path information of the String representation of the given URL.
   * <p>
   * 
   * <pre>
   *   
   *  "file//c:/work/programming/anyfile.jar" --> "anyfile.jar"
   *  "http://jamwg.de"                       --> "" // No file part.
   *  "ftp://files.com/directory2/"           --> "" // File part of URL denotes a directory.
   *    
   * </pre>
   * 
   * Assuming, that '/' is the current file separator character.
   * <p>
   * 
   * @param path
   *            the absolute file path you want the mere file name of.
   * 
   * @return the <code>{@link java.util.Map.Entry}</code> consisting of path
   *         information and file name.
   */
  public static Map.Entry<String, String> cutDirectoryInformation(final java.net.URL path) {
    Map.Entry<String, String> ret = null;
    String pre;
    String suf;
    String parse;
    StringBuffer tmp = new StringBuffer();
    parse = path.toExternalForm();
    if (parse.endsWith("/")) {
      pre = parse;
      suf = "";
    } else {
      StringTokenizer tokenizer = new StringTokenizer(path.getFile(), "/");
      tmp.append(path.getProtocol());
      tmp.append(":");
      tmp.append(path.getHost());
      pre = "";
      while (tokenizer.hasMoreElements()) {
        tmp.append(pre);
        pre = tokenizer.nextToken();
        tmp.append("/");
      }
      suf = pre;
      pre = tmp.toString();
    }
    ret = new Entry<String, String>(pre, suf);
    return ret;
  }

  /**
   * Cuts the path information of the String that is interpreted as a filename
   * into the directory part and the file part. The current operating system's
   * path separator is used to cut all path information from the String.
   * <p>
   * 
   * <pre>
   *   
   *  "c:/work/programming/anyfile.jar" --> Map.Entry("c:/work/programming/","anyfile.jar");
   *  "anyfile.jar"                     --> Map.Entry(new File(".").getAbsolutePath(),"anyfile.jar");
   *  "c:/directory1/directory2/"       --> Map.Entry("c:/directory1/directory2/","");
   *  "c:/directory1/directory2"        --> Map.Entry("c:/directory1/directory2/",""); // directory2 is a dir!
   *  "c:/directory1/file2"             --> Map.Entry("c:/directory1/","file2");       // file2 is a file!
   *  "c:/"                             --> Map.Entry("c:/","");
   *    
   * </pre>
   * 
   * Assuming, that '/' is the current file separator character.
   * <p>
   * 
   * <b>If your string is retrieved from an <tt>URL</tt> instance, use
   * <tt>cutDirectoryInformation(URL path)</tt> instead, because URL's do not
   * depend on the operating systems file separator! </b>
   * <p>
   * 
   * @param path
   *            the absolute file path you want the mere file name of.
   * 
   * @return the <code>{@link java.util.Map.Entry}</code> consisting of path
   *         information and file name.
   */
  public static Map.Entry<String, String> cutDirectoryInformation(final String path) {
    StringBuffer dir = new StringBuffer();
    String file = "";
    String fileseparator = System.getProperty("file.separator");
    StringTokenizer tokenizer = new StringTokenizer(path, fileseparator);
    int size = tokenizer.countTokens();
    switch (size) {
      case 0:
        dir.append(new File(".").getAbsolutePath());
        break;

      case 1:
        File test = new File(tokenizer.nextToken());
        if (new File(path).isDirectory()) {
          dir.append(test.getAbsolutePath());
        } else {
          dir.append(new File(".").getAbsolutePath());
          file = path;
        }
        break;

      default:
        String token;
        while (tokenizer.hasMoreElements()) {
          // reuse String file separator: bad style...
          token = tokenizer.nextToken();
          if (tokenizer.hasMoreTokens()) {
            dir.append(token);
            dir.append(fileseparator);
          } else {
            if (new File(path).isFile()) {
              file = token;
            } else {
              dir.append(token);
            }
          }
        }
    }

    return new Entry<String, String>(dir.toString(), file);
  }

  /**
   * Cuts a String into the part before the last dot and after the last dot. If
   * only one dot is contained on the first position, it will completely be used
   * as prefix part.
   * <p>
   * 
   * <pre>
   * Map.Entry entry = FileUtil.getPotentialExtension("A.Very.Strange.Name.txt");
   * String prefix = (String)entry.getKey(); // prefix is "A.Very.Strange.Name".
   * String suffix = (String)entry.getValue(); // suffix is "txt";
   * 
   * entry = FileUtil.getPotentialExtension(".profile");
   * String prefix = (String)entry.getKey(); // prefix is ".profile".
   * String suffix = (String)entry.getValue(); // suffix is "";
   * 
   * entry = FileUtil.getPotentialExtension("bash");
   * String prefix = (String)entry.getKey(); // prefix is "bash".
   * String suffix = (String)entry.getValue(); // suffix is "";
   * 
   * </pre>
   * 
   * <p>
   * 
   * 
   * @param filename
   *            A String that is interpreted to be a file name: The last dot
   *            ('.') is interpreted to be the extension delimiter.
   * 
   * @return A <tt> java.util.Map.Entry</tt> instance containing a String for
   *         the filename at the key side and a String for the extension at the
   *         value side.
   */
  public static java.util.Map.Entry<String, String> cutExtension(final String filename) {
    String prefix;
    String suffix = null;
    StringTokenizer tokenizer = new StringTokenizer(filename, ".");
    int tokenCount = tokenizer.countTokens();
    if (tokenCount > 1) {
      StringBuffer prefCollect = new StringBuffer();
      while (tokenCount > 1) {
        tokenCount--;
        prefCollect.append(tokenizer.nextToken());
        if (tokenCount > 1) {
          prefCollect.append(".");
        }
      }
      prefix = prefCollect.toString();
      suffix = tokenizer.nextToken();
    } else {
      prefix = filename;
      suffix = "";
    }
    return new Entry<String, String>(prefix, suffix);
  }

  /**
   * Finds a filename based on the given name. If a file with the given name
   * does not exist, <tt>name</tt> will be returned.
   * <p>
   * 
   * Else:
   * 
   * <pre>
   *  "myFile.out"     --> "myFile_0.out"
   *  "myFile_0.out"   --> "myFile_1.out"
   *  "myFile_1.out"   --> "myFile_2.out"
   *  ....
   * </pre>
   * 
   * <p>
   * 
   * The potential extension is preserved, but a number is appended to the
   * prefix name.
   * <p>
   * 
   * @param name
   *            A desired file name.
   * 
   * @return A String that sticks to the naming convention of the given String
   *         but is unique in the directory scope of argument <tt>name</tt>.
   */
  public static String getDefaultFileName(final String name) {
    String result;
    File f = new File(name);
    if (!f.exists()) {
      result = f.getAbsolutePath();
    } else {
      java.util.Map.Entry<String, String> cut = FileUtil.cutExtension(name);
      String prefix = cut.getKey();
      String suffix = cut.getValue();
      int num = 0;
      while (f.exists()) {
        f = new File(prefix + '_' + num + '.' + suffix);
        num++;
      }
      result = f.getAbsolutePath();
    }
    return result;
  }

  /**
   * Returns the singleton instance of this class.
   * <p>
   * 
   * @return the singleton instance of this class.
   */
  public static FileUtil getInstance() {
    if (FileUtil.instance == null) {
      FileUtil.instance = new FileUtil();
    }
    return FileUtil.instance;
  }

  /**
   * Tests wether the given file only contains ASCII characters if interpreted
   * by reading bytes (16 bit).
   * <p>
   * This does not mean that the file is really an ASCII text file. It just
   * might be viewed with an editor showing only valid ASCII characters.
   * <p>
   * 
   * @param f
   *            the file to test.
   * 
   * @return true if all bytes in the file are in the ASCII range.
   * 
   * @throws IOException
   *             on a bad day.
   */
  public static boolean isAllASCII(final File f) throws IOException {
    return FileUtil.isAllASCII(new FileInputStream(f));
  }

  /**
   * Tests wether the given input stream only contains ASCII characters if
   * interpreted by reading bytes (16 bit).
   * <p>
   * This does not mean that the underlying content is really an ASCII text
   * file. It just might be viewed with an editor showing only valid ASCII
   * characters.
   * <p>
   * 
   * @param in
   *            the stream to test.
   * 
   * @return true if all bytes in the given input stream are in the ASCII range.
   * 
   * @throws IOException
   *             on a bad day.
   */
  public static boolean isAllASCII(final InputStream in) throws IOException {
    boolean ret = true;
    int read = -1;
    do {
      read = in.read();
      if (read > 0x7F) {
        ret = false;
        break;
      }

    } while (read != -1);
    return ret;
  }

  /**
   * Tests, wether the content of the given file is identical at character
   * level, when it is opened with both different Charsets.
   * <p>
   * This is most often the case, if the given file only contains ASCII codes
   * but may also occur, when both codepages cover common ranges and the
   * document only contains values m_out of those ranges (like the EUC-CN
   * charset contains all mappings from BIG5).
   * <p>
   * 
   * @param document
   *            the file to test.
   * 
   * @param a
   *            the first character set to interpret the document in.
   * 
   * @param b
   *            the 2nd character set to interpret the document in.
   * 
   * @throws IOException
   *             if something goes wrong.
   * 
   * @return true if both files have all equal contents if they are interpreted
   *         as character data in both given encodings (they may differ at
   *         binary level if both charsets are different).
   */
  public static boolean isEqual(final File document, final Charset a, final Charset b)
      throws IOException {
    boolean ret = true;
    FileInputStream aIn = null;
    FileInputStream bIn = null;
    InputStreamReader aReader = null;
    InputStreamReader bReader = null;
    try {
      aIn = new FileInputStream(document);
      bIn = new FileInputStream(document);
      aReader = new InputStreamReader(aIn, a);
      bReader = new InputStreamReader(bIn, b);
      int readA = -1;
      int readB = -1;
      do {
        readA = aReader.read();
        readB = bReader.read();
        if (readA != readB) {
          // also the case, if one is at the end earlier...
          ret = false;
          break;
        }
      } while (readA != -1 && readB != -1);
      return ret;
    } finally {
      if (aReader != null) {
        aReader.close();
      }
      if (bReader != null) {
        bReader.close();
      }
    }
  }

  /**
   * Invokes {@link #readRAM(File)}, but decorates the result with a
   * {@link java.io.ByteArrayInputStream}.
   * <p>
   * This means: The complete content of the given File has been loaded before
   * using the returned InputStream. There are no IO-delays afterwards but
   * OutOfMemoryErrors may occur.
   * <p>
   * 
   * @param f
   *            the file to cache.
   * 
   * @return an input stream backed by the file read into memory.
   * 
   * @throws IOException
   *             if something goes wrong.
   */
  public static InputStream readCache(final File f) throws IOException {
    return new ByteArrayInputStream(FileUtil.readRAM(f));
  }

  /**
   * Reads the content of the given File into an array.
   * <p>
   * This method currently does not check for maximum length and might cause a
   * java.lang.OutOfMemoryError. It is only intended for
   * performance-measurements of data-based algorithms that want to exclude
   * I/O-usage.
   * <p>
   * 
   * @param f
   *            the file to read.
   * 
   * @throws IOException
   *             if something goes wrong.
   * 
   * @return the contents of the given file.
   * 
   */
  public static byte[] readRAM(final File f) throws IOException {
    int total = (int) f.length();
    byte[] ret = new byte[total];
    InputStream in = new FileInputStream(f);
    try {
      int offset = 0;
      int read = 0;
      do {
        read = in.read(ret, offset, total - read);
        if (read > 0) {
          offset += read;
        }
      } while (read != -1 && offset != total);
      return ret;
    } finally {
      in.close();
    }
  }

  /**
   * Removes the duplicate line breaks in the given file.
   * <p>
   * 
   * Be careful with big files: In order to avoid having to write a tmpfile
   * (cannot read and directly write to the same file) a StringBuffer is used
   * for manipulation. Big files will cost all RAM and terminate VM hard.
   * <p>
   * 
   * @param f
   *            the file to remove duplicate line breaks in.
   */
  public static void removeDuplicateLineBreaks(final File f) {
    String sep = StringUtil.getNewLine();
    if (!f.exists()) {
      System.err.println("FileUtil.removeDuplicateLineBreak(File f): " + f.getAbsolutePath()
          + " does not exist!");
    } else {
      if (f.isDirectory()) {
        System.err.println("FileUtil.removeDuplicateLineBreak(File f): " + f.getAbsolutePath()
            + " is a directory!");
      } else {
        // real file
        FileInputStream inStream = null;
        BufferedInputStream in = null;
        FileWriter out = null;
        try {
          inStream = new FileInputStream(f);
          in = new BufferedInputStream(inStream, 1024);
          StringBuffer result = new StringBuffer();
          int tmpread;
          while ((tmpread = in.read()) != -1) {
            result.append((char) tmpread);
          }
          String tmpstring;
          StringTokenizer toke = new StringTokenizer(result.toString(), sep, true);
          result = new StringBuffer();
          int breaks = 0;
          while (toke.hasMoreTokens()) {
            tmpstring = toke.nextToken().trim();
            if (tmpstring.equals("") && breaks > 0) {
              breaks++;
              // if(breaks<=2)result.append(sep);
              continue;
            }
            if (tmpstring.equals("")) {
              tmpstring = sep;
              breaks++;
            } else {
              breaks = 0;
            }
            result.append(tmpstring);
          }
          // delete original file and write it new from tmpfile.
          f.delete();
          f.createNewFile();
          out = new FileWriter(f);
          out.write(result.toString());
        } catch (FileNotFoundException e) {
          e.printStackTrace(); //CSA
        } catch (IOException g) {
          g.printStackTrace(System.err);
        } finally {
          if (in != null) {
            try {
              in.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          if (out != null) {
            try {
              out.flush();
              out.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }

  /** Needed for localization. */
  private ResourceBundle m_bundle;

  /**
   * Utility class constructor.
   * <p>
   */
  private FileUtil() {
    this.m_bundle = ResourceBundle.getBundle("messages");
  }

  /**
   * Returns the formatted file size to Bytes, KB, MB or GB depending on the
   * given value.
   * <p>
   * 
   * @param filesize
   *            in bytes
   * 
   * @param locale
   *            the locale to translate the result to (e.g. in France they us
   * 
   * @return the formatted filesize to Bytes, KB, MB or GB depending on the
   *         given value.
   */
  public String formatFilesize(final long filesize, final Locale locale) {

    String result;
    long filesizeNormal = Math.abs(filesize);

    if (Math.abs(filesize) < 1024) {
      result = MessageFormat.format(this.m_bundle.getString("GUI_FILEUTIL_FILESIZE_BYTES_1"),
          new Object[] {new Long(filesizeNormal) });
    } else if (filesizeNormal < 1048576) {
      // 1048576 = 1024.0 * 1024.0
      result = MessageFormat.format(this.m_bundle.getString("GUI_FILEUTIL_FILESIZE_KBYTES_1"),
          new Object[] {new Double(filesizeNormal / 1024.0) });
    } else if (filesizeNormal < 1073741824) {
      // 1024.0^3 = 1073741824
      result = MessageFormat.format(this.m_bundle.getString("GUI_FILEUTIL_FILESIZE_MBYTES_1"),
          new Object[] {new Double(filesize / 1048576.0) });
    } else {
      result = MessageFormat.format(this.m_bundle.getString("GUI_FILEUTIL_FILESIZE_GBYTES_1"),
          new Object[] {new Double(filesizeNormal / 1073741824.0) });
    }
    return result;
  }
}
