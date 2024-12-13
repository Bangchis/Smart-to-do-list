import google.generativeai as genai
import sys
import json


def remove_fields_from_json_array(json_array_string):
    """
    Loại bỏ các trường 'taskID', 'status', và 'workspaceId' khỏi mỗi đối tượng trong một mảng JSON.

    Args:
        json_array_string: Chuỗi JSON biểu diễn một mảng các đối tượng.

    Returns:
        Một chuỗi JSON mới biểu diễn mảng đã được xử lý, hoặc None nếu có lỗi.
    """
    try:
        json_array = json.loads(json_array_string)
        cleaned_json_array = []

        for item in json_array:
            cleaned_item = item.copy()  # Tạo bản sao để không sửa đổi đối tượng gốc
            for field in ["taskID", "status", "workspaceId", "reminderIds", "assigneesIds"]:
                if field in cleaned_item:
                    del cleaned_item[field]
            cleaned_json_array.append(cleaned_item)

        # trả về chuỗi json đã format
        return json.dumps(cleaned_json_array, indent=2)
    except json.JSONDecodeError:
        print("Lỗi: Chuỗi JSON không hợp lệ.")
        return None
    except Exception as e:
        print(f"Lỗi không xác định: {e}")
        return None


# Ví dụ sử dụng
json_string = """
[
    {
        "reminderIds": [],
        "description": "fsdaf",
        "assigneesIds": [],
        "tagsname": [
            "Personal",
            "thang bang ngu"
        ],
        "priority": "HIGH",
        "title": "đi chơi với hằng lúc 8h thứ 3 tuần sau",
        "taskID": "454afcf4-2a1c-4159-ad4a-3493f018bbd9",
        "status": "New",
        "workspaceId": "d68bbe29-5915-4e05-9aa8-76ee68094fd4"
    },
    {
        "reminderIds": [],
        "dueDate": {
            "seconds": 1733504400,
            "nanos": 0
        },
        "description": "This is the first task and I'm excited",
        "assigneesIds": [],
        "tagsname": [
            "Personal",
            "Gym",
            "Work",
            "Errand",
            "linh tinh",
            "thang ngu"
        ],
        "priority": "HIGH",
        "title": "go to the gym 5th time",
        "taskID": "4692b906-3fc4-42f5-b19d-338534efb7b3",
        "status": "New",
        "workspaceId": "d68bbe29-5915-4e05-9aa8-76ee68094fd4"
    }
]
"""

cleaned_json = remove_fields_from_json_array(json_string)

if cleaned_json:
    print(cleaned_json)
# try:
#     # Đọc đầu vào từ stdin
#     input_data = sys.stdin.read()
#     tasks = json.loads(input_data)

# except json.JSONDecodeError as e:
#     print("Failed to parse JSON:", str(e), file=sys.stderr)
#     sys.exit(1)
# except Exception as e:
#     print("Error:", str(e), file=sys.stderr)
#     sys.exit(1)

#     # Thiết lập cấu hình API Generative AI
# genai.configure(api_key="AIzaSyDPGNrIoFWJqKfPgTocPqZBCg39mEXe4yA")

# # Định nghĩa prompt
# based = """Lịch sử task mà người dùng tạo nên, việc của bạn là dựa vào lịch sử đó và đưa ra gợi ý 6 tasks, lưu ý tôi muốn bạn gợi ý nhiệm vụ cho tôi, phải phù hợp với việc tôi là một học sinh bình thường trong giai đoạn ôn thi. Dưới đây là lịch sử task của người dùng, tôi muốn bạn trả ra kết quả dưới dạng json:
# 6:00 AM : Thức Dậy và Tập Thể Dục Sáng

# 7:00 AM : Bữa Sáng và Chuẩn Bị

# 8:00 AM : Huấn Luyện và Luyện Tập

# 12:00 PM : Bữa Trưa và Nghỉ Ngơi

# 1:00 PM : Phục Hồi và Thư Giãn

# 3:00 PM : Huấn Luyện Cá Nhân và Thực Hành Kỹ Năng

# 5:00 PM : Bữa Tối Nhẹ và Thời Gian Cá Nhân

# 6:00 PM : Kinh Doanh và Các Hoạt Động Ngoại Khóa

# 8:00 PM : ôn tập toán

# 10:00 PM: Ngủ"""

# # Tổng hợp lịch sử task

# prompt = based

# # Gọi Generative AI để tạo nội dung
# print("Calling Generative AI API...", file=sys.stderr)
# model = genai.GenerativeModel("gemini-1.5-pro")
# response = model.generate_content(prompt)

# # In kết quả từ API

# print(response.text)
