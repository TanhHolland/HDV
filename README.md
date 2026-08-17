# Hệ thống Đặt Xe - Microservices (Nhóm 4)

Hệ thống đặt xe trực tuyến theo kiến trúc microservices: đặt xe, ghép tài xế, quản lý chuyến đi.

## Thành viên

| Họ Tên | MSSV | Vai trò |
|--------|------|---------|
| Nguyễn Anh Quân | B21DCCN103 | Intention Service (`nhom4-intention`) |
| Nguyễn Tuấn Anh | B21DCCN008 | Order Service (`nhom4-order`) |
| Lê Đức Nam | B21DCCN547 | User Service (`nhom4-user`), Position Service (`nhom4-position`) |

## Cấu trúc Gradle

Một Gradle multi-project ở **root**. Mỗi service nghiệp vụ chỉ có **một** `build.gradle`.

```
HDV/
  build.gradle
  gradle.properties
  settings.gradle
  gradlew
  gradlew.bat
  gradle/wrapper/
  Dockerfile
  docker-compose.yml
  nhom4-user/build.gradle
  nhom4-intention/build.gradle
  nhom4-order/build.gradle
  nhom4-position/build.gradle
  eureka/          # Maven
  gateway/         # Maven
```

## Yêu cầu

- JDK **21**
- Docker và Docker Compose
- Maven 3.8+ (chỉ cho `eureka` và `gateway` khi build ngoài Docker)

## Chạy toàn bộ hệ thống (Docker Compose)

Từ thư mục root:

```bash
docker compose up --build
```

Bốn service Gradle dùng **một** `Dockerfile` ở root. Compose truyền `SERVICE_NAME` và `SERVICE_PORT` nên vẫn tạo đủ container (user, position, intention, order). `eureka` và `gateway` giữ `Dockerfile` riêng trong folder của chúng.

Sau khi lên:

- Eureka: http://localhost:8761
- API Gateway: http://localhost:8800
- RabbitMQ Management: http://localhost:15672 (`guest` / `guest`)
- Redis Commander: http://localhost:8081
- MySQL: `localhost:3306`
- Redis: `localhost:6379`

## Chạy từng service trong IDE (Run)

1. Mở **thư mục root** `HDV` (không mở từng folder service).
2. Import Gradle project (Cursor / IntelliJ nhận `settings.gradle` ở root).
3. Chạy class chính của module:

| Service | Module | Main class | Port |
|---------|--------|------------|------|
| User (UC) | `nhom4-user` | `nhom4.uc.UCApplication` | 8000 |
| Intention | `nhom4-intention` | `com.intentionservice.IntentionServiceApplication` | 8001 |
| Order | `nhom4-order` | `com.example.order.OrderApplication` | 8002 |
| Position | `nhom4-position` | `nhom4.position.PositionApplication` | 8003 |

Hoặc Gradle:

```bash
./gradlew :nhom4-user:bootRun
./gradlew :nhom4-intention:bootRun
./gradlew :nhom4-order:bootRun
./gradlew :nhom4-position:bootRun
```

Windows: `gradlew.bat :nhom4-user:bootRun`.

Khi chạy từ IDE, cần hạ tầng local (MySQL, Redis, RabbitMQ, Eureka). Cách nhanh: `docker compose up mysql redis rabbitmq eureka`.

`eureka` và `gateway` vẫn chạy bằng Maven / Docker.

## Microservices

### User Service (`nhom4-user`) — port 8000
Quản lý khách hàng và tài xế. Endpoints: `/users/**`

### Position Service (`nhom4-position`) — port 8003
Vị trí tài xế, match bán kính. Endpoints: `/api/position/update`, `/api/position/match`

### Intention Service (`nhom4-intention`) — port 8001
Đặt xe và xác nhận. Endpoints: `/api/intentions/place`, `/api/intentions/confirm`

### Order Service (`nhom4-order`) — port 8002
Đơn hàng và trạng thái chuyến. Endpoints: `/api/order/aboard`, `/api/order/arrive`, `/api/order/cancel`

### Eureka — port 8761
Service discovery.

### API Gateway — port 8800
Điều hướng request, load balancing (`lb://`).

## API (qua Gateway `:8800`)

**User**
- `GET /users/{id}`
- `GET /users`

**Position**
- `POST /api/position/update` — params: `driverId`, `longitude`, `latitude`
- `GET /api/position/match` — params: `longitude`, `latitude`

**Intention**
- `POST /api/intentions/place` — body: `{ "userId", "startLongitude", "startLatitude", "destLongitude", "destLatitude" }`
- `POST /api/intentions/confirm` — params: `driverId`, `intentionId`

**Order**
- `POST /api/order/aboard?orderId=`
- `POST /api/order/arrive?orderId=`
- `POST /api/order/cancel?orderId=`

## Demo luồng đặt xe

```bash
curl -X POST http://localhost:8800/api/intentions/place \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "startLongitude": 105.8521, "startLatitude": 21.0245, "destLongitude": 105.8315, "destLatitude": 21.0277}'

curl -X POST "http://localhost:8800/api/intentions/confirm?driverId=11&intentionId=1"

curl -X POST "http://localhost:8800/api/order/aboard?orderId=<order_id>"

curl -X POST "http://localhost:8800/api/order/arrive?orderId=<order_id>"
```

## Tài liệu

- `docs/thiet_ke_sequence.md`
- `docs/thiet_ke_database.md`
- `docs/thiet_ke_cauhinh.md`
- `docs/architecture.md`
- `docs/payos.md`

## Tham khảo

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Cloud Netflix](https://spring.io/projects/spring-cloud-netflix)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Gradle](https://gradle.org/)
- [Docker Compose](https://docs.docker.com/compose/)
