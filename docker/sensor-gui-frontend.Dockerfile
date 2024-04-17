FROM node:20
WORKDIR /sensor/gui
#COPY docker/data/config/frontend.env /sensor/gui/.env
COPY ../sensor-gui/src/main/gui/.output  /sensor/gui/.output
COPY ../sensor-gui/src/main/gui/.nuxt /sensor/gui/.nuxt
COPY ../sensor-gui/src/main/gui/node_modules /sensor/gui/node_modules
COPY ../sensor-gui/src/main/gui/package.json  /sensor/gui/package.json
COPY ../sensor-gui/src/main/gui/package-lock.json  /sensor/gui/package-lock.json
COPY ../sensor-gui/src/main/gui/tsconfig.json  /sensor/gui/tsconfig.json
COPY ../sensor-gui/src/main/gui/nuxt.config.ts /sensor/gui/nuxt.config.ts
#RUN npm install
CMD ["npm", "start"]
