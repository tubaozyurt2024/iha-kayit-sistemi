# İHA Kayıt Yönetim Sistemi

İnsansız hava araçlarının (İHA) kaydını tutan tam yığın uygulama.

Arayüzde İHA'lar listelenir; model adına göre aranabilir, kategori ve duruma göre süzülebilir,
her sütuna göre sıralanabilir ve sayfalanabilir. Kayıt ekleme ve düzenleme bir form penceresinden,
silme onay penceresinden yapılır; durum doğrudan tablodan değiştirilebilir.

| Katman   | Teknoloji                                        | Klasör          |
|----------|--------------------------------------------------|-----------------|
| Backend  | Spring Boot, Spring Data JPA, PostgreSQL, Flyway, springdoc | `iha-backend/`  |
| Frontend | React 18, TypeScript, Vite, Vitest               | `iha-frontend/` |

## Çalıştırma

**Veritabanı** — PostgreSQL 16 (`localhost:5432`, veritabanı/kullanıcı/şifre: `ihadb` / `iha` / `iha`).
Docker'ın kurulu ve kullanıcınızın `docker` grubunda olması gerekir.

```bash
docker compose up -d        # proje kökünde; veriler iha-pgdata volume'ünde kalıcıdır
```

Konteyner `restart: unless-stopped` ile tanımlıdır; bir kez başlatıldıktan sonra bilgisayar açıldığında
kendiliğinden başlar. Durdurmak için `docker compose stop`; tüm veriyi silip başlangıç verisine
dönmek için `docker compose down -v` (geri alınamaz).

Kendi PostgreSQL sunucunuzu kullanacaksanız veritabanını ve kullanıcıyı oluşturun ya da
bağlantıyı ortam değişkenleriyle değiştirin: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.

5432 portu doluysa hem Docker'ı hem backend'i aynı `DB_PORT` ile başlatın:
`DB_PORT=5433 docker compose up -d` ve `DB_PORT=5433 mvn spring-boot:run`.

**Backend** (Java 17+ ve Maven gerekir) — `http://localhost:8080`

```bash
cd iha-backend
mvn spring-boot:run
# başka bir sunucu için: DB_URL=jdbc:postgresql://host:5432/ihadb mvn spring-boot:run
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- Şema ve başlangıç verisi Flyway migration'larıyla kurulur: `src/main/resources/db/migration/`.
  Mevcut bir migration dosyasını değiştirmeyin; her değişiklik için yeni bir `V3__...sql` ekleyin.

**Frontend** (Node 18+ gerekir) — `http://localhost:5173`

```bash
cd iha-frontend
cp .env.example .env   # isteğe bağlı; varsayılan API adresi zaten localhost:8080
npm install
npm run dev
```

## Testler

Backend testleri Docker gerektirmez; aynı migration'larla H2'nin PostgreSQL modunda çalışır.

```bash
cd iha-backend && mvn test
cd iha-frontend && npm test && npm run typecheck
```

## API

Temel yol: `/api/v1/ihalar`

| Metot  | Yol            | Açıklama                                                     |
|--------|----------------|--------------------------------------------------------------|
| GET    | `/`            | Sayfalı liste; `durum`, `kategori`, `model`, `page`, `size`, `sort` |
| GET    | `/{id}`        | Tek kayıt                                                    |
| POST   | `/`            | Yeni kayıt (201 + `Location`)                                |
| PUT    | `/{id}`        | Tam güncelleme                                               |
| PATCH  | `/{id}/durum`  | Yalnızca durum güncelleme                                    |
| DELETE | `/{id}`        | Silme (204)                                                  |

## İş kuralları

1. Seri numarası benzersizdir (ihlalde 409).
2. `HURDA` durumundaki bir İHA'nın durumu değiştirilemez.
3. Son bakım tarihi üretim tarihinden önce olamaz; tarihler gelecekte olamaz.

Kurallar üç katmanda uygulanır: frontend formu (`src/utils/validation.ts`) kullanıcıya anında geri bildirim verir,
backend asıl doğrulamayı yapar; veritabanı da `UNIQUE` ve `CHECK` kısıtlarıyla (seri no, kategori/durum değerleri,
pozitif sayılar, bakım tarihi ≥ üretim tarihi) son savunma hattını oluşturur. Bir istisna: HURDA kuralı
önceki duruma bağlı olduğu için veritabanında değil, backend'de uygulanır; arayüz de HURDA kayıtlarda
durum seçimini kilitler.
