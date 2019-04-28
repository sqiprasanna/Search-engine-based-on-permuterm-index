package permuterm;
import java.util.*;
import java.lang.*;
import java.io.*;
public class ir{

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
	    
	public static void matchString(String filename, String str) throws IOException{
		FileReader file = new FileReader(filename);
		BufferedReader br = new BufferedReader(file);
		String st;
		while((st = br.readLine())!= null){
			if(st.toLowerCase().contains(str.toLowerCase()))
				System.out.println(st);
		}
	} 	
	
	public static void main(String[] args) throws IOException{
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
  		matchString("C:\\Users\\lenovo\\Desktop\\permuterm\\outindexedfiles\\permu.txt","o$"); 
		
	}
}
