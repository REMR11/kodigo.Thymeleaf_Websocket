package org.kodigo.websocket.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa un mensaje en el chat
 * Se persiste en la base de datos para mantener el historial
 */
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String usuario;

    @Column(nullable = false, length = 1000)
    private String contenido;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Constructor por defecto requerido por JPA
    public Message() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructor para crear mensajes fácilmente
    public Message(String usuario, String contenido) {
        this.usuario = usuario;
        this.contenido = contenido;
        this.timestamp = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", usuario='" + usuario + '\'' +
                ", contenido='" + contenido + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}