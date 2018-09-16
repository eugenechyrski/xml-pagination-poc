Big xml files(think of wikipedia dumps in terms of size) should not be processed by browser applications directly. It is easier to split huge xml file into small pieces(e.g. each piece contains text for a full screen) and do further processing of xml file in background on server.
This is a SAX based applications powered by spring boot which allows to split xml into pieces. each piece is byitself a valid xml part.
It contains endpoints to get start position of each page and page length. Minimal page length is 8kb.
So javascript application can ask for multiple first pages and render them. In background it can call the same endpoint to get next page positions on demand.




*AHTUNG*
This code was written just for fun as a weeked POC. If it blows up your data, pc or mind is not my responsibility :)
