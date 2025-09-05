package org.kodigo.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     * Configura el broker de mensajes
     *
     * @param config configuración del broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita un broker simple en memoria
        // Los clientes se suscriben a destinos que empiecen con "/topic"
        config.enableSimpleBroker("/topic");

        // Los mensajes enviados desde el cliente tendrán prefijo "/app"
        // Por ejemplo: /app/chat.sendMessage
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registra los endpoints STOMP
     *
     * @param registry registro de endpoints
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint al cual los clientes se conectarán para establecer la conexión WebSocket
        registry.addEndpoint("/ws")
                .withSockJS();  // Fallback SockJS para navegadores que no soporten WebSockets
    }
}
