from agents.router_agent import route

if __name__ == "__main__":
    while True:
        user_input = input("\n질문을 입력하세요 (종료하려면 'exit'): ")
        if user_input.lower() == "exit":
            break

        response = route(user_input)

        print("\n=== 최종 응답 ===")
        print(response)
