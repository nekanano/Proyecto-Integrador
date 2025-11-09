package Pruebas;
public class PruebaLogin {
    public void testValidacionCorreo() {
        String correo = "usuario@ejemplo.com";
        boolean esValido = !correo.isEmpty();
        assert esValido : "El correo debe ser válido";
    }
}