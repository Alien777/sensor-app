package pl.lasota.sensor.device.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import pl.lasota.sensor.configs.properties.DeviceProperties;
import pl.lasota.sensor.device.DeviceApiInterface;
import pl.lasota.sensor.device.DeviceConfigInterface;
import pl.lasota.sensor.device.DeviceSendMessageInterface;
import pl.lasota.sensor.device.model.*;
import pl.lasota.sensor.entities.DeviceConfig;
import pl.lasota.sensor.entities.Member;
import pl.lasota.sensor.exceptions.SensorApiException;
import pl.lasota.sensor.member.MemberLoginDetailsServiceInterface;

import java.io.IOException;
import java.util.List;
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
    public UUID sendConfig(ConfigMessage configS) throws Exception {
        Member member = ms.loggedMember();
        return deviceMessagePublish.sendConfig(member.getId(), configS.deviceId());
    }

    @Override
    public UUID sendDigitalWriteRequest(DigitalWriteMessage digitalWriteMessage) throws Exception {
        Member member = ms.loggedMember();
        return deviceMessagePublish.sendDigitalWriteRequest(member.getId(), digitalWriteMessage.deviceId(), digitalWriteMessage.pin(), digitalWriteMessage.level());
    }

    @Override
    public UUID sendPwmWriteSetUp(PwmWriteSetUpMessage pwmWriteSetUpMessage) throws Exception {
        Member member = ms.loggedMember();
        return deviceMessagePublish.sendPwmWriteSetUp(member.getId(),
                pwmWriteSetUpMessage.deviceId(),
                pwmWriteSetUpMessage.gpio(),
                pwmWriteSetUpMessage.frequency(),
                pwmWriteSetUpMessage.resolution(),
                pwmWriteSetUpMessage.duty());
    }

    @Override
    public UUID sendAnalogReadSetUp(AnalogReadSetUpMessage analogWriteSetUpMessage) throws Exception {
        Member member = ms.loggedMember();
        return deviceMessagePublish.sendAnalogReadSetUp(member.getId(),
                analogWriteSetUpMessage.deviceId(),
                analogWriteSetUpMessage.gpio(),
                analogWriteSetUpMessage.resolution());
    }

    @Override
    public UUID sendDigitalSetUp(DigitalSetUpMessage messageDigitalSetUp) throws Exception {
        Member member = ms.loggedMember();
        return deviceMessagePublish.sendDigitalSetUp(member.getId(),
                messageDigitalSetUp.deviceId(),
                messageDigitalSetUp.gpio(),
                messageDigitalSetUp.mode());
    }

    @Override
    public UUID sendPing(PingMessage pingMessage) throws Exception {
        Member member = ms.loggedMember();
        return deviceMessagePublish.sendPing(member.getId(), pingMessage.deviceId());
    }

    @Override
    public UUID sendPwmWriteRequest(PwmWriteRequestMessage pwmWriteRequestMessage) throws Exception {
        Member member = ms.loggedMember();
        return deviceMessagePublish.sendPwmWriteRequest(member.getId(), pwmWriteRequestMessage.deviceId(), pwmWriteRequestMessage.pin(), pwmWriteRequestMessage.value(), pwmWriteRequestMessage.duration());
    }

    @Override
    public UUID sendAnalogReadOneShotRequest(AnalogReadOneShotRequestMessage analogReadOneShotRequestMessage) throws Exception {
        Member member = ms.loggedMember();
        return deviceMessagePublish.sendAnalogReadOneShotRequest(member.getId(), analogReadOneShotRequestMessage.deviceId(), analogReadOneShotRequestMessage.pin());
    }


    @Override
    public UUID save(DeviceCreateI deviceCreateI) {
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
    public byte[] build(DeviceBuildI device) throws IOException {
        Member member = ms.loggedMember();
        return ds.generateBuildPackage(device.version(), device.name(), device.wifiSsid(), device.wifiPassword(), device.apPassword(), member.getId(), ap);
    }

    @Override
    public List<String> getVersions() {
        ms.loggedMember();
        return ds.getVersions(ap);
    }
}
