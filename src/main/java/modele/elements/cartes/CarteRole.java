package modele.elements.cartes;

import lombok.*;
import modele.elements.enums.CouleurPionsRole;
import modele.elements.enums.NomsRoles;

@Getter
@Setter
@ToString
public abstract class CarteRole {

    private NomsRoles nomRole;
    private CouleurPionsRole couleurPionRole;
    private String descriptionRole;

    public CarteRole(CouleurPionsRole couleurPionRole) {
        this.couleurPionRole = couleurPionRole;
    }

}
