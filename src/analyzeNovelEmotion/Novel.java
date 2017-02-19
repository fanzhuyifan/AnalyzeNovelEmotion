package analyzeNovelEmotion;

import thulac.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.io.*;

//A Novel consists of a sequence of Words. Words not contained in the dictionary are given emotional value 0.
public class Novel {
	private String rawText;
	private String[] parsedText;
	private Word[] words;
	private double[] cumulativeEmotionalContent;
	private int emotionWordCount = 0;
	private File fNovel;
	private static File tempFile;
	private Dictionary d;
	
	//This constructor accepts a filePath for the Novel and a dictPath for the dictionary
	Novel(String filePath,String dictPath) throws IOException{
		fNovel =new File(filePath);
		FileReader reader = new FileReader(fNovel.getAbsolutePath());
		char[] s;
		s = new char[(int) fNovel.length()];
		reader.read(s);
        reader.close();
        rawText = new String(s);
        this.parseText();
        this.calculateEmotion(dictPath);
        
	}
	
	public String getRawText(){
		return new String(this.rawText);
	}
	
	//Uses tool THULAC to fragment Chinese sentences into words
 	private void parseText() throws IOException{
 		tempFile = new File(fNovel.getAbsolutePath()+".txt");
 		if(!tempFile.exists()){
 			String[] args = {"-t2s","-seg_only","-deli","_","-input",fNovel.getAbsolutePath(),"-output",tempFile.getAbsolutePath(),
 					"-model_dir","/Users/themacpro/Desktop/emotionOfNovel/THULAC_lite_java_v1_jar/models"};
 			thulac.main(args);
 		}
		
		FileReader reader = new FileReader(tempFile.getAbsolutePath());
		char[] s;
		s = new char[(int) tempFile.length()];
		reader.read(s);
        reader.close();
        this.parsedText = new String(s).split("[：\\.．。？！；;!?，,、 \"\'“”‘’\n\r【】《》]+");
	}
	
	public int getWordCount(){
		return this.parsedText.length;
	}
	
	//return the number of words in the novel that have entries in the dictionary
	public int getEmotionWordCount(){
		return this.emotionWordCount;
	}
	
	private void calculateEmotion(String dictPath) throws IOException{
		words = new Word[this.getWordCount()];
        File dict = new File(dictPath);
        FileReader reader = new FileReader(dict.getAbsolutePath());
		char[] s = new char[(int) dict.length()];
		reader.read(s);
        reader.close();
        d = new Dictionary(new String(s));
        
        for(int i=0;i<this.getWordCount();i++){
        	if(!d.hasWord(parsedText[i])){
        		words[i] = new Word(parsedText[i],0);
        	}else{
        		this.emotionWordCount++;
        		words[i] = new Word(parsedText[i],d.getEmotion(parsedText[i]));
        	}
        }
        this.cumulativeEmotionalContent = new double[this.getWordCount()];
        this.cumulativeEmotionalContent[0] = words[0].getEmotion();
        for(int i=1;i<this.getWordCount();i++){
        	this.cumulativeEmotionalContent[i] = this.cumulativeEmotionalContent[i-1]+words[i].getEmotion();
        }
	}
	
	//This function calculates the mean of the emotional values of words in numWindow windows
	//of size windowSize evenly spaced in the novel. Higher windowSize means smoother results.
	public double[] getEmotion(int windowSize,int numWindow){
		double d = (this.getWordCount()+0.0-windowSize)/numWindow;
		double[] result = new double[numWindow];
		for(int i=0;i<numWindow;i++){
			result[i] = this.cumulativeEmotionalContent[(int) Math.floor(d*i+windowSize)]
					-this.cumulativeEmotionalContent[(int) Math.floor(d*i)];
			result[i]/=windowSize;
		}
		return result;
	}
	
	//Return the ArrayList of individual words in the novel.
	public ArrayList<String> getParsedText(){
		return new ArrayList<String>(Arrays.asList(this.parsedText));
	}
}

//A Word consists of a String and an emotional value.
class Word implements Comparable<Word>{
	private float emotionIndex;
	private String word;
	Word(String s, float f){
		emotionIndex = f;
		word = s;
	}
	public String getWord(){
		return word;
	}
	public float getEmotion(){
		return emotionIndex;
	}
	public void setWord(String s){
		word = s;
	}
	public void setEmotion(float f){
		emotionIndex = f;
	}
	@Override
	public int compareTo(Word o) {
		return this.word.compareTo(o.getWord());
	}
}

//A Dictionary is a map from Chinese words to a real number between -9 and 9, 
//positive numbers stand for positive emotions and negative numbers stand for negative ones.
class Dictionary{
	private TreeMap<String,Float> dictionaryData;
	//This constructor accepts an array of Strings, each of the format "词语,分数"。
	//For example {"你好,5","糟糕,-5"}.
	Dictionary(String[] s){
		dictionaryData= new TreeMap<String,Float>();
		for(String w:s){
			int index = w.lastIndexOf(",");
			String key = w.substring(0, index);
			float value = Float.valueOf(w.substring(index+1,w.length()));
			dictionaryData.put(key, value);
		}
	}
	//This constructor accepts a single String, with each line having the format "词语,分数".
	//For example:"你好,5\n糟糕,-5"
	Dictionary(String s){
		this(s.split("\n"));
	}
	public boolean hasWord(String s){
		return dictionaryData.containsKey(s);
	}
	//returns emotion value of s
	public float getEmotion(String s){
		return dictionaryData.get(s);
	}
}

