#!/bin/bash
PORT=${1:-3000}
echo "Starting server on port $PORT..."
java -cp bin server.Server $PORT
