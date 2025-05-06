-- Initialize databases
CREATE DATABASE IF NOT EXISTS nhom4;
CREATE DATABASE IF NOT EXISTS nhom4_uc;
CREATE DATABASE IF NOT EXISTS nhom4_position;
CREATE DATABASE IF NOT EXISTS nhom4_intention;
CREATE DATABASE IF NOT EXISTS nhom4_order;

USE nhom4_uc;

-- User table for both customers and drivers
CREATE TABLE IF NOT EXISTS t_user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(64),
    mobile VARCHAR(64) NOT NULL,
    province VARCHAR(64),
    city VARCHAR(64),
    district VARCHAR(64),
    street VARCHAR(255),
    origin_address VARCHAR(255),
    type VARCHAR(32) NOT NULL
);

-- Point of Interest table for landmark locations
CREATE TABLE IF NOT EXISTS tb_poi (
    id INT PRIMARY KEY AUTO_INCREMENT,
    link_man VARCHAR(64),
    shop_name VARCHAR(64),
    cell_phone VARCHAR(64) NOT NULL,
    longitude DOUBLE,
    latitude DOUBLE,
    province VARCHAR(64),
    city VARCHAR(64),
    district VARCHAR(64),
    street VARCHAR(255),
    street_number VARCHAR(64),
    shop_type INT,
    user_code VARCHAR(64),
    origin_address VARCHAR(255)
);

-- Insert sample customer data
INSERT INTO t_user (user_name, mobile, province, city, district, street, origin_address, type) VALUES
('Nguyen Van A', '0987654321', 'Hanoi', 'Hanoi', 'Cau Giay', 'Tran Duy Hung', '123 Tran Duy Hung, Cau Giay, Hanoi', 'Customer'),
('Tran Thi B', '0987654322', 'Hanoi', 'Hanoi', 'Dong Da', 'Xa Dan', '45 Xa Dan, Dong Da, Hanoi', 'Customer'),
('Le Van C', '0987654323', 'Hanoi', 'Hanoi', 'Ba Dinh', 'Lieu Giai', '67 Lieu Giai, Ba Dinh, Hanoi', 'Customer'),
('Pham Thi D', '0987654324', 'Hanoi', 'Hanoi', 'Hai Ba Trung', 'Nguyen Cong Tru', '89 Nguyen Cong Tru, Hai Ba Trung, Hanoi', 'Customer'),
('Hoang Van E', '0987654325', 'Hanoi', 'Hanoi', 'Long Bien', 'Ngoc Lam', '91 Ngoc Lam, Long Bien, Hanoi', 'Customer'),
('Do Thi F', '0987654326', 'Hanoi', 'Hanoi', 'Tay Ho', 'Quang An', '23 Quang An, Tay Ho, Hanoi', 'Customer'),
('Vu Van G', '0987654327', 'Hanoi', 'Hanoi', 'Nam Tu Liem', 'Pham Hung', '45 Pham Hung, Nam Tu Liem, Hanoi', 'Customer'),
('Dao Thi H', '0987654328', 'Hanoi', 'Hanoi', 'Thanh Xuan', 'Nguyen Trai', '67 Nguyen Trai, Thanh Xuan, Hanoi', 'Customer'),
('Bui Van I', '0987654329', 'Hanoi', 'Hanoi', 'Hoang Mai', 'Truong Dinh', '89 Truong Dinh, Hoang Mai, Hanoi', 'Customer'),
('Ly Thi K', '0987654330', 'Hanoi', 'Hanoi', 'Ha Dong', 'Nguyen Khang', '101 Nguyen Khang, Ha Dong, Hanoi', 'Customer');

