import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class KiwiDuckAlertAndTableTests extends ConfigTest {

    @BeforeMethod
    public void setup() {
        ConfigTest.initializeProfileDirectory();
        ChromeOptions options = ConfigTest.createChromeOptions();
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    public void testAlertPasswordEntry_Success() {
        navigateToAlertsPage();

        // Получаем пароль из алерта
        driver.findElement(By.xpath("//button[text()='Get password']")).click();
        String password = handleAlertAndGetPassword();

        // Вводим пароль в диалоговое окно
        driver.findElement(By.xpath("//button[text()='Enter password']")).click();
        enterPasswordInPrompt(password);

        // Проверяем успешный ввод пароля
        assertSuccessMessageDisplayed();
        assertReturnToMenuButtonDisplayed();

        // Возвращаемся на главную страницу
        returnToMenu();
    }

    @Test
    public void testAlertPasswordEntry_Failure() {
        navigateToAlertsPage();

        // Получаем пароль из алерта
        driver.findElement(By.xpath("//button[text()='Get password']")).click();
        String password = handleAlertAndGetPassword();

        // Вводим неверный пароль
        driver.findElement(By.xpath("//button[text()='Enter password']")).click();
        enterPasswordInPrompt(password + "1");

        // Проверяем, что сообщение «Great!» не появилось
        Assert.assertTrue(isElementAbsent("//label[text()='Great!']"), "Сообщение «Great!» появилось ошибочно");
        Assert.assertTrue(isElementAbsent("//button[text()='Return to menu']"), "Кнопка «Return to Menu» появилась ошибочно");
    }

    @Test
    public void testTableManipulation_Success() {
        navigateToTablePage();

        // Удаляем записи из таблицы
        deleteSelectedRows();

        // Добавляем новые записи
        addNewTableEntries(3);

        // Проверяем, что кнопка «Return to Menu» отображается
        Assert.assertTrue(isElementPresent("//a[@href and text()='Great! Return to menu']"), "Кнопка 'Great! Return to menu' не отображается");

        // Возвращаемся на главную страницу
        driver.findElement(By.xpath("//a[@href and text()='Great! Return to menu']")).click();
    }

    private void navigateToAlertsPage() {
        driver.get("https://kiwiduck.github.io");
        driver.findElement(By.linkText("Selenium practice (elements)")).click();
        driver.findElement(By.xpath("//a[@href='alerts']")).click();
    }

    private String handleAlertAndGetPassword() {
        Alert alert = driver.switchTo().alert();
        String password = alert.getText().substring("Your password: ".length());
        alert.accept();
        return password;
    }

    private void enterPasswordInPrompt(String password) {
        Alert prompt = driver.switchTo().alert();
        prompt.sendKeys(password);
        prompt.accept();
    }

    private void assertSuccessMessageDisplayed() {
        Assert.assertTrue(driver.findElements(By.xpath("//label[text()='Great!']")).size() > 0, "Сообщение «Great!» не появилось");
    }

    private void assertReturnToMenuButtonDisplayed() {
        Assert.assertTrue(driver.findElements(By.xpath("//button[text()='Return to menu']")).size() > 0, "Кнопка «Return to Menu» не появилась");
    }

    private void returnToMenu() {
        driver.findElement(By.xpath("//button[text()='Return to menu']")).click();
        dismissAlertIfPresent();
    }

    private void dismissAlertIfPresent() {
        try {
            Alert confirm = driver.switchTo().alert();
            confirm.accept();
        } catch (NoAlertPresentException e) {
            // Игнорируем, если алерт отсутствует
        }
    }

    private void navigateToTablePage() {
        driver.get("https://kiwiduck.github.io");
        driver.findElement(By.linkText("Selenium practice (elements)")).click();
        driver.findElement(By.xpath("//a[@href='table']")).click();
    }

    private void deleteSelectedRows() {
        WebElement table = driver.findElement(By.id("customers"));
        for (WebElement checkbox : table.findElements(By.xpath(".//td[text()='UK' or text()='Germany']/parent::tr//input[@type='checkbox']"))) {
            checkbox.click();
        }
        driver.findElement(By.xpath("//input[@type='button' and @value='Delete']")).click();
    }

    private void addNewTableEntries(int count) {
        for (int i = 1; i <= count; i++) {
            driver.findElement(By.xpath("//label[text()='Company']/following-sibling::input")).sendKeys("Компания %s".formatted(i));
            driver.findElement(By.xpath("//label[text()='Contact']/following-sibling::input")).sendKeys("Контакт %s".formatted(i));
            driver.findElement(By.xpath("//label[text()='Country']/following-sibling::input")).sendKeys("Страна %s".formatted(i));
            driver.findElement(By.xpath("//input[@type='button' and @value='Add']")).click();
        }
    }

    private boolean isElementPresent(String xpath) {
        return driver.findElements(By.xpath(xpath)).size() > 0;
    }

    private boolean isElementAbsent(String xpath) {
        return driver.findElements(By.xpath(xpath)).isEmpty();
    }
}
