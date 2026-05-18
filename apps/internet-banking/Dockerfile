# Stage 1: Build da aplicação Angular com Node.js
FROM node:20-alpine AS build
WORKDIR /app

# Copia dependências primeiro (aproveita o cache do Docker)
COPY package*.json ./
RUN npm install --prefer-offline

# Copia o código fonte e executa o build de produção
COPY . .
RUN npm run build:prod

# Stage 2: Serve os assets estáticos com Nginx (imagem muito menor)
FROM nginx:1.27-alpine
COPY --from=build /app/dist/internet-banking/browser /usr/share/nginx/html
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
