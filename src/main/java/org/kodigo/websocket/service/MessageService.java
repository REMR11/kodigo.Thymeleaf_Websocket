package org.kodigo.websocket.service;

import org.kodigo.websocket.model.Message;
import org.kodigo.websocket.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Servicio que maneja la lógica de negocio para los mensajes
 * Separa la lógica de persistencia de los controladores
 */
@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    /**
     * Guarda un mensaje en la base de datos
     *
     * @param usuario nombre del usuario que envía el mensaje
     * @param contenido contenido del mensaje
     * @return el mensaje guardado con su ID generado
     */
    public Message guardarMensaje(String usuario, String contenido) {
        Message mensaje = new Message(usuario, contenido);
        Message mensajeGuardado = messageRepository.save(mensaje);

        System.out.println("Mensaje guardado: " + mensajeGuardado);
        return mensajeGuardado;
    }

    /**
     * Obtiene todo el historial de mensajes ordenado cronológicamente
     *
     * @return lista de todos los mensajes
     */
    public List<Message> obtenerHistorialCompleto() {
        return messageRepository.findAllOrderByTimestampAsc();
    }

    /**
     * Obtiene los últimos mensajes (útil para cargar historial inicial limitado)
     *
     * @param limite número máximo de mensajes a obtener
     * @return lista de mensajes recientes
     */
    public List<Message> obtenerMensajesRecientes(int limite) {
        List<Message> mensajes = messageRepository.findTopNMessages(limite);
        // Los devolvemos en orden cronológico (el query los trae en orden inverso)
        return mensajes.stream()
                .sorted((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
                .toList();
    }
}