# Fetching the latest node image on apline linux
FROM node:21.7.3-alpine3.20 AS builder
#FROM node:21.7.3 AS builder

# Setting up the work directory
WORKDIR /sn-catalog-app

COPY package.json .

RUN npm install

RUN npm i -g serve

COPY . .

RUN npm run build

EXPOSE 3000

CMD [ "serve", "-s", "dist" ]