-- Insert sample driver data
INSERT INTO t_user (user_name, mobile, province, city, district, street, origin_address, type) VALUES
('Driver 1', '0912345671', 'Hanoi', 'Hanoi', 'Cau Giay', 'Xuan Thuy', '45 Xuan Thuy, Cau Giay, Hanoi', 'Driver'),
('Driver 2', '0912345672', 'Hanoi', 'Hanoi', 'Dong Da', 'Chua Boc', '67 Chua Boc, Dong Da, Hanoi', 'Driver'),
('Driver 3', '0912345673', 'Hanoi', 'Hanoi', 'Ba Dinh', 'Kim Ma', '89 Kim Ma, Ba Dinh, Hanoi', 'Driver'),
('Driver 4', '0912345674', 'Hanoi', 'Hanoi', 'Hai Ba Trung', 'Bach Mai', '101 Bach Mai, Hai Ba Trung, Hanoi', 'Driver'),
('Driver 5', '0912345675', 'Hanoi', 'Hanoi', 'Long Bien', 'Bo De', '123 Bo De, Long Bien, Hanoi', 'Driver'),
('Driver 6', '0912345676', 'Hanoi', 'Hanoi', 'Tay Ho', 'Thuy Khue', '145 Thuy Khue, Tay Ho, Hanoi', 'Driver'),
('Driver 7', '0912345677', 'Hanoi', 'Hanoi', 'Nam Tu Liem', 'Tran Huu Duc', '167 Tran Huu Duc, Nam Tu Liem, Hanoi', 'Driver'),
('Driver 8', '0912345678', 'Hanoi', 'Hanoi', 'Thanh Xuan', 'Le Van Luong', '189 Le Van Luong, Thanh Xuan, Hanoi', 'Driver'),
('Driver 9', '0912345679', 'Hanoi', 'Hanoi', 'Hoang Mai', 'Linh Dam', '211 Linh Dam, Hoang Mai, Hanoi', 'Driver'),
('Driver 10', '0912345680', 'Hanoi', 'Hanoi', 'Ha Dong', 'To Huu', '233 To Huu, Ha Dong, Hanoi', 'Driver');

-- Insert sample POI data
INSERT INTO tb_poi (link_man, shop_name, cell_phone, longitude, latitude, province, city, district, street, street_number, shop_type, user_code, origin_address) VALUES
('Manager A', 'Lotte Center', '0123456789', 105.8135, 21.0294, 'Hanoi', 'Hanoi', 'Ba Dinh', 'Dao Tan', '54', 1, 'LC001', 'Lotte Center, 54 Dao Tan, Ba Dinh, Hanoi'),
('Manager B', 'Vincom Center', '0123456790', 105.8573, 21.0207, 'Hanoi', 'Hanoi', 'Dong Da', 'Pham Ngoc Thach', '2', 1, 'VC001', 'Vincom Center, 2 Pham Ngoc Thach, Dong Da, Hanoi'),
('Manager C', 'AEON Mall', '0123456791', 105.7835, 21.0282, 'Hanoi', 'Hanoi', 'Long Bien', 'Co Linh', '27', 1, 'AM001', 'AEON Mall, 27 Co Linh, Long Bien, Hanoi'),
('Manager D', 'Times City', '0123456792', 105.8693, 20.9956, 'Hanoi', 'Hanoi', 'Hai Ba Trung', 'Minh Khai', '458', 1, 'TC001', 'Times City, 458 Minh Khai, Hai Ba Trung, Hanoi'),
('Manager E', 'Royal City', '0123456793', 105.8154, 21.0019, 'Hanoi', 'Hanoi', 'Thanh Xuan', 'Nguyen Trai', '72A', 1, 'RC001', 'Royal City, 72A Nguyen Trai, Thanh Xuan, Hanoi'),
('Manager F', 'The Garden', '0123456794', 105.7864, 21.0392, 'Hanoi', 'Hanoi', 'Nam Tu Liem', 'Me Tri', '99', 1, 'TG001', 'The Garden, 99 Me Tri, Nam Tu Liem, Hanoi'),
('Manager G', 'Savico Long Bien', '0123456795', 105.8913, 21.0487, 'Hanoi', 'Hanoi', 'Long Bien', 'Nguyen Van Linh', '14', 1, 'SL001', 'Savico Long Bien, 14 Nguyen Van Linh, Long Bien, Hanoi'),
('Manager H', 'Trang Tien Plaza', '0123456796', 105.8521, 21.0245, 'Hanoi', 'Hanoi', 'Hoan Kiem', 'Trang Tien', '24', 1, 'TT001', 'Trang Tien Plaza, 24 Trang Tien, Hoan Kiem, Hanoi'),
('Manager I', 'Vincom Metropolis', '0123456797', 105.7984, 21.0332, 'Hanoi', 'Hanoi', 'Ba Dinh', 'Lieu Giai', '29', 1, 'VM001', 'Vincom Metropolis, 29 Lieu Giai, Ba Dinh, Hanoi'),
('Manager J', 'Aeon Ha Dong', '0123456798', 105.7559, 20.9833, 'Hanoi', 'Hanoi', 'Ha Dong', 'Duong Noi', '30', 1, 'AH001', 'Aeon Ha Dong, 30 Duong Noi, Ha Dong, Hanoi');

