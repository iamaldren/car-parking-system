package com.aldren.properties;

import com.aldren.properties.sub.Type;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@ConfigurationProperties("app.system.vehicle")
public class VehicleProperties {

    private List<Type> types;

    public Map<String, BigDecimal> getFee() {
        return types.stream()
                .collect(Collectors.toMap(Type::getKind, Type::getFee));
    }

    public Map<Integer, String> getKindByIndex() {
        return types.stream()
                .collect(Collectors.toMap(Type::getIndex, Type::getKind));
    }

    public Map<String, String> getLotName() {
        return types.stream()
                .collect(Collectors.toMap(Type::getKind, Type::getLotName));
    }

}
