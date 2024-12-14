import google.generativeai as genai
import sys
import json


def remove_fields_from_json_array(json_array):
    """
    Loại bỏ các trường không cần thiết khỏi mỗi đối tượng trong một mảng JSON.

    Args:
        json_array: Một danh sách các đối tượng JSON.

    Returns:
        Danh sách mới đã được loại bỏ các trường không cần thiết.
    """
    try:
        cleaned_json_array = []

        for item in json_array:
            cleaned_item = item.copy()  # Tạo bản sao để không sửa đổi đối tượng gốc
            for field in ["taskID", "status", "workspaceId", "reminderIds", "assigneesIds"]:
                if field in cleaned_item:
                    del cleaned_item[field]
            cleaned_json_array.append(cleaned_item)

        return cleaned_json_array
    except Exception as e:
        print(f"Lỗi không xác định khi xử lý JSON: {e}", file=sys.stderr)
        sys.exit(1)


def configure_genai():
    """
    Thiết lập cấu hình cho API Generative AI.
    """
    try:
        genai.configure(api_key="AIzaSyDPGNrIoFWJqKfPgTocPqZBCg39mEXe4yA")
    except Exception as e:
        print(f"Error configuring Generative AI: {e}", file=sys.stderr)
        sys.exit(1)


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

        # Tạo prompt cho Generative AI
        based = (
            "Hiện tại chúng tôi đang cần gợi ý các nhiệm vụ (task) cho người dùng ứng dụng smart-to-do-list của chúng tôi, "
            "chúng tôi sẽ cung cấp cho bạn lịch sử task mà người dùng tạo nên dưới dạng 1 chuỗi JSON; trong từng JSON sẽ có chứa các trường: "
            '"title","priority","tagsname","dueDate", "description". '
            "Việc của bạn là dựa vào lịch sử đó và đưa ra gợi ý vài task mới. "
            "Lưu ý, người dùng là một học sinh bình thường trong giai đoạn ôn thi. "
            "Tôi muốn bạn trả ra kết quả dưới dạng một chuỗi JSON và trong từng JSON có các trường: "
            '"title","priority","tagsname","dueDate", "description". '
            "Dưới đây là lịch sử các task của người dùng; lưu ý kết quả của bạn chỉ cầ json, và trong trường tagsname của mỗi phần tử đều có thêm thể loại Personal, và các tagsname là tiếng anhanh:"
        )
        prompt = based + "\n" + json.dumps(cleaned_json, indent=2)

        # Cấu hình Generative AI
        configure_genai()

        # Gọi API Generative AI
        model = genai.GenerativeModel("gemini-2.0-flash-exp")
        response = model.generate_content(prompt)

        # Kiểm tra phản hồi từ API
        if not hasattr(response, "text") or not response.text.strip():
            print("Error: API response is empty", file=sys.stderr)
            sys.exit(1)

        # In kết quả từ API ra stdout
        print(response.text.strip())

    except Exception as e:
        print(f"Unexpected error: {str(e)}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()