-- Initialize Position Service database
USE nhom4_position;

CREATE TABLE IF NOT EXISTS t_position (
    tid INT PRIMARY KEY AUTO_INCREMENT,
    position_longitude DOUBLE,
    position_latitude DOUBLE,
    status VARCHAR(32) NOT NULL,
    driver_id VARCHAR(32),
    upload_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_driver_status (
    d_id INT PRIMARY KEY,
    id INT,
    user_name VARCHAR(64),
    mobile VARCHAR(64),
    type VARCHAR(32),
    current_longitude DOUBLE,
    current_latitude DOUBLE,
    status VARCHAR(32) NOT NULL,
    update_time TIMESTAMP
);

-- Insert initial driver positions in central Hanoi
INSERT INTO t_driver_status (d_id, id, user_name, mobile, type, current_longitude, current_latitude, status, update_time) VALUES
(11, 11, 'Driver 1', '0912345671', 'Driver', 105.8315, 21.0277, 'ONLINE', NOW()),
(12, 12, 'Driver 2', '0912345672', 'Driver', 105.8435, 21.0227, 'ONLINE', NOW()),
(13, 13, 'Driver 3', '0912345673', 'Driver', 105.8135, 21.0294, 'ONLINE', NOW()),
(14, 14, 'Driver 4', '0912345674', 'Driver', 105.8573, 21.0207, 'ONLINE', NOW()),
(15, 15, 'Driver 5', '0912345675', 'Driver', 105.7835, 21.0282, 'ONLINE', NOW()),
(16, 16, 'Driver 6', '0912345676', 'Driver', 105.8693, 20.9956, 'ONLINE', NOW()),
(17, 17, 'Driver 7', '0912345677', 'Driver', 105.8154, 21.0019, 'BUSY', NOW()),
(18, 18, 'Driver 8', '0912345678', 'Driver', 105.7864, 21.0392, 'ONLINE', NOW()),
(19, 19, 'Driver 9', '0912345679', 'Driver', 105.8521, 21.0245, 'OFFLINE', NOW()),
(20, 20, 'Driver 10', '0912345680', 'Driver', 105.7984, 21.0332, 'ONLINE', NOW());

-- Initialize historical position data
INSERT INTO t_position (position_longitude, position_latitude, status, driver_id, upload_time) VALUES
(105.8315, 21.0277, 'ONLINE', '11', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(105.8415, 21.0267, 'ONLINE', '11', DATE_SUB(NOW(), INTERVAL 45 MINUTE)),
(105.8315, 21.0277, 'ONLINE', '11', NOW()),
(105.8435, 21.0227, 'ONLINE', '12', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(105.8335, 21.0237, 'ONLINE', '12', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(105.8435, 21.0227, 'ONLINE', '12', NOW()),
(105.8135, 21.0294, 'ONLINE', '13', DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(105.8135, 21.0294, 'ONLINE', '13', NOW()),
(105.8573, 21.0207, 'ONLINE', '14', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(105.8573, 21.0207, 'ONLINE', '14', NOW()),
(105.7835, 21.0282, 'ONLINE', '15', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(105.7835, 21.0282, 'ONLINE', '15', NOW());

-- Initialize Intention Service database
USE nhom4_intention;

CREATE TABLE IF NOT EXISTS intention_intention (
    mid INT PRIMARY KEY AUTO_INCREMENT,
    start_longitude DOUBLE,
    start_latitude DOUBLE,
    dest_longitude DOUBLE,
    dest_latitude DOUBLE,
    customer_id INT,
    customer_name VARCHAR(64),
    customer_mobile VARCHAR(64),
    user_type VARCHAR(64),
    status VARCHAR(32) NOT NULL,
    id INT,
    user_name VARCHAR(64),
    mobile VARCHAR(64),
    type VARCHAR(64),
    updated TIMESTAMP
);

CREATE TABLE IF NOT EXISTS intention_candidate (
    cid INT PRIMARY KEY AUTO_INCREMENT,
    intention_id INT,
    driver_id INT,
    driver_name VARCHAR(64),
    driver_mobile VARCHAR(64),
    longitude DOUBLE,
    latitude DOUBLE,
    created TIMESTAMP,
    FOREIGN KEY (intention_id) REFERENCES intention_intention(mid)
);

-- Initialize sample intentions
INSERT INTO intention_intention (start_longitude, start_latitude, dest_longitude, dest_latitude, customer_id, customer_name, customer_mobile, user_type, status, updated)
VALUES
(105.8521, 21.0245, 105.8315, 21.0277, 1, 'Nguyen Van A', '0987654321', 'Customer', 'Inited', NOW()),
(105.7984, 21.0332, 105.8573, 21.0207, 2, 'Tran Thi B', '0987654322', 'Customer', 'Unconfirmed', NOW()),
(105.8135, 21.0294, 105.7835, 21.0282, 3, 'Le Van C', '0987654323', 'Customer', 'Confirmed', NOW()),
(105.8693, 20.9956, 105.8154, 21.0019, 4, 'Pham Thi D', '0987654324', 'Customer', 'Failed', NOW());

-- Add confirmed driver to the confirmed intention
UPDATE intention_intention 
SET id = 13, user_name = 'Driver 3', mobile = '0912345673', type = 'Driver' 
WHERE mid = 3;

-- Add candidates for the unconfirmed intention
INSERT INTO intention_candidate (intention_id, driver_id, driver_name, driver_mobile, longitude, latitude, created)
VALUES
(2, 11, 'Driver 1', '0912345671', 105.8315, 21.0277, NOW()),
(2, 12, 'Driver 2', '0912345672', 105.8435, 21.0227, NOW()),
(2, 20, 'Driver 10', '0912345680', 105.7984, 21.0332, NOW());

-- Initialize Order Service database
USE nhom4_order;

CREATE TABLE IF NOT EXISTS t_nhom4_order (
    oid VARCHAR(36) PRIMARY KEY,
    customer_id INT,
    customer_name VARCHAR(64),
    customer_mobile VARCHAR(64),
    driver_id INT,
    driver_name VARCHAR(64),
    driver_mobile VARCHAR(64),
    start_long DOUBLE,
    start_lat DOUBLE,
    dest_long DOUBLE,
    dest_lat DOUBLE,
    opened TIMESTAMP,
    order_status VARCHAR(32) NOT NULL,
    intention_id VARCHAR(36)
);

-- Insert sample orders with different statuses
INSERT INTO t_nhom4_order (oid, customer_id, customer_name, customer_mobile, driver_id, driver_name, driver_mobile, start_long, start_lat, dest_long, dest_lat, opened, order_status, intention_id)
VALUES
(UUID(), 3, 'Le Van C', '0987654323', 13, 'Driver 3', '0912345673', 105.8135, 21.0294, 105.7835, 21.0282, NOW(), 'WAITING_ABOARD', '3'),
(UUID(), 5, 'Hoang Van E', '0987654325', 15, 'Driver 5', '0912345675', 105.8693, 20.9956, 105.8154, 21.0019, DATE_SUB(NOW(), INTERVAL 15 MINUTE), 'WAITING_ARRIVE', '5'),
(UUID(), 6, 'Do Thi F', '0987654326', 16, 'Driver 6', '0912345676', 105.7864, 21.0392, 105.8521, 21.0245, DATE_SUB(NOW(), INTERVAL 30 MINUTE), 'UNPAY', '6'),
(UUID(), 7, 'Vu Van G', '0987654327', 17, 'Driver 7', '0912345677', 105.8693, 20.9956, 105.8154, 21.0019, DATE_SUB(NOW(), INTERVAL 1 HOUR), 'WAITING_COMMENT', '7'),
(UUID(), 9, 'Bui Van I', '0987654329', 19, 'Driver 9', '0912345679', 105.8521, 21.0245, 105.7984, 21.0332, DATE_SUB(NOW(), INTERVAL 2 HOUR), 'CLOSED', '9'),
(UUID(), 10, 'Ly Thi K', '0987654330', 20, 'Driver 10', '0912345680', 105.7984, 21.0332, 105.8315, 21.0277, DATE_SUB(NOW(), INTERVAL 5 MINUTE), 'CANCELED', '10');