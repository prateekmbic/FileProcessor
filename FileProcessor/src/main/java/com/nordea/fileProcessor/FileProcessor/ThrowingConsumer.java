package com.nordea.fileProcessor.FileProcessor;

import java.util.function.Consumer;

/*
 * 
 * This is normal consumer functional interface with throw Exception
 * */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {
    void accept(T t) throws E;
    
    static Consumer<String> consumerExceptionThrower (ThrowingConsumer<String,Exception> throwingConsumer){
  	  return i -> {
  	        try {
  	        	throwingConsumer.accept(i);
  	        } catch (Exception e) {
  	        	
  	        	throw new RuntimeException(e.getMessage());
  	        }
  	    };
  	 
  }
  	
}




