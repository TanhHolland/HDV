# Tài liệu Thiết kế Database — Hệ thống Đặt Xe HDV

Tài liệu mô tả chi tiết thiết kế **DynamoDB** cho hệ thống đặt xe, dựa trên luồng xử lý trong `thiet_ke_sequence.md` và kiến trúc trong `architecture.md`.

---

## 1. Tổng quan

Hệ thống sử dụng **Amazon DynamoDB** (local: DynamoDB Local / Floci) với **một logical database** tên **`nhom4_uc`**, gom toàn bộ bảng nghiệp vụ vào cùng namespace.

| Bảng DynamoDB | Mục đích |
|---|---|
| `t_user` | Tài khoản khách hàng / tài xế |
| `t_position` | Lịch sử vị trí tài xế mỗi lần `updatePosition` (kèm `status`) |
| `t_intention` | Yêu cầu đặt xe, giá, `idempotency_key` |
| `t_candidate` | Tài xế ứng viên sau match (`driver_id` → `t_user`) |
| `t_order` | Đơn đặt xe / trạng thái chuyến / thanh toán PayOS |

**Đã loại bỏ so với thiết kế cũ:**

| Bảng cũ | Lý do |
|---|---|
| `tb_poi` | Không dùng trong luồng đặt xe |
| `t_driver_status` | `t_position` đã lưu đủ thông tin mỗi lần update |
| `intention_intention` | Đổi tên → `t_intention` |
| `intention_candidate` | Đổi tên → `t_candidate` |
| `t_nhom4_order` | Đổi tên → `t_order` |

Dữ liệu được ghi/đọc xuyên suốt các luồng:
- **Luồng vị trí**: Position ghi `t_position` + Redis GEO.
- **Luồng đặt xe**: Intention ghi `t_intention`, `t_candidate`; Order ghi `t_order` sau RabbitMQ.
- **Tra cứu người dùng**: Đọc `t_user` trực tiếp hoặc qua UC Service REST `/users/{id}`.

### 1.1 Ánh xạ bảng → project (Gradle / Java 21)

| Bảng | Project (thư mục root) | Tên Eureka |
|---|---|---|
| `t_user` | `nhom4-user` | `UC-SERVICE` |
| `t_position` | `nhom4-position` | `POSITION-SERVICE` |
| `t_intention`, `t_candidate` | `nhom4-intention` | `INTENTION-SERVICE` |
| `t_order` | `nhom4-order` | `ORDER-SERVICE` |

---

## 2. Bảng `t_user`

### 2.1 Mục đích

Lưu tài khoản người dùng. `type` phân biệt **Customer** và **Driver**. `id` được dùng làm `customer_id` / `driver_id` ở các bảng khác.

### 2.2 Key Schema

| Thuộc tính | Loại | Vai trò |
|---|---|---|
| `id` | Number (N) | Partition Key |

Không có Sort Key.

### 2.3 Attributes

| Thuộc tính | Kiểu DynamoDB | Bắt buộc | Mô tả |
|---|---|---|---|
| `id` | N | ✅ | PK — định danh người dùng |
| `user_name` | S | ❌ | Tên hiển thị |
| `mobile` | S | ✅ | Số điện thoại |
| `province` | S | ❌ | Tỉnh/thành |
| `city` | S | ❌ | Thành phố |
| `district` | S | ❌ | Quận/huyện |
| `street` | S | ❌ | Đường |
| `origin_address` | S | ❌ | Địa chỉ đầy đủ |
| `type` | S | ✅ | `Customer` \| `Driver` |

### 2.4 Enum `Type`

| Giá trị | Ý nghĩa |
|---|---|
| `Customer` | Khách hàng đặt xe |
| `Driver` | Tài xế |

### 2.5 Ví dụ Item

```json
{
  "id": 11,
  "user_name": "Driver 1",
  "mobile": "0912345671",
  "province": "Hanoi",
  "city": "Hanoi",
  "district": "Cau Giay",
  "street": "Xuan Thuy",
  "origin_address": "45 Xuan Thuy, Cau Giay, Hanoi",
  "type": "Driver"
}
```

### 2.6 Access Patterns

