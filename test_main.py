from agents.router_agent import route

if __name__ == "__main__":
    user_input = "사내 헬스장은 어디에 있나요?"
    response = route(user_input)
    
    print("\n=== 최종 응답 ===")
    print(response)
