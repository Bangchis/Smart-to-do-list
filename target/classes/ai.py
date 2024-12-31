# import google.generativeai as genai
# import sys
# import json


# def remove_fields_from_json_array(json_array):
#     """
#     Loại bỏ các trường không cần thiết khỏi mỗi đối tượng trong một mảng JSON.

#     Args:
#         json_array: Một danh sách các đối tượng JSON.

#     Returns:
#         Danh sách mới đã được loại bỏ các trường không cần thiết.
#     """
#     try:
#         cleaned_json_array = []

#         for item in json_array:
#             cleaned_item = item.copy()  # Tạo bản sao để không sửa đổi đối tượng gốc
#             for field in ["taskID", "status", "workspaceId", "reminderIds", "assigneesIds"]:
#                 if field in cleaned_item:
#                     del cleaned_item[field]
#             cleaned_json_array.append(cleaned_item)

#         return cleaned_json_array
#     except Exception as e:
#         print(f"Lỗi không xác định khi xử lý JSON: {e}", file=sys.stderr)
#         sys.exit(1)


# def configure_genai():
#     """
#     Thiết lập cấu hình cho API Generative AI.
#     """
#     try:
#         genai.configure(api_key="AIzaSyDPGNrIoFWJqKfPgTocPqZBCg39mEXe4yA")
#     except Exception as e:
#         print(f"Error configuring Generative AI: {e}", file=sys.stderr)
#         sys.exit(1)


# def main():
#     try:
#         # Đọc đầu vào từ stdin
#         input_data = sys.stdin.read().strip()
#         if not input_data:
#             print("Error: No input data provided", file=sys.stderr)
#             sys.exit(1)

#         # Parse JSON từ đầu vào
#         try:
#             tasks = json.loads(input_data)
#         except json.JSONDecodeError as e:
#             print("Failed to parse JSON:", str(e), file=sys.stderr)
#             sys.exit(1)

#         # Loại bỏ các trường không cần thiết khỏi JSON
#         cleaned_json = remove_fields_from_json_array(tasks)

#         # Tạo prompt cho Generative AI
#         based = (
#             "Hiện tại chúng tôi đang cần gợi ý các nhiệm vụ (task) cho người dùng ứng dụng smart-to-do-list của chúng tôi, "
#             "chúng tôi sẽ cung cấp cho bạn lịch sử task mà người dùng tạo nên dưới dạng 1 chuỗi JSON; trong từng JSON sẽ có chứa các trường: "
#             '"title","priority","tagsname","dueDate", "description". '
#             "Việc của bạn là dựa vào lịch sử đó và đưa ra gợi ý vài task mới. "
#             "Lưu ý, người dùng là một học sinh bình thường trong giai đoạn ôn thi. "
#             "Tôi muốn bạn trả ra kết quả dưới dạng một chuỗi JSON và trong từng JSON có các trường: "
#             '"title","priority","tagsname","dueDate", "description". '
#             "Dưới đây là lịch sử các task của người dùng; lưu ý kết quả của bạn chỉ cầ json, và trong trường tagsname của mỗi phần tử đều có thêm thể loại Personal, và các tagsname là tiếng anhanh:"
#         )
#         prompt = based + "\n" + json.dumps(cleaned_json, indent=2)

#         # Cấu hình Generative AI
#         configure_genai()

#         # Gọi API Generative AI
#         model = genai.GenerativeModel("gemini-2.0-flash-exp")
#         response = model.generate_content(prompt)

#         # Kiểm tra phản hồi từ API
#         if not hasattr(response, "text") or not response.text.strip():
#             print("Error: API response is empty", file=sys.stderr)
#             sys.exit(1)

#         # In kết quả từ API ra stdout
#         print(response.text.strip())

#     except Exception as e:
#         print(f"Unexpected error: {str(e)}", file=sys.stderr)
#         sys.exit(1)


# if __name__ == "__main__":
#     main()

import sys
import json
import os
import subprocess
import datetime


