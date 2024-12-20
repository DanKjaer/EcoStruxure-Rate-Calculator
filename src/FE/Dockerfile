# Stage 1: Build Angular application
FROM node:22 AS build
WORKDIR /app
COPY . ./
RUN npm install
RUN npm run build

# Stage 2: Setup Nginx and serve built Angular application
FROM nginx:alpine
COPY --from=build /app/dist/fe/browser /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
