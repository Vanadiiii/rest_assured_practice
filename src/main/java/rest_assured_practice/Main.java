package rest_assured_practice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class Main {
    //ВСТУПЛЕНИЕ
    /**
     * Всем добрый день! Меня зовут Иван, я автотестировщик в компании Dexsys IT и сегодня хочу поделиться
     * опытом в автоматизации, а именно - навыками работы с библиотекой Rest-Assured.
     * Эта бибилиотека полезна в тестировании, валидации ответов REST-сервисов на языке Java (с использованием синтаксиса Groovy, но об этом чуть позже).
     * Эта статья будет особенно полезна тем, кто только начинает свой путь в автоматизацию.
     * Поехали!
     */

    //БАЗОВЫЕ ПРИМЕРЫ ЗАПРОСОВ (GET, PUT, DELETE) И АВТОРИЗАЦИЯ

    /**
     * Все примеры я буду показывать с помощью довольно понятного и простого в обучении сайта -
     * "Swagger Petstore" - некого магазина животных
     * Рекомендуюю "поиграться" с ним потом самостоятельно
     */
    String petstoreUrl = "https://petstore.swagger.io/";

    /**
     * Для начала посмотрим вообще на эту страницу, к примеру, как устроен её код
     * Воспользуемся для этого методом GET:
     */
    @Test
    public void printThePageCode() {
        RestAssured.given()//Сделаем запрос...
                .get(petstoreUrl) //С методом GET. В сигнатуре метода - URL, на который "стучимся"
                .then() // Тогда ("синтаксический сахар" - согласитесь, с ним намного приятнее читать код!)
                .extract() //Вытащим информацию из...
                .response() //...ответа...
                .prettyPrint(); //...и красиво выведем в консоли
    }

    /**
     * Отобразился HTML-код страницы в консоли? Так и должно быть, значит мы движемся правильно!
     * <p>
     * Но вернёмся к магазину.
     * Для начала создадим пользователя этого магазина - это Я, Вы, ваш сосед, Дарт Вейдер и т.д.
     * Воспользуемся для этого другим методом - POST:
     * (Здесь добавятся некоторые новые методы)
     */
    @Test
    public void createUser() {
        RestAssured.given()
                .body("{\n" +
                        "  \"id\": 666,\n" +
                        "  \"username\": \"Ivan\",\n" +
                        "  \"firstName\": \"Matveev\",\n" +
                        "  \"lastName\": \"Victorovich\",\n" +
                        "  \"email\": \"ivanmatveev@topsecret.ru\",\n" +
                        "  \"password\": \"12345\",\n" +
                        "  \"phone\": \"666\",\n" +
                        "  \"userStatus\": 0\n" +
                        "}") //Информацию о владельце передаём в body..
                .contentType(ContentType.JSON) //В формате JSON (может быть ещё текст, xml и другие)
                .post(petstoreUrl + "v2/user")//Сделаем запрос на этот URL
                .then()
                .extract()
                .response()
                .prettyPrint();
    }

    /**
     * P.S. Тело запроса отформатировано для читаемости пробельными символами
     */

    /**
     * Это всё-таки зоомагазин. Добавим, наконец, нового питомца!
     */
    @Test
    public void addPetInPetstore() {
        RestAssured.given()
                .body("{\n" +
                        "  \"id\": 667,\n" +
                        "  \"category\": {\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"Cat\"\n" +
                        "  },\n" +
                        "  \"name\": \"Pushok\",\n" +
                        "  \"photoUrls\": [\n" +
                        "    \"justMyCat\"\n" +
                        "  ],\n" +
                        "  \"tags\": [\n" +
                        "    {\n" +
                        "      \"id\": 42,\n" +
                        "      \"name\": \"myFavouritePicture№1.ru\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"id\": 84,\n" +
                        "      \"name\": \"myFavouritePicture№2.ru\"\n" +
                        "    }\n" +

                        "  ],\n" +
                        "  \"status\": \"available\"\n" +
                        "}")
                .contentType(ContentType.JSON)
                .post(petstoreUrl + "v2/pet")
                .then()
                .extract()
                .response()
                .prettyPrint();
    }

    /**
     * Отлично, получилось, статус 200! Но не стоит верить на слово никому, даже такому хорошему сайту!
     * Мы ведь тестировщики - проверим по ID правда-ли добавился кот в магазин!
     */

    @Test
    public void checkPetByIdInPetstore() {
        RestAssured.given()
                .get(petstoreUrl + "v2/pet/667")
                .then()
                .extract()
                .response()
                .prettyPrint();
    }

    /**
     * Нашёлся, отлично!
     * Но, похоже, мой кот кому-то приглянулся в магазине и его уже купили...
     * Теперь его нужно удалить с сайта, чтоб никто случайно не купил его повторно.
     */

    @Test
    public void deletePetInfo() {
        RestAssured.given()
                .delete(petstoreUrl + "v2/pet/667")
                .then()
                .extract()
                .response()
                .prettyPrint();
    }

    /**
     * Проверьте сами, удалился он или нет, вызвав предыдущий метод.
     */

    //АВТОРИЗАЦИЯ

    /**
     * Чаще всего для входа на сайт необходима авторизация.
     * Она бывает базовая или с помощью токена (есть ещё варианты, но о них не в этой статье)
     * Рассмотрим самый простой вариант - базовую авторизацию
     * Для этого войдём на сайт под именем уже созданного пользователя
     */
    @Test
    public void basicAuthorize() {
        RestAssured.given()
                .auth()//авторизуемся на сайте...
                .basic("Ivan", "12345") //...с помощью базовой авторизации (логин-пароль)
                .get(petstoreUrl + "v2/user/login")
                .then()
                .extract()
                .response()
                .prettyPrint();
    }

    /**
     * Получилось? Отлично! Я в Вас не сомневался!
     * Переходим дальше!
     */

    //ВАЛИДАЦИЯ ОТВЕТОВ

    /**
     * Часто тестировщику необходимо проверить, верный ли ему пришёл ответ, сравнить с какими-то своими
     * данными, понять, что статус-код ответа 200, или 404. И библиотека Rest-Assured может в этом
     * очень красиво помочь
     * <p>
     * Допустим, нужно проверить, что статус-код ответа - 200. Это очень просто:
     */

    @Test
    public void checkStatusCode() {
        RestAssured.given()
                .get(petstoreUrl)
                .then()
                .assertThat()//Проверяем, что...
                .statusCode(200);//статус код - 200
    }

    /**
     * Аналогичным образом можно проверить ещё и ответ: к примеру, проверим, что тело ответа
     * не пустое (для этого воспользуемся методами класса Matchers)
     */
    @Test
    public void checkResponseByGettingUser() {
        RestAssured.given()
                .get(petstoreUrl + "v2/user/Ivan")//Делаем поиск по имени работника
                .then()
                .assertThat()
                .statusCode(200) //можно делать сколько угодно проверок сразу
                .and() //данный метод тоже можно не писать, но с ним легче читать код
                .body(Matchers.not(Matchers.empty())) //тело не пустое (буквально)
                .and()//...и...
                .body("id", Matchers.equalTo(666));// значение поля id - 666;
    }

    //ПОЛЕЗНЫЕ "ФИШКИ"
    //1) Синтаксис языка Groovy для поиска в body

    /**
     * Иногда JsonBody приходит довольно большой - массивы в объектах, в массивах, те тоже в массивах...
     * А проверить нужно только одно поле. Как быть?
     * В Rest-assured и для этого есть свои методы:
     */

    @Test
    public void findSomethingInBigBody() {
        String name = RestAssured.given()
                .get(petstoreUrl + "v2/pet/667")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .response()
                .body()//возмём тело ответа
                .jsonPath()//и найдём в нём...
                .getString("tags.find{tag -> tag.id == 84}.name");//...строку (с помощью синтаксиса языка Groovy)
        System.out.println("name = " + name);// и выведем строку на печать. У меня получилось "name = myFavouritePicture№2.ru"
        //дословно выражение можно "перевести" так:
        // "найди "tags", у этого массива найди первый объект, у которого "id" равен 548, у него возьми "name""
    }

    /**
     * P.S. Если у Вас выводится совсем не то, что вы писали в тегах, или не выводится вообще, то может
     * Вашего питомца уже поменяли или удалили - сайт общий, используйте уникальные, непростые id!
     */

    //2) baseUri() - базовый путь

    /**
     * В большинстве случаев запросы начинаются одинаково (как и в нашем случае, кстати)
     * И чтоб не писать постоянно один и тот же URL Rest-Assured предлагает такой чудесный метод, как
     * baseUri(String s):
     */
    @Test
    public void checkBaseUri() {
        RestAssured.given()
                .baseUri(petstoreUrl)
                .get("v2/pet/667")
                .then()
                .extract()
                .response()
                .prettyPrint();
    }

    /**
     * А лучше вообще вынести повторяющиеся строки в отдельный метод:
     */
    private RequestSpecification start() {
        return RestAssured.given()
                .baseUri(petstoreUrl + "v2/");
    }

    /**
     * И тесты станут более краткими и выразительными!
     */
    @Test
    public void checkBaseUriLite() {
        start().get("pet/667")
                .then()
                .extract()
                .response()
                .prettyPrint();
    }

    //3) параметризация

    /**
     * Прописывать постоянно вручную id животного - некрасиво, на мой взгляд.
     * Лучше бы это как-то параметризовать. К примеру - вот так:
     */
    @Test
    public void parametrizePath1() {
        int petId = 667;
        start()
                .pathParam("petId", petId)
                .get("pet/{petId}")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .response()
                .prettyPrint();
    }

    /**
     * Или вот так (в этом случае параметризируемые значения записываются в пути в фигурных скобках,
     * а их значения записываются после запятой. параметров может быть несколько):
     */
    @Test
    public void parametrizePath2() {
        int petId = 667;
        String path = "pet/{petId}";
        start()
                .get(path, petId)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .response()
                .prettyPrint();
    }

    /**
     * Но иногда нужно ещё и метод параметризовать. Или проверку статуса. И на это есть варианты:
     */
    @Test
    public void parametrizePath3() {
        int petId = 667;
        String path = "pet/{petId}";
        Method method = Method.GET;
        int statusCode = HttpStatus.SC_OK;

        start()
                .request(method, path, petId)
                .then()
                .assertThat()
                .statusCode(statusCode)
                .and()
                .extract()
                .response()
                .prettyPrint();
    }

    //ЗАКЛЮЧЕНИЕ
    /**
     * Вот мы и рассмотрели небольшую часть того, как библиотека Rest-Assured сможет помочь в
     * тестировании API Ваших сайтов. Остальное - за Вами
     * Всем удачи!
     */
}
