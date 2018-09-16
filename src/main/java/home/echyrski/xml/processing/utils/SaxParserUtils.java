package home.echyrski.xml.processing.utils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.util.function.Supplier;

import org.xml.sax.SAXException;

/**
 *
 */
public class SaxParserUtils {
    private static SAXParserFactory parserFactor;
    private static ThreadLocal<SAXParser> parsers = ThreadLocal.withInitial(new Supplier<SAXParser>() {
        @Override
        public SAXParser get() {
            try {
                return parserFactor.newSAXParser();
            } catch (SAXException | ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
        }
    });

    static {
        parserFactor = SAXParserFactory.newInstance();
    }

    private SaxParserUtils() {
    }

    public static SAXParser getParser() {
        try {
            return parserFactor.newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
         throw new RuntimeException(e);
        }
    }

    public static SAXParser getCleanParser() {
        SAXParser parser = getParser();
        parser.reset();
        return parser;
    }

}
