# Kiến trúc Hệ thống

## Tổng quan
- Hệ thống đặt xe và quản lý đơn hàng được xây dựng với kiến trúc microservices.
- Cho phép mở rộng theo từng dịch vụ và phát triển độc lập.
- Dễ dàng triển khai, nâng cấp và bảo trì.

## Các thành phần hệ thống
- **Service UC**: Quản lý thông tin người dùng và xác thực.
- **Service Position**: Theo dõi vị trí tài xế và tìm tài xế phù hợp.
- **Service Intention**: Quản lý yêu cầu đặt xe và ghép tài xế với khách hàng.
- **Service Order**: Quản lý đơn hàng và thanh toán.
- **API Gateway**: Điều hướng request, cân bằng tải, xác thực.
- **Eureka Server**: Đăng ký và phát hiện dịch vụ.

## Giao tiếp
- Giao tiếp đồng bộ qua REST API
- Giao tiếp bất đồng bộ qua RabbitMQ
- Service Discovery thông qua Eureka
- Sử dụng Circuit Breaker để xử lý lỗi

## Luồng dữ liệu
1. Khách hàng gửi yêu cầu đặt xe qua API Gateway
2. Intention Service xử lý yêu cầu và tìm tài xế phù hợp qua Position Service
3. Khi tài xế xác nhận, Intention Service gửi thông báo qua RabbitMQ
4. Order Service nhận thông báo và tạo đơn hàng mới
5. Quá trình chuyến đi được theo dõi và cập nhật thông qua Order Service

## Sơ đồ
```
+-------------+        +----------------+
|   Client    | <----> |  API Gateway   |
+-------------+        +----------------+
                              |
                              v
                      +----------------+
                      | Eureka Server  |
                      +----------------+
                              |
          +------------------+------------------+
          |                  |                  |
+-----------------+ +----------------+ +----------------+
|   UC Service    | |Position Service| |Intention Service|
+-----------------+ +----------------+ +----------------+
         |                 |                  |
         v                 v                  v
  +-----------+     +-----------+     +-----------+
  |  MySQL DB |     |  MySQL DB |     |  MySQL DB |
  +-----------+     +-----------+     +-----------+
                          |                  |
                          v                  v
                    +-----------+     +-----------+     +-----------+
                    |   Redis   |     |  RabbitMQ | --> |Order Service|
                    +-----------+     +-----------+     +-----------+
                                                              |
                                                              v
                                                        +-----------+
                                                        |  MySQL DB |
                                                        +-----------+
```

## Khả năng mở rộng và chịu lỗi
- Mỗi service có thể mở rộng độc lập
- Sử dụng Circuit Breaker để chịu lỗi
- Service Discovery tự động qua Eureka
- Cân bằng tải giữa các instance qua API Gateway