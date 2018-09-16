package home.echyrski.xml.processing.sax;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.ext.DefaultHandler2;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;

import home.echyrski.xml.processing.model.ParsingInfo;
import home.echyrski.xml.processing.utils.IOUtils;
import home.echyrski.xml.processing.utils.SkipableCountDownLatch;

public final class SAXHandler extends DefaultHandler2 {

    private static final int PAGE_SIZE = 8 * 1024;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Method getCharacterOffset;
    private Object obj;
    private long processedCharacters;
    private Stack<String> elements = new Stack<String>();
    private ByteArrayOutputStream baos;
    private SkipableCountDownLatch latch;

    private int startOffset = -1;

    long currentPage = 0;
    long processedPages = 0;
    int skipedCount;
    private String parentElement;
    private boolean completeProcesing;
    private final Stopwatch stopwatch = Stopwatch.createUnstarted();
    private final ImmutableList.Builder<int[]> pageOffsets = new ImmutableList.Builder<>();

    public SAXHandler(SkipableCountDownLatch latch, int skipedCount) {
        this.skipedCount = skipedCount;
        this.latch = latch;

    }

    public void setDocumentLocator(Locator locator) {
        try {
            Field fLocator = locator.getClass().getDeclaredField("fLocator");
            fLocator.setAccessible(true);
            obj = fLocator.get(locator);
            getCharacterOffset = obj.getClass().getMethod("getCharacterOffset");


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getOffset() {
        try {
            return skipedCount + (Integer) getCharacterOffset.invoke(obj);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

    public void characters(char ch[], int start, int length) {
        processedCharacters += length;
        if (elements.size() > 1) {
            char[] text = Arrays.copyOfRange(ch, start, start + length);
            byte[] bytes = IOUtils.toBytes(text);
            if (baos != null) {
                try {
                    baos.write(bytes);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
    }


    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        elements.push(qName);
        if (elements.size() == 1) {
            startOffset = getOffset();
            parentElement = qName;
        } else if (elements.size() == 2) {
            baos = new ByteArrayOutputStream();
        }


    }

    public void endElement(String uri, String localName, String qName) {
        int currentOffset = getOffset();

        if (elements.size() == 2) {
            try {
                baos.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            long screenNum = processedCharacters / PAGE_SIZE;
            if (screenNum > currentPage) {
                pageOffsets.add(new int[]{startOffset, currentOffset - startOffset});
                startOffset = currentOffset;
                currentPage = screenNum;
                processedPages++;
                latch.countDown();
            }


        }
        elements.pop();

    }

    public void startDocument() {
        stopwatch.start();
    }

    public void endDocument() {
        latch.countDown(latch.getCount());
        latch.countDown();
        completeProcesing = true;
        logger.info("XML processing completed in {} ", stopwatch.stop());
    }

    public ParsingInfo getParsingInfo() {
        ParsingInfo info = new ParsingInfo();
        info.setParentElement(parentElement);
        info.setOffsets(pageOffsets.build());
        info.setComplete(completeProcesing);
        info.setProcessingTime(stopwatch.toString());
        return info;
    }

}