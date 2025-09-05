package org.kodigo.websocket.repository;


import org.kodigo.websocket.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository para operaciones CRUD con la entidad Message
 * Spring Data JPA genera automáticamente la implementación
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Obtiene todos los mensajes ordenados por timestamp ascendente
     * Esto asegura que el historial aparezca en orden cronológico
     *
     * @return Lista de mensajes ordenados por fecha
     */
    @Query("SELECT m FROM Message m ORDER BY m.timestamp ASC")
    List<Message> findAllOrderByTimestampAsc();

    /**
     * Obtiene los últimos N mensajes (útil para limitar el historial inicial)
     *
     * @param limit número máximo de mensajes a obtener
     * @return Lista de los mensajes más recientes
     */
    @Query("SELECT m FROM Message m ORDER BY m.timestamp DESC LIMIT :limit")
    List<Message> findTopNMessages(int limit);
}