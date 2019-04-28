package permuterm;
 
import java.io.IOException;
import java.nio.file.Paths;
 
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.util.Scanner; 
import java.util.*;
import java.lang.*;
import java.io.*;
 
public class LuceneReadIndexFromFile
{
    //directory contains the lucene indexes
    private static final String INDEX_DIR = "indexedFiles";
    public static ArrayList<String> permutate(String name) throws Exception
    {
       int arrLen = name.length();
       ArrayList<String> list = new ArrayList<String>();
       if(name.charAt(arrLen-1) == '$'){
           for(int i = 0;i<arrLen; i++)
           {
 
               if(name.charAt(arrLen-1)=='*')
               {
                   int count = name.length() - name.replaceAll("\\*","").length();

                   if(count != 1)
                   {
                       StringBuilder sb = new StringBuilder(name);
                       sb.deleteCharAt(arrLen-1);
                       name = sb.toString();
                       list = splitString(name);
//                       System.out.println("Splitted String: " + list);
                       return list;
                   }
                   break;
               }
               else
               {
            	   
                   char firstword = name.charAt(0);
                   name = name.substring(1);
                   name += firstword;
                   //System.out.println(name);
                   arrLen = name.length();
               }
           }
           int count = name.length() - name.replaceAll("\\*","").length();
           if(count == 1)
           {
        	   name = name.substring(0, name.length() - 1);
        	   list.add(name);
           }
       }
       return list;
     }
    
    public static ArrayList<String> splitString(String name) throws Exception
    {
        String[] words=name.split("\\*");
        ArrayList<String> list = new ArrayList<String>();
        for(int i=0; i < words.length; i++ )
        {
            if(i==0){
                words[i] = words[i];
            }
            //searchDocs(words[i]);
            list.add(words[i]);
            //System.out.println("SplitString: "+words[i]);
        }
        return list;
    }
    
    public static ArrayList<String> matchString(String filename, String str) throws IOException{
        FileReader file = new FileReader(filename);
        BufferedReader br = new BufferedReader(file);
        String st;
        String var;
        ArrayList<String> list = new ArrayList<String>();
        while((st = br.readLine())!= null){
            if(st.toLowerCase().contains(str.toLowerCase())){
                //System.out.println(st);
                List<String> items = Arrays.asList(st.split(","));
                var = items.get(items.size()-1);
                list.add(var.substring(1,var.length()));
            }
        }
        return list;
    }
    
    public static void main(String[] args) throws Exception
    {
        //Create lucene searcher. It search over a single IndexReader.
        IndexSearcher searcher = createSearcher();
        //System.out.println(searcher);
        System.out.print("Input Wilcard Query: ");
        Scanner sc = new Scanner(System.in); 
        String name = sc.nextLine(); 
        ArrayList<String> list = new ArrayList<String>();       
        String Query ;
        int count = name.length() - name.replaceAll("\\*","").length();
        int  length = name.length();
        if(count == 1){
             if(name.charAt(length-1) == '*')
            {
            	ArrayList<String> namelist = new ArrayList<String>();
                name = '$'+name;
                name = name.substring(0, name.length()-1);
                namelist.add(name);
                list = namelist;
                //System.out.println(list);
            }
            else
            {
                name += '$';
                list = permutate(name);
            }
            
             //System.out.println("Original: "+name);
             
             
        }
        else{
        	//System.out.println("Original");
            name = name+'$';
            list = permutate(name);
        }
         Iterator<String> match_iterator = list.iterator();
         ArrayList<ArrayList<String>> stringList = new ArrayList<ArrayList<String>>();
         List<String> finalList = new ArrayList<String>();

         while(match_iterator.hasNext()) {
        	 
        	 String permuname = match_iterator.next();
        	 ArrayList<String> matched = matchString("C:\\\\Users\\\\lenovo\\\\Desktop\\\\permuterm\\\\outindexedfiles\\\\permu.txt",permuname);
        	 
        	 stringList.add(matched);
         }
         
         
         for(int i=0; i<stringList.size();i++)
         {
        	 if(stringList.size() == 1)
        	 {
        		 for (String t : stringList.get(0)) {
     	                finalList.add(t);
     	        }
        	 }
        	 else
        	 {
        		for (String t : stringList.get(0)) {
        	            if(stringList.get(1).contains(t)) {
        	                finalList.add(t);
        	            }
        	        }
        		
        		//System.out.println("documents "+ stringList.get(i));
        	 }
        	 
        	 
         }
         Set<String> intersectedSet = new HashSet<String>(finalList);         
         System.out.println("finalList "+ intersectedSet);
        //Search indexed contents using search term
         
         Iterator<String> iterator = intersectedSet.iterator();
         List<String> outputs = new ArrayList<String>();
         while(iterator.hasNext()) {
        	 TopDocs foundDocs = searchInContent(iterator.next(), searcher);
       
       //Total found documents
        	 //System.out.println("Total Results :: " + foundDocs.totalHits);
        
       //Let's print out the path of files which have searched term
        	 for (ScoreDoc sd : foundDocs.scoreDocs)
        	 {
        		 Document d = searcher.doc(sd.doc);
        		 String output = "Path : "+ d.get("path") + ", Score : " + sd.score;
        		 if(!output.contains("outputfiles"))
        		 {
        			 outputs.add(output);
        		 }
        		
        	 }
        	 
        	
   }
         HashSet<String> outputset = new HashSet<String>(outputs);

         System.out.println("Total Length: "+ outputset.size());
    	 for(String output: outputset)
    	 {
    		 System.out.println(output);
    	 }
        
    }
    
    private static void searchDocs(String Query) throws Exception
    {
    	
    	//System.out.println(Query);
    	IndexSearcher searcher = createSearcher();
    	ArrayList<String> matched = matchString("C:\\\\Users\\\\lenovo\\\\Desktop\\\\permuterm\\\\outindexedfiles\\\\permu.txt",Query);
        
        System.out.println("matched: "+matched);
        Iterator<String> iterator = matched.iterator();
        while(iterator.hasNext()) {
       	 TopDocs foundDocs = searchInContent(iterator.next(), searcher);
            
            //Total found documents
            System.out.println("Total Results :: " + foundDocs.totalHits);
             
            //Let's print out the path of files which have searched term
            for (ScoreDoc sd : foundDocs.scoreDocs)
            {
                Document d = searcher.doc(sd.doc);
                System.out.println("Path : "+ d.get("path") + ", Score : " + sd.score);
            }
        }
    }
     
    private static TopDocs searchInContent(String textToFind, IndexSearcher searcher) throws Exception
    {
        //Create search query
        QueryParser qp = new QueryParser("contents", new StandardAnalyzer());
        Query query = qp.parse(textToFind);
         
        //search the index
        TopDocs hits = searcher.search(query, 10);
        
        return hits;
    }
 
    private static IndexSearcher createSearcher() throws IOException
    {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
         
        //It is an interface for accessing a point-in-time view of a lucene index
        IndexReader reader = DirectoryReader.open(dir);
         System.out.println(reader.getSumTotalTermFreq(""));
         
        //Index searcher
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }
}