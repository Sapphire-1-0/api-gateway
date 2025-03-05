package com.brihaspathee.sapphire.filter;

import com.brihaspathee.sapphire.dto.auth.AuthorizationRequest;
import com.brihaspathee.sapphire.dto.auth.UserDto;
import com.brihaspathee.sapphire.web.response.SapphireAPIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Created in Intellij IDEA
 * User: Balaji Varadharajan
 * Date: 21, February 2025
 * Time: 1:33 PM
 * Project: sapphire
 * Package Name: com.brihaspathee.sapphire.filter
 * To change this template use File | Settings | File and Code Template
 */
@Slf4j
@Component
@Order(-1)
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    /**
     * An instance of {@code WebClient} used for making HTTP requests.
     * This field is immutable and is initialized through dependency injection.
     * It is utilized within the {@code AuthenticationFilter} class for processing
     * and handling web requests or responses in the global filter pipeline.
     */
    private final WebClient webClient;

    /**
     * Represents the name of the HTTP header used to identify the user ID
     * in the application. The value is injected from the application
     * properties using the key "application.user-info.user-id".
     */
    @Value("${application.user-info.user-id}")
    private String userIdHeader;

    /**
     * Represents the header key used to retrieve the username information
     * from the application's configuration properties.
     *
     * The value of this variable is injected from the 'application.user-info.username'
     * property defined in the application's configuration files.
     */
    @Value("${application.user-info.username}")
    private String usernameHeader;

    /**
     * Represents the header value associated with the service ID configuration.
     * This value is injected from the application properties using the
     * configuration key "application.user-info.service-id".
     */
    @Value("${application.user-info.service-id}")
    private String serviceIdHeader;

    /**
     * Represents the header key used to retrieve the account type information
     * from the incoming HTTP request in the `UserContextInterceptor` class.
     * This value is injected through the application properties using the
     * Spring @Value annotation, mapped to the property key
     * `application.user-info.account-type`.
     *
     * This account type is utilized within the interceptor to extract
     * and include the user's account type in the user context for request handling.
     */
    @Value("${application.user-info.account-type}")
    private String accountTypeHeader;

    /**
     * Constructs an instance of the AuthenticationFilter and configures the WebClient.
     */
    public AuthenticationFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClient = webClientBuilder.baseUrl("http://localhost:7095/api/v1/sapphire/auth").build();
    }

    /**
     * Applies the specified configuration to create a GatewayFilter.
     * This method returns a GatewayFilter that intercepts HTTP requests
     * and delegates the processing to the `authenticate` method.
     *
     * @param config the configuration object for the GatewayFilter. It can contain
     *               any custom settings or metadata needed to apply the filter.
     * @return a GatewayFilter that performs authentication processing for incoming HTTP requests.
     */
    @Override
    public GatewayFilter apply(Config config) {
        log.info("AuthenticationFilter Configured");
        return (this::authenticate);
    }

    /**
     * Authenticates an incoming HTTP request by validating its authorization header.
     * This method checks for the presence of a Bearer token in the Authorization header,
     * validates the token with an external authentication service, and either allows
     * or denies access based on the response.
     *
     * @param exchange the current server web exchange, which represents the web request
     *                 and response. It provides access to the HTTP request details.
     * @param chain    the gateway filter chain, which is used to continue processing
     *                 the request if authentication is successful.
     * @return a {@code Mono<Void>} that completes when the authentication process is finalized.
     *         If authentication fails, it sends an appropriate error response to the client.
     */
    private Mono<Void> authenticate(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Authenticating request...");
        ServerHttpRequest request = exchange.getRequest();
        /*
            This will give the full url that the user tried to access
            i.e. http://localhost:7092/api/v1/sapphire/member
         */
        String fullURL = request.getURI().toString();
        /*
            if the user accessed the URL - http://localhost:7092/api/v1/sapphire/member
            path will be - /api/v1/sapphire/member
         */
        String path = request.getURI().getPath();
        /*
            This gives any query parameters
            if the user accessed the URL - http://localhost:7092/api/v1/sapphire/member?id=123&type=premium&status=active
            query will be - id=123&type=premium&status=active
         */
        String query = request.getURI().getQuery();

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("No Authorization header found");
            return unauthorizedResponse(exchange);
        }
        String token = authHeader.substring(7);
        log.info("Token: {}", token);
        log.info("Full URL: {}", fullURL);
        log.info("Path: {}", path);
        log.info("About to send to auth service...");
        AuthorizationRequest authorizationRequest = AuthorizationRequest.builder()
                .resourceUri(path)
                .build();
        return webClient.post()
                .uri("/resource/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(BodyInserters.fromValue(authorizationRequest))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SapphireAPIResponse<UserDto>>() {
                })
                .flatMap(response -> {
                    UserDto userDto = response.getResponse();
                    if (userDto != null) {
                        /*
                            - **Enrichment**: Injects additional information (e.g., `userId`, `username`, etc.) into
                            the request headers based on some external data source (e.g., `userDto` object).
                            - **Immutability Handling**: Both `ServerHttpRequest` and `ServerWebExchange` are
                            immutable, so the `mutate()` methods are used to create modified versions.
                            - **Reactive Processing**: Ensures the modifications stay compatible with the non-blocking,
                            reactive nature of the web framework.
                         */
                        ServerHttpRequest updatedRequest = exchange.getRequest().mutate()
                                .header(userIdHeader, String.valueOf(userDto.getUserId()))
                                .header(usernameHeader, userDto.getUsername())
                                .header(serviceIdHeader, userDto.getServiceId())
                                .header(accountTypeHeader, userDto.getAccountType())
                                .build();
                        return chain.filter(exchange.mutate().request(updatedRequest).build());
                    } else {
                        return forbiddenResponse(exchange);
                    }
                }).onErrorResume(e -> {
                    log.info("Unauthorized response: {}", e.getMessage());
                    return unauthorizedResponse(exchange);
                });
    }

    /**
     * Sends a response with the HTTP status code 401 Unauthorized to the client.
     * This method is used to indicate that the client is not authenticated and
     * must provide valid authentication credentials to access the requested resource.
     *
     * @param exchange the current server web exchange, which represents the web request
     *                 and response. It is used to set the response status and complete the response.
     * @return a {@code Mono<Void>} that completes when the response is finalized.
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    /**
     * Sends a response with the HTTP status code 403 Forbidden to the client.
     * This method is used to indicate that the client does not have permission
     * to access the requested resource.
     *
     * @param exchange the current server web exchange, which represents the web request
     *                 and response. It is used to set the response status and complete the response.
     * @return a {@code Mono<Void>} that completes when the response is finalized.
     */
    private Mono<Void> forbiddenResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }



    public static class Config {
        public Config() {}
        // You can add custom config fields here if needed
    }


}
