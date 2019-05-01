package com.nordea.fileProcessor.FileProcessor;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SentenceProducerTest {



	@Autowired 
	SentenceProducer sentenceProducer;

	 
	    @DisplayName("Test First Line Parser Regex")
	    @Test
	    void assertFirstLineParser_Test1() {
	    	  String sentence="Hi Mr. Young. How are you.";
	    	   assertEquals("Hi Mr. Young.",sentenceProducer.runRegexPattern(sentence,  "FirstSentence").get(0));
	    }

	    @DisplayName("Test First Line Parser Regex")
	    @Test
	    void assertFirstLineParser_Test2() {
	    	  String sentence="Hi Mr. Young? How are you.";
	    	   assertEquals("Hi Mr. Young?",sentenceProducer.runRegexPattern(sentence,  "FirstSentence").get(0));
	    }

	    @DisplayName("Test First Line Parser Regex")
	    @Test
	    void assertFirstLineParser_Test3() {
	    	  String sentence="Hi Mr. Young";
	    	   assertThrows(IndexOutOfBoundsException.class,()->sentenceProducer.runRegexPattern(sentence,  "FirstSentence").get(0),"Hi Mr. Young");
	    }

	    @DisplayName("Test First Line Parser Regex")
	    @Test
	    void assertFirstLineParser_Test4() {
	    	  String sentence=".Hi Mr. Young";
	    	   assertEquals(".",sentenceProducer.runRegexPattern(sentence,  "FirstSentence").get(0));
	    }

	    @DisplayName("Test Middle Line Parser Regex")
	    @Test
	    void assertMiddleLineParser_Test1() {
	    	  String sentence="Hi Mr. Young? How are you.";
	    	   assertEquals("? How are you",sentenceProducer.runRegexPattern(sentence,  "MiddleSentence").get(0));
	    }

	    @DisplayName("Test Middle Line Parser Regex")
	    @Test
	    void assertMiddleLineParser_Test2() {
	    	  String sentence="Hi Mr. Young. How are you.";
	    	   assertEquals(". How are you",sentenceProducer.runRegexPattern(sentence,  "MiddleSentence").get(0));
	    }

	    @DisplayName("Test Middle Line Parser Regex")
	    @Test
	    void assertMiddleLineParser_Test3() {
	    	  String sentence="Hi Mr. Young. How are you.Mr. Michal is here.";
	    	   assertEquals(".Mr. Michal is here",sentenceProducer.runRegexPattern(sentence,  "MiddleSentence").get(1));
	    }
	    @DisplayName("Test  Middle Line Parser Regex")
	    @Test
	    void assertMiddleLineParser_Test4() {
	    	  String sentence="Hi Mr. Young";
	    	   assertThrows(IndexOutOfBoundsException.class,()->sentenceProducer.runRegexPattern(sentence,  "MiddleSentence").get(1),"Hi Mr. Young");
	    }
	 

	@DisplayName("Test  Middle Line Parser Regex")
	@Test
	void assertMiddleLineParser_Test5() {
		String sentence="My Name is Mr. Michal .Hi Mr. Young.Hope you are";
		assertEquals(".Hi Mr. Young",sentenceProducer.runRegexPattern(sentence,  "MiddleSentence").get(0));


	}

	@DisplayName("Test  Last Line Parser Regex")
	@Test
	void assertLastLineParser_Test1() {

		String  sentence="My Name is Mr. Michal .Hi Mr. Young.Hope you are";
		assertEquals(".Hope you are",sentenceProducer.runRegexPattern(sentence,  "LastSentence").get(0));
		sentence="My Name is Mr. Michal ,  Hi Mr. Young.Hope you are";
		assertEquals("Hope you are",sentenceProducer.runRegexPattern(sentence,  "LastSentence").get(0));
		sentence=" Hi  Young.Hope you are";
		assertEquals("Hope you are",sentenceProducer.runRegexPattern(sentence,  "LastSentence").get(0));


	}




}
