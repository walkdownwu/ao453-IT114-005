#!/bin/bash
echo "Compiling Milestone 1..."
mkdir -p bin
javac -d bin src/common/*.java
javac -d bin -cp bin src/server/*.java
javac -d bin -cp bin src/client/*.java

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
else
    echo "❌ Compilation failed!"
    exit 1
fi