| Pattern | Operation | Key / Index |
|---|---|---|
| Lấy user theo `id` | `GetItem` | PK: `id` |
| CRUD users | `PutItem` / `UpdateItem` / `DeleteItem` | PK: `id` |

---

## 3. Bảng `t_position`

### 3.1 Mục đích

Lưu **mỗi lần** tài xế gọi `updatePosition`. Bản ghi mới nhất của một `driver_id` đại diện cho vị trí và trạng thái hiện tại — **không cần bảng `t_driver_status` riêng**.

### 3.2 Key Schema

| Thuộc tính | Loại | Vai trò |
|---|---|---|
| `driver_id` | N | Partition Key |
| `upload_time` | S (ISO 8601) | Sort Key |

> Sort Key theo thời gian cho phép `Query` lịch sử và lấy bản ghi mới nhất (`ScanIndexForward=false`, `Limit=1`).

### 3.3 Global Secondary Index (GSI)

| Tên GSI | Partition Key | Sort Key | Mục đích |
|---|---|---|---|
| `tid-index` | `tid` | — | Tra cứu theo ID nội bộ (nếu cần) |

### 3.4 Attributes

| Thuộc tính | Kiểu | Bắt buộc | Mô tả |
|---|---|---|---|
| `driver_id` | N | ✅ | PK — tham chiếu `t_user.id` (Driver) |
| `upload_time` | S | ✅ | SK — thời điểm cập nhật |
| `tid` | N | ❌ | ID nội bộ (tuỳ chọn) |
| `position_longitude` | N | ❌ | Kinh độ |
| `position_latitude` | N | ❌ | Vĩ độ |
| `status` | S | ✅ | `ONLINE` \| `OFFLINE` \| `BUSY` |

### 3.5 Enum `Status`

| Giá trị | Ý nghĩa |
|---|---|
| `ONLINE` | Sẵn sàng nhận chuyến |
| `OFFLINE` | Không hoạt động |
| `BUSY` | Đang có chuyến |

### 3.6 Vòng đời dữ liệu

```
POST /api/position/update
        │
        ▼
PutItem → t_position (driver_id, upload_time, lon, lat, status)
        │
        ▼
GEOADD Redis key "Driver"
        │
        ▼
GET /api/position/match
  → GEORADIUS Redis
  → Query t_position (latest) theo driver_id
  → lọc status = ONLINE
```

### 3.7 Ví dụ Item

```json
{
  "driver_id": 11,
  "upload_time": "2026-08-13T10:00:00Z",
  "tid": 1001,
  "position_longitude": 105.8315,
  "position_latitude": 21.0277,
  "status": "ONLINE"
}
```

### 3.8 Access Patterns

| Pattern | Operation | Key / Index |
|---|---|---|
| Ghi vị trí mới | `PutItem` | PK `driver_id`, SK `upload_time` |
| Lấy vị trí mới nhất | `Query` | PK `driver_id`, `Limit=1`, sort desc |
| Lịch sử theo tài xế | `Query` | PK `driver_id` |

---

## 4. Bảng `t_intention`

### 4.1 Mục đích

Lưu yêu cầu đặt xe từ lúc tạo (`Inited`) đến xác nhận (`Confirmed`) hoặc thất bại (`Failed`).

**Không lưu** `id`, `user_name`, `mobile`, `type` của tài xế trên intention — thông tin tài xế nằm ở `t_candidate` và `t_user`. Sau confirm chỉ lưu `selected_driver_id` (FK → `t_user.id`).

### 4.2 Key Schema

| Thuộc tính | Loại | Vai trò |
|---|---|---|
| `mid` | N | Partition Key |

### 4.3 Global Secondary Index (GSI)

| Tên GSI | Partition Key | Sort Key | Mục đích |
|---|---|---|---|
| `idempotency-key-index` | `idempotency_key` | — | Tra cứu Idempotency-Key khi place |
| `customer-id-index` | `customer_id` | `updated` | Intention đang mở của khách |

### 4.4 Attributes

