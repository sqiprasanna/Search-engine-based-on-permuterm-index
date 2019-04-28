package permuterm;

import java.util.*;
import java.lang.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
 
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.AttributeSource;
 
import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

	 
public class LuceneWriteIndexFromFile 
{
	
	public static void appendStrToFile(String fileName, String str){ 
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true)); 
		    out.write(str); 
		    out.close(); 
		} 
		catch (IOException e) { 
		    System.out.println("exception occoured" + e); 
		} 
	 }
	
	
	public static void perm() throws IOException{
		int i=1;
		while(i<7) {
		String s = "C:\\Users\\lenovo\\Desktop\\permuterm\\outputfiles\\"+i+".txt";
		FileReader file = new FileReader(s);
		
		BufferedReader br = new BufferedReader(file);
		StringBuilder st;
		String str;
		while ((str = br.readLine()) != null){ 
			st = new StringBuilder(str);
			Map<String,List<String>> map = new HashMap<String,List<String>>();
			st.append("$");
			char ch;
			List<String> sList = new ArrayList<String>();
			while(true){
				if(st.charAt(0)=='$'){
					break;
				}
				st.append(st.charAt(0));
				st.delete(0,1);
				sList.add(st.toString());
			}		
			map.put(st.toString(),sList);
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			    String key = entry.getKey();
			    List<String> values = entry.getValue();
			    System.out.println("Key = " + key);
			    System.out.println("Values = " + values + "\n");
			    values.add("\n");
			    appendStrToFile("C:\\Users\\lenovo\\Desktop\\permuterm\\outindexedfiles\\permu.txt", String.join(",",values));
			}
  		}
		i+=1;
		}
	}
	
    public static void main (String[] args) throws IOException
    {
        //Input folder
        String docsPath = "inputFiles";
         
        //Output folder
        String indexPath = "indexedFiles";
 
        //Input Path Variable
        final Path docDir = Paths.get(docsPath);
 
        try
        {
            //org.apache.lucene.store.Directory instance
            Directory dir = FSDirectory.open( Paths.get(indexPath) );
            
            //analyzer with the default stop words
            Analyzer analyzer = new StandardAnalyzer() ;
            System.out.println(analyzer);
            System.out.println("Hello");
            indexDoc1();
           // PermutermTokenFilter tokenfilter = new PermutermTokenFilter();
             
            //IndexWriter Configuration
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
             
            //IndexWriter writes new index files to the directory
            IndexWriter writer = new IndexWriter(dir, iwc);
             
            //Its recursive method to iterate all files and directories
            indexDocs(writer, docDir);
 
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        perm();
    }
     
    static void indexDocs(final IndexWriter writer, Path path) throws IOException
    {
        //Directory?
        if (Files.isDirectory(path))
        {
            //Iterate directory
            Files.walkFileTree(path, new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    try
                    {
                        //Index this file
                        indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        else
        {
            //Index this file
            indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
        }
    }
 
    static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException
    {
        try (InputStream stream = Files.newInputStream(file))
        {
            //Create lucene Document
            Document doc = new Document();
             
            doc.add(new StringField("path", file.toString(), Field.Store.YES));
            doc.add(new LongPoint("modified", lastModified));
            doc.add(new TextField("contents", new String(Files.readAllBytes(file)), Store.YES));
            //doc.add(new TextField("permuterms", new String []))
            //doc.add(field);
             
            //Updates a document by first deleting the document(s)
            //containing <code>term</code> and then adding the new
            //document.  The delete and then add are atomic as seen
            //by a reader on the same index
            System.out.println(doc.getFields());
            writer.updateDocument(new Term("path", file.toString()), doc);
        }
    }
    
    static void indexDoc1() throws IOException {
    	Set<String> stopWordsSet = new HashSet<String>();
		String sCurrentLine;
		
		//FileReader fr=new FileReader("C:\\Users\\pavan sai\\Desktop\\IR_1\\inputfiles\\stopwords.txt");
		FileReader fr=new FileReader("C:\\Users\\lenovo\\Desktop\\permuterm\\inputFiles\\stopwords.txt");
		BufferedReader br= new BufferedReader(fr);
        while ((sCurrentLine = br.readLine()) != null){
            //stopwords[k]=sCurrentLine;
             //k++;
        	stopWordsSet.add(sCurrentLine);
        }
		
		File dir = new File("C:\\Users\\lenovo\\Desktop\\permuterm\\inputFiles");
        //get all the files from a directory
        File[] fList = dir.listFiles();
        int a=1;
        for (File file : fList){
        	String s="",os="";
        	ArrayList<String> wordsList = new ArrayList<String>();
           // System.out.println(file.getName());
            FileReader f=new FileReader("C:\\Users\\lenovo\\Desktop\\permuterm\\inputFiles\\"+file.getName());
            BufferedReader br1= new BufferedReader(f);
            while ((sCurrentLine = br1.readLine()) != null){
                //stopwords[k]=sCurrentLine;
                 //k++;
            	s=s+sCurrentLine;
            }
            br1.close();
            String[] words = s.split(" ");
    	    for(String word : words)
    	    {
    	        String wordCompare = word.toUpperCase();
    	        if(!stopWordsSet.contains(wordCompare))
    	        {
    	            wordsList.add(word + '\n');
    	        }
    	    }
    	    //String c;
    	    //sr sound = new sr();
    	    for (String str : wordsList){
    	    	//System.out.println(str);
    	    	//c="";
    	    	//c=sr.getGode(str);
    	    	os=os+str;
    	    }
    	    
    	    wordsList.clear();
    	    File file1 = new File("C:\\Users\\lenovo\\Desktop\\permuterm\\outputfiles\\"+a+".txt");
    	    
    	  //Create the file
    	  
    	  //Write Content
    	  FileWriter writer = new FileWriter(file1);
    	  writer.write(os);
    	  os="";
    	//  System.out.println("yes");
    	  writer.close();
    	  a++;
    	  br.close();
    	  
    }
  }
    
    
    
    

}

