# 📅 PassMaker
멘토와의 1:1 화상 멘토링 및 멘토링 내용 AI 요약 서비스

---

## 🧾 프로젝트 개요
- 프로젝트명: PassMaker
- 개발 기간: 2025년 7월 4일 ~ (진행 중)
- 플랫폼: 웹 (※ 모바일 반응형은 추후 적용 예정)

---

## 🎯 목표
직무 선택과 커리어 고민에 직면한 사람들을 위해, 다양한 분야의 전문가(멘토)와 1:1로 연결되어
실시간 멘토링을 예약하고 진행할 수 있는 직무 기반 화상 멘토링 예약 플랫폼을 제공합니다.

멘토는 전문성 검증 및 승인을 거친 유저만 등록되며, 멘티는 멘토의 공개된 일정 중 원하는 시간을 선택해
1시간 단위의 실시간 멘토링을 요청할 수 있습니다. WebRTC를 통한 실시간 화상 대화를 지원하며, 멘토링 도중 AI가 음성을 텍스트로 변환하고,
세션 종료 후 AI 기반 요약으로 회고와 인사이트를 제공합니다.

---

# 📁 프로젝트 구조

```
📦 root/
├── backend/                            # 🌱 Spring Boot 기반 백엔드 서버
│   └── src/
│       └── main/
│           ├── java/org/example/backend/
│           │   ├── BackendApplication.java     # 🎯 메인 엔트리 포인트
│           │   ├── admin/                      # 🧑‍💼 관리자 기능 (멘토 승인, 통계 등)
│           │   ├── auth/                       # 🔐 인증/인가 (JWT, OAuth2 등)
│           │   ├── common/                     # ⚒️ 공통 상수, 응답 포맷, 예외 처리 등
│           │   ├── config/                     # ⚙️ Spring 설정 (시큐리티, CORS 등)
│           │   ├── external/                   # 🌐 외부 API 연동 (Toss, GPT 등)
│           │   ├── mentor/                     # 🙋‍♂️ 멘토 신청, 정보 관리
│           │   ├── payment/                    # 💳 결제 처리 및 환불
│           │   ├── reservation/                # 📅 멘토링 예약 관리
│           │   ├── review/                     # 📝 멘토링 리뷰 작성
│           │   ├── room/                       # 🎥 화상 면접방 생성/입장
│           │   └── user/                       # 🙍‍♂️ 유저 (회원가입, 소셜 로그인 등)
│           └── resources/
│               └── application.yml             # 📄 설정 파일
│
├── frontend/                          # 💻 React 기반 프론트엔드
│   ├── public/                        # 🌐 정적 파일 (favicon, html 등)
│   └── src/
│       ├── admin/                    # 🧑‍💼 관리자 전용 UI
│       ├── assets/                  # 🖼️ 이미지, 아이콘 등 정적 리소스
│       ├── auth/                    # 🔐 로그인, 회원가입 등 인증 관련
│       ├── common/                  # ⚒️ 공통 모듈
│       │   ├── api/                 # 📡 Axios 등 API 통신
│       │   ├── lib/                 # 🧠 유틸 함수 모음 (e.g. axios.js)
│       │   └── pages/
│       ├── mentor/                  # 🙋‍♂️ 멘토 관련 (신청, 정보 관리 등)
│       ├── reservation/             # 📅 예약 UI 및 기능
│       ├── review/                  # 📝 리뷰 화면
│       ├── room/                    # 🎥 실시간 면접방 관련 UI
│       ├── routes/                  # 🔀 라우팅 설정
│       ├── styles/                  # 🎨 CSS, Tailwind 스타일
│       └── user/                    # 🙍‍♂️ 유저 정보 관련 페이지
```

## 📄 프로젝트 문서

