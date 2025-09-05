package org.kodigo.websocket.controller;

import org.kodigo.websocket.model.Message;
import org.kodigo.websocket.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

/**
 * Controlador HTTP tradicional para servir las páginas del chat
 *
 * Combina HTTP tradicional para cargar páginas con WebSockets para comunicación en tiempo real
 */
@Controller
public class ChatController {

    @Autowired
    private MessageService messageService;

    /**
     * Página principal - formulario para ingresar nombre de usuario
     *
     * @return vista index.html
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * Página del chat - carga el historial de mensajes y habilita WebSocket
     *
     * @param usuario nombre del usuario (viene del formulario)
     * @param model modelo para pasar datos a la vista
     * @return vista chat.html
     */
    @GetMapping("/chat")
    public String chat(@RequestParam("usuario") String usuario, Model model) {
        // Validación básica del nombre de usuario
        if (usuario == null || usuario.trim().isEmpty()) {
            return "redirect:/?error=usuario_requerido";
        }

        // Cargar historial de mensajes desde la base de datos
        List<Message> historial = messageService.obtenerHistorialCompleto();

        // Pasar datos a la vista Thymeleaf
        model.addAttribute("usuario", usuario.trim());
        model.addAttribute("mensajes", historial);

        System.out.println("Usuario '" + usuario + "' accedió al chat. Historial: " + historial.size() + " mensajes");

        return "chat";
    }

    /*
     * ARQUITECTURA HÍBRIDA:
     * - HTTP: Para cargar páginas y obtener historial inicial
     * - WebSocket: Para comunicación en tiempo real
     *
     * Esto es una práctica común en aplicaciones modernas
     */
}