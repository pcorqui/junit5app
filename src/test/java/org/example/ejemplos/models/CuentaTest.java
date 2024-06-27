package org.example.ejemplos.models;

import org.example.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.sound.midi.Soundbank;
import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

class CuentaTest {
    Cuenta cuenta;

    //@AfterAll  y @BeforeAll no se pueden usar en inner class
    @BeforeEach
    void initMetodoTest(TestInfo testInfo,TestReporter testReporter){
        this.cuenta = new Cuenta("Andres",new BigDecimal("1000.12345"));
        System.out.println("iniciando el metodo");
        System.out.println("ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName()
                + " con las etiquetas " + testInfo.getTags());
    }

    @AfterEach
    void tearDown() {
        System.out.println("finalizando el metodo de prueba");
    }

    @Tag("nomina")
    @Nested //sirven para anidar y categorizar pero no hay una regla para hacerlo
    @DisplayName("cuenta anidada")
    class CuentaTestNombreSaldo{
        @Test
        void testNombreCuenta(TestInfo testInfo, TestReporter testReporter) {

            Cuenta cuenta = new Cuenta("andres", new BigDecimal("234.678"));
            cuenta.setPersona("Andres");
            String esperado = "Andres";
            String real = cuenta.getPersona();
            Assertions.assertEquals(esperado, real);
        }

        @Test
        void testSaldoCuenta() {
            Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        }

        @Test
        void testSaldoCuentados() {
            Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Tag("cuenta")//sirve para separar los test por etiquetas
        @Test
        @DisplayName("Testeando referencias que sean iguales")
        void testReferenciaCuenta() {
            Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("8900.9997"));
            Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9997"));

            assertEquals(cuenta2, cuenta);
        }

        @Test
        void testDebitoCuenta() {
            Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("1000.12345", cuenta.getSaldo().toPlainString());
        }

        @Test
        void testCreditoCuenta() {
            Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1000.12345", cuenta.getSaldo().toPlainString());
        }


        //manejo de excepciones con unit pruebas unitarias
        @Test
        void testDineroInsuficienteExceptionCuenta() {
            Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
            Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
                cuenta.debito(new BigDecimal(1500));

            });
        }

        @Test
        @Disabled //para saltar el metodo, se muestra en el reporte pero no se evalua
        @DisplayName("Probando el saldo de la cuenta corriente, que no sea null, mayor que cero, valor esperado")
        void testTransferirDineroCuentas() {
            fail(); //metodo para forzar la falla

            Cuenta cuenta = new Cuenta("Jhon Doe", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

            Banco banco = new Banco();
            banco.addCuenta(cuenta);
            banco.addCuenta(cuenta2);

            banco.setNombre("Banco del Estado");
            banco.transferir(cuenta2, cuenta, new BigDecimal(500));

            assertAll(
                    () -> assertEquals("1000.8989", cuenta2.getSaldo().toPlainString()),
                    () -> assertEquals("3000", cuenta.getSaldo().toPlainString()),
                    () -> assertEquals(2, banco.getCuentas().size()),
                    () -> assertEquals("Banco del Estado", cuenta.getBanco().getNombre(), () -> "algo ando mal"),
                    () -> assertEquals("Andres", banco.getCuentas().stream()
                            .filter(c -> c.getPersona().equals("Andres"))
                            .findFirst()
                            .get().getPersona()),
                    () -> assertTrue(banco.getCuentas().stream()
                            .anyMatch(c -> c.getPersona().equals("Andres")))
            );


        }
    }


    @Nested
    class OperativeSystem{

        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows(){

        }

        @Test
        @EnabledOnOs({OS.LINUX,OS.MAC})
        void testSoloLinuxMac(){

        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows(){

        }
    }

    @Nested
    class JreVersion{
        @Test
        @EnabledOnJre(org.junit.jupiter.api.condition.JRE.JAVA_8)
        void soloJdk8(){

        }

        @Test
        @EnabledOnJre(org.junit.jupiter.api.condition.JRE.JAVA_15)
        void soloJDK15(){

        }

        @Test
        @DisabledOnJre(org.junit.jupiter.api.condition.JRE.JAVA_15)
        void testNoJDK15(){
        }
    }


    @Tag("properties")
    @Nested
    class Propiedades{
        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "15.0.1")
        void imprimirSystemProperties(){
            Properties properties = System.getProperties();
            properties.forEach((k,v) -> System.out.println(k + ":" + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "15.0.2")
        void testJavaVersion(){

        }

        @Test
        @EnabledIfSystemProperty(named = "ENVI", matches = "dev")
        void testDev(){

        }

        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
        void testEnvProdDisabled(){

        }

    }

    @Test
    @DisplayName("test saldo cuenta Dev.")
    void testSaldoCuentaDev() {
        boolean esDev = "DEV".equals(System.getProperty("ENV"));
        assumeTrue(esDev);

        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO)< 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)< 0);
    }

    @Test
    @DisplayName("test saldo cuenta Dev.")
    void testSaldoCuentaDev2() {
        boolean esDev = "DEV".equals(System.getProperty("ENV"));
        assumingThat(esDev, ()-> { //asumir que solo seejecutara en dev

            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO)< 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)< 0);
        });

    }

    @DisplayName("Probando Debito cuenta Repetir!")
    @RepeatedTest(value = 5, name = "{displayName} - Repeticion numero {currentRepetition} de {totalRepetitions}")
    void testNombreCuentaRepetir(RepetitionInfo info) {
        if(info.getCurrentRepetition() == 3){
            System.out.println("estamos en la repeticion " + info.getCurrentRepetition());
        }
        Cuenta cuenta = new Cuenta("andres", new BigDecimal("234.678"));
        cuenta.setPersona("Andres");
        String esperado = "Andres";
        String real = cuenta.getPersona();
        Assertions.assertEquals(esperado, real);
    }

    //pruebas parametrizadas
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}" )
    @ValueSource(strings = {"100","200","300","400","500","600","700"})
    void testDebitoCuentaValueSource(String monto){
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }


    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}" )
    @CsvSource({"1,100","2,200","3,300","4,400","5,500","6,600","7,700"})
    void testDebitoCuentaCsvSource(String index,String monto){
        System.out.println(index + " -> " + monto);
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}" )
    @CsvFileSource(resources = "/data.csv")
    void testDebitoCuentaCsvFileSource(String monto){
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @ParameterizedTest
    @MethodSource("montoList")
    void testDebitoCuentaMethodSource(String monto){
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    static List<String> montoList(){
        return Arrays.asList("100","200","300","400","500","600","900");
    }


    @Nested
    @Tag("Timeout")
    class EjemploTimeOutTest{

        @Test
        @Timeout(1)//determina el tiempo maximo de ejecucion
        void pruebaTimeout() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }


        @Test
        @Timeout(value = 500, unit = TimeUnit.MILLISECONDS) //por default son segundos
        void pruebaTimeout2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(400);
        }

        @Test
        void testTimeoutAssertions(){
            //se puede utilizar en lugar de @Timeout
            assertTimeout(Duration.ofSeconds(5), () -> {
                TimeUnit.MILLISECONDS.sleep(5500);
            });
        }
    }
}