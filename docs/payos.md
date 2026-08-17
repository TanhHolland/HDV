# Cách hoạt động payOS Checkout

payOS Checkout là một giao diện dựng sẵn, giúp giảm thời gian phát triển.

## Luồng hoạt động[​](#luồng-hoạt-động "Đường dẫn trực tiếp đến Luồng hoạt động")

* Bước 1: Khách hàng thực hiện mua hàng trên Website hoặc ứng dụng của merchant và lựa chọn thanh toán trực tuyến Napas 247 cho đơn hàng.
* Bước 2: Website hoặc ứng dụng của merchant tiến hành gọi [tạo link thanh toán](/docs/api/.md#tag/payment-request/operation/payment-request), payOS sẽ kiểm tra dữ liệu và trả về kết quả chứa link thanh toán. Khi hệ thống của merchant nhận kết quả link thanh toán cần chuyển hướng khách hàng của bạn đến trang checkout của payOS bằng cách mở link thanh toán từ kết quả.
* Bước 3: Khách hàng sử dụng ứng dụng ngân hàng để quét mã VietQR từ link thanh toán.
* Bước 4: Giao dịch ghi nhận thành công tại ngân hàng, payOS sẽ trả kết quả thành công về `returnUrl` gồm: trạng thái, mã đơn hàng, mã link thanh toán, ... Từ kết quả nhận được trên `returnUrl` Website hoặc ứng dụng của merchant hiển thị giao diện thành công.
* Bước 5: Sau khi có kết quả ở giao diện, đồng thời payOS sẽ gửi một [kết quả](/docs/tich-hop-webhook/kiem-tra-du-lieu-voi-signature.md) với đầy đủ thông tin thanh toán tới Webhook của cửa hàng được thiết lập trên <https://my.payos.vn>, sau đó merchant cập nhật trạng thái đơn hàng phù hợp.

<!-- -->

## Cách tích hợp payOS với hệ thống của Merchant[​](#cách-tích-hợp-payos-với-hệ-thống-của-merchant "Đường dẫn trực tiếp đến Cách tích hợp payOS với hệ thống của Merchant")

* Code để [tạo link thanh toán](/docs/api/.md#tag/payment-request/operation/payment-request)
* Code xử lý `returnUrl` và `cancelUrl` để nhận thông báo kết quả Thanh toán và Huỷ đơn hàng trên giao diện.
* Code [webhook](https://www.redhat.com/en/topics/automation/what-is-a-webhook) để nhận kết quả thanh toán của một đơn hàng.

## Low-code[​](#low-code "Đường dẫn trực tiếp đến Low-code")

payOS Checkout với yêu cầu ít code và lựa chọn tốt nhất để tích hợp thanh toán bởi những tính năng có sẵn. Lựa chọn hiển thị giao diện Checkout:

* [Chuyển hướng đến trang payOS Checkout](/docs/checkout/quick-start-payos-hosted-page.md)
* [Mở dialog giao diện thanh toán](/docs/sample/react)
* [Nhúng giao diện thanh toán](/docs/checkout/quick-start-payos-embedded-form.md) vào website hoặc ứng dụng của bạn.

# Webhook thông tin thanh toán

[![payOS Logo](https://payos.vn/wp-content/uploads/2025/06/Casso-payOSLogo-1.svg)](https://payos.vn)

* Webhook thanh toán
  * EventWebhook nhận thông tin thanh toán

[API docs by Redocly](https://redocly.com/redoc/)

# payOS Payment Webhook API<!-- --> (<!-- -->latest<!-- -->)

payOS support

<!-- -->

:

<!-- -->

<support@payos.vn> URL: <https://payos.vn>

<!-- -->

[Terms of Service](https://payos.vn/thoa-thuan-su-dung/)

Webhook API cho hệ thống thanh toán payOS.

### Trước khi bắt đầu

* Bạn đã tạo một tài khoản <https://my.payos.vn>.
* Bạn đã xác thực một doanh nghiệp hoặc cá nhân trên <https://my.payos.vn>, [xem hướng dẫn](https://payos.vn/docs/huong-dan-su-dung/xac-thuc-to-chuc/)
* Bạn đã tạo một kênh thanh toán, [xem hướng dẫn](https://payos.vn/docs/huong-dan-su-dung/tao-kenh-thanh-toan/).

### Môi trường

* Production: <https://api-merchant.payos.vn>

Đăng ký chương trình đối tác tích hợp payOS [Tại đây](https://payos.vn/chuong-trinh-doi-tac-tich-hop/)

## [](#tag/payment-webhook)Webhook thanh toán

Webhook thanh toán

## [](#tag/payment-webhook/operation/payment-webhook)Webhook nhận thông tin thanh toán<!-- --> <!-- -->Webhook

Webhook của cửa hàng dùng để nhận dữ liệu thanh toán từ payOS, [Dữ liệu mẫu](https://payos.vn/docs/tich-hop-webhook/kiem-tra-du-lieu-voi-signature/)

##### Request Body schema: application/json

|                   |                                                                                                                                    |
| ----------------- | ---------------------------------------------------------------------------------------------------------------------------------- |
| coderequired      | stringMã lỗi                                                                                                                       |
| descrequired      | stringThông tin lỗi                                                                                                                |
| successrequired   | boolean                                                                                                                            |
| datarequired      | object                                                                                                                             |
| signaturerequired | stringChữ kí để kiểm tra thông tin, [chi tiết dữ liệu mẫu](https://payos.vn/docs/tich-hop-webhook/kiem-tra-du-lieu-voi-signature/) |

### Responses

**200<!-- -->**

Phản hồi trạng thái mã 2XX để xác nhận webhook gửi thành công

### <!-- -->Request samples<!-- -->

* Payload

Content type

application/json

Copy

Expand all  Collapse all

`{
"code": "00",
"desc": "success",
"success": true,
"data": {
"orderCode": 123,
"amount": 3000,
"description": "VQRIO123",
"accountNumber": "12345678",
"reference": "TF230204212323",
"transactionDateTime": "2023-02-04 18:25:00",
"currency": "VND",
"paymentLinkId": "124c33293c43417ab7879e14c8d9eb18",
"code": "00",
"desc": "Thành công",
"counterAccountBankId": "",
"counterAccountBankName": "",
"counterAccountName": "",
"counterAccountNumber": "",
"virtualAccountName": "",
"virtualAccountNumber": ""
},
"signature": "8d8640d802576397a1ce45ebda7f835055768ac7ad2e0bfb77f9b8f12cca4c7f"
}`
