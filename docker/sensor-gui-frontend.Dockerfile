FROM node:20
WORKDIR /app
COPY ../sensor-app/src/main/gui /app/gui
WORKDIR /app/gui
RUN npm install
RUN npm run build
CMD ["npm", "start"]

