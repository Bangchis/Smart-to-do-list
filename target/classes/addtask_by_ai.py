import sys
import json
import os
import subprocess
import datetime


def generate_task_from_title(title, formatted_date_time):
    """
    Hàm sử dụng OpenAI API để sinh nội dung task dựa trên tiêu đề và thời gian hiện tại.
    """
    # Lấy API key từ biến môi trường
    openai_api_key = os.getenv("OPENAI_API_KEY")
    if not openai_api_key:
        raise ValueError("OPENAI_API_KEY environment variable is not set")

    # Danh sách các thẻ hợp lệ
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

    # Định nghĩa prompt
    based = f"""Hiện tại tôi đang cần bạn tạo ra một task từ tiêu đề của task trong app smart to do list của chúng tôi. Bây giờ tôi sẽ cung cấp cho bạn tiêu đề cụ thể của task đó và thời gian hiện tại. Việc của bạn là trả về một JSON có dạng như sau:

{{
  "title": "Study AI for the next exam",
  "description": "Generated description for task: Study AI for the next exam",
  "priority": "MEDIUM",
  "tagsname": ["Project", "Goals"", "Personal"],
  "dueDate": {{
    "seconds": 1734800400,
    "nanos": 0
  }}
}}

Bạn hãy suy luận dựa trên tiêu đề và thời gian để điền thông tin vào các trường này. Nếu có những trường không đủ dữ kiện để suy ra, hãy thêm giá trị hợp lý nhất. Lưu ý:
- Priority chỉ có thể là: \"High Priority\", \"Medium Priority\", hoặc \"Low Priority\".
- Tagsname chỉ được chứa tối đa 3 thẻ và luôn phải có thẻ \"Personal\". Các thẻ khác phải nằm trong danh sách hợp lệ sau: {', '.join(valid_tags)}.
- Thời gian DueDate phải được định dạng theo giây và nano giây, và thời gian phải hợp lý với nội dung title kết hợp với thời gian hiện tại, múi giờ của người dùng là GMT +7

Tiêu đề: {title}
Thời gian hiện tại: {formatted_date_time}
Lưu ý: Chỉ trả về JSON và không giải thích gì thêm.
"""

    # Tạo payload cho API ChatCompletion
    request_body = {
        "model": "gpt-4o",
        "messages": [
            {"role": "system", "content": "You are an assistant that generates tasks based on a given title and current time."},
            {"role": "user", "content": based}
        ],
        "temperature": 0.7,
        "max_tokens": 500
    }

    # Gọi lệnh cURL bằng subprocess
    data_json = json.dumps(request_body, ensure_ascii=False)
    curl_cmd = [
        "curl",
        "-v",
        "-X", "POST",
        "https://api.openai.com/v1/chat/completions",
        "-H", "Content-Type: application/json",
        "-H", f"Authorization: Bearer {openai_api_key.strip()}",
        "-d", data_json
    ]

    try:
        result = subprocess.run(
            curl_cmd,
            capture_output=True,
            text=True,
            check=True
        )

        # Parse kết quả trả về từ API
        response_json = json.loads(result.stdout)

        # Kiểm tra và lấy nội dung trả về
        if (
            "choices" in response_json
            and len(response_json["choices"]) > 0
            and "message" in response_json["choices"][0]
            and "content" in response_json["choices"][0]["message"]
        ):
            print(response_json["choices"][0]["message"]["content"].strip())
        else:
            raise ValueError("Invalid response structure from OpenAI API.")

    except subprocess.CalledProcessError as e:
        print(f"\n[ERROR] Failed to call OpenAI API via curl.", file=sys.stderr)
        print("[ERROR] cURL stdout:", e.stdout, file=sys.stderr)
        print("[ERROR] cURL stderr:", e.stderr, file=sys.stderr)
        sys.exit(1)
    except json.JSONDecodeError as e:
        print("\n[ERROR] Unable to parse response from OpenAI API.",
              file=sys.stderr)
        sys.exit(1)
    except Exception as e:
        print(f"Unexpected error: {str(e)}", file=sys.stderr)
        sys.exit(1)


def main():
    try:
        # Lấy đối tượng datetime chứa cả ngày và giờ hiện tại
        now = datetime.datetime.now()
        formatted_date_time = now.strftime(
            "%Y-%m-%d %H:%M:%S")  # YYYY-MM-DD HH:MM:SS

        # Đọc dòng văn bản từ stdin
        title = sys.stdin.read().strip()

        if not title:
            raise ValueError("Input title cannot be empty")

        # Gọi hàm sinh task từ tiêu đề
        generate_task_from_title(title, formatted_date_time)

    except Exception as e:
        # Xử lý lỗi và in ra stderr
        error_response = {"error": str(e)}
        print(json.dumps(error_response), file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()