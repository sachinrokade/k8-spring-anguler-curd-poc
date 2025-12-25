# ----- Stage 1: Build Angular App -----
FROM node:22-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build --prod


# ----- Stage 2: Serve via NGINX -----
FROM nginx:1.25-alpine

# Copy Angular dist folder correctly
COPY --from=build /app/dist/angular-frontend /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
