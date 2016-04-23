package com.github.junrar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

/**
 * extract an archive to the given location
 *
 * @author edmund wagner / Roy Damman
 *
 */
public class ExtractArchive
{

   public static void extractArchive(String archive, String destination)
   {
      if (archive == null || destination == null)
      {
         throw new RuntimeException("archive and destination must me set");
      }
      File arch = new File(archive);
      if (!arch.exists())
      {
         throw new RuntimeException("the archive does not exit: " + archive);
      }
      File dest = new File(destination);
      if (!dest.exists() || !dest.isDirectory())
      {
         throw new RuntimeException(
                 "the destination must exist and point to a directory: "
                 + destination);
      }
      extractArchive(arch, dest);
   }

   public static void main(String[] args)
   {
      if (args.length == 2)
      {
         extractArchive(args[0], args[1]);
      } else
      {
         System.out.println("usage: java -jar junrar-0.8.0.jar <thearchive> <the destination directory>");
      }
   }

   public static void extractArchive(File archive, File destination)
   {
      Archive arch = null;
      try
      {
         arch = new Archive(archive);
      } catch (RarException e)
      {
         System.err.println(e.getMessage());
      } catch (IOException e1)
      {
         System.err.println(e1.getMessage());
      }
      if (arch != null)
      {
         if (arch.isEncrypted())
         {
            System.err.println("archive is encrypted cannot extreact");
            return;
         }
         FileHeader fh = null;
         while (true)
         {
            fh = arch.nextFileHeader();
            if (fh == null)
            {
               break;
            }
            if (fh.isEncrypted())
            {
               System.err.println("file is encrypted cannot extract: "
                       + fh.getFileNameString());
               continue;
            }
            System.err.println("extracting: " + fh.getFileNameString());
            try
            {
               if (fh.isDirectory())
               {
                  createDirectory(fh, destination);
               } else
               {
                  File f = createFile(fh, destination);
                  OutputStream stream = new FileOutputStream(f);
                  arch.extractFile(fh, stream);
                  stream.close();
                  f.setLastModified(fh.getMTime().getTime());
               }
            } catch (IOException e)
            {
               System.err.println("error extracting the file" + e.getMessage());
            } catch (RarException e)
            {
               System.err.println("error extraction the file" + e.getMessage());
            }
         }
      }
   }

   private static File createFile(FileHeader fh, File destination)
   {
      File f = null;
      String name = fh.getFileNameString();
      f = new File(destination, name);
      if (!f.exists())
      {
         try
         {
            f = makeFile(destination, name);
         } catch (IOException e)
         {
            System.err.println("error creating the new file: " + f.getName() + " " + e.getMessage());
         }
      }
      return f;
   }

   private static String _sRegExFileSeparator;
   
   static
   {
      if (File.separator.equals("\\"))
         _sRegExFileSeparator = "\\\\";
      else
         _sRegExFileSeparator = "\\/";
   }
   
   
   private static File makeFile(File destination, String name)
           throws IOException
   {
      String[] dirs = name.split(_sRegExFileSeparator);
      if (dirs == null)
      {
         return null;
      }
      String path = "";
      int size = dirs.length;
      if (size == 1)
      {
         return new File(destination, name);
      } else if (size > 1)
      {
         for (int i = 0; i < dirs.length - 1; i++)
         {
            path = path + File.separator + dirs[i];
            new File(destination, path).mkdir();
         }
         path = path + File.separator + dirs[dirs.length - 1];
         File f = new File(destination, path);
         f.createNewFile();
         return f;
      } else
      {
         return null;
      }
   }

   private static void createDirectory(FileHeader fh, File destination)
   {
      File f = null;
      if (fh.isDirectory())
      {
         f = new File(destination, fh.getFileNameString());
         if (!f.exists())
         {
            makeDirectory(destination, fh.getFileNameString());
         }
      }
   }

   private static void makeDirectory(File destination, String fileName)
   {
      String[] dirs = fileName.split(_sRegExFileSeparator);
      if (dirs == null)
      {
         return;
      }
      String path = "";
      for (String dir : dirs)
      {
         path = path + File.separator + dir;
         new File(destination, path).mkdir();
      }

   }
}
