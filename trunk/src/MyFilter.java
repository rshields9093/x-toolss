//package edu.auburn.eng.aci.xtoolss;


   import java.io.File;
   import javax.swing.filechooser.FileFilter;

 
 public class MyFilter extends FileFilter { 
      String allowedFileType = ".*"; 
       
       public MyFilter(String fileType){
         allowedFileType = fileType;
      }
       public boolean accept(File f) 
      { 
         if (f.isDirectory()) { 
            return true; } 
            
         String fileName = ((f.getName()).trim()).toLowerCase(); 
         boolean isGoodFile = false;
            
         if(fileName.endsWith(allowedFileType)){
            isGoodFile = true;
         }
         return isGoodFile; 
      } 
       public String getDescription() 
      { 
         return ("*"+allowedFileType); 
      } 
   }