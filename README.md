# 📅 PassMaker
멘토와의 1:1 화상 멘토링 및 멘토링 내용 AI 요약 서비스

---

## 🧾 프로젝트 개요
- 프로젝트명: PassMaker
- 개발 기간: 2025.07.04 ~ 08.06
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
# ⚙️ 기술 스택

### FE
<div align=center> 
  <img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=black">
  <img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">
  <img src="https://img.shields.io/badge/vite-646CFF?style=for-the-badge&logo=vite&logoColor=white">
  <br> 
  <img src="https://img.shields.io/badge/tailwindcss-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white">
  <img src="https://img.shields.io/badge/axios-5A29E4?style=for-the-badge&logo=axios&logoColor=white"> 
  <img src="https://img.shields.io/badge/npm-ED1C24?style=for-the-badge&logo=npm&logoColor=white">
  <br> 
  <img src="https://img.shields.io/badge/ESLint-4B32C3?style=for-the-badge&logo=ESLint&logoColor=white"> 
  <img src="https://img.shields.io/badge/prettier-FF69B4?style=for-the-badge&logo=prettier&logoColor=white">
</div>

### BE
<div align=center> <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/jpa-59666C?style=for-the-badge&logo=hibernate&logoColor=white"> <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <br> <img src="https://img.shields.io/badge/jwt-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white"> <img src="https://img.shields.io/badge/oauth2-EB5424?style=for-the-badge&logo=auth0&logoColor=white"> <img src="https://img.shields.io/badge/websocket-010101?style=for-the-badge&logo=websocket&logoColor=white"> <img src="https://img.shields.io/badge/STT-FF9900?style=for-the-badge&logo=googlecloud&logoColor=white"> <br> <img src="https://img.shields.io/badge/nginx-009639?style=for-the-badge&logo=nginx&logoColor=white"> </div>

### INFRA

<div align=center> <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/github actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"> <br> <img src="https://img.shields.io/badge/aws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white"> <img src="https://img.shields.io/badge/ec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"> <img src="https://img.shields.io/badge/rds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white"> <img src="https://img.shields.io/badge/s3-569A31?style=for-the-badge&logo=amazons3&logoColor=white"> </div>

### 공통
<div align=center> <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white"> <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white"> <img src="https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white"> </div>

---

# 🏗️ AWS 3-Tier Architecture for PassMaker

