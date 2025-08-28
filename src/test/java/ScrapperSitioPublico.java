import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.util.List;

public class ScrapperSitioPublico {

    public static void main(String[] args) {
        // Configurar el driver con WebDriverManager
        WebDriverManager.chromedriver().setup();

        // Instanciar ChromeDriver
        WebDriver driver = new ChromeDriver();

        // Maximizar ventana
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

        // Limpiar y escribir "testing"
        inputBusqueda.clear();
        inputBusqueda.sendKeys("Desarollo software");

        // Botón Buscar
        WebElement botonBuscar = driver.findElement(By.id("btnBuscar"));
        botonBuscar.click();
        // Cerrar navegador
        //driver.quit();

        // Esperar unos segundos para ver la página cargada
        try {
            Thread.sleep(5000); // 5 segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Buscar todos los bloques de licitaciones
        List<WebElement> licitaciones = driver.findElements(By.xpath("//div[@class='responsive-resultado']"));

        for (WebElement lic : licitaciones) {
            String id = lic.findElement(By.xpath("//span[@class='clearfix']")).getText().trim();
            String estado = lic.findElement(By.cssSelector(".estado-texto")).getText().trim();
            String titulo = lic.findElement(By.cssSelector("h2")).getText().trim();
            String descripcion = lic.findElement(By.cssSelector("p.text-weight-light")).getText().trim();
            String monto = lic.findElement(By.xpath("//span[@class='highlight-text text-weight-light campo-numerico-punto-coma']")).getText().trim();
            String fechaPublicacion = lic.findElement(By.xpath(".//div[@class='col-md-4'][p[strong[contains(text(),'Fecha de publicación')]]]/span")).getText().trim();
            String fechaCierre = lic.findElement(By.xpath(".//div[@class='col-md-4'][p[strong[contains(text(),'Fecha de cierre')]]]/span")).getText().trim();
            String organismo = lic.findElement(By.cssSelector(".lic-bloq-footer .col-md-4 strong")).getText().trim();

            // Armar el texto para correo
            String mailBody = String.format(
                    "📌 Licitación: %s\n" +
                            "📖 Título: %s\n" +
                            "📝 Descripción: %s\n" +
                            "💲 Monto: %s\n" +
                            "📅 Publicación: %s\n" +
                            "⏳ Cierre: %s\n" +
                            "🏛 Organismo: %s\n" +
                            "📌 Estado: %s\n",
                    id, titulo, descripcion, monto, fechaPublicacion, fechaCierre, organismo, estado
            );

            System.out.println(mailBody);
        }
    }
}