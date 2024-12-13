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


# # Ví dụ sử dụng
# json_string = """
# [
#     {
#         "reminderIds": [],
#         "description": "fsdaf",
#         "assigneesIds": [],
#         "tagsname": [
#             "Personal",
#             "thang bang ngu"
#         ],
#         "priority": "HIGH",
#         "title": "đi chơi với hằng lúc 8h thứ 3 tuần sau",
#         "taskID": "454afcf4-2a1c-4159-ad4a-3493f018bbd9",
#         "status": "New",
#         "workspaceId": "d68bbe29-5915-4e05-9aa8-76ee68094fd4"
#     },
#     {
#         "reminderIds": [],
#         "dueDate": {
#             "seconds": 1733504400,
#             "nanos": 0
#         },
#         "description": "This is the first task and I'm excited",
#         "assigneesIds": [],
#         "tagsname": [
#             "Personal",
#             "Gym",
#             "Work",
#             "Errand",
#             "linh tinh",
#             "thang ngu"
#         ],
#         "priority": "HIGH",
#         "title": "go to the gym 5th time",
#         "taskID": "4692b906-3fc4-42f5-b19d-338534efb7b3",
#         "status": "New",
#         "workspaceId": "d68bbe29-5915-4e05-9aa8-76ee68094fd4"
#     }
# ]
# """


try:
    # Đọc đầu vào từ stdin
    input_data = sys.stdin.read()
    tasks = json.loads(input_data)

except json.JSONDecodeError as e:
    print("Failed to parse JSON:", str(e), file=sys.stderr)
    sys.exit(1)
except Exception as e:
    print("Error:", str(e), file=sys.stderr)
    sys.exit(1)

# Thiết lập cấu hình API Generative AI
genai.configure(api_key="AIzaSyDPGNrIoFWJqKfPgTocPqZBCg39mEXe4yA")

# Định nghĩa prompt
based = """Hiện tại chúng tôi đang cần gợi ý các nhiệm vụ(task) cho người dùng ứng dựng smart-to-do-list của chúng tôi,
chúng tôi sẽ cung cấp cho bạn lịch sử task mà người dùng tạo nên dưới dạng 1 chuỗi json; trong từng json sẽ có chứa các field như sau: "title","priority","tagsname","dueDate", "description",
việc của bạn là dựa vào lịch sử đó và đưa ra gợi ý vài tasks cho tôi. Lưu ý,  người dùng là một học sinh bình thường trong giai đoạn ôn thi. Tôi muốn bạn trả ra kết quả dưới dạng 1 chuỗi json và trong từng json có các field:"title","priority","tagsname","dueDate", "description".
. Dưới đây là lịch sử các task của người dùng ở dạng 1 chuỗi json (sẽ có thể có một số field vô nghĩa, và riêng ở field dueDate bạn hãy gán nó với một Json object có dạng như này: "dueDate": { "seconds": 1734800400,
      "nanos": 0}, đây là cách thời gian của tôi được lưu trong cơ sở dữ liệu firebase) :
  
  
"""
cleaned_json = remove_fields_from_json_array(tasks)
# Tổng hợp lịch sử task

prompt = based + str(cleaned_json)

# Gọi Generative AI để tạo nội dung
print("Calling Generative AI API...", file=sys.stderr)
model = genai.GenerativeModel("gemini-1.5-pro")
response = model.generate_content(prompt)

# In kết quả từ API

print(response.text)
