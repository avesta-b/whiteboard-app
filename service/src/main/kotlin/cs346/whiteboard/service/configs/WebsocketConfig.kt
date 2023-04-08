package cs346.whiteboard.service.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean





@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        // TODO, verify username on handshake
        registry.addEndpoint("/ws")
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.setApplicationDestinationPrefixes("/app")
        registry.enableSimpleBroker("/topic")
    }

    override fun configureWebSocketTransport(registry: WebSocketTransportRegistration) {
        registry.setMessageSizeLimit(1024 * 1024 * 20) // 20MB
        registry.setSendBufferSizeLimit(1024 * 1024 * 20) // 20MB
        registry.setSendTimeLimit(1000 * 10) // 10 seconds
    }

    @Bean
    fun createServletServerContainerFactoryBean(): ServletServerContainerFactoryBean? {
        // kotlin style APIs produce build error
        val container = ServletServerContainerFactoryBean()
        container.setMaxTextMessageBufferSize(1024 * 1024 * 20);
        container.setMaxSessionIdleTimeout(1000 * 20);
        container.setAsyncSendTimeout(1000 * 20)
        container.setMaxBinaryMessageBufferSize(1024 * 1024 * 20);
        return container
    }
}
