package co.edu.escuelaing.microspringboot;

/**
 * Controlador de usuarios que demuestra el auto-descubrimiento de componentes.
 * Este controlador será descubierto automáticamente por ComponentScanner.
 */
@RestController
public class UserController {

    @GetMapping("/users")
    public static String getAllUsers() {
        return "Lista de usuarios: Juan, María, Carlos, Ana";
    }

    @GetMapping("/users/profile")
    public static String getUserProfile(@RequestParam(value = "id", defaultValue = "1") String id) {
        return "Perfil del usuario con ID: " + id;
    }

    @GetMapping("/users/search")
    public static String searchUsers(@RequestParam(value = "name", defaultValue = "") String name,
                                   @RequestParam(value = "age", defaultValue = "0") String age) {
        if (name.isEmpty() && age.equals("0")) {
            return "Búsqueda de usuarios: Sin filtros aplicados";
        }
        
        StringBuilder result = new StringBuilder("Búsqueda de usuarios:");
        if (!name.isEmpty()) {
            result.append(" Nombre: ").append(name);
        }
        if (!age.equals("0")) {
            result.append(" Edad: ").append(age);
        }
        
        return result.toString();
    }

    @GetMapping("/users/count")
    public static String getUserCount() {
        return "Total de usuarios en el sistema: 4";
    }
}

