package pl.lasota.sensor.api.apis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import pl.lasota.sensor.api.apis.mappper.Mapper;
import pl.lasota.sensor.api.apis.mqtt.MqttPreSendLayout;
import pl.lasota.sensor.api.entities.DeviceConfig;
import pl.lasota.sensor.api.exception.SensorApiException;
import pl.lasota.sensor.api.properties.ApiProperties;
import pl.lasota.sensor.api.services.DeviceService;
import pl.lasota.sensor.api.services.DeviceUtilsService;
import pl.lasota.sensor.internal.apis.api.SensorMicroserviceEndpoint;
import pl.lasota.sensor.internal.apis.api.device.*;
import pl.lasota.sensor.member.entities.Member;
import pl.lasota.sensor.member.services.MemberService;

import java.util.List;


@RestController()
@RequiredArgsConstructor
@Slf4j
public class SensorMicroserviceController implements SensorMicroserviceEndpoint {

    private final MqttPreSendLayout mqttPreSendLayout;
    private final MemberService ms;
    private final DeviceService ds;
    private final ApiProperties ap;
    private final DeviceUtilsService dsu;

    @Override
    public void sendConfigToDevice(SendConfigI configS) throws Exception {
        Member member = ms.loggedMember();
        mqttPreSendLayout.sendConfig(member.getId(), configS.deviceId());
    }

    @Override
    public void sendPwmValueToDevice(SendPwmI configS) throws Exception {
        Member member = ms.loggedMember();
        mqttPreSendLayout.sendPwm(member.getId(), configS.deviceId(), configS.pin(), configS.value());
    }

    @Override
    public void sendRequestForDataAnalog(SendForAnalogDataI configS) throws Exception {
        Member member = ms.loggedMember();
        mqttPreSendLayout.sendForAnalogData(member.getId(), configS.deviceId(), configS.pin());
    }

    @Override
    public String save(DeviceCreateI deviceCreateI) {
        Member member = ms.loggedMember();
        return ds.saveTemporary(member.getId(), deviceCreateI.id(), deviceCreateI.name());
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
            mqttPreSendLayout.sendConfig(member.getId(), deviceId);
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
    public String getMqttIp() {
        return ap.getMqtt().getUrl();
    }

}