def remove_fields_from_json_array(json_array):
    """Ví dụ hàm remove_fields_from_json_array (placeholder)"""
    # Giữ nguyên, hoặc tuỳ chỉnh theo nhu cầu
    cleaned = []
    for item in json_array:
        # Bỏ các trường không cần thiết, ví dụ:
        allowed_keys = {"title", "priority",
                        "tagsname", "dueDate", "description"}
        cleaned_item = {k: item[k] for k in item if k in allowed_keys}
        cleaned.append(cleaned_item)
    return cleaned


def main():

    try:
        # Đọc đầu vào từ stdin
        input_data = sys.stdin.read().strip()
        if not input_data:
            print("Error: No input data provided", file=sys.stderr)
            sys.exit(1)

        # Parse JSON từ đầu vào
        try:
            tasks = json.loads(input_data)
        except json.JSONDecodeError as e:
            print("Failed to parse JSON:", str(e), file=sys.stderr)
            sys.exit(1)

        # Loại bỏ các trường không cần thiết khỏi JSON
        cleaned_json = remove_fields_from_json_array(tasks)

        # Định nghĩa danh sách các thẻ hợp lệ
        valid_tags = [
            "Personal",
            "Work",
            "Health",
            "Fitness",
            "Education",
            "Finance",
            "Shopping",
            "Travel",
            "Family",
            "Friends",
            "Hobbies",
            "Home",
            "Project",
            "Meetings",
            "Deadlines",
            "Self-Care",
            "Entertainment",
            "Appointments",
            "Goals",
            "Chores"
        ]

        # Lấy đối tượng datetime chứa cả ngày và giờ hiện tại
        now = datetime.datetime.now()

        # Định dạng ngày giờ theo ý muốn với strftime()
        formatted_date_time = now.strftime(
            "%Y-%m-%d %H:%M:%S")  # YYYY-MM-DD HH:MM:SS

        formatted_date = now.strftime("%d/%m/%Y")  # DD/MM/YYYY

        # Tạo prompt cho ChatGPT
        based = (
            "Hiện tại chúng tôi đang cần gợi ý các nhiệm vụ (task) cho người dùng ứng dụng smart-to-do-list của chúng tôi, "
            "chúng tôi sẽ cung cấp cho bạn lịch sử task mà người dùng tạo nên dưới dạng 1 chuỗi JSON; trong từng JSON sẽ có chứa các trường: "
            '"title","priority","tagsname","dueDate", "description". '
            "Việc của bạn là dựa vào lịch sử đó và đưa ra gợi ý vài task mới. "
            "Lưu ý, người dùng là một học sinh bình thường trong giai đoạn ôn thi. "
            "Tôi muốn bạn trả ra kết quả dưới dạng một chuỗi JSON và trong từng JSON có các trường: "
            '"title","priority","tagsname","dueDate", "description". '
            "Dưới đây là lịch sử các task của người dùng; lưu ý kết quả của bạn chỉ cần JSON, "
            "và trong trường tagsname của mỗi phần tử đều có thêm thể loại Personal, "
            "và các tagsname phải thuộc danh sách sau, và có ít nhất 3 tags mỗi tasktask: " +
            ", ".join(valid_tags) + ". "
            "Các tagsname phải được viết bằng tiếng Anh, ngoài ra tôi sẽ cung cấp cho bạn ngày giờ cụ thể hiện tại để bạn đưa ra duedate chính xác hơn: " +
            str(formatted_date) + ". Dưới đây là lịch sử các task của người dùng: "
        )
        prompt = based + "\n" + \
            json.dumps(cleaned_json, indent=2, ensure_ascii=False)

        # Lấy OpenAI API Key từ biến môi trường
        openai_api_key = os.environ.get("OPENAI_API_KEY")

        # Debug: Kiểm tra xem biến OPENAI_API_KEY có nội dung gì (ẩn một phần nếu muốn)
        # print("[DEBUG] OPENAI_API_KEY repr:", repr(openai_api_key))

        if not openai_api_key:
            print("Error: OPENAI_API_KEY environment variable not set.",
                  file=sys.stderr)
            sys.exit(1)

        # Tạo payload cho API ChatCompletion (LOẠI BỎ response_format)
        request_body = {
            "model": "gpt-4o-mini",  # Bạn có thể thay đổi model nếu cần
            "messages": [
                {
                    "role": "system",
                    "content": "You are an assistant that helps generate to-do tasks based on user history."
                },
                {
                    "role": "user",
                    "content": prompt
                }
            ],
            "temperature": 0.7,
            "max_tokens": 500
        }

        data_json = json.dumps(request_body, ensure_ascii=False)

        # Debug: In payload để kiểm tra JSON
        # print("\n[DEBUG] JSON Payload being sent:")
        # print(data_json)

        # Tạo lệnh cURL (thêm -X POST để rõ ràng)
        curl_cmd = [
            "curl",
            "-v",            # Bật chế độ debug của curl
            "-X", "POST",    # Bảo đảm cURL sẽ gửi POST
            "https://api.openai.com/v1/chat/completions",
            "-H", "Content-Type: application/json",
            # strip() để loại bỏ \n, nếu có
            "-H", f"Authorization: Bearer {openai_api_key.strip()}",
            "-d", data_json
        ]

        # # In lệnh cURL để debug (EXACT ARGS)
        # print("\n[DEBUG] cURL Command EXACT (args):")
        # print(curl_cmd)

        # # In lệnh cURL dưới dạng chuỗi để tiện copy-paste
        # print("\n[DEBUG] cURL Command as string:")
        # print(" ".join(curl_cmd))

        # Gọi lệnh cURL bằng subprocess
        try:
            result = subprocess.run(
                curl_cmd,
                capture_output=True,
                text=True,
                check=True
            )

            # # In ra kết quả từ curl (stdout và stderr)
            # print("\n[DEBUG] cURL Response (stdout):")
            # print(result.stdout)

            # print("\n[DEBUG] cURL Error (stderr):")
            # print(result.stderr)

        except subprocess.CalledProcessError as e:
            print("\n[ERROR] Failed to call OpenAI API via curl.",
                  file=sys.stderr)
            print("[ERROR] cURL stdout:", e.stdout, file=sys.stderr)
            print("[ERROR] cURL stderr:", e.stderr, file=sys.stderr)
            sys.exit(1)

        # Parse kết quả trả về từ API
        try:
            response_json = json.loads(result.stdout)

            # # In toàn bộ phản hồi JSON để debug
            # print("\n[DEBUG] Full API Response JSON:")
            # print(json.dumps(response_json, indent=2, ensure_ascii=False))

        except json.JSONDecodeError as e:
            print(
                "\n[ERROR] Unable to parse response from OpenAI API.", file=sys.stderr)
            # print("[DEBUG] Raw response:", result.stdout, file=sys.stderr)
            sys.exit(1)

        # Kiểm tra xem phản hồi có chứa trường 'error' không
        if 'error' in response_json:
            print("\n[ERROR] API returned an error:",
                  response_json['error']['message'], file=sys.stderr)
            sys.exit(1)

        # Kiểm tra xem API có trả về 'choices' đúng cấu trúc không
        if (
            "choices" not in response_json
            or len(response_json["choices"]) == 0
        ):
            print(
                "\n[ERROR] API response is missing 'choices' or it's empty.", file=sys.stderr)
            # print("[DEBUG] Full API Response:", json.dumps(
            #     response_json, indent=2, ensure_ascii=False), file=sys.stderr)
            sys.exit(1)

        # Kiểm tra xem mỗi lựa chọn có chứa 'message' và 'content' không
        for idx, choice in enumerate(response_json["choices"]):
            if "message" not in choice or "content" not in choice["message"]:
                print(
                    f"\n[ERROR] Choice at index {idx} is missing 'message.content'.", file=sys.stderr)
                # print("[DEBUG] Choice:", json.dumps(
                #     choice, indent=2, ensure_ascii=False), file=sys.stderr)
                sys.exit(1)

        # Nếu tất cả đều ổn, lấy nội dung trả về
        api_response = response_json["choices"][0]["message"]["content"].strip(
        )

        # In kết quả từ API ra stdout
        # print("\n[DEBUG] API Response:")
        print(api_response)

    except Exception as e:
        print(f"Unexpected error: {str(e)}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()
