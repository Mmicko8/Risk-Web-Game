##Stage 1
FROM node:17-alpine as builder
WORKDIR /risk-front-end
ENV PATH /risk-front-end/node_modules/.bin:$PATH
COPY package*.json ./
RUN npm install --silent
COPY . .
RUN npm run build

# production environment
FROM nginx:stable-alpine
COPY ./nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /risk-front-end/build /usr/share/nginx/html
EXPOSE 3000
CMD ["nginx", "-g", "daemon off;"]