package home.echyrski.xml.processing.utils.lucene;


import java.util.List;
import java.util.Map;

import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;

import home.echyrski.xml.processing.LuceneHighlightingPoc;

/**
 *
 */
public class MapTerms extends Terms {
    private Map<LuceneHighlightingPoc.Position, List<int[]>> terms;

    public MapTerms(Map<LuceneHighlightingPoc.Position, List<int[]>> terms) {
        this.terms = terms;
    }

    @Override
    public TermsEnum iterator() {
        return new MapTermsEnum(terms);
    }

    @Override
    public long size() {
        return terms.size();
    }

    @Override
    public long getSumTotalTermFreq() {
        return 0;
    }

    @Override
    public long getSumDocFreq() {
        return 0;
    }

    @Override
    public int getDocCount() {
        return 1;
    }

    @Override
    public boolean hasFreqs() {
        return false;
    }

    @Override
    public boolean hasOffsets() {
        return true;
    }

    @Override
    public boolean hasPositions() {
        return true;
    }

    @Override
    public boolean hasPayloads() {
        return true;
    }
}
