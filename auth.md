# Secure API Gateway with Spring Cloud Gateway

## 1. **Request Flow**
### **Login Request**
1. UI sends `username` and `password` to **Spring Cloud Gateway**.
2. Gateway **routes** the request to the **authentication/authorization service**.
3. The authentication service **validates credentials** and returns an **access token** (e.g., JWT or opaque token).

### **Subsequent API Requests**
1. UI includes the **access token** in the `Authorization` header.
2. The Gateway’s **authentication filter** forwards the request to the authentication service for token validation.
3. If **valid**, it forwards the request to the **account management service**.

---

## 2. **Spring Cloud Gateway Configuration (`application.yml`)**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: http://auth-service
          predicates:
            - Path=/auth/**
            
        - id: account-management-public
          uri: http://account-management-service
          predicates:
            - Path=/account/public/** # Define public endpoints
          filters: [] # No authentication filter

        - id: account-management-secure
          uri: http://account-management-service
          predicates:
            - Path=/account/**
          filters:
            - AuthenticationFilter # Apply authentication only for secured endpoints
```

---

## 3. **Authentication Service (Spring Boot)**
### **Login API**
```java
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AuthController(JwtService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            User user = (User) authentication.getPrincipal();
            String token = jwtService.generateToken(user);

            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        }
    }
}
```

---

## 4. **JWT Generation Service**
```java
@Service
public class JwtService {
    private final String SECRET_KEY = "your_secret_key";

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour expiry
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
```

---

## 5. **Authentication Filter in Spring Cloud Gateway**
```java
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final WebClient webClient;
    private final List<String> publicEndpoints = List.of("/account/public/info", "/account/public/status");

    public AuthenticationFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://auth-service").build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Bypass authentication for public endpoints
        if (publicEndpoints.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        // Extract Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorizedResponse(exchange);
        }

        String token = authHeader.substring(7);

        return webClient.get()
                .uri("/auth/validate?token=" + token)
                .retrieve()
                .bodyToMono(UserDetails.class)
                .flatMap(userDetails -> {
                    if (userDetails.hasPermission("ACCESS_ACCOUNT_MANAGEMENT")) {
                        return chain.filter(exchange);
                    } else {
                        return forbiddenResponse(exchange);
                    }
                })
                .onErrorResume(e -> unauthorizedResponse(exchange));
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private Mono<Void> forbiddenResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
```

---

## 6. **Security Considerations**
- Use **HTTPS** to prevent token exposure.
- Store JWT **securely in HTTP-only cookies** instead of local storage.
- Consider using **OAuth2 with an Authorization Server** if your system scales.

---

## 7. **Saving This Document for Reference**
### **Option 1: Bookmark This Page**
- Press `Ctrl + D` (Windows) or `Cmd + D` (Mac) to bookmark this chat.

### **Option 2: Copy & Save Locally**
- Copy this Markdown and paste it into a **Google Doc, Notion, or a `.md` file**.

### **Option 3: Use ChatGPT History**
- Check your **chat history** if available.

---

This document provides a structured guide for securing APIs with **Spring Cloud Gateway, JWT authentication, and public/private endpoints**. 🚀