| Thuộc tính | Kiểu | Bắt buộc | Mô tả |
|---|---|---|---|
| `mid` | N | ✅ | PK — ID intention |
| `start_longitude` / `start_latitude` | N | ❌ | Điểm đón |
| `dest_longitude` / `dest_latitude` | N | ❌ | Điểm trả |
| `customer_id` | N | ✅ | FK → `t_user.id` (Customer) |
| `customer_name` | S | ❌ | Denormalize — hiển thị nhanh |
| `customer_mobile` | S | ❌ | Denormalize |
| `user_type` | S | ❌ | Thường `Customer` |
| `status` | S | ✅ | `IntentionStatus` |
| `idempotency_key` | S | ✅ | Header `Idempotency-Key` — đại diện tiếp nhận yêu cầu |
| `price` | N | ✅ | Giá cố định tính lúc đặt (VND) |
| `selected_driver_id` | N | ❌ | FK → `t_user.id` — set sau confirm |
| `version` | N | ❌ | Optimistic locking khi confirm |
| `updated` | S | ❌ | ISO 8601 — lần cập nhật cuối |

### 4.5 Enum `IntentionStatus`

| Giá trị | Ý nghĩa | Chuyển từ |
|---|---|---|
| `Inited` | Vừa tạo, chờ match | — |
| `Unconfirmed` | Đã có candidate, chờ confirm | `Inited` |
| `Confirmed` | Tài xế đã xác nhận | `Unconfirmed` |
| `Failed` | Không match được tài xế | `Inited` |

### 4.6 Idempotency-Key

| Quy tắc | Chi tiết |
|---|---|
| Nguồn | Header HTTP `Idempotency-Key: <uuid>` từ Client |
| Lưu trữ | Cột `idempotency_key` trên `t_intention` |
| Tra cứu | GSI `idempotency-key-index` — `Query` trước khi tạo mới |
| Hit | Trả về cùng `mid` — không `PutItem` thêm |
| Unique | Mỗi key chỉ map 1 intention (trong phạm vi `customer_id`) |

### 4.7 Vòng đời dữ liệu

```
POST /api/intentions/place (Idempotency-Key)
        │
        ▼
Query GSI idempotency-key-index
        │
        ├── Hit → trả mid cũ
        └── Miss → PutItem t_intention (Inited, price, idempotency_key)
                │
                ▼
        matchDriver → PutItem t_candidate (batch)
                │
                ▼
        UpdateItem status = Unconfirmed
                │
                ▼
POST /api/intentions/confirm
                │
                ▼
        UpdateItem status = Confirmed, selected_driver_id
                │
                ▼
        Publish RabbitMQ → Order Service
```

### 4.8 Ví dụ Item

```json
{
  "mid": 3,
  "start_longitude": 105.8135,
  "start_latitude": 21.0294,
  "dest_longitude": 105.7835,
  "dest_latitude": 21.0282,
  "customer_id": 3,
  "customer_name": "Le Van C",
  "customer_mobile": "0987654323",
  "user_type": "Customer",
  "status": "Confirmed",
  "idempotency_key": "abc-123-uuid",
  "price": 50000,
  "selected_driver_id": 13,
  "version": 2,
  "updated": "2026-08-13T10:05:00Z"
}
```

### 4.9 Access Patterns

| Pattern | Operation | Key / Index |
|---|---|---|
| Tạo intention | `PutItem` | PK `mid` |
| Idempotency lookup | `Query` | GSI `idempotency-key-index` |
| Cập nhật status / selected driver | `UpdateItem` (conditional) | PK `mid` |
| Intention đang mở của khách | `Query` | GSI `customer-id-index` |

---

## 5. Bảng `t_candidate`

### 5.1 Mục đích

Danh sách tài xế ứng viên cho một intention sau bước match. `driver_id` tham chiếu **`t_user.id`**.

### 5.2 Key Schema

| Thuộc tính | Loại | Vai trò |
|---|---|---|
| `intention_id` | N | Partition Key |
| `driver_id` | N | Sort Key — FK → `t_user.id` |

### 5.3 Attributes

| Thuộc tính | Kiểu | Bắt buộc | Mô tả |
|---|---|---|---|
| `intention_id` | N | ✅ | PK — FK → `t_intention.mid` |
| `driver_id` | N | ✅ | SK — FK → `t_user.id` |
| `cid` | N | ❌ | ID nội bộ (tuỳ chọn) |
| `longitude` / `latitude` | N | ❌ | Vị trí lúc match |
| `created` | S | ❌ | ISO 8601 |

