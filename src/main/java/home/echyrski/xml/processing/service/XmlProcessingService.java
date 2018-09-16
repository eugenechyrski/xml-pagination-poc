package home.echyrski.xml.processing.service;

import javax.xml.parsers.SAXParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.xerces.impl.io.UTF8Reader;
import org.xml.sax.SAXException;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import home.echyrski.xml.processing.model.ParsingInfo;
import home.echyrski.xml.processing.sax.SAXHandler;
import home.echyrski.xml.processing.utils.SaxParserUtils;
import home.echyrski.xml.processing.utils.SkipableCountDownLatch;

/**
 *
 */
public class XmlProcessingService {
    private final Cache<String, SAXHandler> cache;
    private final ExecutorService executorService;

    public XmlProcessingService() {
        cache = CacheBuilder.newBuilder().maximumSize(10).build();
        executorService = Executors.newFixedThreadPool(50);
    }

    public ParsingInfo processContent(String id, InputStream is, int skipCount, int sectionsCount,String parentElement) throws IOException, SAXException {
        SAXHandler future = cache.getIfPresent(id);
        if (future == null) {
            int realSkipCount = skipCount - getXmlHeader(parentElement).length();
            SkipableCountDownLatch latch = new SkipableCountDownLatch(sectionsCount);
            SAXHandler handler = new SAXHandler(latch, skipCount > 0 ? realSkipCount : 0);
            SAXParser parser = SaxParserUtils.getCleanParser();
            InputStream stream = is;
            if (skipCount > 0) {
                UTF8Reader reader = new UTF8Reader(is);
                while (skipCount > 0) {
                    skipCount -= reader.skip(skipCount);
                }
                stream = new SequenceInputStream(new ByteArrayInputStream(getXmlHeader(parentElement).getBytes(Charset.defaultCharset())), is);
            }
            InputStream finalstream = stream;
            CompletableFuture.supplyAsync(() -> {
                try {

                    parser.parse(finalstream, handler);
                    return handler;

                } catch (SAXException | IOException e) {
                    throw new RuntimeException(e);
                }
            }, executorService);
            cache.put(id, handler);

            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return handler.getParsingInfo();
        } else {
            return future.getParsingInfo();

        }
    }

    private static String getXmlHeader(String parentElement) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><" + parentElement + ">";
    }
}
