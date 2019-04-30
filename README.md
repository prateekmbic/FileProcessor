# fileProcessor
File processor for processing Text file into xml and csv

User can build the jar using gradle bootjar.
To run the jar navigate to  FileProcessor\build\libs
and execute below command.


java -jar FileProcessor-0.0.1-SNAPSHOT.jar --sourceFileName=C:\\Users\\prattr\\Desktop\\nordea\\sample_data\\large.in --processQueue.initialCapaity=50


The jar expects a source file name parameter. Target files are created in the same directory with .csv and.xml extension
