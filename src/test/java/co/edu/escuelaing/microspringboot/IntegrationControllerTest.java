package co.edu.escuelaing.microspringboot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class IntegrationControllerTest {
    

    @BeforeEach
    void setUp() {
        // Limpiar estado antes de cada test
        HttpServer.services.clear();
        HttpServer.requests.clear();
        
        // Cargar el controlador de integración
        HttpServer.loadComponent(IntegrationController.class);
    }
    
    @AfterEach
    void tearDown() {
        // Limpiar después de cada test
        HttpServer.services.clear();
        HttpServer.requests.clear();
    }
    
    @Test
    void testControllerLoading() {
        // Test que todos los endpoints del controlador se cargan correctamente
        assertTrue(HttpServer.services.containsKey("/hello"));
        assertTrue(HttpServer.services.containsKey("/greeting"));
        assertTrue(HttpServer.services.containsKey("/user"));
        assertTrue(HttpServer.services.containsKey("/math"));
        
        assertEquals(4, HttpServer.services.size());
    }
    
    @Test
    void testHelloEndpoint() throws URISyntaxException {
        // Test que el endpoint /hello funciona correctamente
        URI uri = new URI("/hello");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Hello World!"));
    }
    
    @Test
    void testGreetingEndpointWithName() throws URISyntaxException {
        // Test que el endpoint /greeting responde con un nombre específico
        URI uri = new URI("/greeting?name=John");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Hello John"));
    }
    
    @Test
    void testGreetingEndpointWithDefaultValue() throws URISyntaxException {
        // Test que el endpoint /greeting usa el valor por defecto
        URI uri = new URI("/greeting");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Hello World"));
    }
    
    @Test
    void testGreetingEndpointWithEmptyName() throws URISyntaxException {
        // Test que el endpoint /greeting maneja nombres vacíos
        URI uri = new URI("/greeting?name=");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Hello "));
    }
    
    @Test
    void testGreetingEndpointWithSpecialCharacters() throws URISyntaxException {
        // Test que el endpoint /greeting maneja caracteres especiales
        URI uri = new URI("/greeting?name=José");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Hello José"));
    }
    
    @Test
    void testUserEndpointWithValidParameters() throws URISyntaxException {
        // Test que el endpoint /user funciona con parámetros válidos
        URI uri = new URI("/user?id=123&role=admin");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("User ID: 123"));
        assertTrue(response.contains("Role: admin"));
    }
    
    @Test
    void testUserEndpointWithDifferentValues() throws URISyntaxException {
        // Test que el endpoint /user funciona con diferentes valores
        URI uri = new URI("/user?id=456&role=user");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("User ID: 456"));
        assertTrue(response.contains("Role: user"));
    }
    
    @Test
    void testUserEndpointWithEmptyParameters() throws URISyntaxException {
        // Test que el endpoint /user maneja parámetros vacíos
        URI uri = new URI("/user?id=&role=");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("User ID: "));
        assertTrue(response.contains("Role: "));
    }
    
    @Test
    void testMathEndpointWithValidNumbers() throws URISyntaxException {
        // Test que el endpoint /math funciona con números válidos
        URI uri = new URI("/math?a=5&b=3");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Result: 8"));
    }
    
    @Test
    void testMathEndpointWithDefaultValues() throws URISyntaxException {
        // Test que el endpoint /math usa valores por defecto
        URI uri = new URI("/math");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Result: 0"));
    }
    
    @Test
    void testMathEndpointWithPartialParameters() throws URISyntaxException {
        // Test que el endpoint /math maneja parámetros parciales
        URI uri = new URI("/math?a=10");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Result: 10"));
    }
    
    @Test
    void testMathEndpointWithNegativeNumbers() throws URISyntaxException {
        // Test que el endpoint /math funciona con números negativos
        URI uri = new URI("/math?a=-5&b=3");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Result: -2"));
    }
    
    @Test
    void testMathEndpointWithLargeNumbers() throws URISyntaxException {
        // Test que el endpoint /math funciona con números grandes
        URI uri = new URI("/math?a=1000&b=2000");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Result: 3000"));
    }
    
    @Test
    void testMathEndpointWithInvalidNumbers() throws URISyntaxException {
        // Test que el endpoint /math maneja números inválidos
        URI uri = new URI("/math?a=abc&b=xyz");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Invalid numbers"));
    }
    
    @Test
    void testMathEndpointWithMixedValidInvalid() throws URISyntaxException {
        // Test que el endpoint /math maneja mezcla de números válidos e inválidos
        URI uri = new URI("/math?a=5&b=abc");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Invalid numbers"));
    }
    
    @Test
    void testRequestParameterRegistration() {
        // Test que los parámetros de request se registran correctamente
        assertTrue(HttpServer.requests.containsKey("/greeting"));
        assertTrue(HttpServer.requests.containsKey("/user"));
        assertTrue(HttpServer.requests.containsKey("/math"));
        
        // Verificar que /hello no tiene parámetros registrados
        assertFalse(HttpServer.requests.containsKey("/hello"));
    }
    
    @Test
    void testControllerAnnotations() {
        // Test que las anotaciones están correctamente configuradas
        assertTrue(IntegrationController.class.isAnnotationPresent(RestController.class));
        
        // Verificar métodos con @GetMapping
        Method[] methods = IntegrationController.class.getDeclaredMethods();
        
        boolean hasGetMapping = false;
        for (Method method : methods) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                hasGetMapping = true;
                break;
            }
        }
        
        assertTrue(hasGetMapping, "IntegrationController should have @GetMapping methods");
    }
    
    @Test
    void testGreetingParameterAnnotation() {
        // Test que el parámetro de greeting tiene @RequestParam
        Method greetingMethod = HttpServer.services.get("/greeting");
        Parameter[] parameters = greetingMethod.getParameters();
        
        assertTrue(parameters.length > 0);
        assertTrue(parameters[0].isAnnotationPresent(RequestParam.class));
        
        RequestParam annotation = parameters[0].getAnnotation(RequestParam.class);
        assertEquals("name", annotation.value());
        assertEquals("World", annotation.defaultValue());
    }
    
    @Test
    void testUserParametersAnnotation() {
        // Test que los parámetros de user tienen @RequestParam
        Method userMethod = HttpServer.services.get("/user");
        Parameter[] parameters = userMethod.getParameters();
        
        assertEquals(2, parameters.length);
        
        // Verificar primer parámetro (id)
        assertTrue(parameters[0].isAnnotationPresent(RequestParam.class));
        RequestParam idAnnotation = parameters[0].getAnnotation(RequestParam.class);
        assertEquals("id", idAnnotation.value());
        
        // Verificar segundo parámetro (role)
        assertTrue(parameters[1].isAnnotationPresent(RequestParam.class));
        RequestParam roleAnnotation = parameters[1].getAnnotation(RequestParam.class);
        assertEquals("role", roleAnnotation.value());
    }
    
    @Test
    void testMathParametersAnnotation() {
        // Test que los parámetros de math tienen @RequestParam
        Method mathMethod = HttpServer.services.get("/math");
        Parameter[] parameters = mathMethod.getParameters();
        
        assertEquals(2, parameters.length);
        
        // Verificar primer parámetro (a)
        assertTrue(parameters[0].isAnnotationPresent(RequestParam.class));
        RequestParam aAnnotation = parameters[0].getAnnotation(RequestParam.class);
        assertEquals("a", aAnnotation.value());
        assertEquals("0", aAnnotation.defaultValue());
        
        // Verificar segundo parámetro (b)
        assertTrue(parameters[1].isAnnotationPresent(RequestParam.class));
        RequestParam bAnnotation = parameters[1].getAnnotation(RequestParam.class);
        assertEquals("b", bAnnotation.value());
        assertEquals("0", bAnnotation.defaultValue());
    }
    
    @Test
    void testNonExistentEndpoint() throws URISyntaxException {
        // Test que endpoints inexistentes retornan 404
        URI uri = new URI("/nonexistent");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 404 Not Found"));
        assertTrue(response.contains("Service not found"));
    }
    
    @Test
    void testComplexQueryString() throws URISyntaxException {
        // Test que el parsing de query strings complejas funciona
        URI uri = new URI("/user?id=123&role=admin&extra=value");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("User ID: 123"));
        assertTrue(response.contains("Role: admin"));
    }
    
   
}
