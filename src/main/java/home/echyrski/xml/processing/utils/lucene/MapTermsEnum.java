package home.echyrski.xml.processing.utils.lucene;


import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

import home.echyrski.xml.processing.LuceneHighlightingPoc;

/**
 *
 */
public class MapTermsEnum extends TermsEnum {
    private final Map<LuceneHighlightingPoc.Position, List<int[]>> terms;
    private Map.Entry<LuceneHighlightingPoc.Position, List<int[]>> currentTerm;
    private final Iterator<Map.Entry<LuceneHighlightingPoc.Position, List<int[]>>> iterator;

    public MapTermsEnum(Map<LuceneHighlightingPoc.Position, List<int[]>> terms) {
        this.iterator = terms.entrySet().iterator();
        this.terms = terms;
        currentTerm = iterator.next();
    }

    @Override
    public SeekStatus seekCeil(BytesRef bytesRef) {
        System.out.println(bytesRef.utf8ToString());
        return SeekStatus.END;
    }

    @Override
    public void seekExact(long l) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BytesRef term() {
        return new BytesRef(currentTerm.getKey().getBytes());
    }

    @Override
    public long ord() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int docFreq() {
        return 1;
    }

    @Override
    public long totalTermFreq() {
        return currentTerm.getValue().size();
    }

    @Override
    public PostingsEnum postings(PostingsEnum postingsEnum, int i) {
        return new MapTermsPostingEnum(currentTerm.getValue(), currentTerm.getKey().getLength());
    }

    @Override
    public BytesRef next() {
        if (iterator.hasNext()) {

            currentTerm = iterator.next();
            return term();
        }
        return null;
    }
}
