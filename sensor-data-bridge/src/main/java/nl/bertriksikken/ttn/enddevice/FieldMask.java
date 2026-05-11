package nl.bertriksikken.ttn.enddevice;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Structure representing a field mask for partial updates when calling the
 * TTN registry API.
 */
public final class FieldMask {

    @JsonProperty("paths")
    List<String> paths = new ArrayList<>();
    
    FieldMask(List<String> fields) {
        paths.addAll(fields);
    }
    
}
