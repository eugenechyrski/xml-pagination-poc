package home.echyrski.xml.processing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import home.echyrski.xml.processing.config.AppConfig;
import home.echyrski.xml.processing.config.SwaggerConfig;

@SpringBootApplication
@Import({SwaggerConfig.class, AppConfig.class})
@PropertySource({
        "classpath:application.properties"
})
public class WebUIApplication {

    public static void main(String[] args) {
         SpringApplication.run(WebUIApplication.class, args);


    }

}
