package HomeWork5;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.hw.db.dao.CategoriesMapper;
import ru.hw.db.dao.ProductsMapper;
import ru.hw.dto.Category;
import ru.hw.dto.Product;
import ru.hw.enums.CategoryTypes;
import ru.hw.service.CategoryService;
import ru.hw.service.ProductService;
import ru.hw.utils.DbUtils;
import ru.hw.utils.PrettyLogger;
import ru.hw.utils.RetrofitUtils;

import java.io.IOException;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Tests {
    int productId;
    static ProductsMapper productsMapper;
    static CategoriesMapper categoriesMapper;
    static Retrofit client;
    static ProductService productService;
    static CategoryService categoryService;
    Faker faker = new Faker();
    Product product;
    Product brokenProduct;
    Product updateProduct;
    static Integer existingID;

    PrettyLogger prettyLogger = new PrettyLogger();

    @BeforeAll
    static void beforeAll() {
        client = RetrofitUtils.getRetrofit();
        productService = client.create(ProductService.class);
        categoryService = client.create(CategoryService.class);
        productsMapper = DbUtils.getProductsMapper();
        categoriesMapper = DbUtils.getCategoriesMapper();
    }

    @BeforeEach
    void setUp() {
        product = new Product()
                .withTitle(faker.food().dish())
                .withPrice((int) ((Math.random() + 1) * 100))
                .withCategoryTitle(CategoryTypes.FOOD.getTitle());
        brokenProduct = new Product()
                .withTitle("")
                .withCategoryTitle("Random product category that does not exist");
        updateProduct = new Product()
                .withId(existingID)
                .withTitle(faker.food().dish())
                .withPrice((int) ((Math.random() + 1) * 100))
                .withCategoryTitle(CategoryTypes.FOOD.getTitle());
    }
    @Test
    @Order(1)
    void putProductIDDontExistTest() throws IOException {
        Integer before = DbUtils.countProducts(productsMapper);
        Response<Product> response = productService.updateProduct(updateProduct).execute();
        Integer after = DbUtils.countProducts(productsMapper);

        assertThat(before, equalTo(after));
        assertThat(response.code(), equalTo(400));
    }

    @Test
    @Order(2)
    void postProductTest() throws IOException, InterruptedException {
        Integer productsBefore = DbUtils.countProducts(productsMapper);
        Response<Product> response = productService.createProduct(product).execute();
        Integer productsAfter = DbUtils.countProducts(productsMapper);

        assertThat(productsAfter, equalTo(productsBefore + 1));
        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));
        existingID = response.body().getId();
    }

    @Test
    void getProductNotNullTest() throws IOException {
        Response<ArrayList<Product>> response = productService.getProducts().execute();
        assertNotNull(response.body());
    }

    @Test
    void getProductContainsObjectsTest() throws IOException {
        Response<ArrayList<Product>> response = productService.getProducts().execute();
        assertTrue(response.body().size() > 0);
    }

    @Test
    @Order(3)
    void getProductByIDTest() throws IOException {
        Integer before = DbUtils.countProducts(productsMapper);
        Response<Product> response = productService.getProduct(existingID).execute();
        Integer after = DbUtils.countProducts(productsMapper);

        assertThat(before, equalTo(after));
        assertNotNull(response.body());
        assertThat(response.body().getId(), equalTo(existingID));
    }

    @Test
    void getProductByIDNotExistTest() throws IOException {
        Integer before = DbUtils.countProducts(productsMapper);
        Response<Product> response = productService.getProduct(999999999).execute();
        Integer after = DbUtils.countProducts(productsMapper);

        assertThat(before, equalTo(after));
        assertThat(response.code(), equalTo(404));
    }

    @Test
    @Order(4)
    void putProductExistTest() throws IOException {
        Integer before = DbUtils.countProducts(productsMapper);
        Response<Product> response = productService.updateProduct(updateProduct).execute();
        Integer after = DbUtils.countProducts(productsMapper);

        assertThat(before, equalTo(after));
        assertThat(response.body().getId(), equalTo(existingID));
        assertThat(response.body().getTitle(), equalTo(updateProduct.getTitle()));
    }

    @Test
    void postProductCategoryDontExistTest() throws IOException {
        Response<Product> response = productService.createProduct(brokenProduct).execute();
        assertThat(response.code(), equalTo(500));
    }

    @Test
    @Order(5)
    void deleteProductByIDTest() throws IOException {
        Response<Void> response = productService.deleteProduct(existingID).execute();
        assertThat(response.code(), equalTo(200));
    }

    @Test
    void deleteProductByIDNotExistTest() throws IOException {
        Response<Void> response = productService.deleteProduct(999999999).execute();
        assertThat(response.code(), equalTo(500));
    }

    @Test
    void getCategoryByIdTest() throws IOException {
        Integer before = DbUtils.countCategories(categoriesMapper);
        DbUtils.createNewCategory(categoriesMapper);
        Integer after = DbUtils.countCategories(categoriesMapper);
        Integer id = CategoryTypes.FOOD.getId();
        Response<Category> response = categoryService.getCategory(id).execute();
        prettyLogger.log(response.body().toString());

        assertThat(after, equalTo(before + 1));
        assertThat(response.body().getTitle(), equalTo(CategoryTypes.FOOD.getTitle()));
        assertThat(response.body().getId(), equalTo(id));
    }

    @Test
    void getCategoryByIdNotExistTest() throws IOException {
        Integer id = 444444444;
        Response<Category> response = categoryService.getCategory(id).execute();
        assertThat(response.code(), equalTo(404));
    }

    @AfterAll
    static void deleteCreatedItem() {
        DbUtils.deleteProduct(productsMapper, existingID);
    }
}
