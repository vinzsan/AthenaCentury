#!/usr/bin/env bash

# Pindah ke direktori script
cd "$(dirname "$0")" || exit 1

MEMORY="4G"
JAR_NAME="paper-26.2-112.jar"

# Fungsi untuk mematikan semua proses saat script di-stop (Ctrl+C)
cleanup() {
    echo -e "\n[!] Menghentikan script dan proses background..."
    if [ -n "${PYTHON_PID}" ]; then
        kill "${PYTHON_PID}" 2>/dev/null
    fi
    exit 0
}

# Tangkap sinyal SIGINT (Ctrl+C) dan SIGTERM
trap cleanup SIGINT SIGTERM EXIT

# Jalankan script DDNS python di background
if [ -f "update_dns.py" ]; then
    python3 update_dns.py &
    PYTHON_PID=$!
    echo "[*] DDNS updater running with PID: ${PYTHON_PID}"
fi

echo "========================================================="
echo "Memulai Minecraft Paper Server (${MEMORY} RAM)..."
echo "Tekan CTRL+C untuk mematikan server sepenuhnya."
echo "========================================================="

java -Xms${MEMORY} -Xmx${MEMORY} \
  -Dsun.net.client.defaultReadTimeout=120000 \
  -Dsun.net.client.defaultConnectTimeout=120000 \
  -Dpaper.playerconnection.keepalive=120 \
  -XX:+UseG1GC \
  -XX:+ParallelRefProcEnabled \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UnlockExperimentalVMOptions \
  -XX:+DisableExplicitGC \
  -XX:+AlwaysPreTouch \
  -XX:G1NewSizePercent=30 \
  -XX:G1MaxNewSizePercent=40 \
  -XX:G1ReservePercent=20 \
  -XX:G1HeapWastePercent=5 \
  -XX:G1MixedGCCountTarget=4 \
  -XX:InitiatingHeapOccupancyPercent=15 \
  -XX:G1MixedGCLiveThresholdPercent=90 \
  -XX:G1RSetUpdatingPauseTimePercent=5 \
  -XX:SurvivorRatio=32 \
  -XX:+PerfDisableSharedMem \
  -XX:MaxTenuringThreshold=1 \
  -XX:G1HeapRegionSize=8M \
  -Dusing.aikars.flags=https://mcflags.emc.gs \
  -Daikars.new.flags=true \
  --add-modules=jdk.incubator.vector \
  -jar "${JAR_NAME}" --nogui
