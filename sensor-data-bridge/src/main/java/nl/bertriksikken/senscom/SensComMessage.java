package nl.bertriksikken.senscom;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

    /**
     * <b>FR :</b> Message Sensor.community tel que reçu via POST, adapté à la sérialisation JSON par Jackson.<br>
     * <b>EN :</b> Sensor.community message as uploaded through POST, suitable for JSON serialization by Jackson.
     */
@JsonAutoDetect(isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public final class SensComMessage {

    @JsonProperty("software_version")
    private final String softwareVersion;

    @JsonProperty("sensordatavalues")
    private final List<SensComItem> items = new ArrayList<>();

    /**
     * <b>FR :</b> Constructeur d’un message Sensor.community. <br>
     * <b>EN :</b> Constructor for a Sensor.community message.
     */
    public SensComMessage(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public void addItem(String name, String value) {
        items.add(new SensComItem(name, value));
    }

    public void addItem(String name, Double value) {
        items.add(new SensComItem(name, value));
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{softwareVersion=%s,items=%s}", softwareVersion, items);
    }

    record SensComItem(@JsonProperty("value_type") String name, @JsonProperty("value") String value) {
        /**
         * <b>FR :</b> Constructeur pratique : permet d’ajouter un item avec une valeur numérique (arrondie à une décimale).<br>
         * <b>EN :</b> Convenience constructor: add an item with a numeric value (rounded to 1 decimal).
         *
         * @param name  nom de l’item / item name
         * @param value valeur numérique arrondie / item value as double, rounded to 1 decimal
         */
        public SensComItem(String name, Double value) {
            this(name, String.format(Locale.ROOT, "%.1f", value));
        }
    }

}
