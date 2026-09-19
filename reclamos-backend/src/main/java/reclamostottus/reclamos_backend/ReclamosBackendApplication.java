package reclamostottus.reclamos_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync; // Importación nueva

@SpringBootApplication
@EnableAsync // Permite enviar correos en segundo plano sin trabar el sistema
public class ReclamosBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReclamosBackendApplication.class, args);
	}

}
