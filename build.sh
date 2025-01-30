#!/bin/bash

./mvnw clean package -DskipTests=true &&
docker build --no-cache -t e-commerce/ggruzdov-demo-app:1.0 .