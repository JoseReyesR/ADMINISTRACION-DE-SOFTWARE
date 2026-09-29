package reclamostottus.reclamos_backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import static org.mockito.Mockito.*;

class WebConfigTest {

    @Test
    void addResourceHandlersExponeLaCarpetaUploads() {
        WebConfig webConfig = new WebConfig();

        ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);
        ResourceHandlerRegistration registration = mock(ResourceHandlerRegistration.class);

        when(registry.addResourceHandler("/uploads/**")).thenReturn(registration);
        when(registration.addResourceLocations(anyString())).thenReturn(registration);

        webConfig.addResourceHandlers(registry);

        verify(registry, times(1)).addResourceHandler("/uploads/**");
        verify(registration, times(1)).addResourceLocations("file:./uploads/");
    }
}
