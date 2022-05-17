# SSWU2021_Project
2021소프트웨어경진대회 성공하자

> ViewPager를 이용하여 Fragment 전환이 가능한 앱 개발
</br>

## 결과

<p align="center">
<img src="https://user-images.githubusercontent.com/77660379/168830641-b531a39b-65e7-4366-8e6a-c4f23f1bfc59.png" width="32%"/>
<img src="https://user-images.githubusercontent.com/77660379/168830895-2d7740cc-976a-4eb5-8e86-789e7fb68f9d.png" width="32%"/>
<img src="https://user-images.githubusercontent.com/77660379/153538759-50b20afc-c5c9-400d-96bb-0cf03ce6d26a.gif" width="32%"/>
</p>


## 수행정도

### 필수 과제

#### 기본 BottomNavigationView 화면 구현
* `ViewPager2+BottomNavigationView` 활용 -> 영화 순위, 영화 목록 화면 구현
* `영화 순위 화면`은 `별점(rt_score)`를 기준으로 `내림차순` 정렬
* `영화 목록 화면`은 `제목(title)`을 알파벳 순(`A~Z`)으로 정렬
* `즐겨찾기 화면`은 선택과제가 진행되지 않아 MainActivity에서 제거
* 각 리스트의 아이템은 ‘RecyclerView’ 활용 -> `제목`, `감독`, `설명`, `점수`, `이미지`가 노출되도록 설정

## 회고

### 선택 과제

#### 즐겨찾기 기능
* `즐겨찾기` Fragment, layout 완성 후 MainActivity와 연결했으나 Room을 활용한 로컬 데이터베이스 저장 및 즐겨찾기 기능은 구현하지 못했음

* Room Components 룸 구성 요소 이해하고 사전과제 기한에 구애받지 않고 추후 구현해볼 것
* ‘Entity’ : Database 안에 있는 테이블을 Java나 Kotlin 클래스로 나타낸 것이다. 데이터 모델 클래스라고 볼 수 있다. 
* ‘DAO’ : Database Access Object, 데이터베이스에 접근해서 실질적으로 insert, delete 등을 수행하는 메소드를 포함한다.
* ‘Database’ : database holder를 포함하며, 앱에 영구 저장되는 데이터와 기본 연결을 위한 주 액세스 지점이다. `RoomDatabase’를 extend 하는 추상 클래스여야 하며, 테이블과 버전을 정의하는 곳이다.

#### 상세페이지 기능
* `상세페이지` Activity, layout 완성했으나 리사이클러뷰 내부 아이템을 눌렀을 때 상세페이지로 이동하도록 구현하지 못했음
* RecyclerView가 사용되는 Fragment에서 Click 이벤트 적용하는 과정 이해하고 사전과제 기한에 구애받지 않고 추후 구현해볼 것

## Tech stack & Open-source libraries
### Architecture
- `MVVM Architecture`
- `Koin` 을 이용한 `Dependency Injection`

### AAC Libraries
 - `ViewModel`
 - `DataBinding`
 - `ViewBinding`
 - `LiveData`

### REST API 통신
 - `Retrofit2`
 - `OkHttp3`
 - `Gson`

### Other Libraries
 - `ViewPager2`
 - `Fragment`
 - `Coroutine`
 - `RecyclerView` + `ListAdapter`
 - `BottomNavigation`
