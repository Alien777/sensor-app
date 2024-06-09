#!/bin/bash

# Function to download a file
download_file() {
    local url=$1
    local output=$2
    echo "curl -L -v -o" $output $url
    curl -L -o $output $url
}

# Function to unzip a ZIP file
unzip_file() {
    local ZIP_FILE=$1
    local dest_dir=$2
    echo "Unzipping file $ZIP_FILE to directory $dest_dir"
    unzip "$ZIP_FILE" -d "$dest_dir" > /dev/null 2>&1
}

# Function to generate the config.csv file
generate_config_csv() {
    local SSID=$1
    local PASSWORD=$2
    local MEMBER_ID=$3
    local SERVER_IP=$4
    local TOKEN=$5
    local PATH=$6
    local CSV_FILE="$PATH/config.csv"


    echo "key,type,encoding,value" >> "$CSV_FILE"
    echo "storage,namespace,," >> "$CSV_FILE"
    echo "wifi_ssid,string,string,$SSID" >> "$CSV_FILE"
    echo "wifi_password,string,string,$PASSWORD" >> "$CSV_FILE"
    echo "member_id,string,string,$MEMBER_ID" >> "$CSV_FILE"
    echo "server_ip,string,string,$SERVER_IP" >> "$CSV_FILE"
    echo "token,string,string,$TOKEN" >> "$CSV_FILE"

}

# Function to run the Python script
run_python_script() {
    local TMP_PATH=$1
    local script_path="$TMP_PATH/esp-idf-master/components/nvs_flash/nvs_partition_generator/nvs_partition_gen.py"
    local input_file="$TMP_PATH/config.csv"
    local output_file="$TMP_PATH/nvs.bin"
    local size="0x6000"

    echo "Running the Python script"
    cd "$TMP_PATH/esp-idf-master" || { echo "Failed to change directory to $TMP_PATH/esp-idf-master"; exit 1; }
    source export.sh

    pip install --user -r requirements.txt
    pip install --user esp-idf-nvs-partition-gen

    python3 $script_path generate $input_file $output_file "0x6000" 2>&1 | tee "$TMP_PATH/python_script.log"
    if [[ ! -f "$output_file" ]]; then
        echo "Error: The nvs.bin file was not generated"
        cat "$TMP_PATH/python_script.log"
        exit 1
    fi

    echo "The Python script has finished running"
}

# Main function
main() {
    local SSID=$1
    local PASSWORD=$2
    local MEMBER_ID=$3
    local SERVER_IP=$4
    local TOKEN=$5
    local SRC_PATH=$6

    local TMP_PATH=$(mktemp -d)

    local REMO_IDF_URL="https://github.com/espressif/esp-idf/archive/refs/heads/master.zip"
    local ZIP_FILE="$TMP_PATH/repo.zip"

    download_file "$REMO_IDF_URL" "$ZIP_FILE"
    unzip_file "$ZIP_FILE" "$TMP_PATH"
    rm "$ZIP_FILE"

    generate_config_csv "$SSID" "$PASSWORD" "$MEMBER_ID" "$SERVER_IP" "$TOKEN" "$TMP_PATH"
    run_python_script "$TMP_PATH"

    cp $SRC_PATH/bin/*.bin $TMP_PATH
    cp "$SRC_PATH/upload_firmware_by_usb.sh" "$TMP_PATH"
    cp "$SRC_PATH/upload_firmware_by_usb.bat" "$TMP_PATH"

    echo "The script has finished running"
    rm -rf "$TMP_PATH/esp-idf-master"
    echo "$TMP_PATH"
}

if [ "$#" -ne 6 ]; then
    echo "Usage: $0 <ssid> <password> <member_id> <mqtt_ip_external> <token> <TMP_PATH>"
    exit 1
fi

# Call the main function
main "$1" "$2" "$3" "$4" "$5" "$6"