package pl.lasota.sensor.flow.services.keeper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.entities.Member;
import pl.lasota.sensor.exceptions.SensorFlowException;
import pl.lasota.sensor.flow.SettingDeviceConfigInterface;
import pl.lasota.sensor.member.MemberLoginDetailsServiceInterface;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SettingDeviceConfigApi implements SettingDeviceConfigInterface {

    private final MemberLoginDetailsServiceInterface ms;
    private final KeeperForSetUp keeperForSetUp;

    @Override
    public List<Integer> configuredPwmGpioPins(String deviceId) throws SensorFlowException {
        Member member = ms.loggedMember();
        return keeperForSetUp.filter(deviceId, KeeperForSetUp.TypeConfig.PWM);
    }

    @Override
    public List<Integer> configuredAnalogGpioPins(String deviceId) throws SensorFlowException {
        Member member = ms.loggedMember();
        return keeperForSetUp.filter(deviceId, KeeperForSetUp.TypeConfig.ANALOG);
    }

    @Override
    public List<Integer> configuredDigitalGpioPins(String deviceId) throws SensorFlowException {
        Member member = ms.loggedMember();
        return keeperForSetUp.filter(deviceId, KeeperForSetUp.TypeConfig.DIGITAL);
    }
}
