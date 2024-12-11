import google.generativeai as genai

# Thay "YOUR_API_KEY" bằng API Key thực tế của bạn
genai.configure(api_key="AIzaSyDPGNrIoFWJqKfPgTocPqZBCg39mEXe4yA")

# Thiết lập cấu hình sinh nội dung


# Định nghĩa tên mô hình với định dạng đầy đủ

# Khởi tạo mô hình GenerativeModel với tên mô hình đã chỉnh sửa


# Định nghĩa prompt dựa trên lịch sử task của người dùng
based = """Lịch sử task mà người dùng tạo nên, việc của bạn là dựa vào lịch sử đó và đưa ra gợi ý 6 tasks, lưu ý tôi muốn bạn gợi ý nhiệm vụ cho tôi, phải phù hợp với việc tôi là một học sinh bình thường trong giai đoạn ôn thi. Dưới đây là lịch sử task của người dùng, tôi muốn bạn trả ra kết quả dưới dạng json:
6:00 AM : Thức Dậy và Tập Thể Dục Sáng

7:00 AM : Bữa Sáng và Chuẩn Bị

8:00 AM : Huấn Luyện và Luyện Tập

12:00 PM : Bữa Trưa và Nghỉ Ngơi

1:00 PM : Phục Hồi và Thư Giãn

3:00 PM : Huấn Luyện Cá Nhân và Thực Hành Kỹ Năng

5:00 PM : Bữa Tối Nhẹ và Thời Gian Cá Nhân

6:00 PM : Kinh Doanh và Các Hoạt Động Ngoại Khóa

8:00 PM : ôn tập toán

10:00 PM: Ngủ"""

# Tạo danh sách các phần của prompt (ở đây chỉ có một phần)
prompt_parts = [f"{based}" + " tôi muốn định dạng kết quả từng task của tôi ở dạng sau:  { time: 8:00 AM - 12:00 PM,      task: Ôn tập Toán: Tập trung vào các dạng bài tập đã được học trong tuần, ưu tiên các phần trọng tâm trong kỳ thi.,      description: Sử dụng các tài liệu ôn tập, đề kiểm tra cũ để luyện tập.  Có thể chia nhỏ thời gian thành các khoảng 50 phút học, 10 phút nghỉ ngơi.}"]

model = genai.GenerativeModel("gemini-1.5-pro")
response = model.generate_content(f"{based}")
print(response.text)

# import sys
# import google.generativeai as genai

# # Cấu hình API Key
# genai.configure(api_key="YOUR_API_KEY")

# # Lấy prompt từ tham số dòng lệnh
# if len(sys.argv) < 2:
#     print("Error: No prompt provided.")
#     sys.exit(1)

# prompt = sys.argv[1]

# # Sử dụng prompt để gọi Gemini
# model = genai.GenerativeModel("gemini-1.5-pro")
# response = model.generate_content(prompt)

# # In kết quả JSON
# print(response.text)
