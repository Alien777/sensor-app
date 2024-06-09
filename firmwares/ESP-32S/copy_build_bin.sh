#!/bin/bash

# Pobierz katalog, w którym znajduje się skrypt

# Zbuduj absolutne ścieżki
SRC_DIR="src/.pio/build/nodemcu-32s"
DEST_DIR="bin/"

# Upewnij się, że katalog docelowy istnieje
mkdir -p "$DEST_DIR"
echo $SRC_DIR/*.bin
echo $DEST_DIR

cp $SRC_DIR/*.bin $DEST_DIR