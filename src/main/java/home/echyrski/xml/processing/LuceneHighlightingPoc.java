package home.echyrski.xml.processing;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.TokenStreamFromTermVector;
import org.apache.lucene.search.highlight.WeightedSpanTerm;

import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;

import home.echyrski.xml.processing.utils.lucene.MapTerms;

/**
 * This is a POC demonstrates how to extract words from text and create Map based Terms enum with positions and offset.
 * Finally i does lucene highlighting on the fly. The goal is to prepare TermsEnum without Analyzer
 */
public class LuceneHighlightingPoc {


    public static void main(String[] args) throws Exception {
        String text = IOUtils.toString(new InputStreamReader(new URL("https://en.wikipedia.org/wiki/Roush_Fenway_Racing").openStream()));
        Scorer scorer = new QueryScorer(new WeightedSpanTerm[]{
                new WeightedSpanTerm(1.0f, "of"),
                new WeightedSpanTerm(1.0f, "the"),
                new WeightedSpanTerm(4.0f, "new"),
                new WeightedSpanTerm(5.0f, "series")
        });
        while (text.getBytes(Charsets.UTF_8.name()).length / (1024 * 1024 * 100 * 1.0 / 3) < 1) {
            text += text;
        }
        Highlighter highlighter = new Highlighter(scorer);
        Map<Position, List<int[]>> positions = null;
        for (int i = 0; i < 10; i++) {
            Stopwatch sw = Stopwatch.createStarted();
            positions = index(text.toCharArray());
            System.out.println("indexed:" + text.length() + " " + sw);
        }
        for (int i = 0; i < 100; i++) {

            Stopwatch sw = Stopwatch.createStarted();

            MapTerms mapTerms = new MapTerms(positions);
            TokenStreamFromTermVector ts = new TokenStreamFromTermVector(mapTerms, Integer.MAX_VALUE);
            String fragment = highlighter.getBestFragment(ts, text);
            System.out.println(sw.stop() + " highlighted " + text.length() + "fragment: " + fragment);

        }

    }


    public static Map<Position, List<int[]>> index(char[] chars) {
        int termPos = -1;

        Map<Position, List<int[]>> positions = new TreeMap<>();
        int length = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            Character current = Character.toLowerCase(chars[i]);
            chars[i] = current;
            int type = Character.getType(current);
            switch (type) {
                case Character.COMBINING_SPACING_MARK:
                case Character.CONNECTOR_PUNCTUATION:
                case Character.CONTROL:
                case Character.CURRENCY_SYMBOL:
                case Character.DASH_PUNCTUATION:

                case Character.ENCLOSING_MARK:
                case Character.END_PUNCTUATION:
                case Character.FINAL_QUOTE_PUNCTUATION:
                case Character.FORMAT:
                case Character.INITIAL_QUOTE_PUNCTUATION:

                case Character.LINE_SEPARATOR:

                case Character.MATH_SYMBOL:
                case Character.MODIFIER_LETTER:
                case Character.MODIFIER_SYMBOL:
                case Character.NON_SPACING_MARK:
                case Character.OTHER_LETTER:
                case Character.OTHER_NUMBER:
                case Character.OTHER_PUNCTUATION:
                case Character.OTHER_SYMBOL:
                case Character.PARAGRAPH_SEPARATOR:
                case Character.PRIVATE_USE:
                case Character.SPACE_SEPARATOR:
                case Character.START_PUNCTUATION:
                case Character.SURROGATE:
                case Character.TITLECASE_LETTER:
                case Character.UNASSIGNED:
                case Character.UPPERCASE_LETTER:
                    if (length > 0) {
                        termPos++;


                        int startOffset = i - length;
                        Position pos = new Position();
                        pos.start = startOffset;
                        pos.buffer = chars;
                        pos.length = length;
                        pos.position = termPos;
                        positions.computeIfAbsent(pos, p -> new ArrayList<>())
                                .add((new int[]{++termPos, i - sb.length()}));

                        length = 0;
                    }
                    break;
                case Character.DECIMAL_DIGIT_NUMBER:
                case Character.LOWERCASE_LETTER:
                case Character.LETTER_NUMBER:
                    length++;
                    break;
                default:
                    throw new RuntimeException("" + Character.getType(current));
            }

        }
        return positions;
    }

    public static class Position implements Comparable<Position> {
        private char[] buffer;
        private byte[] bytes;
        private int start;
        private int length;
        private int position;

        public int getStart() {
            return start;
        }

        public int getLength() {
            return length;
        }

        public int getPosition() {
            return position;
        }

        public byte[] getBytes() {
            if (bytes == null) {
                bytes = home.echyrski.xml.processing.utils.IOUtils.toBytes(buffer, start, length);
            }
            return bytes;
        }

        @Override
        public int compareTo(Position o) {
            if (this == o) {
                return 0;
            }
            int finalLength = Math.min(o.length, length);
            for (int i = 0; i < finalLength; i++) {

                int dif = buffer[start + i] - o.buffer[o.start + i];
                if (dif != 0) {
                    return dif;
                }

            }
            return length - o.length;
        }
    }
}
