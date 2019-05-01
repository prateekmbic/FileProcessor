package com.nordea.fileProcessor.FileProcessor;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class SentenceTest {
	
	@Autowired
	Sentence sentence1;
	@Autowired
	Sentence sentence2;

	@DisplayName("Test  Sentence Equals Method")
	@Test
	void assetSentenceEquality() {
		
		sentence1.addWord("Hello");
		sentence1.addWord("hello");
		
		sentence2.addWord("Hello");
		sentence2.addWord("hello");
		assertEquals(sentence1,sentence2);
	}


	@DisplayName("Test  Sentence Hash Method")
	@Test
	void assetSentenceHash() {
	
		sentence1.addWord("Hello");
		sentence1.addWord("hello");
		
		sentence2.addWord("Hello");
		sentence2.addWord("helloHi");
		Map<Sentence,String> testMap = new HashMap<Sentence, String>();

		testMap.put(sentence2, "checking Hash Function");
		testMap.put(sentence1, "checking Hash Function1");

		assertEquals("checking Hash Function",testMap.get(sentence2) );
		assertEquals("checking Hash Function1",testMap.get(sentence1) );


	}

}
