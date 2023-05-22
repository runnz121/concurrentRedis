# 동시성 이슈 해결

## 작업환경 세팅

docker 설치
````
brew install docker
brew link docker
docker version
````

mysql 설치 및 실행
````
docker pull mysql
docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=1234 --name mysql mysql
docker ps
````

docker: no matching manifest for linux/arm64/v8 in the manifest list entries. 
오류 발생시
````
docker pull --platform linux/x86_64 mysql
````

my sql 데이터베이스 생성
````
docker exec -it mysql bash
mysql -u root -p
create database stock_example;
use stock_example;
````


## Database 를 활용하여 레이스컨디션 해결해보기

### Optimistic Lock
lock 을 걸지않고 문제가 발생할 때 처리합니다.
대표적으로 version column 을 만들어서 해결하는 방법이 있습니다.
-> 개발자가 재시도 로직을 작성해줘야함
-> 성능상 이점은 있음

### Pessimistic Lock (exclusive lock)
다른 트랜잭션이 특정 row 의 lock 을 얻는것을 방지합니다.
A 트랜잭션이 끝날때까지 기다렸다가 B 트랜잭션이 lock 을 획득합니다.
특정 row 를 update 하거나 delete 할 수 있습니다.
일반 select 는 별다른 lock 이 없기때문에 조회는 가능합니다.
-> 동시성 이슈가 빈번하기 일어난다면 추천 

### named Lock 활용하기 -> meta data locking
이름과 함께 lock 을획득합니다. 해당 lock 은 다른세션에서 획득 및 해제가 불가능합니다.
-> 트랜잭션이 종료되어도 락이 해제 되지 않기 때문에 직접 구현해줘야함 
-> 별도의 Lock 전용 공간에 락을 건다
-> 접근하는 세션은 락 공간을 확인해서 락이 걸려있는지 확인한다
-> namedLock를 사용하는 datasoruce와 같이 사용할 경우 컨넥션 풀이 부족해질 수 있다
-> 분산락 구현시 많이 사용됨 


# Redis 를 사용해서 락 구현

### Lettuce
- Setnx 명령어 활용하여 분산락 구현
- Spin Lock 방식
  - 락이 풀렸는지 지속적으로 확인 
- mysql namedlock과 비슷
- 세션관리를 안해도 된다 

### Redisson
- pub-sub 기반의 락 구현
  - 채널을 만들고 락을 점유중인 해당 스레드가 해제시 채널로 해제 상태를 보내고 획득이 필요한 스레드가 해당 채널을 구독하고있다가 요청 받고 락 획득 
  

### Redis 설정

````
docker pull redis

docker run --name myredis -d -p 6379:6379 redis

docker exec -it 컨테이너id redis-cli
````

### Lettuce
구현이 간단하다
spring data redis 를 이용하면 lettuce 가 기본이기때문에 별도의 라이브러리를 사용하지 않아도 된다.
spin lock 방식이기때문에 동시에 많은 스레드가 lock 획득 대기 상태라면 redis 에 부하가 갈 수 있다.

### Redisson
락 획득 재시도를 기본으로 제공한다.
pub-sub 방식으로 구현이 되어있기 때문에 lettuce 와 비교했을 때 redis 에 부하가 덜 간다.
별도의 라이브러리를 사용해야한다.
lock 을 라이브러리 차원에서 제공해주기 떄문에 사용법을 공부해야 한다.


### 실무에서는 ?
재시도가 필요하지 않은 lock 은 lettuce 활용
재시도가 필요한 경우에는 redisson 를 활용
