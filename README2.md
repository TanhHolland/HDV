# 🧩 Hệ thống Đặt Xe - Microservices

Đây là dự án xây dựng hệ thống đặt xe trực tuyến dựa trên kiến trúc microservices. Hệ thống được thiết kế để quản lý toàn bộ quá trình từ đặt xe, ghép tài xế đến hoàn thành chuyến đi và thanh toán.

---

## 📁 Cấu trúc thư mục

```
microservices-assignment/
├── README.md                       # File hướng dẫn chính
├── .env.example                    # Biến môi trường mẫu
├── docker-compose.yml              # Cấu hình Docker Compose
├── docs/                           # Thư mục tài liệu
│   ├── architecture.md             # Mô tả kiến trúc hệ thống
│   ├── analysis-and-design.md      # Phân tích và thiết kế
│   ├── asset/                      # Hình ảnh, sơ đồ
│   └── api-specs/                  # Tài liệu API (OpenAPI/YAML)
│       ├── service-a.yaml
│       └── service-b.yaml
├── scripts/                        # Script hỗ trợ
│   └── init.sh                     # Script khởi tạo
├── services/                       # Các microservices
│   ├── uc/                         # User & Customer Service
│   │   ├── Dockerfile
│   │   └── src/                    # Mã nguồn
│   ├── position/                   # Position Service
│   │   ├── Dockerfile
│   │   └── src/                    # Mã nguồn
│   ├── intention-service/          # Intention Service
│   │   ├── Dockerfile
│   │   └── src/                    # Mã nguồn
│   └── order/                      # Order Service
│       ├── Dockerfile
│       └── src/                    # Mã nguồn
├── eureka/                         # Eureka Service Discovery
│   ├── Dockerfile
│   └── src/                        # Mã nguồn
└── gateway/                        # API Gateway
    ├── Dockerfile
    └── src/                        # Mã nguồn
```

---

## 🚀 Hướng dẫn cài đặt

### Yêu cầu hệ thống
- Docker và Docker Compose
- JDK 17 trở lên
- Maven 3.8 trở lên

### Các bước cài đặt

1. **Clone repository**

   ```bash
   git clone https://github.com/username/booking-microservices.git
   cd booking-microservices
   ```

2. **Cấu hình biến môi trường**

   ```bash
   cp .env.example .env
   # Chỉnh sửa file .env theo môi trường của bạn
   ```

3. **Khởi động hệ thống với Docker Compose**

   ```bash
   docker-compose up --build
   ```

   Hoặc sử dụng script khởi tạo:

   ```bash
   chmod +x scripts/init.sh
   ./scripts/init.sh
   ```

4. **Kiểm tra các dịch vụ**
    - Eureka Dashboard: http://localhost:8761
    - RabbitMQ Management: http://localhost:15672 (guest/guest)
    - API Gateway: http://localhost:8800
    - MySQL: localhost:3306
    - Redis: localhost:6379

---

## 📚 Mô tả Microservices

### UC Service (User & Customer)
- **Port**: 8000
- **Chức năng**: Quản lý thông tin người dùng (khách hàng và tài xế)
- **Công nghệ**: Spring Boot, Spring Data JPA, MySQL
- **Endpoints**: `/users/**`, `/users/{id}`

### Position Service
- **Port**: 8003
- **Chức năng**: Quản lý vị trí tài xế, tìm tài xế gần khách hàng
- **Công nghệ**: Spring Boot, Redis, MySQL
- **Endpoints**: `/api/position/update`, `/api/position/match`

### Intention Service
- **Port**: 8001
- **Chức năng**: Xử lý yêu cầu đặt xe, ghép tài xế với khách hàng
- **Công nghệ**: Spring Boot, MySQL, Redis, RabbitMQ
- **Endpoints**: `/api/intentions/place`, `/api/intentions/confirm`

### Order Service
- **Port**: 8002
- **Chức năng**: Quản lý đơn hàng và quá trình chuyến đi
- **Công nghệ**: Spring Boot, MySQL, RabbitMQ
- **Endpoints**: `/api/order/create`, `/api/order/aboard`, `/api/order/arrive`, `/api/order/paying`

### Eureka Server
- **Port**: 8761
- **Chức năng**: Service discovery và đăng ký
- **Công nghệ**: Spring Cloud Netflix Eureka

### API Gateway
- **Port**: 8800
- **Chức năng**: Điều hướng request, cân bằng tải
- **Công nghệ**: Spring Cloud Gateway

---

## 📌 API Endpoints

### UC Service
- `GET /users/{id}`: Lấy thông tin người dùng theo ID
- `GET /users`: Lấy danh sách tất cả người dùng

### Position Service
- `POST /api/position/update`: Cập nhật vị trí tài xế
    - Params: `driverId`, `longitude`, `latitude`
- `GET /api/position/match`: Tìm tài xế gần khách hàng
    - Params: `longitude`, `latitude`

### Intention Service
- `POST /api/intentions/place`: Đặt yêu cầu đặt xe
    - Body: `{ "userId": int, "startLongitude": double, "startLatitude": double, "destLongitude": double, "destLatitude": double }`
- `POST /api/intentions/confirm`: Xác nhận yêu cầu đặt xe
    - Params: `driverId`, `intentionId`
- `GET /api/intentions/all`: Lấy tất cả yêu cầu đặt xe
- `GET /api/intentions/{id}`: Lấy thông tin yêu cầu đặt xe theo ID

### Order Service
- `GET /api/order/all`: Lấy tất cả đơn hàng
- `POST /api/order/create`: Tạo đơn hàng mới
    - Body: IntentionVo
- `POST /api/order/aboard`: Cập nhật trạng thái khách hàng đã lên xe
    - Params: `orderId`
- `POST /api/order/arrive`: Cập nhật trạng thái đã đến nơi
    - Params: `orderId`
- `POST /api/order/paying`: Cập nhật trạng thái thanh toán
    - Params: `orderId`
- `POST /api/order/cancel`: Hủy đơn hàng
    - Params: `orderId`

---

## 🧪 Demo

### Luồng Đặt Xe Mẫu

1. Tạo yêu cầu đặt xe:
   ```bash
   curl -X POST http://localhost:8800/api/intentions/place \
   -H "Content-Type: application/json" \
   -d '{"userId": 1, "startLongitude": 105.8521, "startLatitude": 21.0245, "destLongitude": 105.8315, "destLatitude": 21.0277}'
   ```

2. Xác nhận yêu cầu đặt xe (Tài xế):
   ```bash
   curl -X POST "http://localhost:8800/api/intentions/confirm?driverId=11&intentionId=1"
   ```

3. Cập nhật trạng thái chuyến đi (Lên xe):
   ```bash
   curl -X POST "http://localhost:8800/api/order/aboard?orderId=<order_id>"
   ```

4. Cập nhật trạng thái chuyến đi (Đến nơi):
   ```bash
   curl -X POST "http://localhost:8800/api/order/arrive?orderId=<order_id>"
   ```

5. Thanh toán:
   ```bash
   curl -X POST "http://localhost:8800/api/order/paying?orderId=<order_id>"
   ```

---

## 👩‍💻 Thành viên nhóm 4

| Họ Tên | MSSV | Vai trò |
|--------|------|---------|
---

## 📚 Tài liệu tham khảo

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Cloud Netflix](https://spring.io/projects/spring-cloud-netflix)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Data Redis](https://spring.io/projects/spring-data-redis)
- [RabbitMQ](https://www.rabbitmq.com/)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)
