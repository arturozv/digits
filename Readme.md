**What it does:**
Send digits to a server and write them on a file using java Socket server and Guava event bus

**Client side:**
	- Class _ClientRunner_ makes sure there’s always 5 active connections sending data to the server, if one of them gets disconnected it gets replaced by a new one. 
	- Class _Client_ sends data to the server. It randomly sends a bad digit or a terminate signal. It uses callbacks to tell the Runner that it has been disconnected after a bad digit or a terminate signal has been sent.
	
**Server side:**
	- Class _ServerManager_ sets up everything and makes sure 5 connections are reading the socket. If one of the connections gets a terminate signal it tells the others to stop.
	- Class _DigitSocketReader_ reads from it's socket and calls DigitProcessor.
	- Class _DigitProcessor_ reads a digit and processes it with 3 options: good unique digit goes into the event bus, bad digit tells SocketReader to shut down, terminate signal sends an event to the bus.
	- Class _DigitEventBusConsumer_ consumes the good digits from the event bus and sends them to the aggregator.
  	- Class _DigitWriterAggregator_ aggregates digits and sends them in chunks to the file writer.
	- Class _DigitFileWriter_ writes chunks of digits into the digits.log file.
	- Class _DigitUniqueControl_ checks the digit has not been processed before
	- Class _StatsReporter_ periodically writes the state of the system 
	
**How to run it:**
Option 1: import the project to your IDE and run the Application.java main
Option 2: create a fat-jar and run it. check the shell run.sh