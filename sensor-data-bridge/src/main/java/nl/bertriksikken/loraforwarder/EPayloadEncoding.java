
package nl.bertriksikken.loraforwarder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EPayloadEncoding {

    TTN_ULM("TTN_ULM"),
    CAYENNE("CAYENNE"),
    APELDOORN("APELDOORN"),
    SOUNDKIT("SOUNDKIT"), 
    JSON("JSON"), 
    NOT_SET("NOT_SET");

    private final String id;

    EPayloadEncoding(String id) {
        this.id = id;
    }


    @JsonValue
    public String getId() {
        return id;
    }

    @JsonCreator
    public static EPayloadEncoding fromId(String id) {
        if (id == null) {
            return null;
        }
        for (EPayloadEncoding encoding : values()) {
            if (encoding.id.equalsIgnoreCase(id)) {
                return encoding;
            }
        }
        return null;
    }
}
