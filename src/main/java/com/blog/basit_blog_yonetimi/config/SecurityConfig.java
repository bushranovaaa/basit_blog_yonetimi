// src/main/java/com/blog/basit_blog_yonetimi/config/SecurityConfig.java

package com.blog.basit_blog_yonetimi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Bu sınıf, Spring Security'nin yapılandırmasını sağlar.
 * Kimlik doğrulama (Authentication) ve yetkilendirme (Authorization) kurallarını burada tanımlarız.
 *
 * @Configuration: Bu sınıfın Spring'e bir yapılandırma sınıfı olduğunu bildirir.
 * @EnableWebSecurity: Spring Security'nin web güvenliği entegrasyonunu etkinleştirir.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Güvenlik filtresi zincirini (SecurityFilterChain) yapılandırır.
     * Bu metot, HTTP isteklerinin nasıl işleneceğini ve hangi URL'lerin korunacağını tanımlar.
     *
     * @param http HttpSecurity nesnesi, güvenlik yapılandırması için kullanılır.
     * @return Yapılandırılmış SecurityFilterChain nesnesi.
     * @throws Exception Herhangi bir yapılandırma hatası durumunda fırlatılır.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF (Cross-Site Request Forgery) korumasını devre dışı bırakır.
            // REST API'lerde genellikle devre dışı bırakılır, ancak web uygulamalarında etkin tutulmalıdır.
            // Basit bir blog için şimdilik devre dışı bırakıldı, ancak gerçek projelerde dikkatli olunmalı.
            .csrf(csrf -> csrf.disable()) // Lambda ifadesi ile csrf yapılandırması

            // Yetkilendirme kurallarını tanımlar.
            .authorizeHttpRequests(authorize -> authorize
                // "/login" ve "/register" gibi herkese açık URL'lere erişime izin verir.
                .requestMatchers("/login", "/register", "/static/**", "/css/**", "/js/**", "/img/**").permitAll()
                // Diğer tüm isteklerin kimlik doğrulaması gerektirdiğini belirtir.
                .anyRequest().authenticated()
            )
            // Form tabanlı kimlik doğrulamasını yapılandırır.
            .formLogin(form -> form
                // Giriş sayfasının URL'sini belirtir. Spring Security bu URL'ye yönlendirme yapar.
                .loginPage("/login")
                // Giriş başarılı olduğunda kullanıcıyı yönlendireceği varsayılan URL.
                .defaultSuccessUrl("/posts", true) // true: her zaman bu URL'ye yönlendir
                // Giriş başarısız olduğunda yönlendirilecek URL.
                .failureUrl("/login?error=true")
                // Giriş formundaki kullanıcı adı alanının adını belirtir (varsayılan "username").
                .usernameParameter("username")
                // Giriş formundaki şifre alanının adını belirtir (varsayılan "password").
                .passwordParameter("password")
                .permitAll() // Giriş sayfası ve ilgili işlemler herkese açık olmalı
            )
            // Çıkış (logout) işlemini yapılandırır.
            .logout(logout -> logout
                // Çıkış URL'sini belirtir. Bu URL'ye yapılan bir POST isteği çıkış işlemini tetikler.
                .logoutUrl("/logout")
                // Çıkış başarılı olduğunda yönlendirilecek URL.
                .logoutSuccessUrl("/login?logout=true")
                // Oturumun geçersiz kılınmasını sağlar.
                .invalidateHttpSession(true)
                // Kimlik doğrulama bilgilerini temizler.
                .deleteCookies("JSESSIONID")
                .permitAll() // Çıkış işlemi herkese açık olmalı
            );

        return http.build(); // Yapılandırılmış HttpSecurity nesnesini döndürür.
    }

    /**
     * Şifreleri güvenli bir şekilde depolamak ve doğrulamak için kullanılan PasswordEncoder bean'ini tanımlar.
     * BCryptPasswordEncoder, endüstri standardı ve önerilen bir şifre hashleme algoritmasıdır.
     *
     * @return BCryptPasswordEncoder nesnesi.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Kullanıcı detaylarını yüklemek için kullanılan UserDetailsService bean'ini tanımlar.
     * Bu örnekte, basitlik için bellekte (in-memory) kullanıcılar tanımlanmıştır.
     * Gerçek bir uygulamada, bu metot veritabanından kullanıcı bilgilerini yükleyen
     * özel bir UserDetailsService implementasyonu ile değiştirilmelidir.
     *
     * @param passwordEncoder Şifreleri hashlemek için kullanılan PasswordEncoder.
     * @return InMemoryUserDetailsManager nesnesi (geçici kullanıcılar için).
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // Geçici olarak bellekte bir kullanıcı tanımlıyoruz.
        // Gerçek uygulamalarda bu kısım veritabanından kullanıcı çeken bir servis ile değiştirilmelidir.
        UserDetails user = User.builder()
            .username("user") // Kullanıcı adı
            .password(passwordEncoder.encode("password")) // Şifre hashlenmiş olmalı
            .roles("USER") // Kullanıcının rolü
            .build();

        UserDetails admin = User.builder()
            .username("admin") // Yönetici kullanıcı adı
            .password(passwordEncoder.encode("adminpass")) // Yönetici şifresi hashlenmiş olmalı
            .roles("ADMIN", "USER") // Yöneticinin rolleri
            .build();

        return new InMemoryUserDetailsManager(user, admin); // Bellekteki kullanıcıları yöneten servis
    }
}