![AWS Architecture Diagram](https://github.com/DDongHyun00/PassMaker_fe/blob/dev/src/assets/final_images/infra.png)

---

## 🖥️ 서비스 소개
|   메인 화면  |  로그인  |   멘토링방   |
|:--------:|:------:|:--------:|
| <img width="150" alt="main page" src="https://github.com/DDongHyun00/PassMaker_fe/blob/dev/src/assets/final_images/%EB%A9%94%EC%9D%B8%ED%8E%98%EC%9D%B4%EC%A7%801.png" /> <img width="150" alt="main page" src="https://github.com/DDongHyun00/PassMaker_fe/blob/dev/src/assets/final_images/%EB%A9%94%EC%9D%B8%ED%8E%98%EC%9D%B4%EC%A7%802.png" /> |<img width="310" alt="로그인" src="https://github.com/DDongHyun00/PassMaker_fe/blob/dev/src/assets/final_images/%EB%A1%9C%EA%B7%B8%EC%9D%B8%ED%8E%98%EC%9D%B4%EC%A7%80.png" /> | <img width="310" alt="멘토링방 페이지" src="https://github.com/DDongHyun00/PassMaker_fe/blob/dev/src/assets/final_images/%EB%A9%98%ED%86%A0%EB%A7%81%EB%B0%A9%ED%8E%98%EC%9D%B4%EC%A7%801.png" />|

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

## 🌐 아키텍처 개요

본 프로젝트는 AWS 기반 3-Tier 구조로 구성된 웹 애플리케이션입니다. 각 계층은 다음과 같이 역할이 분리되어 있습니다:

Web (Frontend): 정적 콘텐츠 제공 (React, Vite, Tailwind)

WAS (Backend): API 서버, 인증/인가, 비즈니스 로직 처리 (Spring Boot)

DB (Database): MySQL RDS 데이터베이스

---

## 🧩 구성 요소 설명

| 구성 요소            | 설명 |
|----------------------|------|
| **Public Subnet**    | Web 서버와 NAT Gateway 위치. 외부 인터넷과 직접 통신 가능 |
| **Private Subnet**   | WAS, DB, Redis 인스턴스 위치. 외부 인터넷 접근 차단 |
| **Web (EC2 + Nginx)**| React 기반 정적 페이지 제공 |
| **WAS (EC2)**        | Spring Boot 기반 API 처리 및 비즈니스 로직 수행 |
| **DB (RDS - MySQL)** | 사용자, 멘토, 예약, 리뷰, 문의 등 주요 데이터 관리 |
| **Redis**            | 토큰 블랙리스트, 세션 관리, 캐싱 처리 등 실시간 데이터 관리 |
| **NAT Gateway**      | Private Subnet에 위치한 WAS가 외부로 통신할 수 있도록 지원 |
| **Internet Gateway** | Web 서버를 외부 인터넷과 연결 |
| **VPC**              | 전체 리소스를 담고 있는 가상 네트워크 |

---

## 🔄 동작 흐름

- 사용자가 도메인(passmaker.kro.kr) 으로 접속
- Web 서버에서 정적 파일 서빙, 필요 시 WAS로 API 요청 전송
- WAS(Spring Boot)는 DB와 통신하며 요청 처리
- 외부 API 호출(OAuth, Toss, STT 등)은 NAT Gateway를 통해 인터넷 접근

---

## ✅ 설계 목적 및 이점

- 보안 강화: WAS와 DB는 Private Subnet에 배치하여 외부 노출 최소화
- 확장성 확보: Web / WAS / DB 계층 분리로 수평·수직 확장 용이
- 실시간 멘토링 지원: WebRTC + WebSocket 기반 화상 멘토링 제공
- 비용 최적화: NAT Gateway를 통해 필요한 외부 접근만 허용
- 유지보수 용이성: 계층별로 문제 추적 및 디버깅 용이

---

## 📝 참고 사항

- **CI/CD**: GitHub Actions 기반 자동 배포 파이프라인 적용
- **컨테이너화**: 모든 서비스는 Docker 기반으로 실행
- **보안**: Spring Security + JWT 인증, Refresh Token 관리, Token Blacklist 도입
- **캐시/세션 관리**: Redis를 활용한 토큰 블랙리스트, 세션 캐싱, 실시간 데이터 처리


---



# 🚀 실행 방법

## 1. 환경 변수 설정

backend/src/main/resources/application-secret.properties 파일을 생성하고 다음과 같이 환경 변수를 입력합니다:

```
env
spring.datasource.url=jdbc:mysql://{RDS_ENDPOINT}:3306/passmaker
spring.datasource.username={RDS_USERNAME}
spring.datasource.password={RDS_PASSWORD}

jwt.secret={JWT_SECRET}
jwt.access-token-expiration=900000
jwt.refresh-token-expiration=604800000

KAKAO_CLIENT_ID={KAKAO_CLIENT_ID}
KAKAO_REDIRECT_URI={KAKAO_REDIRECT_URI}
GOOGLE_CLIENT_ID={GOOGLE_CLIENT_ID}
GOOGLE_REDIRECT_URI={GOOGLE_REDIRECT_URI}
TOSS_SECRET_KEY={TOSS_SECRET_KEY}
```

# 🌟 주요 기능

🔑 JWT + OAuth2 (카카오/구글) 기반 로그인 & 인증/인가

🎥 WebRTC + WebSocket 기반 실시간 화상 멘토링

📅 멘토 예약 관리 (신청, 승인, 거절, 자동 Room 생성)

💳 Toss Payments API 기반 결제 처리

📝 STT 기반 면접 기록 및 자동 요약

💬 1:1 문의하기 / 문의내역 관리

📌 마이페이지 (내 정보 수정, 예약 내역, 문의 내역, 리뷰 관리)

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
