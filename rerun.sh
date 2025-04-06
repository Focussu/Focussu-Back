#!/bin/sh

echo "Stopping and removing containers, networks, volumes..."
docker compose down -v

echo "Starting containers in detached mode..."
docker compose up -d

