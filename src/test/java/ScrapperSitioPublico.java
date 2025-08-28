import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ScrapperSitioPublico {

    @Test
    public void scrapperTest() {
        // Configurar el driver con WebDriverManager
        WebDriverManager.chromedriver().setup();

        // Configurar opciones de Chrome para modo headless
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        // Instanciar ChromeDriver con las opciones
        WebDriver driver = new ChromeDriver(options);
        // Maximizar ventana (no es estrictamente necesario en headless, pero es buena práctica)
        driver.manage().window().maximize();

        // Cargar la página
        driver.get("https://www.mercadopublico.cl/Home/BusquedaLicitacion");

        // Esperar unos segundos para ver la página cargada
        try {
            Thread.sleep(5000); // 5 segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        driver.switchTo().frame("form-iframe");

        WebElement inputBusqueda = driver.findElement(By.id("textoBusqueda"));

        // Limpiar y escribir "Desarollo software"
        inputBusqueda.clear();
        inputBusqueda.sendKeys("desarrollo software");

        // Botón Buscar
        WebElement botonBuscar = driver.findElement(By.id("btnBuscar"));
        botonBuscar.click();

        // Esperar unos segundos para ver los resultados
        try {
            Thread.sleep(5000); // 5 segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Buscar todos los bloques de licitaciones
        List<WebElement> licitaciones = driver.findElements(By.xpath("//div[@class='responsive-resultado']"));
        List<String> oportunidadesEncontradas = new ArrayList<>();

        for (WebElement lic : licitaciones) {
            String id = lic.findElement(By.xpath(".//span[@class='clearfix']")).getText().trim();
            String estado = lic.findElement(By.cssSelector(".estado-texto")).getText().trim();
            String titulo = lic.findElement(By.cssSelector("h2")).getText().trim();
            String descripcion = lic.findElement(By.cssSelector("p.text-weight-light")).getText().trim();
            String monto = lic.findElement(By.xpath("//p[strong[contains(normalize-space(.), 'Monto')]]/following-sibling::span")).getText().trim();
            String fechaPublicacion = lic.findElement(By.xpath(".//div[@class='col-md-4'][p[strong[contains(text(),'Fecha de publicac')]]]/span")).getText().trim();
            String fechaCierre = lic.findElement(By.xpath(".//div[@class='col-md-4'][p[strong[contains(text(),'Fecha de cierre')]]]/span")).getText().trim();
            String organismo = lic.findElement(By.cssSelector(".lic-bloq-footer .col-md-4 strong")).getText().trim();

            String onclickAttr = driver.findElement(
                    By.xpath("//div[@class='col-md-12 row margin-bottom-md']//a")
            ).getAttribute("onclick");

            // Limpiar el string para extraer solo la URL
            String url = onclickAttr.replaceAll(".*\\('(.*)'\\).*", "$1");
            // Armar el texto para correo
            String mailBody = String.format(
                    "📌 Licitación: %s\n" +
                            "📖 Título: %s\n" +
                            "📝 Descripción: %s\n" +
                            "💲 Monto: %s\n" +
                            "📅 Publicación: %s\n" +
                            "⏳ Cierre: %s\n" +
                            "🏛 Organismo: %s\n" +
                            "🏛  Ver Info: %s\n" +
                            "📌 Estado: %s\n",
                    id, titulo, descripcion, monto, fechaPublicacion, fechaCierre, organismo, url, estado
            );
            oportunidadesEncontradas.add(detalleOportunidad);
        }

        // Si se encontraron oportunidades, construir y enviar un único correo
        if (!oportunidadesEncontradas.isEmpty()) {
            StringBuilder mailBody = new StringBuilder();
            mailBody.append("Oportunidades encontradas hoy en Mercado Publico en base al criterio \"desarrollo software\" que fue el parametro usado:\n\n");

            for (String oportunidad : oportunidadesEncontradas) {
                mailBody.append(oportunidad).append("\n");
            }

            sendEmail("Oportunidades de Desarrollo de Software", mailBody.toString());
        } else {
            System.out.println("No se encontraron nuevas oportunidades.");
        }


        // Cerrar navegador
        driver.quit();
    }

    private void sendEmail(String subject, String body) {
        // Leer configuración de variables de entorno
        final String host = System.getenv("SMTP_HOST");
        final String port = System.getenv("SMTP_PORT");
        final String username = System.getenv("SMTP_USER");
        final String password = System.getenv("SMTP_PASSWORD");
        final String recipientEmail = System.getenv("RECIPIENT_EMAIL");

        // Validar que las variables de entorno estén configuradas
        if (host == null || port == null || username == null || password == null || recipientEmail == null) {
            System.out.println("Error: Las variables de entorno para el envío de correo no están configuradas.");
            System.out.println("Cuerpo del correo (no enviado):\n" + body);
            return; // No intentar enviar si falta configuración
        }

        // Configurar propiedades para la sesión de JavaMail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        // Crear una sesión con autenticación
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Crear el mensaje de correo
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject, "UTF-8");
            // Establecer el contenido con codificación UTF-8
            message.setContent(body, "text/plain; charset=utf-8");


            // Enviar el mensaje
            Transport.send(message);

            System.out.println("Correo enviado exitosamente a " + recipientEmail);

        } catch (MessagingException e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
            e.printStackTrace();
        }

        // Cerrar navegador
        driver.quit();
    }
}