> Tên / SĐT tài xế **không denormalize** ở đây — lấy qua `GetItem t_user` khi cần hiển thị hoặc gửi thông báo.

### 5.4 Access Patterns

| Pattern | Operation | Key / Index |
|---|---|---|
| Lưu danh sách sau match | `BatchWriteItem` | PK `intention_id`, SK `driver_id` |
| Kiểm tra driver ∈ candidates khi confirm | `GetItem` | PK + SK |
| List candidates của intention | `Query` | PK `intention_id` |

### 5.5 Ví dụ Item

```json
{
  "intention_id": 2,
  "driver_id": 11,
  "cid": 101,
  "longitude": 105.8315,
  "latitude": 21.0277,
  "created": "2026-08-13T10:01:00Z"
}
```

---

## 6. Bảng `t_order`

### 6.1 Mục đích

Đơn đặt xe sau khi tài xế confirm. Quản lý vòng đời chuyến qua `order_status` (`FlowState`) và thanh toán PayOS.

### 6.2 Key Schema

| Thuộc tính | Loại | Vai trò |
|---|---|---|
| `oid` | S (UUID) | Partition Key |

### 6.3 Global Secondary Index (GSI)

| Tên GSI | Partition Key | Sort Key | Mục đích |
|---|---|---|---|
| `intention-id-index` | `intention_id` | — | Tra cứu order theo intention |
| `customer-id-index` | `customer_id` | `opened` | Lịch sử đơn của khách |

### 6.4 Attributes

| Thuộc tính | Kiểu | Bắt buộc | Mô tả |
|---|---|---|---|
| `oid` | S | ✅ | PK — UUID đơn |
| `customer_id` | N | ✅ | FK → `t_user.id` |
| `customer_name` | S | ❌ | Denormalize |
| `customer_mobile` | S | ❌ | Denormalize |
| `driver_id` | N | ✅ | FK → `t_user.id` — từ `selected_driver_id` |
| `driver_name` | S | ❌ | Denormalize |
| `driver_mobile` | S | ❌ | Denormalize |
| `start_long` / `start_lat` | N | ✅ | Điểm đón |
| `dest_long` / `dest_lat` | N | ✅ | Điểm trả |
| `opened` | S | ❌ | ISO 8601 — thời điểm tạo |
| `order_status` | S | ✅ | `FlowState.stateId` |
| `intention_id` | S | ✅ | Tham chiếu `t_intention.mid` |
| `price` | N | ✅ | Giá copy từ `t_intention.price` lúc tạo đơn |
| `payos_order_code` | S | ❌ | Mã đơn PayOS (sau khi tạo link) |
| `payos_payment_link` | S | ❌ | URL thanh toán PayOS |
| `payos_transaction_id` | S | ❌ | ID giao dịch sau webhook |

### 6.5 Enum / State `FlowState`

| stateId | Ý nghĩa | Chuyển bởi |
|---|---|---|
| `WAITING_ABOARD` | Chờ đón khách | `createOrder` |
| `WAITING_ARRIVE` | Đang đi tới điểm trả | `aboard` |
| `UNPAY` | Đã đến nơi, chờ thanh toán PayOS | `arrive` |
| `PAYING` | Đang xử lý thanh toán | tạo link PayOS |
| `PAID` | Thanh toán thành công | webhook PayOS |
| `CANCELED` | Đã hủy | `cancel` (≤ 3 phút sau `opened`) |

### 6.6 Vòng đời dữ liệu

```
RabbitMQ IntentionVo
        │
        ▼
PutItem t_order — WAITING_ABOARD, price (copy từ intention)
        │
        ├── aboard()  → WAITING_ARRIVE
        ├── arrive()  → UNPAY
        ├── create PayOS link → PAYING
        ├── PayOS webhook → PAID
        └── cancel()  → CANCELED
```

### 6.7 Ví dụ Item

