package modele.enums;

public enum DonneesVille {

    /**
     * Villes Bleu
     */

    Atlanta(47500,700),
    Chicago(400,41),
    Essen(100,24),
//    Londres,
//    Madrid,
//    Milan,
//    Montréal,
//    Paris,
//    Saint_Pétersbourg,
//    San_Francisco,
//    New_York,
//    Washington;

    /*
    * Villes Jaune:
    * */
    Bogota(8702000,21000),
    Buenos_Aires(13639000,5200),
    Johannesburg(3888000,2400),
    Khartoum(4897000,4500),
    Kinshasa(9046000,15500),
    Lagos(1547000,12700),
    Lima(9121000,14000),
    Los_Angeles(14900000,2400),
    Mexico(19463000,9500),
    Miami(5582000,1700),
    Santiago(6015000,6500),
    Sao_Paulo(20186000,6400),

    /*
    * Villes Noir:
    * */
    Alger(2946000,6500),
    Bagdad(6204000,10400),
    Calcutta(14374000,1900),
    Chennai(8965000,14600),
    Delhi(22242000,1500),
    Istanbul(13576000,9700),
    Karachi(2071000,25800),
    Le_Caire(14718000,8900),
    Moscou(15512000,3500),
    Mumbai(1690000,30900),
    Riyad(5037000,3400),
    Teheran(7419000,9500),

    /*
    * Villes Rouge:
    * */
    Bangkok(7151000,3200),
    Hô_chi_minh_ville(834000,9900),
    Hong_kong(7106000,25900),
    Jakarta(26063000,9400),
    Manille(20767000,14400),
    Osaka(2871000,19000),
    Pékin(17311000,5000),
    Séoul(22547000,10400),
    Shangai(13482000,2200),
    Sydney(3785000,2100),
    Taipei(8338000,7300),
    Tokyo(13189000,6030);

    private final int populationTotaleVille;
    private final int populationKmCarreVille;

    DonneesVille(int populationTotaleVille, int populationKmCarreVille) {
        this.populationTotaleVille = populationTotaleVille;
        this.populationKmCarreVille = populationKmCarreVille;
    }

    public int getPopulationTotaleVille() {
        return populationTotaleVille;
    }

    public int getPopulationKmCarreVille() {
        return populationKmCarreVille;
    }
}
