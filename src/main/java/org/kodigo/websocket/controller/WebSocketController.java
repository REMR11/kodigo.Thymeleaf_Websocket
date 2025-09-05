package org.kodigo.websocket.controller;

import org.kodigo.websocket.model.Message;
import org.kodigo.websocket.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * Controlador que maneja los mensajes WebSocket
 *
 * Diferencias clave con controladores HTTP:
 * - No usa @RequestMapping sino @MessageMapping
 * - Los mensajes se envían a temas/topics, no como respuestas HTTP
 * - La comunicación es asíncrona y bidireccional
 */
@Controller
public class WebSocketController {

    @Autowired
    private MessageService messageService;

    /**
     * Maneja los mensajes de chat enviados por los clientes
     *
     * @param mensaje el mensaje recibido del cliente
     * @return el mensaje que se retransmitirá a todos los clientes suscritos
     */
    @MessageMapping("/chat.sendMessage")  // Escucha mensajes enviados a /app/chat.sendMessage
    @SendTo("/topic/public")              // Retransmite a todos los suscritos a /topic/public
    public Message sendMessage(@Payload Message mensaje) {
        // Persistir el mensaje en la base de datos
        Message mensajePersistido = messageService.guardarMensaje(
                mensaje.getUsuario(),
                mensaje.getContenido()
        );

        System.out.println("Mensaje recibido y retransmitido: " + mensajePersistido);

        // Retornamos el mensaje con su ID y timestamp generados
        return mensajePersistido;
    }

    /**
     * Maneja cuando un usuario se une al chat
     *
     * @param mensaje mensaje de unión (contiene el nombre del usuario)
     * @param headerAccessor para acceder a la sesión WebSocket
     * @return mensaje de notificación que se envía a todos los clientes
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public Message addUser(@Payload Message mensaje, SimpMessageHeaderAccessor headerAccessor) {
        // Agregar el nombre de usuario a la sesión WebSocket
        headerAccessor.getSessionAttributes().put("username", mensaje.getUsuario());

        // Crear mensaje de notificación (no lo persistimos, es solo informativo)
        Message mensajeNotificacion = new Message();
        mensajeNotificacion.setUsuario("Sistema");
        mensajeNotificacion.setContenido(mensaje.getUsuario() + " se unió al chat!");

        System.out.println("Usuario agregado: " + mensaje.getUsuario());

        return mensajeNotificacion;
    }

    /*
     * FLUJO DEL MENSAJE:
     * 1. Cliente envía mensaje a /app/chat.sendMessage
     * 2. Este método lo procesa y guarda en BD
     * 3. Se retransmite a /topic/public
     * 4. Todos los clientes suscritos lo reciben inmediatamente
     *
     * Esto es mucho más eficiente que hacer polling HTTP cada segundo!
     */
}