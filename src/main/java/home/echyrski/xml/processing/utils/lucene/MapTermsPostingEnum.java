package home.echyrski.xml.processing.utils.lucene;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.BytesRef;

/**
 *
 */
public class MapTermsPostingEnum extends PostingsEnum {
    private final List<int[]> positions;
    private int currentPosition = -1;
    private final int length;

    public MapTermsPostingEnum(List<int[]> positions, int length) {
        this.positions = positions;
        this.length = length;
    }

    @Override
    public int freq() {
        return positions.size();
    }

    @Override
    public int nextPosition() {
        currentPosition++;
        return positions.get(currentPosition)[0];
    }

    @Override
    public int startOffset() {

        return positions.get(currentPosition)[1];
    }

    @Override
    public int endOffset() {
        return positions.get(currentPosition)[1] + length;
    }

    @Override
    public BytesRef getPayload() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int docID() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int nextDoc() {
        return DocIdSetIterator.NO_MORE_DOCS;
    }

    @Override
    public int advance(int i) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long cost() {
        throw new UnsupportedOperationException();
    }
}
