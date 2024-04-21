FROM node:20
WORKDIR /app
COPY ../sensor-gui/src/main/gui /app/gui
WORKDIR /app/gui
RUN npm install
RUN npm run build
CMD ["npm", "start"]

