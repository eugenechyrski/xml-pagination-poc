package home.echyrski.xml.processing.controllers;

import java.io.IOException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import home.echyrski.xml.processing.model.ParsingInfo;
import home.echyrski.xml.processing.service.XmlProcessingService;
import home.echyrski.xml.processing.utils.IOUtils;

/**
 *
 */
@RestController
public class XmlProcessingController {

    @Autowired
    private XmlProcessingService xmlProcessingService;

    @RequestMapping(value = "process-xml", method = RequestMethod.POST)
    public ParsingInfo processXml(String xmlUri, int numSectionsToParse) throws IOException, SAXException {
        return xmlProcessingService.processContent(xmlUri, new URL(xmlUri).openStream(), 0, numSectionsToParse, "");
    }

    @RequestMapping(value = "xml-chunk", method = RequestMethod.POST, produces = "text/xml")
    public byte[] processXml(String xmlUri, int start, int end) throws IOException {
        return IOUtils.extractCharactersFromStream(new URL(xmlUri).openStream(), start, end - start);

    }

}
