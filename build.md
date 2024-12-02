# 手動Build手順

``` sh
./gradlew bootBuildImage --imageName=ablankz/nova-storage-service:1.0.2
docker push ablankz/nova-storage-service:1.0.2
```