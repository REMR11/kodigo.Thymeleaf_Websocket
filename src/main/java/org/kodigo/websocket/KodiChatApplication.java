package org.kodigo.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KodiChatApplication {

	public static void main(String[] args) {

		System.out.println("🚀 Iniciando aplicación de Chat en Tiempo Real...");
		System.out.println("📡 WebSockets habilitados");
		System.out.println("🗄️ Base de datos configurada");
		System.out.println("🌐 Interfaz Thymeleaf lista");
		SpringApplication.run(KodiChatApplication.class, args);
		System.out.println("✅ Aplicación iniciada correctamente");
		System.out.println("🔗 Accede a: http://localhost:8080");
		System.out.println("🔧 Consola H2: http://localhost:8080/h2-console");
	}

}
