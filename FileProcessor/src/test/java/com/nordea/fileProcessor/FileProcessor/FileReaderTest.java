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
class FileReaderTest {

	@Autowired
	FileReader fileReader;
	
	@DisplayName("Test File Not Found")
    @Test
    void asserFileNotFoundException() {

    	Throwable exception =    assertThrows(FileProcessorException.class, ()->fileReader.bufferedReaderRead("test.cmk"));
    	 assertEquals(exception.getMessage(), "test.cmk (The system cannot find the file specified)");
    }



}
