package reclamostottus.reclamos_backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CorsConfigTest {

    @Test
    void corsConfigurerConfiguraLosMappingsEsperados() {
        CorsConfig corsConfig = new CorsConfig();
        WebMvcConfigurer configurer = corsConfig.corsConfigurer();

        assertThat(configurer).isNotNull();

        CorsRegistry registry = mock(CorsRegistry.class);
        CorsRegistration registration = mock(CorsRegistration.class);

        when(registry.addMapping("/api/**")).thenReturn(registration);
        when(registration.allowedOrigins(anyString())).thenReturn(registration);
        when(registration.allowedMethods(any(String[].class))).thenReturn(registration);
        when(registration.allowedHeaders(anyString())).thenReturn(registration);
        when(registration.allowCredentials(anyBoolean())).thenReturn(registration);

        configurer.addCorsMappings(registry);

        verify(registry, times(1)).addMapping("/api/**");
        verify(registration, times(1)).allowedOrigins("http://localhost:4200");
        verify(registration, times(1)).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        verify(registration, times(1)).allowedHeaders("*");
        verify(registration, times(1)).allowCredentials(true);
    }
}
