package com.nordea.fileProcessor.FileProcessor;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/*
 * This is the Sentence Object. It Contains the list of words in sorted order.
 * It provides its XML and CSV Format using toCSV and toXML methods.
 * */
@Component
@Scope("prototype")
public class Sentence implements Comparator<String>{

	private static Logger logger = LoggerFactory.getLogger(Sentence.class);
	
	@Autowired
	private List<String> wordList ;
	
	Pattern p= Pattern.compile("[\u0027\u2019]", Pattern.UNICODE_CHARACTER_CLASS);
	
	@Autowired
	private StringBuilder xmlStringBuilder;
	@Autowired
	private StringBuilder csvStringBuilder;
	
	
	private int sentenceId=0;

	
	public int getSentenceId() {
		return sentenceId;
	}

	public void setSentenceId(int sentenceId) {
		this.sentenceId = sentenceId;
	}

	public List<String> getWordList() {
		return wordList;
	}

	public void setWordList(List<String> wordList) {
		this.wordList = wordList;
	}

	@Override
	public String toString() {
		return "Sentence [wordList=" + wordList + "]";
	}
	
	public void addWord(String word) {
		wordList.add(word);
	}
	
	public StringBuilder toXML() {
		
		xmlStringBuilder.append("\n<sentence>");
		xmlStringBuilder.append( wordList
						.stream()
						.map(arrayItem->
								{   
									return (new StringBuilder("<word>").
											append(
													p.matcher(arrayItem).replaceAll("&apos;")
													).append("</word>"));
									
								}).reduce(
										new StringBuilder(),
										(s,a)->s.append(a))
										);
			xmlStringBuilder.append("</sentence>");
			logger.debug("XML Message"+xmlStringBuilder);
		return xmlStringBuilder;
	}
	
	
	public StringBuilder toCSV() {
		
		csvStringBuilder.append("\nSentence ").append(sentenceId).append(", ");
		csvStringBuilder.append(
				wordList
					.stream()
					.collect(Collectors.joining(", "))
				);
		logger.debug("CSV Message"+csvStringBuilder);
		return csvStringBuilder;
		
	}


	@Override
	public int compare(String o1, String o2) {
		
		 int compare= o1.compareToIgnoreCase(o2);
		  return compare>0?compare:-1;
	}
	
	
	 @Override
	    public boolean equals(Object o) {

	        if (o == this) return true;
	        if (!(o instanceof Sentence)) {
	            return false;
	        }
	        Sentence sent = (Sentence) o;
	        return wordList.equals(sent.getWordList());
	       
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(wordList);
	    }

}

