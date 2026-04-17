package nl.sikken.bertrik.cayenne;

import nl.sikken.bertrik.cayenne.formatter.FloatFormatter;
import nl.sikken.bertrik.cayenne.formatter.GpsFormatter;
import nl.sikken.bertrik.cayenne.formatter.IFormatter;
import nl.sikken.bertrik.cayenne.formatter.IntegerFormatter;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * <b>FR :</b> Enumération des types d’item possibles du protocole Cayenne LPP.<br>
 * <b>EN :</b> Enumeration of possible Cayenne LPP item types.
 */
public enum ECayenneItem {
    DIGITAL_INPUT(0, new IntegerFormatter(1, 1, false)), //
    DIGITAL_OUTPUT(1, new IntegerFormatter(1, 1, false)), //
    ANALOG_INPUT(2, new FloatFormatter(1, 2, 0.01, true)), //
    ANALOG_OUTPUT(3, new FloatFormatter(1, 2, 0.01, true)), //
    ILLUMINANCE(101, new FloatFormatter(1, 2, 1.0, false)), //
    PRESENCE(102, new IntegerFormatter(1, 1, false)), //
    TEMPERATURE(103, new FloatFormatter(1, 2, 0.1, true)), //
    HUMIDITY(104, new FloatFormatter(1, 1, 0.5, false)), //
    ACCELEROMETER(113, new FloatFormatter(3, 2, 0.001, true)), //
    BAROMETER(115, new FloatFormatter(1, 2, 0.1, false)), //
    GYROMETER(134, new FloatFormatter(3, 2, 0.01, true)), //
    GPS_LOCATION(136, new GpsFormatter()), //
    ;

    private final int type;
    private final IFormatter formatter;

    // reverse lookup table
    private static final Map<Integer, ECayenneItem> LOOKUP = new HashMap<>();
    static {
        Stream.of(values()).forEach((e) -> LOOKUP.put(e.getType(), e));
    }

    /**
     * <b>FR :</b> Constructeur du type Cayenne.<br>
     * <b>EN :</b> Cayenne item type constructor.
     * 
     * @param type code numérique du type / numeric type code
     * @param formatter formateur associé / associated formatter
     */
    ECayenneItem(int type, IFormatter formatter) {
        this.type = type;
        this.formatter = formatter;
    }

    /**
     * <b>FR :</b> Analyse un code type entier et retourne l’énumération Cayenne correspondante.<br>
     * <b>EN :</b> Parses a type code into the corresponding Cayenne enum.
     * 
     * @param type code type entier / integer type code
     * @return l’énumération ou null si non trouvée / enum value, or null if not found
     */
    public static ECayenneItem parse(int type) throws CayenneException {
        ECayenneItem item = LOOKUP.get(type);
        if (item == null) {
            throw new CayenneException("Invalid cayenne type " + type);
        }
        return item;
    }

    public int getType() {
        return type;
    }

    /**
     * <b>FR :</b> Formate les valeurs de mesure sous forme de tableau de chaînes de caractères.<br>
     * <b>EN :</b> Formats measurement values as a string array.
     * 
     * @param values valeurs à encoder / values to encode
     * @return valeurs formatées / formatted values
     */
    public String[] format(Number[] values) {
        return formatter.format(values);
    }

    /**
     * <b>FR :</b> Parse le contenu d’un buffer binaire en un tableau de valeurs numériques.<br>
     * <b>EN :</b> Parses the content of a byte buffer into an array of numerical values.
     * 
     * @param bb buffer binaire à parser / byte buffer to parse from
     * @return les valeurs numériques extraites / extracted numerical values
     */
    public Number[] parse(ByteBuffer bb) {
        return formatter.parse(bb);
    }

    /**
     * <b>FR :</b> Encode un tableau de valeurs numériques dans un buffer binaire.<br>
     * <b>EN :</b> Encodes an array of numerical values into a byte buffer.
     * 
     * @param bb buffer binaire de destination / destination byte buffer
     * @param values valeurs numériques à encoder / values to encode
     */
    public void encode(ByteBuffer bb, Number[] values) {
        formatter.encode(bb, values);
    }

}
