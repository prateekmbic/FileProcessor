package com.nordea.fileProcessor.FileProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * This class contains all the logic for converting the lines in the file to 
 * Sentence Objects. It uses Regex to identify the Sentences.
 * */
@Service
public class SentenceProducer implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(SentenceProducer.class);

	private static final String SENTENCE_PRODUCER = "Sentence Producer";
	private static final String MIDDLE_SENTENCE = "MiddleSentence";
	private static final String LAST_SENTENCE = "LastSentence";
	private static final String FIRST_SENTENCE = "FirstSentence";

	@Autowired
	private ProcessQueue sentenceBlockingQueue;
	@Autowired
	private BlockingQueue<String> readerSentenceQueue;

	@Autowired
	private ObjectFactory<Sentence> prototypeBeanObjectFactory;
	private final AtomicInteger counter = new AtomicInteger();

	public Sentence getPrototypeSentenceInstance() {
		return prototypeBeanObjectFactory.getObject();
	}

	private StringBuilder incompleteSentenceBuilder = new StringBuilder();
	private String exclusionRegex="(Mr|Dr|Ms)";
	private String sentenceTerminator="(\\.|\\?|\\!)";
	private String middleSentenceRegex ="(?<!"+exclusionRegex+")"+sentenceTerminator+".*?(?<!"+exclusionRegex+")(?="+sentenceTerminator+")";
	private Pattern patternMiddleSentence = Pattern.compile(middleSentenceRegex);
	private String firstSentenceRegex="^.*?(?<!"+exclusionRegex+")[.?!]";
	private Pattern patternFirstSentence = Pattern.compile(firstSentenceRegex);
	private String wordSplitter ="[\\w'\\u2019\\p{Lo}\\p{S}]+((?<="+exclusionRegex+")\\.)?";
	private Pattern patternWordSplitter = Pattern.compile(wordSplitter);
	private String sentenceSplitRegex="(?<!"+exclusionRegex+"\\s*)"+sentenceTerminator+"+";
	private Pattern splitString =Pattern.compile(sentenceSplitRegex);

	public SentenceProducer(ProcessQueue sentenceBlockingQueue, BlockingQueue<String> readerSentenceQueue) {
		super();
		this.sentenceBlockingQueue = sentenceBlockingQueue;
		this.readerSentenceQueue= readerSentenceQueue;
	}

	@Override
	public void run() {
		Thread.currentThread().setName(SENTENCE_PRODUCER);
		logger.info(SENTENCE_PRODUCER+" Process Started");
		try {
			produceSentences();
			logger.info(SENTENCE_PRODUCER+" Process Completed");
		}
		catch(FileProcessorException ex) {
			logger.error(SENTENCE_PRODUCER+" Process Failed");
			FileProcessorException.raiseProcessFailureException(SENTENCE_PRODUCER,ex.getMessage());
		}

	}

	/**
	 * This is the lambda function to create the Sentence object and 
	 * populate it with the sorted word list
	 */
	public ThrowingConsumer<String,Exception> sentenceProcessorLambda(){

		return	sentenceInList->

		{
			try {
				Sentence processedSentence = getPrototypeSentenceInstance();
				processedSentence.setSentenceId(counter.incrementAndGet());
				Matcher matcher = patternWordSplitter.matcher(sentenceInList);

				while(matcher.find()) {
					processedSentence.addWord(matcher.group());
				}
				if(processedSentence.getWordList().size()>0) {
						processedSentence.getWordList().sort(new Sentence());					
						sentenceBlockingQueue.put(processedSentence);
				}
			}
			catch(Exception e) {
				logger.error(e.getMessage());
				FileProcessorException.raiseException(e);
			}
		};
	};

	/*
	 * This method dequeu the line from readerSentence Blocking Queue and 
	 * runs the regex on it to identify the sentences in it.
	 * */
	public void produceSentences() throws FileProcessorException{

		String line =null;
		do {		
			try {

				line=readerSentenceQueue.poll(5, TimeUnit.SECONDS);
				logger.debug("sentence at producter"+line);
				System.out.println("sentence at produce sentence"+line);
				if(line!=null) {

					sentenceIdentifier(line)
					.forEach(ThrowingConsumer.consumerExceptionThrower(sentenceProcessorLambda()));
				}	
			}catch(Exception e){

				logger.error( e.getMessage(),e);
				FileProcessorException.raiseException(e);
			}

		}while( line!=null);
	}
	public List<String> sentenceIdentifier(String line) {
		List<String> sentenceList = new ArrayList<String>();

		incompleteSentenceBuilder.append(runRegexPattern(line, FIRST_SENTENCE ));

		Matcher matcher = splitString.matcher(line);
		if(matcher.find())
		{
			sentenceList.add(incompleteSentenceBuilder.toString());
			incompleteSentenceBuilder= new StringBuilder();
			incompleteSentenceBuilder.append(runRegexPattern(line, LAST_SENTENCE));
			sentenceList.addAll(runRegexPattern(line, MIDDLE_SENTENCE));
			logger.debug("input Line ->"+line +" output Sentence List ->"+sentenceList);

		}else {
			incompleteSentenceBuilder.append(line);
		}

		return sentenceList;
	}
	/*
	 * This method identifiers the first, middle and last sentences of the line.
	 * 
	 * */
	public List<String> runRegexPattern(String line, String patternName) {

		Matcher matcher ;
		List<String> sentenceList = new ArrayList<String>();
		switch(patternName) {
		case FIRST_SENTENCE: 

			matcher = patternFirstSentence.matcher(line);
			while(matcher.find())
			{
				logger.debug(FIRST_SENTENCE+"sentence->"+line+"pattern->"+firstSentenceRegex+ "  output->"+matcher.group());
				sentenceList.add(   matcher.group());
			}
			break;
		case MIDDLE_SENTENCE:     

			matcher = patternMiddleSentence.matcher(line);
			while(matcher.find())
			{

				sentenceList.add(   matcher.group());
				logger.debug(MIDDLE_SENTENCE+"sentence->"+line+"pattern->"+middleSentenceRegex+ "  output->"+matcher.group());

			}

			break;
		case LAST_SENTENCE: 
			matcher = patternMiddleSentence.matcher(line);
			int lastindex=0;
			while(matcher.find())
			{
				matcher.group();
				lastindex = matcher.end();
			}
			if(lastindex>0) {
				sentenceList.add(  line.substring(lastindex));
				logger.debug(LAST_SENTENCE+"sentence->"+line+"pattern->"+middleSentenceRegex+ "  output->"+line.substring(lastindex));

			}else {
				matcher = patternFirstSentence.matcher(line);
				while(matcher.find())
				{
					matcher.group();
					lastindex = matcher.end();
				}
				if(lastindex>0) {
					sentenceList.add(  line.substring(lastindex));
					logger.debug(LAST_SENTENCE+"sentence->"+line+"pattern->"+firstSentenceRegex+ "  output->"+line.substring(lastindex));
				}
			}

		}

		return sentenceList;
	}

}
