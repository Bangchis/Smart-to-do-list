import google.generativeai as genai
import sys
import json

import datetime


# Lấy đối tượng datetime chứa cả ngày và giờ hiện tại
now = datetime.datetime.now()

# Định dạng ngày giờ theo ý muốn với strftime()
formatted_date_time = now.strftime("%Y-%m-%d %H:%M:%S")  # YYYY-MM-DD HH:MM:SS


formatted_date = now.strftime("%d/%m/%Y")  # DD/MM/YYYY


def generate_task_from_title(title, formatted_date_time):
    """
    Hàm giả lập logic xử lý tiêu đề và sinh các trường còn lại của task.
    """

    # Thiết lập cấu hình API Generative AI
    genai.configure(api_key="AIzaSyDPGNrIoFWJqKfPgTocPqZBCg39mEXe4yA")

    # Định nghĩa prompt
    # based = """Hiện tại chúng tôi đang cần gợi ý các nhiệm vụ(task) cho người dùng ứng dựng smart-to-do-list của chúng tôi,
    # chúng tôi sẽ cung cấp cho bạn lịch sử task mà người dùng tạo nên dưới dạng 1 chuỗi json; trong từng json sẽ có chứa các field như sau: "title","priority","tagsname","dueDate", "description",
    # việc của bạn là dựa vào lịch sử đó và đưa ra gợi ý vài tasks cho tôi. Lưu ý,  người dùng là một học sinh bình thường trong giai đoạn ôn thi. Tôi muốn bạn trả ra kết quả dưới dạng 1 chuỗi json và trong từng json có các field:"title","priority","tagsname","dueDate", "description".
    # . Dưới đây là lịch sử các task của người dùng ở dạng 1 chuỗi json (sẽ có thể có một số field vô nghĩa, và riêng ở field dueDate bạn hãy gán nó với một Json object có dạng như này: "dueDate": { "seconds": 1734800400,
    #     "nanos": 0}, đây là cách thời gian của tôi được lưu trong cơ sở dữ liệu firebase) :

    # """
    based = """Hiện tại tôi đang cần bạn tạo ra một task từ tiêu đề của task trong app smart to do list của chúng tôi, bây giờ tôi sẽ cung cấp cho bạn tiêu đề cụ thể của task đó và thời gian hiện tại, việc của bạn là trả về cho tôi một json có dạng như sau:
    
    {
  "title": "Study AI for the next exam",
  "description": "Generated description for task: Study AI for the next exam",
  "priority": "MEDIUM",
  "tagsname": ["AI", "Generated","PersonalPersonal"],
  "dueDate": {
    "seconds": 1734800400,
    "nanos": 0
  }
}
    . Bạn hãy suy luận xem từ đoạn title của người dùng nhập và thười gian hiện tại, thì nên điền thông tin như nào vào từng field này, nếu có những field không đủ dữ kiện để suy ra, bạn hãy thêm các giá trị mà bạn cho là hợp lí nhất. Lưu ý 
    priority chỉ có những loại như sau :"High Priority";"Medium Priority";"Low Priority". Đồng thời trong tagsname bạn luôn cần có tag Personal và tối đa 3 tags ; Dưới đây là đoạn title của người dùng nhập vào và thời gian hiện tại, lưu ý Chỉ in ra chuỗi json và khong cần giải thíchthích: 
    """
    ##########################################################################

    # Tổng hợp lịch sử task

    prompt = based + \
        str(title) + \
        f", thời gian người dùng nhập vào là:{formatted_date_time} "

    # Gọi Generative AI để tạo nội dung
    # print("Calling Generative AI API...", file=sys.stderr)
    model = genai.GenerativeModel("gemini-2.0-flash-exp")
    response = model.generate_content(prompt)

    # In kết quả từ API

    print(response.text)


try:
    # Đọc dòng văn bản từ stdin
    # print("Waiting for input title...", file=sys.stderr)
    title = sys.stdin.read().strip()  # Đọc toàn bộ đầu vào và xóa khoảng trắng thừa

    if not title:
        raise ValueError("Input title cannot be empty")
    # title = "go sleep 9PM tommorow"
    # Gọi hàm sinh task từ tiêu đề
    generated_task = generate_task_from_title(title, formatted_date_time)


except Exception as e:
    # Xử lý lỗi và in ra stderr
    error_response = {"error": str(e)}
    print(json.dumps(error_response), file=sys.stderr)
    sys.exit(1)
