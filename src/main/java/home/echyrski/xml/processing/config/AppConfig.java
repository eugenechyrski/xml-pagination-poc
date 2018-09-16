package home.echyrski.xml.processing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import home.echyrski.xml.processing.service.XmlProcessingService;

/**
 *
 */
@Configuration
public class AppConfig {

    @Bean
    public XmlProcessingService xmlProcessingService() {
        return new XmlProcessingService();
    }
}
