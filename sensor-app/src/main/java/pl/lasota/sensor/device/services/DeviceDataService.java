package pl.lasota.sensor.device.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.lasota.sensor.configs.properties.DeviceProperties;
import pl.lasota.sensor.device.services.repositories.DeviceConfigRepository;
import pl.lasota.sensor.device.services.repositories.DeviceRepository;
import pl.lasota.sensor.device.services.repositories.DeviceTemporaryRepository;
import pl.lasota.sensor.device.services.repositories.DeviceTokenRepository;
import pl.lasota.sensor.entities.Device;
import pl.lasota.sensor.entities.DeviceConfig;
import pl.lasota.sensor.entities.DeviceTemporary;
import pl.lasota.sensor.entities.DeviceToken;
import pl.lasota.sensor.exceptions.SensorApiException;
import pl.lasota.sensor.flow.SettingDeviceConfigInterface;
import pl.lasota.sensor.payload.PayloadType;
import pl.lasota.sensor.payload.message.Config;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceDataService {

    private final DeviceRepository dr;
    private final DeviceConfigRepository dcr;
    private final DeviceConfigService dsu;
    private final DeviceTemporaryRepository dtr;
    private final DeviceTokenRepository dtor;
    private final SettingDeviceConfigInterface sdci;

    @Transactional
    public UUID saveTemporary(String memberId, String name) {
        UUID token = UUID.randomUUID();
        DeviceToken deviceToken = DeviceToken.builder()
                .member(memberId)
                .token(token).build();

        dtr.save(DeviceTemporary.builder()
                .time(OffsetDateTime.now())
                .name(name)
                .member(memberId)
                .currentDeviceToken(deviceToken).build());
        return token;
    }


    @Transactional
    public boolean moveToDeviceFromTemporary(String memberId, String deviceId, UUID token) {

        Optional<DeviceTemporary> deviceTemplateOptional = dtr.getDeviceTemplate(memberId, token);
        Optional<Device> deviceOptional = dr.findDeviceBy(memberId, deviceId);

        if (deviceTemplateOptional.isPresent() && deviceOptional.isPresent()) {
            DeviceTemporary deviceTemporary = deviceTemplateOptional.get();
            Device device = deviceOptional.get();
            device.setCurrentDeviceToken(deviceTemporary.getCurrentDeviceToken());
            device.setName(deviceTemporary.getName());
            dr.save(device);
            dtr.delete(deviceTemporary);
            return true;
        } else if (deviceTemplateOptional.isPresent()) {
            DeviceTemporary deviceTemporary = deviceTemplateOptional.get();
            Device device = Device.builder()
                    .member(memberId)
                    .name(deviceTemporary.getName())
                    .currentDeviceToken(deviceTemporary.getCurrentDeviceToken())
                    .id(deviceId.toUpperCase())
                    .build();
            dr.save(device);
            dtr.delete(deviceTemporary);
            return true;
        } else return deviceOptional.isPresent();
    }


    @Transactional
    public DeviceConfig currentDeviceConfig(String memberId, String deviceId) {
        Device device = dr.findDeviceBy(memberId, deviceId).orElseThrow(() -> new SensorApiException("Not found device by {}", deviceId));
        return dsu.currentDeviceConfigCheck(device);
    }

    @Transactional
    public LinkedList<DeviceConfig> getConfigForDevice(String memberId, String deviceId) {
        Device device = dr.findDeviceBy(memberId, deviceId).orElseThrow(() -> new SensorApiException("Not found device by {}", deviceId));
        LinkedList<DeviceConfig> allConfigBy = dcr.findAllDeviceConfigBy(device.getId());
        if (device.getCurrentDeviceConfig() == null) {
            return allConfigBy;
        }
        allConfigBy.remove(device.getCurrentDeviceConfig());
        allConfigBy.addFirst(device.getCurrentDeviceConfig());
        return allConfigBy;
    }


    @Transactional
    public DeviceConfig saveConfig(String memberId, String config, String versionConfig, String deviceId) {
        try {
            new ObjectMapper().readValue(config, Config.class);
        } catch (JsonProcessingException e) {
            throw new SensorApiException(e, "Occurred problem with parser config");
        }


        dsu.testConfigWithSchema(config, dsu.schemaForVersion(versionConfig));

        Device deviceOptional = dr.findDeviceBy(memberId, deviceId).orElseThrow(() -> new SensorApiException("Not found device by {}", deviceId));

        long checkSum = dsu.checkSum(config + versionConfig);
        Optional<DeviceConfig> existByCheckSum = dcr.getConfigByChecksum(checkSum, deviceOptional.getId());

        if (existByCheckSum.isPresent()) {
            throw new SensorApiException("All ready exist configuration " + existByCheckSum.get().getId());
        }

        DeviceConfig build = DeviceConfig
                .builder()
                .device(deviceOptional)
                .time(OffsetDateTime.now())
                .forVersion(versionConfig)
                .config(config)
                .checksum(checkSum)
                .build();

        return dcr.save(build);
    }

    @Transactional
    public void activateConfig(String memberId, String deviceId, Long configId) {
        Device device = dr.findDeviceBy(memberId, deviceId).orElseThrow(() -> new SensorApiException("Not found device by {}", deviceId));
        DeviceConfig deviceConfig = dcr.getDeviceConfig(deviceId, configId).orElseThrow(() -> new SensorApiException("Not found device config by {}", deviceId));
        dsu.testConfigWithSchema(deviceConfig.getConfig(), dsu.schemaForVersion(device.getVersion()));
        device.setCurrentDeviceConfig(deviceConfig);
        dr.save(device);
    }


    public boolean isDeviceExist(String memberId, String deviceId) {
        return dr.existsDevice(memberId, deviceId);
    }

    public boolean isCurrentTokenValid(String memberId, String deviceId, UUID token) {
        return dr.isCurrentTokenValid(memberId, deviceId, token);
    }

    public List<Device> getAllDevices() {
        return dr.findAll();
    }

    public List<Device> getAllDeviceBy(String memberId) {
        return dr.findAllDevicesBy(memberId);
    }

    public List<DeviceTemporary> getAllTemporaryBy(String memberId) {
        return dtr.findAllDevicesBy(memberId);
    }


    public boolean hasConfig(String deviceId) {
        return dcr.existsDeviceConfigBy(deviceId);
    }


    public Optional<Device> getDevice(String memberId, String deviceId) {
        return dcr.getDevice(memberId, deviceId);
    }


    public DeviceConfig getConfig(String memberId, String deviceId, String configId) {
        return dcr.getDeviceConfigById(memberId, deviceId, configId);
    }

    @Transactional
    public List<Integer> getPwmPins(String memberId, String deviceId) {
        return sdci.configuredPwmGpioPins(deviceId);
    }

    @Transactional
    public List<Integer> getDigitalPins(String memberId, String deviceId) {
        return sdci.configuredDigitalGpioPins(deviceId);
    }

    @Transactional
    public List<Integer> getAnalogPins(String memberId, String deviceId) {
        return sdci.configuredAnalogGpioPins(deviceId);
    }

    @Transactional
    public List<PayloadType> getMessageType(String memberId, String deviceId) {

        return PayloadType.getListMessageTypeFromDevice();
    }

    @Transactional
    public byte[] generateBuildPackage(String version,
                                       String deviceName,
                                       String wifiSsid,
                                       String wifiPassword,
                                       String apPassword,
                                       String memberId,
                                       DeviceProperties ap) throws IOException {
        UUID token = saveTemporary(memberId, deviceName);
        String[] command = {
                Paths.get(ap.getFirmwareFolder(), version, ap.getGenerateBuildPackage()).toString(),
                wifiSsid,
                wifiPassword,
                deviceName,
                apPassword,
                memberId,
                ap.getMqttIpExternal(),
                token.toString(),
                Paths.get(ap.getFirmwareFolder(), version).toAbsolutePath().toString()
        };
        String last = "";
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                last = line;
                log.info("SCRIPT {}", line);
            }
            process.waitFor();
            byte[] bytes = zipFolder(last);

            try (Stream<Path> walk = Files.walk(Path.of(last))) {
                walk.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
            log.info("Generated build package {}", last);
            return bytes;
        } catch (Exception e) {
            log.error("Problem ", e);
            throw new SensorApiException(e, "Error building device config");
        }
    }

    public byte[] zipFolder(String sourceDirPath) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {

            Path sourceDir = Paths.get(sourceDirPath);
            try (Stream<Path> walk = Files.walk(sourceDir)) {
                walk.filter(path -> !Files.isDirectory(path)).forEach(path -> {
                    ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString());
                    try {
                        zipOutputStream.putNextEntry(zipEntry);
                        Files.copy(path, zipOutputStream);
                        zipOutputStream.closeEntry();
                    } catch (IOException e) {
                        throw new RuntimeException("Problem with generate zip", e);
                    }
                });
            }
        }
        return byteArrayOutputStream.toByteArray();
    }


    public List<String> getVersions(DeviceProperties ap) {
        return Arrays.stream(Objects.requireNonNull(new File(ap.getFirmwareFolder()).listFiles()))
                .filter(File::isDirectory)
                .map(File::getName).toList();

    }

    public boolean isTokenExist(String memberId, UUID token) {
        return dtor.isExist(memberId, token);
    }


}
