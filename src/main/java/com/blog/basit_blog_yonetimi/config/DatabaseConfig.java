// src/main/java/com/blog/basit_blog_yonetimi/config/DatabaseConfig.java

package com.blog.basit_blog_yonetimi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Bu sınıf, uygulamanın veritabanı bağlantısını ve JPA (Java Persistence API)
 * ayarlarını yapılandırmak için kullanılır.
 *
 * @Configuration: Bu sınıfın Spring'e bir yapılandırma sınıfı olduğunu bildirir.
 * Spring, bu sınıftaki @Bean metotlarını tarayarak bileşenler (beans) oluşturur.
 * @PropertySource: 'application.properties' dosyasından veritabanı bağlantı bilgilerini yükler.
 * @EnableJpaRepositories: JPA Repositories'lerinin nerede olduğunu Spring'e bildirir.
 * Bu sayede Spring, repository arayüzlerinden otomatik olarak implementasyonlar oluşturur.
 * @EnableTransactionManagement: Uygulamada işlem (transaction) yönetimini etkinleştirir.
 * @Transactional notasyonunu kullanabilmek için gereklidir.
 */
@Configuration
@PropertySource("classpath:application.properties") // application.properties dosyasını yükler
@EnableJpaRepositories(basePackages = "com.blog.basit_blog_yonetimi.repository") // Repository'lerin bulunduğu paket
@EnableTransactionManagement // İşlem yönetimini etkinleştirir
public class DatabaseConfig {

    // Environment nesnesi, application.properties dosyasındaki özelliklere erişmek için kullanılır.
    private final Environment env;

    /**
     * Yapıcı metot (Constructor Injection).
     * Spring, Environment nesnesini otomatik olarak enjekte eder.
     * @param env Spring Environment nesnesi
     */
    public DatabaseConfig(Environment env) {
        this.env = env;
    }

    /**
     * DataSource bean'ini tanımlar. Bu, uygulamanın veritabanına bağlanmak için kullandığı nesnedir.
     * Basit bir DriverManagerDataSource kullanılmıştır, ancak üretim ortamları için
     * HikariCP gibi daha performanslı bağlantı havuzları önerilir.
     *
     * application.properties dosyasında şu özelliklerin tanımlı olması beklenir:
     * spring.datasource.url=jdbc:h2:mem:blogdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
     * spring.datasource.driver-class-name=org.h2.Driver
     * spring.datasource.username=sa
     * spring.datasource.password=
     *
     * @return Yapılandırılmış DataSource nesnesi
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        return dataSource;
    }

    /**
     * EntityManagerFactory bean'ini tanımlar. Bu, JPA'nın temel bileşenidir ve
     * veritabanı etkileşimleri için EntityManager örneklerini oluşturur.
     * Hibernate'i JPA sağlayıcısı olarak kullanır.
     *
     * @param dataSource Veritabanı bağlantı nesnesi
     * @return Yapılandırılmış LocalContainerEntityManagerFactoryBean
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource); // Kullanılacak DataSource'u ayarlar
        em.setPackagesToScan("com.blog.basit_blog_yonetimi.entity"); // Entity sınıflarının bulunduğu paketi tarar

        // Hibernate'i JPA sağlayıcısı olarak ayarlar
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        // Hibernate özelliklerini ayarlar
        Properties jpaProperties = new Properties();
        // Veritabanı şemasını otomatik olarak oluşturur/günceller (devamlı geliştirme için uygun)
        // Üretim ortamında 'none' veya 'validate' kullanılması önerilir.
        jpaProperties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        // SQL sorgularını konsola yazdırır (debugging için faydalı)
        jpaProperties.setProperty("hibernate.show_sql", env.getProperty("spring.jpa.show-sql"));
        // Yazdırılan SQL'i daha okunur hale getirir
        jpaProperties.setProperty("hibernate.format_sql", env.getProperty("spring.jpa.properties.hibernate.format_sql"));
        // Dialect, kullanılan veritabanına özgü SQL oluşturulmasını sağlar (örn: H2, MySQL, PostgreSQL)
        jpaProperties.setProperty("hibernate.dialect", env.getProperty("spring.jpa.database-platform"));

        em.setJpaProperties(jpaProperties); // JPA özelliklerini EntityManagerFactory'ye ekler

        return em;
    }

    /**
     * PlatformTransactionManager bean'ini tanımlar. Bu, Spring'in işlem yönetimini sağlar.
     * JPA uygulamalarında JpaTransactionManager kullanılır.
     * Bu sayede @Transactional notasyonunu kullanarak metotları atomik işlemler haline getirebilirsin.
     *
     * @param entityManagerFactory EntityManagerFactory nesnesi
     * @return Yapılandırılmış PlatformTransactionManager
     */
    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject()); // EntityManagerFactory'yi ayarlar
        return transactionManager;
    }
}