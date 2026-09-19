package reclamostottus.reclamos_backend.config; // Ajusta a tu paquete

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Le decimos a Spring que cuando alguien busque
        // "http://localhost:8080/uploads/..."
        // vaya a buscar el archivo físico en la carpeta "uploads" de tu proyecto.
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }
}