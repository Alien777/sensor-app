package pl.lasota.sensor.device.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import pl.lasota.sensor.configs.properties.DeviceProperties;
import pl.lasota.sensor.device.DeviceApiInterface;
import pl.lasota.sensor.device.DeviceConfigInterface;
import pl.lasota.sensor.device.DeviceSendMessageInterface;
import pl.lasota.sensor.device.model.*;
import pl.lasota.sensor.entities.Device;
import pl.lasota.sensor.entities.DeviceConfig;
import pl.lasota.sensor.entities.Member;
import pl.lasota.sensor.exceptions.SensorApiException;
import pl.lasota.sensor.member.MemberLoginDetailsServiceInterface;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController()
@RequiredArgsConstructor
@Slf4j
public class DeviceGatewayApi implements DeviceApiInterface, DeviceSendMessageInterface, DeviceConfigInterface {

    private final DeviceMessagePublish deviceMessagePublish;
    private final MemberLoginDetailsServiceInterface ms;
    private final DeviceDataService ds;
    private final DeviceProperties ap;
    private final DeviceConfigService dsu;

    @Override
    public UUID sendConfigToDevice(SendConfigI configS) throws Exception {
        Member member = ms.loggedMember();
        return deviceMessagePublish.sendConfig(member.getId(), configS.deviceId());
    }

    @Override
    public UUID sendDigitalValueToDevice(SendDigitalI configS) throws Exception {
        Member member = ms.loggedMember();
        return deviceMessagePublish.sendDigital(member.getId(), configS.deviceId(), configS.pin(), configS.value());
    }


    @Override
    public UUID sendPwmValueToDevice(SendPwmI configS) throws Exception {
        Member member = ms.loggedMember();
        return deviceMessagePublish.sendPwm(member.getId(), configS.deviceId(), configS.pin(), configS.value(), configS.duration());
    }

    @Override
    public UUID sendRequestForDataAnalog(SendForAnalogDataI configS) throws Exception {
        Member member = ms.loggedMember();
        return deviceMessagePublish.sendForAnalogData(member.getId(), configS.deviceId(), configS.pin());
    }


    @Override
    public UUID sendPing(SendDigitalI configS) throws Exception {
        Member member = ms.loggedMember();
        return deviceMessagePublish.sendForAnalogData(member.getId(), configS.deviceId(), configS.pin());
    }

    @Override
    public String save(DeviceCreateI deviceCreateI) {
        Member member = ms.loggedMember();
        return ds.saveTemporary(member.getId(), deviceCreateI.name());
    }

    @Override
    public List<DeviceI> get() {
        Member member = ms.loggedMember();
        return ds.getAllDeviceBy(member.getId()).stream().map(Mapper::map).toList();
    }

    @Override
    public DeviceI get(String deviceId) {
        Member member = ms.loggedMember();
        return Mapper.map(ds.getDevice(member.getId(), deviceId).orElseThrow(() -> new SensorApiException("Not found device {}", deviceId)));
    }

    @Override
    public List<DeviceI> getTemporaries() {
        Member member = ms.loggedMember();
        return ds.getAllTemporaryBy(member.getId()).stream().map(Mapper::map).toList();
    }

    @Override
    public void activateConfig(String deviceId, Long configId) {
        Member member = ms.loggedMember();
        ds.activateConfig(member.getId(), deviceId, configId);
        try {
            deviceMessagePublish.sendConfig(member.getId(), deviceId);
        } catch (Exception e) {
            log.error("Config sensor was save but not send to device", e);
        }
    }

    @Override
    public List<ConfigI> getConfigs(String deviceId) {
        Member member = ms.loggedMember();
        return ds.getConfigForDevice(member.getId(), deviceId).stream().map(Mapper::map).toList();
    }

    @Override
    public ConfigI getConfig(String deviceId, String configId) {
        Member member = ms.loggedMember();
        return Mapper.map(ds.getConfig(member.getId(), deviceId, configId));
    }

    @Override
    public ConfigI getCurrentConfig(String deviceId) {
        Member member = ms.loggedMember();
        DeviceConfig deviceConfig = ds.currentDeviceConfig(member.getId(), deviceId);
        return Mapper.map(deviceConfig);
    }

    @Override
    public String getSchema(String versionDevice) {
        return dsu.schemaForVersion(versionDevice);
    }

    @Override
    public void saveConfig(String deviceId, ConfigCreateI configCreate) {
        Member member = ms.loggedMember();
        ds.saveConfig(member.getId(), configCreate.config(), configCreate.version(), deviceId);
    }

    @Override
    public List<Integer> getConfigPwmPins(String deviceId) {
        Member member = ms.loggedMember();
        return ds.getPwmPins(member.getId(), deviceId);
    }

    @Override
    public List<Integer> getConfigAnalogPins(String deviceId) {
        Member member = ms.loggedMember();
        return ds.getAnalogPins(member.getId(), deviceId);
    }

    @Override
    public List<String> getConfigMessageType(String deviceId) {
        Member member = ms.loggedMember();
        return ds.getMessageType(member.getId(), deviceId).stream().map(Enum::name).toList();
    }

    @Override
    public List<Integer> getConfigDigitalPins(String deviceId) {
        Member member = ms.loggedMember();
        return ds.getDigitalPins(member.getId(), deviceId);
    }


    @Override
    public String getMqttIp() {
        return ap.getMqttIpExternal();
    }

    @Override
    public DeviceConfig currentDeviceConfig(String memberId, String deviceId) {
        return ds.currentDeviceConfig(memberId, deviceId);
    }

    @Override
    public Optional<Device> getDevice(String memberId, String deviceId) {
        return ds.getDevice(memberId, deviceId);
    }

    @Override
    public byte[] build(String version, String name, String ssid, String password) throws IOException {
        Member member = ms.loggedMember();
        return ds.generateBuildPackage(version, name, ssid, password, member.getId(), ap);
    }

    @Override
    public List<String> getVersions() {
        ms.loggedMember();
        return ds.getVersions(ap);
    }
}
