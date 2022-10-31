package modele.elements.cartes.effets.evenements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import modele.elements.Ville;
import modele.elements.cartes.effets.IEffetType;

@Getter
@Setter
@AllArgsConstructor
public class EffetTypeSubventionPubliqueImpl implements IEffetType {
    private Ville ville;
}
