/**
 * Cliente WebSocket para el chat en tiempo real
 *
 * Este archivo maneja:
 * 1. Conexión WebSocket con el servidor
 * 2. Envío y recepción de mensajes
 * 3. Actualización de la interfaz en tiempo real
 * 4. Manejo de estados de conexión
 */

// Variables globales
let stompClient = null;
let connected = false;

// Elementos del DOM
const messageArea = document.getElementById('messageArea');
const messageForm = document.getElementById('messageForm');
const messageInput = document.getElementById('messageInput');
const sendButton = document.getElementById('sendButton');
const connectionStatus = document.getElementById('connectionStatus');

/**
 * Inicializa la aplicación cuando se carga la página
 */
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Iniciando cliente WebSocket...');
    console.log('👤 Usuario actual:', window.currentUser);

    // Conectar a WebSocket
    connect();

    // Configurar formulario de envío
    setupMessageForm();

    // Scroll inicial al final del historial
    scrollToBottom();

    // Focus automático en el input de mensaje
    messageInput.focus();
});

/**
 * Establece la conexión WebSocket con el servidor
 */
function connect() {
    updateConnectionStatus('connecting', 'Conectando...');

    // Crear conexión SockJS (fallback para navegadores sin WebSocket nativo)
    const socket = new SockJS('/ws');

    // Crear cliente STOMP sobre la conexión SockJS
    stompClient = Stomp.over(socket);

    // Configuración del cliente STOMP
    stompClient.connect({}, onConnected, onError);

    console.log('🔌 Intentando conectar a WebSocket...');
}

/**
 * Callback ejecutado cuando la conexión WebSocket es exitosa
 */
function onConnected() {
    console.log('✅ Conectado a WebSocket');
    connected = true;
    updateConnectionStatus('connected', 'Conectado');

    // Suscribirse al canal público de mensajes
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Notificar al servidor que el usuario se unió
    const joinMessage = {
        usuario: window.currentUser,
        contenido: '',
        tipo: 'JOIN'
    };

    stompClient.send("/app/chat.addUser", {}, JSON.stringify(joinMessage));

    // Habilitar el formulario de envío
    enableMessageForm();
}

/**
 * Callback ejecutado cuando hay un error de conexión
 */
function onError(error) {
    console.error('❌ Error de conexión WebSocket:', error);
    connected = false;
    updateConnectionStatus('disconnected', 'Desconectado');

    // Deshabilitar el formulario
    disableMessageForm();

    // Intentar reconectar después de 5 segundos
    setTimeout(() => {
        console.log('🔄 Intentando reconectar...');
        connect();
    }, 5000);
}

/**
 * Maneja los mensajes recibidos del servidor
 */
function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    console.log('📨 Mensaje recibido:', message);

    // Agregar mensaje al área de chat
    displayMessage(message);

    // Scroll automático al final
    scrollToBottom();
}

/**
 * Configura el formulario de envío de mensajes
 */
function setupMessageForm() {
    messageForm.addEventListener('submit', function(event) {
        event.preventDefault();
        sendMessage();
    });

    // Enviar mensaje con Enter (pero no con Shift+Enter)
    messageInput.addEventListener('keypress', function(event) {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            sendMessage();
        }
    });
}

/**
 * Envía un mensaje a través de WebSocket
 */
function sendMessage() {
    const messageContent = messageInput.value.trim();

    // Validar que el mensaje no esté vacío
    if (messageContent === '') {
        return;
    }

    // Verificar que estemos conectados
    if (!connected || !stompClient) {
        alert('❌ No hay conexión con el servidor. Intenta recargar la página.');
        return;
    }

    // Crear objeto mensaje
    const message = {
        usuario: window.currentUser,
        contenido: messageContent,
        tipo: 'CHAT'
    };

    // Enviar mensaje al servidor
    stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(message));

    // Limpiar el input y mantener el focus
    messageInput.value = '';
    messageInput.focus();

    console.log('📤 Mensaje enviado:', message);
}

/**
 * Muestra un mensaje en el área de chat
 */
function displayMessage(message) {
    const messageContainer = document.createElement('div');
    messageContainer.className = 'message-container';

    const messageDiv = document.createElement('div');
    messageDiv.className = 'message';

    // Determinar el tipo de mensaje y aplicar estilos apropiados
    if (message.usuario === 'Sistema') {
        messageDiv.classList.add('system-message');
        messageDiv.innerHTML = `
            <div class="message-content">${escapeHtml(message.contenido)}</div>
        `;
    } else {
        // Mensaje normal de usuario
        const isOwnMessage = message.usuario === window.currentUser;
        messageDiv.classList.add(isOwnMessage ? 'own-message' : 'other-message');

        // Formatear timestamp
        const timestamp = message.timestamp ? formatTimestamp(message.timestamp) : 'Ahora';

        messageDiv.innerHTML = `
            <div class="message-header">
                <span class="username">${escapeHtml(message.usuario)}</span>
                <span class="timestamp">${timestamp}</span>
            </div>
            <div class="message-content">${escapeHtml(message.contenido)}</div>
        `;
    }

    messageContainer.appendChild(messageDiv);
    messageArea.appendChild(messageContainer);
}

/**
 * Formatea un timestamp para mostrar en el chat
 */
function formatTimestamp(timestamp) {
    // Si timestamp es un array (formato de LocalDateTime de Java)
    if (Array.isArray(timestamp)) {
        // [year, month, day, hour, minute, second, nano]
        const [year, month, day, hour, minute] = timestamp;
        const date = new Date(year, month - 1, day, hour, minute);
        return date.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
    }

    // Si es un string ISO o timestamp normal
    const date = new Date(timestamp);
    return date.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
}

/**
 * Escapa caracteres HTML para prevenir XSS
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * Scroll automático al final del área de mensajes
 */
function scrollToBottom() {
    messageArea.scrollTop = messageArea.scrollHeight;
}

/**
 * Actualiza el estado visual de la conexión
 */
function updateConnectionStatus(status, text) {
    connectionStatus.className = `connection-status status-${status}`;
    connectionStatus.querySelector('.status-text').textContent = text;
}

/**
 * Habilita el formulario de envío de mensajes
 */
function enableMessageForm() {
    messageInput.disabled = false;
    sendButton.disabled = false;
    messageInput.placeholder = 'Escribe tu mensaje...';
}

/**
 * Deshabilita el formulario de envío de mensajes
 */
function disableMessageForm() {
    messageInput.disabled = true;
    sendButton.disabled = true;
    messageInput.placeholder = 'Reconectando...';
}

/**
 * Manejo de desconexión cuando se cierra la ventana
 */
window.addEventListener('beforeunload', function() {
    if (stompClient && connected) {
        stompClient.disconnect();
        console.log('👋 Desconectado del WebSocket');
    }
});

/*
 * FLUJO DE COMUNICACIÓN:
 *
 * 1. Página carga → connect() → Conecta a /ws
 * 2. onConnected() → Se suscribe a /topic/public
 * 3. Usuario envía mensaje → sendMessage() → Envía a /app/chat.sendMessage
 * 4. Servidor procesa y retransmite → onMessageReceived() → Muestra mensaje
 *
 * VENTAJAS VS HTTP POLLING:
 * - Latencia baja: Los mensajes llegan inmediatamente
 * - Menor carga del servidor: No hay requests constantes
 * - Menor uso de ancho de banda: Conexión persistente
 * - Mejor experiencia de usuario: Comunicación fluida
 */