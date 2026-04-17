package nl.bertriksikken.geo;

/**
 * <b>FR :</b> Modèle géographique simple effectuant des calculs d’approximation de distance sur la Terre.<br>
 * <b>EN :</b> Simple geo model that calculates basic approximate distances on Earth.
 */
public final class SimpleGeoModel {

    private static final double CIRCUMFERENCE = 40075e3;

    /**
     * <b>FR :</b> Calcule une distance approximative entre deux localisations sur Terre.<br>
     * NB : approximation imprécise près des pôles, de la ligne de changement de date ou sur longues distances.<br>
     * <b>EN :</b> Calculates approximate distance between two locations on Earth.<br>
     * Note: accuracy is poor near poles, date line, or across large distances.
     *
     * @param p1 paire (lat,lon), degrés / pair (lat,lon), degrees
     * @param p2 paire (lat,lon), degrés / pair (lat,lon), degrees
     * @return distance approximative en mètres / approximate distance in meters
     */
    double distance(double[] p1, double[] p2) {
        double[] middle = new double[2];
        middle[0] = (p1[0] + p2[0]) / 2;
        middle[1] = (p1[1] + p2[1]) / 2;

        double circle = CIRCUMFERENCE * Math.cos(Math.toRadians(middle[0]));
        double dlat = CIRCUMFERENCE * (p2[0] - p1[0]) / 360.0;
        double dlon = circle * (p2[1] - p1[1]) / 360.0;
        return Math.sqrt(dlat * dlat + dlon * dlon);
    }
}