```json
{
  "oid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "customer_id": 3,
  "customer_name": "Le Van C",
  "customer_mobile": "0987654323",
  "driver_id": 13,
  "driver_name": "Driver 3",
  "driver_mobile": "0912345673",
  "start_long": 105.8135,
  "start_lat": 21.0294,
  "dest_long": 105.7835,
  "dest_lat": 21.0282,
  "opened": "2026-08-13T10:06:00Z",
  "order_status": "UNPAY",
  "intention_id": "3",
  "price": 50000,
  "payos_order_code": "ORDER-20260813-001",
  "payos_payment_link": "https://pay.payos.vn/web/..."
}
```

### 6.8 Access Patterns

| Pattern | Operation | Key / Index |
|---|---|---|
| Tạo đơn từ MQ | `PutItem` | PK `oid` |
| Cập nhật trạng thái / PayOS | `UpdateItem` (conditional) | PK `oid` |
| Get đơn | `GetItem` | PK `oid` |
| Order theo intention | `Query` | GSI `intention-id-index` |

---

## 7. Redis (bổ trợ — không thay DynamoDB)

| Key / Pattern | Kiểu | Service | Mục đích | Luồng |
|---|---|---|---|---|
| `Driver` | GEO | Position | Lưu (lon, lat, driverId); `GEORADIUS` 500m | §1 Position update, §2 Match |
| `nhom4.lock.intention{id}` | STRING (SET NX + TTL 30s) | Intention | Distributed lock khi confirm | §3 Confirm |

> **Idempotency-Key** lưu trên **`t_intention.idempotency_key`** (DynamoDB GSI), **không** dùng Redis làm nguồn sự thật chính.

Redis DB index (local config): `2`.

---

## 8. Tương tác giữa các thành phần và DynamoDB

```
┌─────────────────────────────────────────────────────────────────┐
│                     LUỒNG VỊ TRÍ                                  │
│  Position Service                                                 │
│    PutItem → t_position                                           │
│    GEOADD  → Redis "Driver"                                       │
│    GetItem → t_user (nếu cần validate driver)                   │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                     LUỒNG ĐẶT XE                                  │
│  Intention Service                                                │
│    Query GSI → idempotency_key (hit/miss)                         │
│    PutItem   → t_intention (Inited, price, idempotency_key)       │
│    BatchWrite→ t_candidate                                        │
│    UpdateItem→ t_intention (Unconfirmed / Confirmed / Failed)     │
│    LOCK      → Redis nhom4.lock.intention{id}                     │
│    SEND      → RabbitMQ queue "intention"                         │
│                                                                   │
│  Order Service                                                    │
│    CONSUME   → createOrder                                        │
│    GetItem   → t_user (customer, driver)                          │
│    PutItem   → t_order (WAITING_ABOARD, price)                    │
│    UpdateItem→ lifecycle + PayOS fields                           │
└─────────────────────────────────────────────────────────────────┘
```

---

## 9. Tóm tắt cấu hình bảng

| Thuộc tính | `t_user` | `t_position` | `t_intention` | `t_candidate` | `t_order` |
|---|---|---|---|---|---|
| Database | `nhom4_uc` | `nhom4_uc` | `nhom4_uc` | `nhom4_uc` | `nhom4_uc` |
| Partition Key | `id` | `driver_id` | `mid` | `intention_id` | `oid` |
| Sort Key | — | `upload_time` | — | `driver_id` | — |
| GSI | — | `tid-index` | `idempotency-key-index`, `customer-id-index` | — | `intention-id-index`, `customer-id-index` |
| FK chính | — | → `t_user.id` | → `t_user.id` (customer, selected_driver) | → `t_user.id`, `t_intention.mid` | → `t_user.id`, `t_intention.mid` |
| Enum / State | `Type` | `Status` | `IntentionStatus` | — | `FlowState` |
| Billing Mode | On-demand | On-demand | On-demand | On-demand | On-demand |

### 9.1 Liên kết tài liệu

| Tài liệu | Nội dung |
|---|---|
| `thiet_ke_sequence.md` | Luồng REST / RabbitMQ / Redis / PayOS |
| `thiet_ke_cauhinh.md` | DynamoDB endpoint, Docker, Gradle/Java 21, init script |
| `architecture.md` | Tổng quan kiến trúc |
