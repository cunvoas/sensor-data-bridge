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
    @Deprecated
    double oldDistance(double[] p1, double[] p2) {
        double[] middle = new double[2];
        middle[0] = (p1[0] + p2[0]) / 2;
        middle[1] = (p1[1] + p2[1]) / 2;

        double circle = CIRCUMFERENCE * Math.cos(Math.toRadians(middle[0]));
        double dlat = CIRCUMFERENCE * (p2[0] - p1[0]) / 360.0;
        double dlon = circle * (p2[1] - p1[1]) / 360.0;
        return Math.sqrt(dlat * dlat + dlon * dlon);
    }
    
    
    /**
     * <b>FR :</b> Calcule la distance orthodromique (distance à vol d'oiseau) entre deux points géographiques exprimés en degrés.
     * Utilise la méthode crowFlyDistance de DistanceHelper, qui applique la formule de Haversine pour une précision correcte sur toute la surface du globe.
     *
     * <b>EN :</b> Computes the great-circle (crow-fly) distance between two geographic points in degrees.
     * Uses DistanceHelper.crowFlyDistance, which applies the Haversine formula for accurate results worldwide.
     *
     * @param p1 tableau [latitude, longitude] du premier point (degrés)
     * @param p2 tableau [latitude, longitude] du second point (degrés)
     * @return distance en mètres entre les deux points
     */
    double distance(double[] p1, double[] p2) {
    	
    	Double vLat1 = p1[0];
    	Double vLon1 = p1[1];

    	Double vLat2 = p2[0];
    	Double vLon2 = p2[1];
    	
    	return DistanceHelper.crowFlyDistance(vLat1, vLon1, vLat2, vLon2)*1000;
    }
}