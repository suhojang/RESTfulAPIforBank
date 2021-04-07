### Bank 거래를 위한 RESTful API Server

+ INTRO
  + eGovframework, maven, mybatis 을 활용하여 개발
  + 고객사에서 뱅킹 업무가 필요한 경우 API 호출로 간편하게 처리 할 수 있도록 서버 구성

+ ROLE
  + Thread를 활용한 Socket(TCP) 통신, 전문(Data) Parsing, Batch Schedule 기능 구현
  + eGovframework, Maven, Mybatis를 활용
  + System Architecture Design
  + Database Design
  + WBS Management
  + RESTful API Develop

+ ETC
  + [고객사 -> RESTfull API Server -> KS-NET(VAN) -> BANK] 흐름을 따른다.
  + 고객사에 배포되는 [jar파일](https://github.com/suhojang/RESTfulAPIforBank-jar) 을 이용하여 Server와 통신 

```
git clone https://github.com/suhojang/RESTfulAPIforBank.git
```
