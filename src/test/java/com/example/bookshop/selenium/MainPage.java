package com.example.bookshop.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class MainPage {

    private String url = "http://localhost:8081/";
    private ChromeDriver driver;

    public MainPage(ChromeDriver driver){
        this.driver = driver;
    }

    public MainPage callPage() {
        driver.get(url);
        return this;
    }

    public MainPage pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }

    public MainPage setUpSearchToken(String token) {
        WebElement element = driver.findElement(By.id("query"));
        element.sendKeys(token);
        return this;
    }

    public MainPage submit() {
        WebElement element = driver.findElement(By.id("search"));
        element.submit();
        return this;
    }

    public MainPage clickPageGenres(){
        WebElement element = driver.findElement(By.xpath("//*[@id=\"navigate\"]/ul/li[2]/a"));
        element.click();
        return this;
    }

    public MainPage clickPageNews(){
        WebElement element = driver.findElement(By.xpath("//*[@id=\"navigate\"]/ul/li[3]/a"));
        element.click();
        return this;
    }

    public MainPage clickPagePopular(){
        WebElement element = driver.findElement(By.xpath("//*[@id=\"navigate\"]/ul/li[4]/a"));
        element.click();
        return this;
    }

    public MainPage clickPageAuthors(){
        WebElement element = driver.findElement(By.xpath("//*[@id=\"navigate\"]/ul/li[5]/a"));
        element.click();
        return this;
    }

    public MainPage clickAuthor(){
        WebElement element = driver.findElement(By.xpath("/html/body/div[1]/div/main/div/div/div[2]/div/div/a"));
        element.click();
        return this;
    }
    public MainPage clickPageMain(){
        WebElement element = driver.findElement(By.xpath("//*[@id=\"navigate\"]/ul/li[1]/a"));
        element.click();
        return this;
    }

    public MainPage clickPageSignin(){
        WebElement element = driver.findElement(By.xpath("/html/body/header/div[1]/div/div/div[3]/div/a[3]"));
        element.click();
        return this;
    }

    public MainPage clickLoginEmail() {
        WebElement element = driver
                .findElement(By.xpath("/html/body/div[1]/div[2]/main/form/div/div[1]/div[2]/div/div[2]/label/input"));
        element.click();
        return this;
    }

    public MainPage inputEmailLogin(String email){
        WebElement element = driver
                .findElement(By.xpath("//*[@id=\"mail\"]"));
        element.sendKeys(email);
        return this;
    }

    public MainPage clickSedAuth(){
        WebElement element = driver
                .findElement(By.id("sendauth"));
        element.click();
        return this;
    }

    public MainPage inputPassLogin(String pass){
        WebElement element = driver
                .findElement(By.xpath("//*[@id=\"mailcode\"]"));
        element.sendKeys(pass);
        return this;
    }

    public MainPage clickLogin(){
        WebElement element = driver
                .findElement(By.xpath("//*[@id=\"toComeInMail\"]"));
        element.click();
        return this;
    }

    public MainPage clickBookSlug(){
        WebElement element = driver
                .findElement(By.xpath(" /html/body/div[1]/div/main/div[1]/div[2]/div[1]/div[1]/div/div/div/strong/a"));
        element.click();
        return this;
    }

    public MainPage clickPaidBook(){
        WebElement element = driver
                .findElement(By.xpath("/html/body/div[1]/div/main/div/div[1]/div[2]/div[3]/div[2]/button"));
        element.click();
        return this;
    }

    public MainPage clickLogoSite(){
        WebElement element = driver.findElement(By.className("logo"));
        element.click();
        return this;
    }

    public MainPage clickCartPage(){
        WebElement element = driver.findElement(By.xpath("/html/body/header/div[1]/div/div/div[3]/div/a[2]"));
        element.click();
        return this;
    }

    public MainPage clickSigUp(){
        WebElement element = driver.findElement(By.xpath("/html/body/div[1]/div[2]/main/form/div/div[4]/a"));
        element.click();
        return this;
    }

    public MainPage inputName(String name){
        WebElement element = driver.findElement(By.xpath("//*[@id=\"name\"]"));
        element.sendKeys(name);
        return this;
    }

    public MainPage inputPhone(String phone){
        WebElement element = driver.findElement(By.id("phone"));
        element.sendKeys(phone);
        return this;
    }

    public MainPage clickSubmitPhone(){
        WebElement element = driver.findElement(By.xpath("//*[@id=\"submitPhone\"]"));
        element.click();
        return this;
    }

    public MainPage inputPhoneCode(String code){
        WebElement element = driver.findElement(By.id("phoneCode"));
        element.sendKeys(code);
        return this;
    }

    public MainPage inputEmail(String email){
        WebElement element = driver.findElement(By.id("mail"));
        element.sendKeys(email);
        return this;
    }

    public MainPage clickSubmitEmail(){
        WebElement el = driver.findElement(By.id("submitMail"));
        el.click();
        return  this;
    }

    public MainPage inputEmailCode(String code){
        WebElement el = driver.findElement(By.id("mailCode"));
        el.sendKeys(code);
        return this;
    }

    public MainPage inputPass(String pass){
        WebElement el = driver.findElement(By.id("password"));
        el.sendKeys(pass);
        return this;
    }

    public MainPage clickRegButton(){
        WebElement el = driver.findElement(By.id("registration"));
        el.click();
        return this;
    }

    public MainPage clickFromInput(){
        WebElement element = driver.findElement(By.xpath("//*[@id=\"name\"]"));
        element.click();
        return this;
    }

}
