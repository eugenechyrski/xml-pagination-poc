package home.echyrski.xml.processing.model;

import java.util.List;

/**
 *
 */
public class ParsingInfo {
    private String parentElement;
    private List<int[]> offsets;
    private boolean complete;
    private String processingTime;

    public String getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(String processingTime) {
        this.processingTime = processingTime;
    }

    public String getParentElement() {
        return parentElement;
    }

    public void setParentElement(String parentElement) {
        this.parentElement = parentElement;
    }

    public List<int[]> getOffsets() {
        return offsets;
    }

    public void setOffsets(List<int[]> offsets) {
        this.offsets = offsets;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
