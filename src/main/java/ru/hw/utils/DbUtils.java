package ru.hw.utils;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import ru.hw.db.dao.CategoriesMapper;
import ru.hw.db.dao.ProductsMapper;
import ru.hw.db.model.Categories;
import ru.hw.db.model.CategoriesExample;
import ru.hw.db.model.ProductsExample;

import java.io.IOException;

@UtilityClass
public class DbUtils {
    private String resource = "mybatisConfig.xml";
    static Faker faker = new Faker();

    private static SqlSession getSqlSession() throws IOException {
        SqlSessionFactory sqlSessionFactory;
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream(resource));

        return sqlSessionFactory.openSession(true);
    }

    @SneakyThrows
    public static CategoriesMapper getCategoriesMapper() {return getSqlSession().getMapper(CategoriesMapper.class);}
    @SneakyThrows
    public static ProductsMapper getProductsMapper() {return getSqlSession().getMapper(ProductsMapper.class);}

    public static void createNewCategory(CategoriesMapper categoriesMapper) {
        Categories newCategory = new Categories();
        newCategory.setTitle(faker.animal().name());

        categoriesMapper.insert(newCategory);
    }

    public static Integer countCategories(CategoriesMapper categoriesMapper) {
        long categoriesCount = categoriesMapper.countByExample(new CategoriesExample());
        return Math.toIntExact(categoriesCount);
    }

    public static Integer countProducts(ProductsMapper productsMapper) {
        long products = productsMapper.countByExample(new ProductsExample());
        return Math.toIntExact(products);
    }

    public static void  deleteProduct(ProductsMapper productsMapper, Integer id) {
        productsMapper.deleteByPrimaryKey(id.longValue());
    }
}
