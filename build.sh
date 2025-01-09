./mvnw clean package -DskipTests=true &&
docker build -t e-commerce/ggruzdov-demo-app:1.0 .