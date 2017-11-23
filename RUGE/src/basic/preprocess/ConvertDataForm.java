package basic.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertDataForm {

	public HashMap<String, Integer> MapRelationID = null;

	public HashMap<String, Integer> MapEntityID = null;
    
	/**
	 * @param args
	 */	
	public void convert_data( 
			String fn_Ent,
			String fn_Rel,
			String fn_train,
			String fn_valid,
			String fn_test,
			String fn_trainid,
			String fn_validid,
			String fn_testid
			
		) throws Exception{
		MapRelationID = new HashMap<String, Integer>();
		MapEntityID = new HashMap<String, Integer>(); 

		BufferedReader read = new BufferedReader(new InputStreamReader(
				new FileInputStream(fn_train), "UTF-8"));
		String line="";
		int count =0;
		while ((line = read.readLine()) != null) {
			String head = line.split("\t")[0].trim();
			String relation = line.split("\t")[1].trim();
			String tail = line.split("\t")[2].trim();
			MapRelationID.put(relation, -1);
			MapEntityID.put(head, -1);
			MapEntityID.put(tail, -1);
		}
		read.close();

		BufferedWriter ent = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fn_Ent), "UTF-8"));	
		Iterator<String> itemLst = MapEntityID.keySet().iterator();
		int ict =0;
		while(itemLst.hasNext()){
			String entity = itemLst.next();
			ent.write(ict+"\t"+entity+ "\n");
			MapEntityID.put(entity, ict);
			ict++;
		}
		ent.close();
		
		ict =0;
		BufferedWriter rel = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fn_Rel), "UTF-8"));
		itemLst = MapRelationID.keySet().iterator();
		while(itemLst.hasNext()){
			String relation = itemLst.next();
			rel.write(ict+"\t"+relation+ "\n");
			MapRelationID.put(relation, ict);
			ict++;
		}
		rel.close();
		
		BufferedReader read_train = new BufferedReader(new InputStreamReader(
				new FileInputStream(fn_train), "UTF-8"));
		BufferedWriter write_trainid = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fn_trainid), "UTF-8"));
		while ((line = read_train.readLine()) != null) {
			String head = line.split("\t")[0].trim();
			String relation = line.split("\t")[1].trim();
			String tail = line.split("\t")[2].trim();
			write_trainid.write(MapEntityID.get(head) + "\t" 
			+ MapRelationID.get(relation) 
			+ "\t" + MapEntityID.get(tail) + "\n");
		}
		read_train.close();
		write_trainid.close();
		
		BufferedReader read_valid = new BufferedReader(new InputStreamReader(
				new FileInputStream(fn_valid), "UTF-8"));
		BufferedWriter write_validid = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fn_validid), "UTF-8"));
		while ((line = read_valid.readLine()) != null) {
			String head = line.split("\t")[0].trim();
			String relation = line.split("\t")[1].trim();
			String tail = line.split("\t")[2].trim();
			write_validid.write(MapEntityID.get(head) + "\t" 
			+ MapRelationID.get(relation) 
			+ "\t" + MapEntityID.get(tail) + "\n");
		}
		read_valid.close();
		write_validid.close();
		
		BufferedReader read_test = new BufferedReader(new InputStreamReader(
				new FileInputStream(fn_test), "UTF-8"));
		BufferedWriter write_test = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fn_testid), "UTF-8"));
		while ((line = read_test.readLine()) != null) {
			String head = line.split("\t")[0].trim();
			String relation = line.split("\t")[1].trim();
			String tail = line.split("\t")[2].trim();
			write_test.write(MapEntityID.get(head) + "\t" 
			+ MapRelationID.get(relation) 
			+ "\t" + MapEntityID.get(tail) + "\n");
		};
		read_test.close();		
		write_test.close();
		
		// id translation
	}
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub

		String fn_Ent = "datasets\\yago37\\entityid.txt";
		String fn_Rel = "datasets\\yago37\\relationid.txt";
		String fn_train = "datasets\\yago37\\yago37_triples.train";
		String fn_valid = "datasets\\yago37\\yago37_triples.valid";
		String fn_test = "datasets\\yago37\\yago37_triples.test";
		String fn_trainid = "datasets\\yago37\\train.txt";
		String fn_validid = "datasets\\yago37\\valid.txt";
		String fn_testid = "datasets\\yago37\\test.txt";

		ConvertDataForm infer = new ConvertDataForm();
        infer.convert_data(
        		fn_Ent, fn_Rel,
        		fn_train, fn_valid,fn_test,
        		fn_trainid, fn_validid, fn_testid);
	}

}
