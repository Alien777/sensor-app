FROM openjdk:21
COPY target/*.jar api.jar
COPY ../sensor-api/src/main/resources/application.yml /sensor/api/application.yml
ENTRYPOINT ["java","-jar","api.jar","--spring.config.location=file:/app/api/application.yml"]

FROM node:16
# Ustaw katalog roboczy w kontenerze
WORKDIR /app
# Skopiuj pliki `package.json` oraz `package-lock.json` (jeśli istnieje) do katalogu roboczego
COPY package*.json ./
# Zainstaluj zależności projektu, korzystając z pamięci podręcznej npm, jeśli to możliwe
RUN npm install
# Skopiuj pozostałe pliki i katalogi projektu do katalogu roboczego w kontenerze
COPY ../sensor-api .
# Zbuduj aplikację za pomocą skryptu `build`
RUN npm run build
# Zdefiniuj zmienną środowiskową dla trybu produkcji
ENV NODE_ENV production
# Otwórz port, na którym będzie nasłuchiwała aplikacja
EXPOSE 3000
# Uruchom aplikację
CMD ["npm", "run", "start"]