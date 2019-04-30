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


	public void produceSentences() throws FileProcessorException{

		String sentence =null;
		do {		
			try {

				sentence=readerSentenceQueue.poll(5, TimeUnit.SECONDS);
				logger.debug("sentence at producter"+sentence);
				System.out.println("sentence at produce sentence"+sentence);
				if(sentence!=null) {

					sentenceIdentifier(sentence)
					.forEach(ThrowingConsumer.consumerExceptionThrower(sentenceProcessorLambda()));
				}	
			}catch(Exception e){

				logger.error( e.getMessage(),e);
				FileProcessorException.raiseException(e);
			}

		}while( sentence!=null);
	}
	public List<String> sentenceIdentifier(String sentence) {
		List<String> sentenceList = new ArrayList<String>();

		incompleteSentenceBuilder.append(runRegexPattern(sentence, FIRST_SENTENCE ));

		Matcher matcher = splitString.matcher(sentence);
		if(matcher.find())
		{
			sentenceList.add(incompleteSentenceBuilder.toString());
			incompleteSentenceBuilder= new StringBuilder();
			incompleteSentenceBuilder.append(runRegexPattern(sentence, LAST_SENTENCE));
			sentenceList.addAll(runRegexPattern(sentence, MIDDLE_SENTENCE));
			logger.debug("input Sentence ->"+sentence +" output Sentence List ->"+sentenceList);

		}else {
			incompleteSentenceBuilder.append(sentence);
		}

		return sentenceList;
	}

	public List<String> runRegexPattern(String sentence, String patternName) {

		Matcher matcher ;
		List<String> sentenceList = new ArrayList<String>();
		switch(patternName) {
		case FIRST_SENTENCE: 

			matcher = patternFirstSentence.matcher(sentence);
			while(matcher.find())
			{
				logger.debug(FIRST_SENTENCE+"sentence->"+sentence+"pattern->"+firstSentenceRegex+ "  output->"+matcher.group());
				sentenceList.add(   matcher.group());
			}
			break;
		case MIDDLE_SENTENCE:     

			matcher = patternMiddleSentence.matcher(sentence);
			while(matcher.find())
			{

				sentenceList.add(   matcher.group());
				logger.debug(MIDDLE_SENTENCE+"sentence->"+sentence+"pattern->"+middleSentenceRegex+ "  output->"+matcher.group());

			}

			break;
		case LAST_SENTENCE: 
			matcher = patternMiddleSentence.matcher(sentence);
			int lastindex=0;
			while(matcher.find())
			{
				matcher.group();
				lastindex = matcher.end();
			}
			if(lastindex>0) {
				sentenceList.add(  sentence.substring(lastindex));
				logger.debug(LAST_SENTENCE+"sentence->"+sentence+"pattern->"+middleSentenceRegex+ "  output->"+sentence.substring(lastindex));

			}else {
				matcher = patternFirstSentence.matcher(sentence);
				while(matcher.find())
				{
					matcher.group();
					lastindex = matcher.end();
				}
				if(lastindex>0) {
					sentenceList.add(  sentence.substring(lastindex));
					logger.debug(LAST_SENTENCE+"sentence->"+sentence+"pattern->"+firstSentenceRegex+ "  output->"+sentence.substring(lastindex));
				}
			}

		}

		return sentenceList;
	}

}
