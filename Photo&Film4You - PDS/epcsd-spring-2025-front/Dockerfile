# Build angular deliverable
FROM node:24 AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .

ARG BUILD_SCRIPT=build-prod

RUN npm run ${BUILD_SCRIPT}

# Serve angular deliverable
FROM nginx:1.29.3-alpine AS serve
RUN rm -rf /usr/share/nginx/html/*
COPY --from=build /app/dist/epcsd-spring2025-front/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
