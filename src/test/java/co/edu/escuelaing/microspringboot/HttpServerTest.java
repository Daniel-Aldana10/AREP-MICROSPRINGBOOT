package co.edu.escuelaing.microspringboot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.util.Map;

public class HttpServerTest {
    
    @RestController
    public static class TestController {
        @GetMapping("/test")
        public static String testMethod() {
            return "test response";
        }
        
        @GetMapping("/greeting")
        public static String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
            return "Hello " + name;
        }
        
        @GetMapping("/params")
        public static String withParams(@RequestParam("id") String id, @RequestParam("type") String type) {
            return "ID: " + id + ", Type: " + type;
        }
        
        @GetMapping("/error")
        public static String errorMethod() {
            throw new RuntimeException("Test error");
        }
    }
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        // Limpiar servicios antes de cada test
        HttpServer.services.clear();
        HttpServer.requests.clear();
        
        // Restaurar directorio por defecto
        HttpServer.ROOT_DIRECTORY = "target/classes/webroot";
    }
    
    @AfterEach
    void tearDown() {
        // Limpiar después de cada test
        HttpServer.services.clear();
        HttpServer.requests.clear();
    }
    
    // ========== TESTS DE REGISTRO DE SERVICIOS ==========
    
    @Test
    void testGetMethodRegistration() {
        // Test que el método get registra servicios correctamente
        Method testMethod = null;
        try {
            testMethod = TestController.class.getMethod("testMethod");
        } catch (NoSuchMethodException e) {
            fail("Method should exist");
        }
        
        HttpServer.get("/test", testMethod);
        
        assertTrue(HttpServer.services.containsKey("/test"));
        assertEquals(testMethod, HttpServer.services.get("/test"));
    }
    
    @Test
    void testMultipleServiceRegistration() {
        // Test que se pueden registrar múltiples servicios
        Method testMethod = null;
        Method greetingMethod = null;
        
        try {
            testMethod = TestController.class.getMethod("testMethod");
            greetingMethod = TestController.class.getMethod("greeting", String.class);
        } catch (NoSuchMethodException e) {
            fail("Methods should exist");
        }
        
        HttpServer.get("/test", testMethod);
        HttpServer.get("/greeting", greetingMethod);
        
        assertEquals(2, HttpServer.services.size());
        assertTrue(HttpServer.services.containsKey("/test"));
        assertTrue(HttpServer.services.containsKey("/greeting"));
    }
    
    @Test
    void testServiceOverwrite() {
        // Test que un servicio puede sobrescribir a otro
        Method testMethod = null;
        Method greetingMethod = null;
        
        try {
            testMethod = TestController.class.getMethod("testMethod");
            greetingMethod = TestController.class.getMethod("greeting", String.class);
        } catch (NoSuchMethodException e) {
            fail("Methods should exist");
        }
        
        HttpServer.get("/test", testMethod);
        HttpServer.get("/test", greetingMethod); // Sobrescribir
        
        assertEquals(1, HttpServer.services.size());
        assertEquals(greetingMethod, HttpServer.services.get("/test"));
    }
    
    // ========== TESTS DE ARCHIVOS ESTÁTICOS ==========
    
    @Test
    void testStaticFilesPathSetting() {
        // Test que se puede cambiar el directorio de archivos estáticos
        String originalPath = HttpServer.ROOT_DIRECTORY;
        
        HttpServer.staticfiles("/custom");
        assertEquals("target/classes/custom", HttpServer.ROOT_DIRECTORY);
        
        // Restaurar el valor original
        HttpServer.ROOT_DIRECTORY = originalPath;
    }
    
    @Test
    void testStaticFilesPathWithLeadingSlash() {
        // Test que se maneja correctamente el slash inicial
        String originalPath = HttpServer.ROOT_DIRECTORY;
        
        HttpServer.staticfiles("/custom");
        assertEquals("target/classes/custom", HttpServer.ROOT_DIRECTORY);
        
        HttpServer.staticfiles("custom");
        // Restaurar el valor original
        HttpServer.ROOT_DIRECTORY = originalPath;
    }
    
    // ========== TESTS DE MIME TYPES ==========
    
    @Test
    void testGetTypeWithHtmlFile() {
        // Test que getType retorna el MIME type correcto para HTML
        String mimeType = HttpServer.getType(Path.of("test.html"));
        assertEquals("text/html; charset=utf-8", mimeType);
    }
    
    @Test
    void testGetTypeWithCssFile() {
        // Test que getType retorna el MIME type correcto para CSS
        String mimeType = HttpServer.getType(Path.of("style.css"));
        assertEquals("text/css; charset=utf-8", mimeType);
    }
    
    @Test
    void testGetTypeWithJsFile() {
        // Test que getType retorna el MIME type correcto para JavaScript
        String mimeType = HttpServer.getType(Path.of("script.js"));
        assertEquals("application/javascript; charset=utf-8", mimeType);
    }
    
    @Test
    void testGetTypeWithPngFile() {
        // Test que getType retorna el MIME type correcto para PNG
        String mimeType = HttpServer.getType(Path.of("image.png"));
        assertEquals("image/png", mimeType);
    }
    
    @Test
    void testGetTypeWithJpgFile() {
        // Test que getType retorna el MIME type correcto para JPG
        String mimeType = HttpServer.getType(Path.of("photo.jpg"));
        assertEquals("image/jpeg", mimeType);
    }
    
    @Test
    void testGetTypeWithJpegFile() {
        // Test que getType retorna el MIME type correcto para JPEG
        String mimeType = HttpServer.getType(Path.of("photo.jpeg"));
        assertEquals("image/jpeg", mimeType);
    }
    
    @Test
    void testGetTypeWithGifFile() {
        // Test que getType retorna el MIME type correcto para GIF
        String mimeType = HttpServer.getType(Path.of("animation.gif"));
        assertEquals("image/gif", mimeType);
    }
    
    @Test
    void testGetTypeWithSvgFile() {
        // Test que getType retorna el MIME type correcto para SVG
        String mimeType = HttpServer.getType(Path.of("icon.svg"));
        assertEquals("image/svg+xml", mimeType);
    }
    
    @Test
    void testGetTypeWithIcoFile() {
        // Test que getType retorna el MIME type correcto para ICO
        String mimeType = HttpServer.getType(Path.of("favicon.ico"));
        assertEquals("image/x-icon", mimeType);
    }
    
    @Test
    void testGetTypeWithJsonFile() {
        // Test que getType retorna el MIME type correcto para JSON
        String mimeType = HttpServer.getType(Path.of("data.json"));
        assertEquals("application/json; charset=utf-8", mimeType);
    }
    
    @Test
    void testGetTypeWithHtmFile() {
        // Test que getType retorna el MIME type correcto para HTM
        String mimeType = HttpServer.getType(Path.of("page.htm"));
        assertEquals("text/html; charset=utf-8", mimeType);
    }
    
    @Test
    void testGetTypeWithUnknownFile() {
        // Test que getType retorna el MIME type por defecto para archivos desconocidos
        String mimeType = HttpServer.getType(Path.of("unknown.xyz"));
        assertEquals("application/octet-stream", mimeType);
    }
    
    @Test
    void testGetTypeWithNullPath() {
        // Test que getType maneja paths null correctamente
        String mimeType = HttpServer.getType(null);
        assertEquals("application/octet-stream", mimeType);
    }
    
    @Test
    void testGetTypeWithPathWithoutExtension() {
        // Test que getType maneja paths sin extensión correctamente
        String mimeType = HttpServer.getType(Path.of("filename"));
        assertEquals("application/octet-stream", mimeType);
    }
    
    @Test
    void testGetTypeWithPathWithMultipleDots() {
        // Test que getType maneja paths con múltiples puntos correctamente
        String mimeType = HttpServer.getType(Path.of("file.backup.html"));
        assertEquals("text/html; charset=utf-8", mimeType);
    }
    
    @Test
    void testGetTypeCaseInsensitive() {
        // Test que getType es insensible a mayúsculas/minúsculas
        String mimeType1 = HttpServer.getType(Path.of("file.HTML"));
        String mimeType2 = HttpServer.getType(Path.of("file.html"));
        String mimeType3 = HttpServer.getType(Path.of("file.Html"));
        
        assertEquals("text/html; charset=utf-8", mimeType1);
        assertEquals("text/html; charset=utf-8", mimeType2);
        assertEquals("text/html; charset=utf-8", mimeType3);
    }
    
    // ========== TESTS DE GREETING SERVICE ==========
    
    @Test
    void testGreetingServiceWithValidName() throws URISyntaxException {
        // Test que greetingService funciona con un nombre válido
        URI uri = new URI("/test?name=John");
        String response = HttpServer.greetingService(uri, false);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Hello John"));
        assertFalse(response.contains("today's date is"));
    }
    
    @Test
    void testGreetingServiceWithTime() throws URISyntaxException {
        // Test que greetingService incluye la fecha cuando time=true
        URI uri = new URI("/test?name=John");
        String response = HttpServer.greetingService(uri, true);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Hello John"));
        assertTrue(response.contains("today's date is"));
    }
    
    @Test
    void testGreetingServiceWithoutName() throws URISyntaxException {
        // Test que greetingService maneja requests sin nombre correctamente
        URI uri = new URI("/test");
        String response = HttpServer.greetingService(uri, false);
        
        assertTrue(response.contains("HTTP/1.1 400 Bad Request"));
        assertTrue(response.contains("Name not found"));
    }
    
    @Test
    void testGreetingServiceWithEmptyQuery() throws URISyntaxException {
        // Test que greetingService maneja queries vacías correctamente
        URI uri = new URI("/test?");
        String response = HttpServer.greetingService(uri, false);
        
        assertTrue(response.contains("HTTP/1.1 400 Bad Request"));
        assertTrue(response.contains("Name not found"));
    }
    
    @Test
    void testGreetingServiceWithComplexQuery() throws URISyntaxException {
        // Test que greetingService maneja queries complejas correctamente
        URI uri = new URI("/test?name=John&extra=value");
        String response = HttpServer.greetingService(uri, false);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Hello John"));
    }
    
    @Test
    void testGreetingServiceWithSpecialCharacters() throws URISyntaxException {
        // Test que greetingService maneja caracteres especiales
        URI uri = new URI("/test?name=José");
        String response = HttpServer.greetingService(uri, false);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Hello José"));
    }
    
    @Test
    void testGreetingServiceWithURLEncoding() throws URISyntaxException {
        // Test que greetingService maneja codificación de URL
        URI uri = new URI("/test?name=John%20Doe");
        String response = HttpServer.greetingService(uri, false);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));

    }
    
    // ========== TESTS DE CARGA DE COMPONENTES ==========
    
    @Test
    void testLoadComponentWithRestController() {
        // Test que loadComponent carga controladores con @RestController
        HttpServer.loadComponent(TestController.class);
        
        assertTrue(HttpServer.services.containsKey("/test"));
        assertTrue(HttpServer.services.containsKey("/greeting"));
        assertTrue(HttpServer.services.containsKey("/params"));
        assertTrue(HttpServer.services.containsKey("/error"));
    }
    
    @Test
    void testLoadComponentWithoutRestController() {
        // Test que loadComponent ignora clases sin @RestController
        class RegularClass {
            @GetMapping("/test")
            public static String test() { return "test"; }
        }
        
        HttpServer.loadComponent(RegularClass.class);
        
        // No debería haber servicios registrados
        assertTrue(HttpServer.services.isEmpty());
    }
    
    @Test
    void testLoadComponentWithMethodsWithoutGetMapping() {
        // Test que loadComponent ignora métodos sin @GetMapping
        @RestController
        class PartialController {
            @GetMapping("/test")
            public static String test() { return "test"; }
            
            public static String noMapping() { return "no mapping"; }
        }
        
        HttpServer.loadComponent(PartialController.class);
        
        // Solo debería registrar el método con @GetMapping
        assertEquals(1, HttpServer.services.size());
        assertTrue(HttpServer.services.containsKey("/test"));
    }

    // ========== TESTS DE INVOCACIÓN DE SERVICIOS ==========
    
    @Test
    void testInvokeServiceWithValidEndpoint() throws URISyntaxException {
        // Test que invokeService funciona con un endpoint válido
        HttpServer.loadComponent(TestController.class);
        
        URI uri = new URI("/test");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("test response"));
    }
    
    @Test
    void testInvokeServiceWithParameters() throws URISyntaxException {
        // Test que invokeService funciona con parámetros
        HttpServer.loadComponent(TestController.class);
        
        URI uri = new URI("/greeting?name=John");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Hello John"));
    }
    
    @Test
    void testInvokeServiceWithMultipleParameters() throws URISyntaxException {
        // Test que invokeService funciona con múltiples parámetros
        HttpServer.loadComponent(TestController.class);
        
        URI uri = new URI("/params?id=123&type=admin");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("ID: 123"));
        assertTrue(response.contains("Type: admin"));
    }
    
    @Test
    void testInvokeServiceWithNonExistentEndpoint() throws URISyntaxException {
        // Test que invokeService retorna 404 para endpoints inexistentes
        URI uri = new URI("/nonexistent");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 404 Not Found"));
        assertTrue(response.contains("Service not found"));
    }
    
    @Test
    void testInvokeServiceWithMethodException() throws URISyntaxException {
        // Test que invokeService maneja excepciones de métodos
        HttpServer.loadComponent(TestController.class);
        
        URI uri = new URI("/error");
        String response = HttpServer.invokeService(uri);
        
        assertTrue(response.contains("HTTP/1.1 500 Internal Server Error"));
        assertTrue(response.contains("Internal Server Error"));
    }
    

    
    @Test
    void testServiceRegistrationWithNullMethod() {
        // Test que el registro con method null no causa errores
        assertDoesNotThrow(() -> {
            HttpServer.get("/test", null);
        });
    }
    
    @Test
    void testStaticFilesWithNullPath() {
        // Test que staticfiles con path null no causa errores
        assertDoesNotThrow(() -> {
            HttpServer.staticfiles(null);
        });
    }
    
    @Test
    void testGetTypeWithEmptyPath() {
        // Test que getType con path vacío funciona correctamente
        String mimeType = HttpServer.getType(Path.of(""));
        assertEquals("application/octet-stream", mimeType);
    }
    
    @Test
    void testGreetingServiceWithMalformedQuery() throws URISyntaxException {
        // Test que greetingService maneja queries malformadas
        URI uri = new URI("/test?name=John&invalid");
        String response = HttpServer.greetingService(uri, false);
        
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Hello John"));
    }
}