| 문서 종류            | 링크                                                                 |
|---------------------|----------------------------------------------------------------------|
| 📝 프로젝트 기획 문서 (Notion) | [📎 바로가기](https://super-bridge-61f.notion.site/PassMaker-21f54de8ddbb8045a8c9ea72f190cd1b?source=copy_link) |

---

## 🖥️ 서비스 소개
|   메인 화면  |  로그인  |   멘토링방   |
|:--------:|:------:|:--------:|
| <img width="310" alt="main page" src="https://github.com/DDongHyun00/PassMaker_fe/blob/dev/src/assets/final_images/%EB%A9%94%EC%9D%B8%ED%8E%98%EC%9D%B4%EC%A7%801.png" /> |<img width="310" alt="로그인" src="https://github.com/DDongHyun00/PassMaker_fe/blob/dev/src/assets/final_images/%EB%A1%9C%EA%B7%B8%EC%9D%B8%ED%8E%98%EC%9D%B4%EC%A7%80.png" /> | <img width="310" alt="멘토링방 페이지" src="https://github.com/DDongHyun00/PassMaker_fe/blob/dev/src/assets/final_images/%EB%A9%98%ED%86%A0%EB%A7%81%EB%B0%A9%ED%8E%98%EC%9D%B4%EC%A7%801.png" />|

| 문의 페이지  |   결제   | 관리자 |
|:-------:|:--------:|:------:|
| <img width="310" alt="마이 페이지" src="https://github.com/DDongHyun00/PassMaker_fe/blob/dev/src/assets/final_images/%EB%AC%B8%EC%9D%98%ED%8E%98%EC%9D%B4%EC%A7%801.png" />| <img width="310" alt="상세 페이지" src="https://github.com/DDongHyun00/PassMaker_fe/blob/dev/src/assets/final_images/%EB%A9%98%ED%86%A0%EB%A7%81%EA%B2%B0%EC%A0%9C%ED%8E%98%EC%9D%B4%EC%A7%801.png" />| <img width="310" alt="찜 및 시청 기록" src="https://github.com/DDongHyun00/PassMaker_fe/blob/dev/src/assets/final_images/%EA%B4%80%EB%A6%AC%EC%9E%90_%EB%8C%80%EC%8B%9C%EB%B3%B4%EB%93%9C%ED%8E%98%EC%9D%B4%EC%A7%80.png" />|


---

## 👤 멤버 소개

| Github | [<img src="https://avatars.githubusercontent.com/DDongHyun00" width="100"/>](https://github.com/DDongHyun00) | [<img src="https://avatars.githubusercontent.com/PangDDoA" width="100"/>](https://github.com/PangDDoA) | [<img src="https://avatars.githubusercontent.com/yubeen777" width="100"/>](https://github.com/yubeen777) | [<img src="https://avatars.githubusercontent.com/eunsujang3028" width="100"/>](https://github.com/eunsujang3028) |
|--------|--------------------|--------------------|----------------------|--------------------------|
| **이름** | 김동현 | 정대현 | 장유빈 | 장은수 |
| **담당** | 팀장 | BackEnd | BackEnd | Front |


---

## 📑 프로젝트 규칙

### Branch Strategy
> - main / dev / 브랜치 기본 생성 

### Git Convention
> 1. 적절한 커밋 접두사 작성
> 2. 커밋 메시지 내용 작성
> 3. 내용 뒤에 이슈 (#이슈 번호)와 같이 작성하여 이슈 연결

### Pull Request
> ### Title
> * 제목은 '[Feat] 홈 페이지 구현'과 같이 작성합니다.

> ### PR Type
  > - [ ] FEAT: 새로운 기능 구현
  > - [ ] FIX: 버그 수정
  > - [ ] DOCS: 문서 수정
  > - [ ] STYLE: 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우
  > - [ ] REFACTOR: 코드 리펙토링
  > - [ ] CHORE: 빌드 업무 수정, 패키지 매니저 수정

### Code Convention
>BE
> - 패키지명 전체 소문자
> - 클래스명, CamelCase
> - 클래스 이름 명사 사용


> FE
> - 클래스명, CamelCase
> - Event handler 사용 (ex. handle ~)
> - export방식 (ex. export default ~)
> - 화살표 함수 사용


### Communication Rules
> - Notion 활용
