# 📊 Phân tích và Thiết kế Hệ thống Đặt Xe

## 1. 🎯 Bài toán

Hệ thống cần giải quyết bài toán tìm tài xế phù hợp cho khách hàng có nhu cầu đặt xe, quản lý toàn bộ quá trình từ khi đặt xe đến khi hoàn thành chuyến đi và thanh toán.

### Người dùng
- **Khách hàng**: Người cần dịch vụ đặt xe
- **Tài xế**: Người cung cấp dịch vụ xe
- **Quản trị viên**: Quản lý hệ thống

### Mục tiêu chính
- Tạo nền tảng kết nối khách hàng và tài xế
- Tối ưu quá trình ghép tài xế dựa trên vị trí và các yếu tố khác
- Theo dõi toàn bộ quá trình chuyến đi
- Đảm bảo an toàn và tin cậy cho người dùng

### Dữ liệu xử lý
- Thông tin người dùng (khách hàng, tài xế)
- Dữ liệu vị trí theo thời gian thực
- Thông tin đơn hàng và thanh toán
- Dữ liệu đánh giá và phản hồi

## 2. 🧩 Các Microservices

| Service | Chức năng | Công nghệ |
|---------|-----------|-----------|
| UC Service | Quản lý thông tin người dùng, đăng ký, xác thực | Spring Boot, MySQL |
| Position Service | Cập nhật và theo dõi vị trí tài xế, tìm tài xế gần khách hàng | Spring Boot, Redis, MySQL |
| Intention Service | Xử lý yêu cầu đặt xe, tìm tài xế phù hợp | Spring Boot, MySQL, Redis, RabbitMQ |
| Order Service | Quản lý đơn hàng, thanh toán và trạng thái chuyến đi | Spring Boot, MySQL, RabbitMQ |
| Eureka Server | Đăng ký và phát hiện dịch vụ | Spring Cloud Netflix Eureka |
| API Gateway | Điều hướng yêu cầu từ client đến services | Spring Cloud Gateway |

## 3. 🔄 Giao tiếp giữa các Service

- **API Gateway ⟷ UC Service**: REST API, xác thực người dùng
- **API Gateway ⟷ Position Service**: REST API, cập nhật và truy vấn vị trí
- **API Gateway ⟷ Intention Service**: REST API, xử lý đặt xe
- **API Gateway ⟷ Order Service**: REST API, quản lý đơn hàng
- **Intention Service ⟷ Position Service**: REST API, tìm tài xế gần khách hàng
- **Intention Service → Order Service**: Giao tiếp bất đồng bộ qua RabbitMQ
- **Service Discovery**: Tất cả services đăng ký với Eureka Server

## 4. 🗂️ Thiết kế Dữ liệu

### UC Service
- **User**: Thông tin người dùng chung
    - id, userName, mobile, type (Customer/Driver), địa chỉ, v.v.
- **POI**: Thông tin điểm địa lý quan trọng
    - id, tên, vị trí, loại, v.v.

### Position Service
- **Position**: Lịch sử vị trí tài xế
    - tid, driverId, vị trí, thời gian, trạng thái
- **DriverStatus**: Trạng thái và vị trí hiện tại của tài xế
    - dId, thông tin tài xế, vị trí hiện tại, trạng thái, thời gian cập nhật

### Intention Service
- **Intention**: Yêu cầu đặt xe
    - mid, khách hàng, vị trí đón/trả, trạng thái, tài xế được chọn
- **Candidate**: Danh sách tài xế ứng viên cho mỗi yêu cầu
    - cid, intentionId, driverId, vị trí, thời gian

### Order Service
- **Order**: Đơn hàng
    - oid, thông tin khách hàng, thông tin tài xế, vị trí đón/trả, thời gian, trạng thái, intentionId

## 5. 🔐 Bảo mật

- Xác thực người dùng qua API Gateway
- Mã hóa dữ liệu nhạy cảm
- Kiểm tra quyền truy cập dựa trên vai trò
- Xác thực API giữa các services

## 6. 📦 Kế hoạch Triển khai

- Sử dụng Docker và Docker Compose để đóng gói và quản lý các services
- Cấu hình môi trường qua file `.env`
- Khởi tạo cơ sở dữ liệu tự động qua script
- Mỗi service có Dockerfile riêng

## 7. 🎨 Sơ đồ Luồng Đặt Xe

```
+-------------+        +----------------+
| Khách hàng  | -----> |  API Gateway   | -----> +----------------+
+-------------+        +----------------+        | UC Service     |
                               |                 | (Xác thực)     |
                               v                 +----------------+
                        +----------------+
                        |Intention Service|
                        +----------------+
                               |
                               v
                        +----------------+
                        |Position Service | (Tìm tài xế gần)
                        +----------------+
                               |
                               v
                        +----------------+
                        |Intention Service| (Ghép tài xế)
                        +----------------+
                               |
                               v
                        +----------------+
                        |    RabbitMQ    |
                        +----------------+
                               |
                               v
                        +----------------+
                        | Order Service  | (Tạo đơn hàng)
                        +----------------+
```

## ✅ Tổng kết

Kiến trúc microservices được chọn để đáp ứng các yêu cầu:
- **Khả năng mở rộng**: Mỗi service có thể mở rộng độc lập
- **Phát triển độc lập**: Các team có thể làm việc đồng thời trên các service khác nhau
- **Khả năng chịu lỗi**: Lỗi ở một service không ảnh hưởng đến toàn bộ hệ thống
- **Công nghệ đa dạng**: Mỗi service có thể sử dụng công nghệ phù hợp nhất với chức năng

Hệ thống đảm bảo hiệu suất cao và trải nghiệm người dùng tốt thông qua việc sử dụng Redis cho dữ liệu thời gian thực và RabbitMQ cho giao tiếp bất đồng bộ.