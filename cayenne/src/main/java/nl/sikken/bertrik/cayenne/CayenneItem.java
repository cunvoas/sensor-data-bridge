package nl.sikken.bertrik.cayenne;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Locale;

    /**
     * <b>FR :</b> Représentation d’une mesure unique sous forme d’item dans un message Cayenne LPP.<br>
     * <b>EN :</b> Representation of a single measurement item in a Cayenne LPP message.
     */
public final class CayenneItem {

    private final int channel;
    private final ECayenneItem type;
    private final Number[] values;

    /**
     * <b>FR :</b> Constructeur générique.<br>
     * <b>EN :</b> Generic constructor.
     * 
     * @param channel canal unique / unique channel
     * @param type    type de mesure / measurement type
     * @param values  valeurs / measurement values
     */
    public CayenneItem(int channel, ECayenneItem type, Number[] values) {
        this.channel = channel;
        this.type = type;
        this.values = values.clone();
    }

    /**
     * <b>FR :</b> Constructeur pour une mesure unique.<br>
     * <b>EN :</b> Constructor for a single value measurement.
     * 
     * @param channel canal unique / unique channel
     * @param type    type de mesure / measurement type
     * @param value   valeur unique / single value
     */
    public CayenneItem(int channel, ECayenneItem type, Number value) {
        this(channel, type, new Number[] { value });
    }

    public int getChannel() {
        return channel;
    }

    public ECayenneItem getType() {
        return type;
    }

    public Number[] getValues() {
        return values.clone();
    }

    public Number getValue() {
        return values[0];
    }

    public String[] format() {
        return type.format(values);
    }

    /**
     * <b>FR :</b> Parse un item à partir d’un buffer binaire et le retourne.<br>
     * <b>EN :</b> Parses an item from a byte buffer and returns it.
     * 
     * @param bb buffer binaire source / byte buffer
     * @return nouvel item Cayenne / new Cayenne item
     * @throws CayenneException en cas d’erreur de parsing / if an error occurs during parsing
     */
    public static CayenneItem parse(ByteBuffer bb) throws CayenneException {
        try {
            int channel = bb.get();
            return parsePacked(bb, channel);
        } catch (BufferUnderflowException e) {
            throw new CayenneException(e);
        }
    }

    /**
     * <b>FR :</b> Parse un item (format "packed") à partir d’un buffer binaire et le retourne.<br>
     * <b>EN :</b> Parses a packed item from a byte buffer and returns it.
     * 
     * @param bb buffer binaire source / byte buffer
     * @param channel canal associé / associated channel
     * @return nouvel item Cayenne / new Cayenne item
     * @throws CayenneException en cas d’erreur de parsing / if an error occurs during parsing
     */
    public static CayenneItem parsePacked(ByteBuffer bb, int channel) throws CayenneException {
        try {
            int type = bb.get() & 0xFF;
            ECayenneItem ct = ECayenneItem.parse(type);
            Number[] values = ct.parse(bb);
            return new CayenneItem(channel, ct, values);
        } catch (BufferUnderflowException e) {
            throw new CayenneException(e);
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{chan=%d,type=%s,value=%s}", channel, type, Arrays.toString(format()));
    }

    public void encode(ByteBuffer bb) throws CayenneException {
        try {
            bb.put((byte) channel);
            bb.put((byte) type.getType());
            type.encode(bb, values);
        } catch (BufferOverflowException e) {
            throw new CayenneException(e);
        }

    }

}
