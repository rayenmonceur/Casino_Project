# Étape 1: build Angular
FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json .npmrc* ./
RUN npm install --legacy-peer-deps
COPY . .
RUN npx ng build

# Étape 2: Nginx sert les fichiers
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/casino-frontend/browser /usr/share/nginx/html
EXPOSE 80