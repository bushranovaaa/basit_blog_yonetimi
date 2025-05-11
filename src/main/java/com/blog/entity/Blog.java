package com.blog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blogs")
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String baslik;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String icerik;

    private LocalDateTime yayinTarihi;

    // Blog nesnesi oluşturulduğunda yayın tarihini otomatik ayarlamak için PrePersist kullanıyoruz
    @PrePersist
    protected void onCreate() {
        this.yayinTarihi = LocalDateTime.now();
    }

    public Blog() {}

    // Getter ve Setter'lar
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBaslik() { return baslik; }
    public void setBaslik(String baslik) {
        if (baslik == null || baslik.trim().isEmpty()) {
            throw new IllegalArgumentException("Başlık boş olamaz!");
        }
        this.baslik = baslik;
    }

    public String getIcerik() { return icerik; }
    public void setIcerik(String icerik) {
        if (icerik == null || icerik.trim().isEmpty()) {
            throw new IllegalArgumentException("İçerik boş olamaz!");
        }
        this.icerik = icerik;
    }

    public LocalDateTime getYayinTarihi() { return yayinTarihi; }
}
